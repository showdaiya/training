package com.example.large.membersystem.controller.order;

import com.example.large.membersystem.service.order.OrderDto;
import com.example.large.membersystem.service.order.OrderService;
import com.example.large.membersystem.service.order.SearchOrdersInDto;
import com.example.large.membersystem.service.order.SearchOrdersOutDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderListController {

    private final OrderService orderService;

    public OrderListController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ModelAttribute("orderSearchForm")
    public OrderSearchForm initForm() {
        OrderSearchForm form = new OrderSearchForm();
        form.memberId = 0L;
        form.fromDate = null;
        form.toDate = null;
        return form;
    }

    @GetMapping("/orders")
    public String index(@ModelAttribute("orderSearchForm") OrderSearchForm form,
                        Model model) {
        SearchOrdersInDto inDto = new SearchOrdersInDto();
        inDto.memberId = form.memberId;
        inDto.fromDate = form.fromDate;
        inDto.toDate = form.toDate;

        SearchOrdersOutDto outDto = orderService.searchOrders(inDto);

        List<OrderDto> list = new ArrayList<>(outDto.orderList);
        model.addAttribute("orders", list);
        model.addAttribute("summary", outDto.summary);
        model.addAttribute("messages", outDto.messageList);
        return "order/list";
    }

    @PostMapping(value = "/orders", params = "search")
    public String search(@ModelAttribute("orderSearchForm") OrderSearchForm form,
                         Model model) {
        return index(form, model);
    }
}
