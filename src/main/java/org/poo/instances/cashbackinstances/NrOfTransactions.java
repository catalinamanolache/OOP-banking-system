package org.poo.instances.cashbackinstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

import static org.poo.instances.Constants.NR_OF_TRANSACTIONS_FIRST_THRESHOLD;
import static org.poo.instances.Constants.NR_OF_TRANSACTIONS_SECOND_THRESHOLD;
import static org.poo.instances.Constants.NR_OF_TRANSACTIONS_THIRD_THRESHOLD;
import static org.poo.instances.Constants.FOOD_CASHBACK;
import static org.poo.instances.Constants.CLOTHES_CASHBACK;
import static org.poo.instances.Constants.TECH_CASHBACK;

public class NrOfTransactions implements CashbackStrategy {

    /**
     * Calculate the number of transactions cashback for a user.
     * @param account the account
     * @param user the user
     * @param commerciant the commerciant at which the user is paying
     */
    @Override
    public void calculateCashback(final Account account, final User user,
                                  final Commerciant commerciant) {
        Map<Commerciant.CommerciantType, Double> cashbackMap =
                account.getNrOfTransactionsCashback();

        // get how many transactions the user has made at the current commerciant
        int nrOfTransactions = account.getNrOfTransactions().get(commerciant);

        // for each number of transactions, set the cashback percentage for the commerciant
        if (nrOfTransactions == NR_OF_TRANSACTIONS_FIRST_THRESHOLD) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Food)) {
                cashbackMap.put(Commerciant.CommerciantType.Food, FOOD_CASHBACK);
            }
        } else if (nrOfTransactions == NR_OF_TRANSACTIONS_SECOND_THRESHOLD) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Clothes)) {
                cashbackMap.put(Commerciant.CommerciantType.Clothes, CLOTHES_CASHBACK);
            }
        } else if (nrOfTransactions == NR_OF_TRANSACTIONS_THIRD_THRESHOLD) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Tech)) {
                cashbackMap.put(Commerciant.CommerciantType.Tech, TECH_CASHBACK);
            }
        }
    }
}
