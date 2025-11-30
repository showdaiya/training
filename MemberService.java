package com.example.large.membersystem.service.member;

import com.example.large.membersystem.domain.member.MemberEntity;
import com.example.large.membersystem.repository.MemberRepository;
import com.example.large.membersystem.repository.OrderRepository;
import com.example.large.membersystem.shared.SharedLogicStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final Clock clock;

    public MemberService(MemberRepository memberRepository,
                         OrderRepository orderRepository,
                         Clock clock) {
        this.memberRepository = memberRepository;
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    public Clock getClock() {
        return this.clock;
    }

    public SearchMembersOutDto searchMembers(SearchMembersInDto inDto) {
        SearchMembersOutDto outDto = new SearchMembersOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();
        outDto.memberSummaryList = new ArrayList<>();

        List<MemberEntity> all = memberRepository.findAll();

        List<MemberEntity> filtered = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            MemberEntity m = all.get(i);

            if (!matchesKeyword(m, inDto.keyword)) {
                continue;
            }
            if (!matchesStatus(m, inDto.status)) {
                continue;
            }
            if (!matchesRegisteredDate(m, inDto.registeredFrom, inDto.registeredTo)) {
                continue;
            }
            filtered.add(m);
        }

        HashMap<Long, Boolean> riskyMap = new HashMap<>();
        for (int i = 0; i < filtered.size(); i++) {
            MemberEntity m = filtered.get(i);
            boolean risky = isRiskyMember(m);
            riskyMap.put(Long.valueOf(m.id), Boolean.valueOf(risky));
        }

        Collections.sort(filtered, new Comparator<MemberEntity>() {
            @Override
            public int compare(MemberEntity o1, MemberEntity o2) {
                LocalDateTime a = o1.lastLoginAt;
                LocalDateTime b = o2.lastLoginAt;
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

        for (int i = 0; i < filtered.size(); i++) {
            MemberEntity m = filtered.get(i);
            MemberSummaryDto s = new MemberSummaryDto();
            s.memberId = m.id;
            s.memberCode = m.memberCode;
            s.memberName = m.name;
            s.email = m.email;
            s.statusCode = m.statusCode;
            s.registeredDate = m.registeredDate;
            s.lastLoginAt = m.lastLoginAt;
            s.totalAmount = m.totalAmount == null ? BigDecimal.ZERO : m.totalAmount;
            Boolean risky = riskyMap.get(Long.valueOf(m.id));
            s.riskyFlag = (risky != null && risky.booleanValue());
            outDto.memberSummaryList.add(s);
        }

        if (outDto.memberSummaryList.isEmpty()) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("No members found.");
        }

        return outDto;
    }

    private boolean matchesKeyword(MemberEntity m, String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }
        String lower = keyword.toLowerCase();

        String name = m.name == null ? "" : m.name;
        String code = m.memberCode == null ? "" : m.memberCode;
        String email = m.email == null ? "" : m.email;

        if (name.toLowerCase().contains(lower)) {
            return true;
        }
        if (code.toLowerCase().contains(lower)) {
            return true;
        }
        if (email.toLowerCase().contains(lower)) {
            return true;
        }
        return false;
    }

    private boolean matchesStatus(MemberEntity m, String status) {
        if (!StringUtils.hasText(status)) {
            return true;
        }
        if (m.statusCode == null) {
            return false;
        }
        return m.statusCode.equals(status);
    }

    private boolean matchesRegisteredDate(MemberEntity m, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return true;
        }
        LocalDate reg = m.registeredDate;
        if (reg == null) {
            return false;
        }
        if (from != null && reg.isBefore(from)) {
            return false;
        }
        if (to != null && reg.isAfter(to)) {
            return false;
        }
        return true;
    }

    private boolean isRiskyMember(MemberEntity m) {
        if (!"ACTIVE".equals(m.statusCode)) {
            return false;
        }
        if (m.lastLoginAt == null) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime threshold = now.minusDays(180);
        return m.lastLoginAt.isBefore(threshold);
    }

    public FetchDetailOutDto fetchMemberDetail(FetchDetailInDto inDto) {
        FetchDetailOutDto outDto = new FetchDetailOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();

        MemberEntity m = memberRepository.findById(inDto.memberId);
        if (m == null) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Member not found. id=" + inDto.memberId);
            outDto.detail = null;
            return outDto;
        }

        MemberDetailDto d = new MemberDetailDto();
        d.memberId = m.id;
        d.memberCode = m.memberCode;
        d.memberName = m.name;
        d.email = m.email;
        d.statusCode = m.statusCode;
        d.registeredDate = m.registeredDate;
        d.lastLoginAt = m.lastLoginAt;
        d.totalAmount = m.totalAmount == null ? BigDecimal.ZERO : m.totalAmount;
        d.memo = m.memo;

        outDto.detail = d;
        return outDto;
    }

    public ChangeStatusOutDto changeStatus(ChangeStatusInDto inDto) {
        ChangeStatusOutDto outDto = new ChangeStatusOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();

        MemberEntity m = memberRepository.findById(inDto.memberId);
        if (m == null) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Member not found. id=" + inDto.memberId);
            return outDto;
        }

        String before = m.statusCode;
        String after = inDto.newStatusCode;

        if ("CANCELED".equals(before)) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Cannot change status from CANCELED.");
            return outDto;
        }

        if ("PAUSED".equals(before) && "ACTIVE".equals(after)) {
            if (!StringUtils.hasText(inDto.reason)) {
                outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
                outDto.messageList.add("Reason is required to resume from PAUSED.");
                return outDto;
            }
        }

        if (before != null && before.equals(after)) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Status is already " + after + ".");
            return outDto;
        }

        m.statusCode = after;
        m.updatedAt = LocalDateTime.now(clock);
        m.updatedBy = inDto.operatorId;
        memberRepository.save(m);

        outDto.changedMemberId = m.id;
        outDto.beforeStatusCode = before;
        outDto.afterStatusCode = after;
        outDto.messageList.add("Status changed: " + before + " -> " + after);

        return outDto;
    }

    public UpdateProfileOutDto updateProfile(UpdateProfileInDto inDto) {
        UpdateProfileOutDto outDto = new UpdateProfileOutDto();
        outDto.logicStatus = SharedLogicStatus.SUCCESS;
        outDto.messageList = new ArrayList<>();

        MemberEntity m = memberRepository.findById(inDto.memberId);
        if (m == null) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Member not found. id=" + inDto.memberId);
            return outDto;
        }

        if (!StringUtils.hasText(inDto.memberName)) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Member name is required.");
            return outDto;
        }
        if (!StringUtils.hasText(inDto.email)) {
            outDto.logicStatus = SharedLogicStatus.BUSINESS_ERROR;
            outDto.messageList.add("Email is required.");
            return outDto;
        }

        m.name = inDto.memberName.trim();
        m.email = inDto.email.trim();
        m.memo = inDto.memo;
        m.updatedAt = LocalDateTime.now(clock);
        m.updatedBy = inDto.operatorId;

        memberRepository.save(m);

        outDto.updatedMemberId = m.id;
        outDto.messageList.add("Profile updated.");
        return outDto;
    }
}
