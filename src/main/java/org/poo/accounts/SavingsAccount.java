package org.poo.accounts;

import org.poo.instances.Commerciant;
import org.poo.instances.User;

public final class SavingsAccount extends Account {
    private double interestRate;

    SavingsAccount(final String currency, final double interestRate, final User owner){
        super(currency, AccountType.SAVINGS, owner);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }
}
