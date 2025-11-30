package com.example.large.membersystem.service.member;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class FetchDetailOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public MemberDetailDto detail;
}
