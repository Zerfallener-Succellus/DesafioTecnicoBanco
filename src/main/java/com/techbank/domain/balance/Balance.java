package com.techbank.domain.balance;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_balances") // Base B
@Data
@NoArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long accountId;

    private BigDecimal availableAmount;
    private BigDecimal blockedAmount;

    public Balance(Long accountId, BigDecimal availableAmount, BigDecimal blockedAmount) {
        this.accountId = accountId;
        this.availableAmount = availableAmount;
        this.blockedAmount = blockedAmount;
    }
}
