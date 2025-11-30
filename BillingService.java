package com.example.large.membersystem.service.billing;

import com.example.large.membersystem.domain.billing.BillingEntity;
import com.example.large.membersystem.domain.order.OrderEntity;
import com.example.large.membersystem.repository.BillingRepository;
import com.example.large.membersystem.repository.OrderRepository;
import com.example.large.membersystem.shared.SharedLogicStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class BillingService {

    private final OrderRepository orderRepository;
    private final BillingRepository billingRepository;
    private final Clock clock;

    public BillingService(OrderRepository orderRepository,
                          BillingRepository billingRepository,
                          Clock clock) {
        this.orderRepository = orderRepository;
        this.billingRepository = billingRepository;
        this.clock = clock;
    }

    public CalcMonthlyBillingOutDto calcMonthlyBilling(CalcMonthlyBillingInDto inDto) {
        CalcMonthlyBillingOutDto outDto = new CalcMonthlyBillingOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();
        outDto.billingTargetList = new ArrayList<>();

        if (inDto.targetMonth == null) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("targetMonth is required.");
            return outDto;
        }

        YearMonth ym = inDto.targetMonth;
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<OrderEntity> orders = orderRepository.findByPeriod(from, to);

        HashMap<Long, BillingTargetDto> map = new HashMap<>();

        for (int i = 0; i < orders.size(); i++) {
            OrderEntity e = orders.get(i);
            Long memberId = Long.valueOf(e.memberId);

            BillingTargetDto target = map.get(memberId);
            if (target == null) {
                target = new BillingTargetDto();
                target.memberId = e.memberId;
                target.totalAmount = BigDecimal.ZERO;
                target.orderCount = 0;
                target.alreadyBilled = false;
                map.put(memberId, target);
            }

            BigDecimal amount = e.amount == null ? BigDecimal.ZERO : e.amount;
            target.totalAmount = target.totalAmount.add(amount);
            target.orderCount = target.orderCount + 1;
        }

        List<BillingEntity> already = billingRepository.findByMonth(ym);
        for (int i = 0; i < already.size(); i++) {
            BillingEntity b = already.get(i);
            Long memberId = Long.valueOf(b.memberId);
            BillingTargetDto target = map.get(memberId);
            if (target == null) {
                continue;
            }
            target.alreadyBilled = true;
        }

        outDto.billingTargetList = new ArrayList<>(map.values());
        return outDto;
    }

    public ExecuteMonthlyBillingOutDto executeMonthlyBilling(ExecuteMonthlyBillingInDto inDto) {
        ExecuteMonthlyBillingOutDto outDto = new ExecuteMonthlyBillingOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();
        outDto.billingTargetList = new ArrayList<>();

        CalcMonthlyBillingInDto calcIn = new CalcMonthlyBillingInDto();
        calcIn.targetMonth = inDto.targetMonth;
        CalcMonthlyBillingOutDto calcOut = calcMonthlyBilling(calcIn);

        if (calcOut.logicStatus != SharedLogicStatus.SUCCESS) {
            outDto.logicStatus = calcOut.logicStatus;
            outDto.messageList.addAll(calcOut.messageList);
            return outDto;
        }

        List<BillingTargetDto> resultList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(clock);

        for (int i = 0; i < calcOut.billingTargetList.size(); i++) {
            BillingTargetDto target = calcOut.billingTargetList.get(i);
            if (target.alreadyBilled) {
                continue;
            }
            BillingEntity entity = new BillingEntity();
            entity.memberId = target.memberId;
            entity.billingMonth = inDto.targetMonth;
            entity.amount = target.totalAmount;
            entity.orderCount = target.orderCount;
            entity.executedAt = now;
            entity.executedBy = inDto.operatorId;

            billingRepository.save(entity);
            resultList.add(target);
        }

        outDto.billingTargetList = resultList;
        return outDto;
    }
}
