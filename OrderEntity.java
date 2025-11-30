package com.example.large.membersystem.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderEntity {
    public long id;
    public long memberId;
    public LocalDateTime orderDate;
    public BigDecimal amount;
    public String statusCode;
    public String paymentMethodCode;
}
