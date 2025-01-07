package org.poo.commands.accountOperations.cashbackInstances;

import org.poo.accounts.Account;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Map;

public class CashbackContext {
    private final CashbackStrategy strategy;

    public CashbackContext(String strategy) {
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

    public void calculateFutureCashback(Account account, User user, Commerciant commerciant) {
        this.strategy.calculateCashback(account, user, commerciant);
    }

    // TODO: incasare cashback ca metode pt fiecare implementare
    public void useDiscountCashback(User user, Account account, Commerciant commerciant, double amount) {
        for (Map.Entry<Commerciant.CommerciantType, Double> entry : account.getNrOfTransactionsCashback().entrySet()) {
            if (entry.getKey().equals(Commerciant.CommerciantType.valueOf(commerciant.getType()))) {
                double cashback = entry.getValue();
                account.getNrOfTransactionsCashback().remove(entry.getKey());
                System.out.println("will get discount for " +commerciant.getType() + " balance before " + account.getBalance());
                account.deposit(amount * cashback);
                System.out.println("Deposited cashback " + amount * cashback + account.getCurrency() + " user " + user.getEmail());
                System.out.println("Balance after and before commision " + account.getBalance());
                break;
            }
        }
    }

    public void useCashback(Account account, Commerciant commerciant, double amount) {
       Map<Commerciant, Double> spendingThresholdCashback = account.getSpendingThresholdCashback();
       if (!spendingThresholdCashback.containsKey(commerciant)) {
           return;
       }
       double cashback = spendingThresholdCashback.get(commerciant);
       System.out.println("discount spending treshold balance before " + account.getBalance());
       account.deposit(amount * cashback);
       System.out.println("Deposited cashback " + amount * cashback + account.getCurrency());
       System.out.println("Balance after and before commision " + account.getBalance());
    }
}
