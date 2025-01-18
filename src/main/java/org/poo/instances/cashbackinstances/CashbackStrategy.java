package org.poo.instances.cashbackinstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

public interface CashbackStrategy {
    /**
     * Calculate the cashback for a user.
     * @param account the account
     * @param user the user
     * @param commerciant the commerciant at which the user is paying
     */
    void calculateCashback(Account account, User user, Commerciant commerciant);
}
