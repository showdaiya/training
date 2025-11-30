package com.example.large.membersystem.controller.billing;

import com.example.large.membersystem.service.billing.BillingService;
import com.example.large.membersystem.service.billing.BillingTargetDto;
import com.example.large.membersystem.service.billing.CalcMonthlyBillingInDto;
import com.example.large.membersystem.service.billing.CalcMonthlyBillingOutDto;
import com.example.large.membersystem.service.billing.ExecuteMonthlyBillingInDto;
import com.example.large.membersystem.service.billing.ExecuteMonthlyBillingOutDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @ModelAttribute("billingForm")
    public BillingForm initForm() {
        BillingForm form = new BillingForm();
        form.targetMonth = YearMonth.now();
        return form;
    }

    @GetMapping("/billing")
    public String index(@ModelAttribute("billingForm") BillingForm form,
                        Model model) {
        CalcMonthlyBillingInDto inDto = new CalcMonthlyBillingInDto();
        inDto.targetMonth = form.targetMonth;

        CalcMonthlyBillingOutDto outDto = billingService.calcMonthlyBilling(inDto);

        List<BillingTargetDto> list = new ArrayList<>(outDto.billingTargetList);
        model.addAttribute("targets", list);
        model.addAttribute("messages", outDto.messageList);
        return "billing/index";
    }

    @PostMapping(value = "/billing", params = "execute")
    public String execute(@ModelAttribute("billingForm") BillingForm form,
                          Model model) {
        ExecuteMonthlyBillingInDto inDto = new ExecuteMonthlyBillingInDto();
        inDto.targetMonth = form.targetMonth;
        inDto.operatorId = 1L;

        ExecuteMonthlyBillingOutDto outDto = billingService.executeMonthlyBilling(inDto);

        List<BillingTargetDto> list = new ArrayList<>(outDto.billingTargetList);
        model.addAttribute("executedTargets", list);
        model.addAttribute("messages", outDto.messageList);
        return "billing/result";
    }
}
