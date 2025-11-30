package com.example.large.membersystem.controller.member;

import com.example.large.membersystem.service.member.MemberService;
import com.example.large.membersystem.service.member.SearchMembersInDto;
import com.example.large.membersystem.service.member.SearchMembersOutDto;
import com.example.large.membersystem.service.member.MemberSummaryDto;
import com.example.large.membersystem.service.order.MonthlyOrderSummaryInDto;
import com.example.large.membersystem.service.order.MonthlyOrderSummaryOutDto;
import com.example.large.membersystem.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MemberListController {

    private final MemberService memberService;
    private final OrderService orderService;

    public MemberListController(MemberService memberService,
                                OrderService orderService) {
        this.memberService = memberService;
        this.orderService = orderService;
    }

    @ModelAttribute("memberListSearchForm")
    public MemberListSearchForm initSearchForm() {
        MemberListSearchForm form = new MemberListSearchForm();
        form.status = "";
        form.keyword = "";
        form.showOnlyRisky = false;
        form.registeredFrom = null;
        form.registeredTo = null;
        return form;
    }

    @GetMapping("/members")
    public String index(@ModelAttribute("memberListSearchForm") MemberListSearchForm form,
                        Model model) {
        SearchMembersInDto inDto = new SearchMembersInDto();
        inDto.keyword = form.keyword;
        inDto.status = form.status;
        inDto.registeredFrom = form.registeredFrom;
        inDto.registeredTo = form.registeredTo;

        SearchMembersOutDto outDto = memberService.searchMembers(inDto);

        List<MemberSummaryDto> resultList = new ArrayList<>();
        for (int i = 0; i < outDto.memberSummaryList.size(); i++) {
            MemberSummaryDto s = outDto.memberSummaryList.get(i);
            if (form.showOnlyRisky && !s.riskyFlag) {
                continue;
            }
            resultList.add(s);
        }

        model.addAttribute("members", resultList);
        model.addAttribute("messages", outDto.messageList);

        YearMonth targetMonth = YearMonth.now(orderService.getClock());
        MonthlyOrderSummaryInDto in2 = new MonthlyOrderSummaryInDto();
        in2.targetMonth = targetMonth;
        MonthlyOrderSummaryOutDto out2 = orderService.summarizeMonthly(in2);
        model.addAttribute("monthlySummary", out2);

        return "member/list";
    }

    @PostMapping(value = "/members", params = "search")
    public String search(@ModelAttribute("memberListSearchForm") MemberListSearchForm form,
                         Model model) {
        return index(form, model);
    }

    @PostMapping(value = "/members", params = "clear")
    public String clear(@ModelAttribute("memberListSearchForm") MemberListSearchForm form,
                        Model model) {
        form.keyword = "";
        form.status = "";
        form.registeredFrom = null;
        form.registeredTo = null;
        form.showOnlyRisky = false;
        return index(form, model);
    }
}
