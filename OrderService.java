package com.example.large.membersystem.service.order;

import com.example.large.membersystem.domain.order.OrderEntity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final Clock clock;

    public OrderService(OrderRepository orderRepository, Clock clock) {
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    public Clock getClock() {
        return this.clock;
    }

    public SearchOrdersOutDto searchOrders(SearchOrdersInDto inDto) {
        SearchOrdersOutDto outDto = new SearchOrdersOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();
        outDto.orderList = new ArrayList<>();

        if (inDto.memberId <= 0L) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("memberId is required.");
            OrderSummaryDto s = new OrderSummaryDto();
            s.totalAmount = BigDecimal.ZERO;
            s.orderCount = 0;
            outDto.summary = s;
            return outDto;
        }

        LocalDate from = inDto.fromDate;
        LocalDate to = inDto.toDate;
        if (from != null && to != null && from.isAfter(to)) {
            outDto.logicStatus = SharedLogicStatus.BUSNESS_ERROR;
            outDto.messageList.add("Date range is invalid.");
            OrderSummaryDto s = new OrderSummaryDto();
            s.totalAmount = BigDecimal.ZERO;
            s.orderCount = 0;
            outDto.summary = s;
            return outDto;
        }

        List<OrderEntity> all = orderRepository.findByMemberId(inDto.memberId);

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        List<OrderDto> list = new ArrayList<>();

        for (int i = 0; i < all.size(); i++) {
            OrderEntity e = all.get(i);

            LocalDate orderDate = e.orderDate == null ? null : e.orderDate.toLocalDate();
            if (from != null && (orderDate == null || orderDate.isBefore(from))) {
                continue;
            }
            if (to != null && (orderDate == null || orderDate.isAfter(to))) {
                continue;
            }

            OrderDto d = new OrderDto();
            d.orderId = e.id;
            d.memberId = e.memberId;
            d.orderDate = e.orderDate;
            d.amount = e.amount == null ? BigDecimal.ZERO : e.amount;
            d.statusCode = e.statusCode;
            d.paymentMethodCode = e.paymentMethodCode;

            list.add(d);
            total = total.add(d.amount);
            count = count + 1;
        }

        Collections.sort(list, new Comparator<OrderDto>() {
            @Override
            public int compare(OrderDto o1, OrderDto o2) {
                LocalDateTime a = o1.orderDate;
                LocalDateTime b = o2.orderDate;
                if (a == null && b == null) {
                    return 0;
                }
                if (a == null) {
                    return 1;
                }
                if (b == null) {
                    return -1;
                }
                return b.compareTo(a);
            }
        });

        OrderSummaryDto summary = new OrderSummaryDto();
        summary.totalAmount = total;
        summary.orderCount = count;

        outDto.orderList = list;
        outDto.summary = summary;

        return outDto;
    }

    public MonthlyOrderSummaryOutDto summarizeMonthly(MonthlyOrderSummaryInDto inDto) {
        MonthlyOrderSummaryOutDto outDto = new MonthlyOrderSummaryOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();
        outDto.byStatus = new HashMap<>();

        if (inDto.targetMonth == null) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("targetMonth is required.");
            outDto.totalAmount = BigDecimal.ZERO;
            outDto.totalOrderCount = 0;
            return outDto;
        }

        YearMonth ym = inDto.targetMonth;
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        List<OrderEntity> all = orderRepository.findByPeriod(from, to);

        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        HashMap<String, Integer> byStatus = new HashMap<>();

        for (int i = 0; i < all.size(); i++) {
            OrderEntity e = all.get(i);
            BigDecimal amount = e.amount == null ? BigDecimal.ZERO : e.amount;
            total = total.add(amount);
            count = count + 1;

            String status = e.statusCode == null ? "UNKNOWN" : e.statusCode;
            Integer current = byStatus.get(status);
            if (current == null) {
                byStatus.put(status, Integer.valueOf(1));
            } else {
                byStatus.put(status, Integer.valueOf(current.intValue() + 1));
            }
        }

        outDto.totalAmount = total;
        outDto.totalOrderCount = count;
        outDto.byStatus = byStatus;

        return outDto;
    }
}
