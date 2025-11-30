package com.example.large.membersystem.service.billing;

import java.math.BigDecimal;

public class BillingTargetDto {
    public long memberId;
    public BigDecimal totalAmount;
    public int orderCount;
    public boolean alreadyBilled;
}
