package org.poo.instances;

import org.poo.accounts.Account;

public interface CashbackStrategy {
    double calculateCashback(Account account, User user, Commerciant commerciant);
}
