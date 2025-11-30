package com.example.large.membersystem.service.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberSummaryDto {
    public long memberId;
    public String memberCode;
    public String memberName;
    public String email;
    public String statusCode;
    public LocalDate registeredDate;
    public LocalDateTime lastLoginAt;
    public BigDecimal totalAmount;
    public boolean riskyFlag;
}
