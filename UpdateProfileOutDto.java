package com.example.large.membersystem.service.member;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class UpdateProfileOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public long updatedMemberId;
}
