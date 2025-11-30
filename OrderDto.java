package com.example.large.membersystem.service.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDto {
    public long orderId;
    public long memberId;
    public LocalDateTime orderDate;
    public BigDecimal amount;
    public String statusCode;
    public String paymentMethodCode;
}
