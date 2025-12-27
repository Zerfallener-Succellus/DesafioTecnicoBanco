package com.techbank.domain.account;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_accounts") // Base A
@Data
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String holderName;
    private String type;  //coorentista ou poupanca

    public Account(String accountNumber, String holderName, String type) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.type = type;
    }
}