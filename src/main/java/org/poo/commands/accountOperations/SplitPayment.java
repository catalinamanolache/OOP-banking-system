package org.poo.commands.accountOperations;

import org.poo.accounts.Account;
import org.poo.bankManager.SplitPaymentContext;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class SplitPayment implements Command {
    private CommandData command;
    private Bank bank;

    public SplitPayment(final CommandData command, final Bank bank) {
        this.command = command;
        this.bank = bank;
    }

    /**
     * Executes the splitPayment command.
     */
    @Override
    public void execute() {
        System.out.println("Executing command " + this.command.getCommand() + " timestamp " + this.command.getTimestamp());
        List<String> accountIbans = this.command.getAccounts();
        String currency = this.command.getCurrency();
        double amount = this.command.getAmount();
        int timestamp = this.command.getTimestamp();
        String type = this.command.getSplitPaymentType();
        List<Double> amountForUsers = this.command.getAmountForUsers();

        // check if all accounts have enough funds for the split payment
        int accountsNumber = accountIbans.size();
        boolean failed = false;
        String accountFailed = null;

        if (amountForUsers == null) {
            amountForUsers = new ArrayList<>();
            for (int i = 0; i < accountsNumber; i++) {
                amountForUsers.add(amount / accountsNumber);
            }
        }

        for (int i = 0; i < accountsNumber; i++) {
            Account account = this.bank.getAccountByIban(accountIbans.get(i));
            if (account == null) {
                System.out.println("account at position " + i + " is null in finding if it has enough funds");
                // TODO : “One of the accounts is invalid.” →
                //  cand unul dintre conturile date in lista de conturi pentru split este invalid
                continue;
            }

            // convert the amount to the account's currency
            double amountConverted = CurrencyConverter.convert(currency, account.getCurrency(),
                    amountForUsers.get(i));
            System.out.println("user " + account.getOwner() +  " with account " + account.getIban() +" balance in currency " + currency + " is " +
                    CurrencyConverter.convert(account.getCurrency(), currency, account.getBalance()) + " balance in account currency " + account.getBalance() + " " + account.getCurrency());

            // check if the account has enough funds and get the first account that failed
            if (account.getBalance() < amountConverted) {
                failed = true;
                accountFailed = accountIbans.get(i);
                break;
            }
        }

        // if the payment failed, add the failed transaction to each account
        if (failed) {
            System.out.println("failed split payment at timestamp " + timestamp);
            for (int i = 0; i < accountsNumber; i++) {
                Account accountInvolved = this.bank.getAccountByIban(accountIbans.get(i));
                if (accountInvolved == null) {
                    System.out.println("account at position " + i + " is null in printing failed transaction");
                    // TODO : “One of the accounts is invalid.” →
                    //  cand unul dintre conturile date in lista de conturi pentru split este invalid
                    continue;
                }
                String formattedAmount = String.format("%.2f", amount);

                Transaction transaction;
                transaction = new Transaction.TransactionBuilder(timestamp,
                        "Split payment of " + formattedAmount + " " + currency,
                        this.command.getCommand())
                        .currency(currency)
                        .amount(amountForUsers.get(i))
                        .involvedAccounts(accountIbans)
                        .splitPaymentType(type)
                        .error("Account " + accountFailed
                                + " has insufficient funds for a split payment.")
                        .build();
                accountInvolved.addTransaction(transaction);
            }
            return;
        }

//        SplitPaymentContext splitPaymentContext = new SplitPaymentContext(accountIbans, currency,
//                amountForUsers, amount, type, timestamp);
//        this.bank.setSplitPaymentContext(splitPaymentContext);
//        System.out.println("split payment before create");
        this.bank.createSplitPaymentContext(accountIbans, currency, amountForUsers, amount, type, timestamp);
//        System.out.println("split payment before print");

//        // withdraw the amount if the payment didn't fail (each account has enough funds)
//        if (!failed) {
//            for (int i = 0; i < accountsNumber; i++) {
//                Account account = this.bank.getAccountByIban(accountIbans.get(i));
//
//                // convert the amount to the account's currency
//                double amountConverted = CurrencyConverter.convert(currency, account.getCurrency(),
//                        amountForUsers.get(i));
//
//                account.withdraw(amountConverted);
//            }
//        }


//        // add the transaction to each account
//        for (int i = 0; i < accountsNumber; i++) {
//            Account accountInvolved = this.bank.getAccountByIban(accountIbans.get(i));
//            if (accountInvolved == null) {
//                continue;
//            }
//            String formattedAmount = String.format("%.2f", amount);
//
//            Transaction transaction;
//            if (!failed) {
//                // if the payment didn't fail, add the successful transaction
//                transaction = new Transaction.TransactionBuilder(timestamp,
//                        "Split payment of " + formattedAmount + " " + currency,
//                        this.command.getCommand())
//                        .currency(currency)
//                        .amount(amountForUsers.get(i))
//                        .involvedAccounts(accountIbans)
//                        .build();
//                accountInvolved.addTransaction(transaction);
//            } else {
                // if the payment failed, add the failed transaction
//                transaction = new Transaction.TransactionBuilder(timestamp,
//                            "Split payment of " + formattedAmount + " " + currency,
//                        this.command.getCommand())
//                        .currency(currency)
//                        .amount(amountForUsers.get(i))
//                        .involvedAccounts(accountIbans)
//                        .error("Account " + accountFailed
//                                + " has insufficient funds for a split payment.")
//                        .build();
//                accountInvolved.addTransaction(transaction);
//            }
       // }
    }
}
