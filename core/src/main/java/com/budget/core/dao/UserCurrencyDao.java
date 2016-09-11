package com.budget.core.dao;

import com.budget.core.entity.UserCurrency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCurrencyDao extends JpaRepository<UserCurrency, Long>{
}
