package com.example.large.membersystem.controller.member;

import com.example.large.membersystem.service.member.ChangeStatusInDto;
import com.example.large.membersystem.service.member.ChangeStatusOutDto;
import com.example.large.membersystem.service.member.FetchDetailInDto;
import com.example.large.membersystem.service.member.FetchDetailOutDto;
import com.example.large.membersystem.service.member.MemberDetailDto;
import com.example.large.membersystem.service.member.MemberService;
import com.example.large.membersystem.service.member.UpdateProfileInDto;
import com.example.large.membersystem.service.member.UpdateProfileOutDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MemberDetailController {

    private final MemberService memberService;

    public MemberDetailController(MemberService memberService) {
        this.memberService = memberService;
    }

    @ModelAttribute("memberDetailForm")
    public MemberDetailForm initForm() {
        MemberDetailForm form = new MemberDetailForm();
        form.canEdit = false;
        form.canChangeStatus = false;
        return form;
    }

    @GetMapping("/members/{memberId}")
    public String show(@PathVariable("memberId") long memberId,
                       @ModelAttribute("memberDetailForm") MemberDetailForm form,
                       Model model) {
        FetchDetailInDto inDto = new FetchDetailInDto();
        inDto.memberId = memberId;

        FetchDetailOutDto outDto = memberService.fetchMemberDetail(inDto);
        List<String> messages = new ArrayList<>(outDto.messageList);

        if (outDto.detail == null) {
            model.addAttribute("messages", messages);
            return "member/detail";
        }

        MemberDetailDto d = outDto.detail;
        form.memberId = d.memberId;
        form.memberCode = d.memberCode;
        form.memberName = d.memberName;
        form.email = d.email;
        form.statusCode = d.statusCode;
        form.memo = d.memo;

        form.canEdit = !"CANCELED".equals(d.statusCode);
        form.canChangeStatus = !"CANCELED".equals(d.statusCode);

        model.addAttribute("messages", messages);
        return "member/detail";
    }

    @PostMapping(value = "/members/{memberId}", params = "updateProfile")
    public String updateProfile(@PathVariable("memberId") long memberId,
                                @ModelAttribute("memberDetailForm") MemberDetailForm form,
                                Model model) {
        UpdateProfileInDto inDto = new UpdateProfileInDto();
        inDto.memberId = memberId;
        inDto.memberName = form.memberName;
        inDto.email = form.email;
        inDto.memo = form.memo;
        inDto.operatorId = 1L;

        UpdateProfileOutDto outDto = memberService.updateProfile(inDto);

        List<String> messages = new ArrayList<>(outDto.messageList);
        model.addAttribute("messages", messages);

        return "redirect:/members/" + memberId;
    }

    @PostMapping(value = "/members/{memberId}", params = "changeStatus")
    public String changeStatus(@PathVariable("memberId") long memberId,
                               @ModelAttribute("memberDetailForm") MemberDetailForm form,
                               Model model) {
        ChangeStatusInDto inDto = new ChangeStatusInDto();
        inDto.memberId = memberId;
        inDto.newStatusCode = form.statusCode;
        inDto.reason = "UI operation";
        inDto.operatorId = 1L;

        ChangeStatusOutDto outDto = memberService.changeStatus(inDto);
        List<String> messages = new ArrayList<>(outDto.messageList);
        model.addAttribute("messages", messages);

        return "redirect:/members/" + memberId;
    }
}
