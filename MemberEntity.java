package com.example.large.membersystem.domain.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberEntity {
    public long id;
    public String memberCode;
    public String name;
    public String email;
    public String statusCode;
    public LocalDate registeredDate;
    public LocalDateTime lastLoginAt;
    public BigDecimal totalAmount;
    public String memo;
    public LocalDateTime updatedAt;
    public long updatedBy;
}
