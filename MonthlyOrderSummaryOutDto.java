package com.example.large.membersystem.service.order;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class MonthlyOrderSummaryOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public BigDecimal totalAmount;
    public int totalOrderCount;
    public HashMap<String, Integer> byStatus;
}
