package org.poo.accounts;

import org.poo.instances.Plan;
import org.poo.instances.User;

public final class AccountFactory {
    private AccountFactory() {
    }

    /**
     * Create an account based on the type given as parameter.
     * @param type the type of the account.
     * @param currency the currency of the account
     * @param interestRate the interest rate of the account
     * @param owner the owner of the account
     * @return the account created
     */
    public static Account createAccount(final String type, final String currency,
                                        final double interestRate, final User owner){
        if (type.equals("classic")) {
            return new ClassicAccount(currency, owner);
        } else if (type.equals("savings")) {
            return new SavingsAccount(currency, interestRate, owner);
        } else if (type.equals("business")) {
            return new BusinessAccount(currency, owner);
        } else {
            return null;
        }
    }
}
