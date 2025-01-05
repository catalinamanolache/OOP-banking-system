package org.poo.accounts;

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
                                        final double interestRate, final User owner) {
        return switch (type) {
            case "classic" -> new ClassicAccount(currency, owner);
            case "savings" -> new SavingsAccount(currency, interestRate, owner);
            case "business" -> new BusinessAccount(currency, owner);
            default -> null;
        };
    }
}
