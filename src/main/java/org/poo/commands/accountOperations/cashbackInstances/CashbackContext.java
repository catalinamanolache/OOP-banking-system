package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

public class CashbackContext {
    private final CashbackStrategy strategy;

    /**
     * Constructor for the CashbackContext class.
     * @param strategy the cashback strategy to be used
     */
    public CashbackContext(final String strategy) {
        switch (strategy) {
            case "nrOfTransactions":
                this.strategy = new NrOfTransactions();
                break;
            case "spendingThreshold":
                this.strategy = new SpendingThreshold();
                break;
            default:
                this.strategy = null;
                break;
        }
    }

    /**
     * Calculate the cashback for a user.
     * @param account the account
     * @param user the user
     * @param commerciant the commerciant at which the user is paying
     */
    public void calculateCashback(final Account account, final User user,
                                        final Commerciant commerciant) {
        this.strategy.calculateCashback(account, user, commerciant);
    }

    /**
     * Use the discount for the nrOfTransactions type of cashback before the current transaction
     * is processed.
     * @param user the user
     * @param account the account
     * @param commerciant the commerciant
     * @param amount the amount of the current transaction
     */
    public void useDiscount(final User user, final Account account,
                                    final Commerciant commerciant, double amount) {
        // go through the types of discount and apply the one that matches the commerciant's type
        for (Map.Entry<Commerciant.CommerciantType, Double> entry
                : account.getNrOfTransactionsCashback().entrySet()) {
            if (entry.getKey().equals(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
                // deposit the cashback in the account
                double cashback = entry.getValue();
                account.deposit(amount * cashback);

                System.out.println("used nrOfTransactions discount " + (amount * cashback) + " account " + account.getIban() + " user " + user.getEmail());
                // remove the discount from the account
                account.getNrOfTransactionsCashback().remove(entry.getKey());
                break;
            }
        }
    }

    /**
     * Use the discount for the spendingThreshold type of cashback after the current transaction.
     * @param account the account
     * @param commerciant the commerciant
     * @param amount the amount of the current transaction
     */
    public void useCashback(final Account account, final Commerciant commerciant, double amount) {
       Map<Commerciant, Double> spendingThresholdCashback = account.getSpendingThresholdCashback();

       if (!spendingThresholdCashback.containsKey(commerciant)) {
           return;
       }

       // get the cashback percentage for the commerciant based on the total spent
       double cashback = spendingThresholdCashback.get(commerciant);
//        double cashback = account.getSpendingThresholdTotal();
        System.out.println("cashback variable " + cashback);
//       System.out.println("discount spending treshold balance before " + account.getBalance());
//        System.out.println("amount " + amount + " cashback " + cashback);
       account.deposit(amount * cashback);
//       spendingThresholdCashback.remove(commerciant);
//       System.out.println("Deposited cashback " + amount * cashback + account.getCurrency());
//       System.out.println("Balance after and before commision " + account.getBalance());
        System.out.println("used spendingThreshold discount " + (amount * cashback) + " account " + account.getIban());
    }
}
