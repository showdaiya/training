package com.example.large.membersystem.repository;

import com.example.large.membersystem.domain.order.OrderEntity;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository {

    List<OrderEntity> findByMemberId(long memberId);

    List<OrderEntity> findByPeriod(LocalDate from, LocalDate to);
}
