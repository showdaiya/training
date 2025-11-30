package com.example.large.membersystem.repository;

import com.example.large.membersystem.domain.billing.BillingEntity;
import java.time.YearMonth;
import java.util.List;

public interface BillingRepository {

    List<BillingEntity> findByMonth(YearMonth month);

    void save(BillingEntity entity);
}
