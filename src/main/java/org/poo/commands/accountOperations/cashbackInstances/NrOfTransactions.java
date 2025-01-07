package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

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

//        if (!cashbackMap.containsKey(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
//            return -1;
//        }

        // get how many transactions the user has made at the current commerciant
        int nrOfTransactions = account.getNrOfTransactions().get(commerciant);

        // for each number of transactions, set the cashback percentage for the commerciant
        if (nrOfTransactions == 2) {
            cashbackMap.put(Commerciant.CommerciantType.Food, 0.002);
            System.out.println("will get cashback for food");
        } else if (nrOfTransactions == 5) {
            cashbackMap.put(Commerciant.CommerciantType.Clothes, 0.005);
            System.out.println("will get cashback for clothes");
        } else if (nrOfTransactions == 10) {
            cashbackMap.put(Commerciant.CommerciantType.Tech, 0.01);
            System.out.println("will get cashback for tech");
        }
    }
}
