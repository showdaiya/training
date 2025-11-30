// File: src/main/java/com/example/large/membersystem/domain/member/Member.java
package com.example.large.membersystem.domain.member;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Member {
    public long id;
    public String memberCode;
    public String name;
    public String email;
    public String statusCode; // "ACTIVE", "PAUSED", "CANCELED" 等
    public LocalDate registeredDate;
    public LocalDateTime lastLoginAt;
    public BigDecimal totalAmount;
    public String memo;
    public LocalDateTime updatedAt;
    public long updatedBy;
}
