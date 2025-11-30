package com.example.large.membersystem.service.order;

import com.example.large.membersystem.shared.SharedLogicStatus;
import java.util.List;

public class SearchOrdersOutDto {
    public SharedLogicStatus logicStatus;
    public List<String> messageList;
    public java.util.List<OrderDto> orderList;
    public OrderSummaryDto summary;
}
