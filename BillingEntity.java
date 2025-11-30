package com.example.large.membersystem.domain.billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class BillingEntity {
    public long id;
    public long memberId;
    public YearMonth billingMonth;
    public BigDecimal amount;
    public int orderCount;
    public LocalDateTime executedAt;
    public long executedBy;
}
