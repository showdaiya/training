package com.example.large.membersystem.service.member;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class SearchMembersOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public java.util.List<MemberSummaryDto> memberSummaryList;
}
