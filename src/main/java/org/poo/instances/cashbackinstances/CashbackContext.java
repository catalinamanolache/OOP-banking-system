package org.poo.instances.cashbackinstances;

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
     * Handles the creation and application of the cashback for a transaction.
     * @param user the user for which the transaction is made
     * @param account the account from which the transaction is made
     * @param commerciant the commerciant at which the transaction is made
     * @param amount the amount of the transaction
     * @param amountConverted the amount of the transaction in the account's currency
     * @param currency the currency of the transaction
     */
    public void handleCashbackTransaction(final User user, final Account account,
                                          final Commerciant commerciant, final double amount,
                                          final double amountConverted, final String currency) {

        // use the discount for the nrOfTransactions type of cashback
        this.useDiscount(user, account,
                commerciant, amountConverted);

        // update the total spent for the commerciant
        account.updateTotalSpent(commerciant, amount, currency);

        // update the number of transactions for the commerciant
        account.updateNrOfTransactions(commerciant);

        // calculate the future cashback for the sender
        this.calculateCashback(account, user, commerciant);

        // get the cashback benefit for spendingThreshold cashback
        this.useCashback(account, commerciant, amountConverted);
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
                                    final Commerciant commerciant, final double amount) {
        // go through the types of discount and apply the one that matches the commerciant's type
        for (Map.Entry<Commerciant.CommerciantType, Double> entry
                : account.getNrOfTransactionsCashback().entrySet()) {
            if (entry.getKey().equals(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
                // deposit the cashback in the account
                double cashback = entry.getValue();
                account.deposit(amount * cashback);

                // remove the discount from the account
                account.getNrOfTransactionsCashback().put(entry.getKey(), 0.0);
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
    public void useCashback(final Account account, final Commerciant commerciant,
                            final double amount) {
       Map<Commerciant, Double> spendingThresholdCashback = account.getSpendingThresholdCashback();

       if (!spendingThresholdCashback.containsKey(commerciant)) {
           return;
       }

       // get the cashback percentage for the commerciant based on the total spent
       double cashback = spendingThresholdCashback.get(commerciant);

       // deposit the cashback in the account based on the amount of the transaction
       account.deposit(amount * cashback);
    }
}
