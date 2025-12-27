package com.techbank.domain.balance;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByAccountIdIn(List<Long> accountIds);
}
