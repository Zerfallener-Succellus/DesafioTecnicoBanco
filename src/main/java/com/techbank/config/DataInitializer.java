package com.techbank.config;

import com.techbank.domain.account.Account;
import com.techbank.domain.account.AccountRepository;
import com.techbank.domain.balance.Balance;
import com.techbank.domain.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepo;
    private final BalanceRepository balanceRepo;

    @Override
    public void run(String... args) {
        log.info("Iniciando carga de dados...");
        accountRepo.deleteAll();
        balanceRepo.deleteAll();

        create("0001-DINO", "Dino da Silva Sauro", "CORRENTE", "15.50", "4500.00");
        create("0002-FRAN", "Fran da Silva Sauro", "CORRENTE", "1250.00", "0.00");
        create("0003-BABY", "Baby 'Não é a Mamãe'", "POUPANCA", "1000000.00", "50.00");
        create("0004-CHAR", "Charlene Sauro", "CORRENTE", "0.05", "0.00");
        create("0005-ROBB", "Robbie Sauro", "POUPANCA", "350.00", "20.00");
        create("0006-BOSS", "B.P. Richfield", "CORRENTE", "99999999.99", "0.00");
        create("0007-ROYH", "Roy Hess", "CORRENTE", "2.00", "0.00");
        create("0008-MONI", "Mônica (A vizinha)", "POUPANCA", "5000.00", "0.00");
        create("0009-ZILD", "Vovó Zilda", "POUPANCA", "50000.00", "100000.00");
        create("0010-TIO", "Tio Moleza", "CORRENTE", "0.00", "500.00");
        create("0011-ZERO", "Fossil Desconhecido", "CORRENTE", "0.00", "0.00");
        create("0012-CAVE", "Imobiliária Pangeia", "CORRENTE", "0.00", "12000.00");
        create("0013-NEGA", "Dino (Fim do Mês)", "CORRENTE", "-500.00", "0.00");
        create("0014-METE", "Seguro Contra Meteoros", "POUPANCA", "5000000.55", "0.00");
        create("0015-CENT", "Senhor das Moscas", "CORRENTE", "0.01", "0.01");
        create("0016-PIZZ", "Entregador de Pizza", "CORRENTE", "45.00", "0.00");
        create("0017-TV", "Apresentador do TV Dinossauro", "CORRENTE", "8500.00", "1000.00");
        create("0018-GELA", "O Monstro da Geladeira", "POUPANCA", "100.00", "50.00");
        create("0019-SPIK", "Spike (O Mascote)", "POUPANCA", "10.00", "0.00");
        create("0020-EXT", "Extinção Ltda", "CORRENTE", "0.00", "9999.00");

        log.info("Carga de dados concluída.");
    }

    private void create(String accNumber, String holder, String type, String available, String blocked) {
        Account account = new Account(accNumber, holder, type);
        Account savedAccount = accountRepo.save(account);

        Balance balance = new Balance(
                savedAccount.getId(),
                new BigDecimal(available),
                new BigDecimal(blocked)
        );
        balanceRepo.save(balance);
    }
}