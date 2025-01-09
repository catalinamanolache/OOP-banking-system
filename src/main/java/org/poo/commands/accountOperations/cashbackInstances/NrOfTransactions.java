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
//            return;
//        }

        // get how many transactions the user has made at the current commerciant
        int nrOfTransactions = account.getNrOfTransactions().get(commerciant);

        // for each number of transactions, set the cashback percentage for the commerciant
        if (nrOfTransactions == 2) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Food)) {
                cashbackMap.put(Commerciant.CommerciantType.Food, 0.02);
            }
            System.out.println("after paying at " + commerciant.getCommerciant() + " " + user.getEmail() + " will get cashback for food");
        } else if (nrOfTransactions == 5) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Clothes)) {
                cashbackMap.put(Commerciant.CommerciantType.Clothes, 0.05);
            }
            System.out.println("after paying at " + commerciant.getCommerciant() + " " + user.getEmail() + " will get cashback for clothes");
        } else if (nrOfTransactions == 10) {
            if (!cashbackMap.containsKey(Commerciant.CommerciantType.Tech)) {
                cashbackMap.put(Commerciant.CommerciantType.Tech, 0.1);
            }
            System.out.println("after paying at " + commerciant.getCommerciant() + " " + user.getEmail() + " will get cashback for tech");
        }
    }
}