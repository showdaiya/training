package com.example.large.membersystem.service.member;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class ChangeStatusOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public long changedMemberId;
    public String beforeStatusCode;
    public String afterStatusCode;
}
