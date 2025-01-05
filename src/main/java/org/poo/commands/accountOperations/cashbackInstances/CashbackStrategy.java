package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

public interface CashbackStrategy {
    double calculateCashback(Account account, User user, Commerciant commerciant);
}
