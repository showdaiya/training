package com.example.large.membersystem.service.billing;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class CalcMonthlyBillingOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public java.util.List<BillingTargetDto> billingTargetList;
}
