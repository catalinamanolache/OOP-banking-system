package org.poo.commands.accountoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.instances.cashbackinstances.CashbackContext;
import org.poo.transactions.Transaction;
import org.poo.bankmanager.Bank;
import org.poo.bankmanager.CurrencyConverter;
import java.util.Map;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.instances.Commerciant;
import org.poo.instances.CommandData;
import org.poo.instances.jsonexceptions.JSONException;

public class SendMoney implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public SendMoney(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the sendMoney command.
     * @throws JSONException if the sender or receiver are not found
     */
    @Override
    public void execute() throws JSONException {
        String senderIban = this.command.getAccount();
        double amount = this.command.getAmount();
        String receiverIban = this.command.getReceiver();
        String description = this.command.getDescription();
        int timestamp = this.command.getTimestamp();
        String email = this.command.getEmail();

        // get the sender and receiver accounts and the alias map
        Account senderAccount;
        Account receiverAccount;
        Map<String, String> aliasMap = this.bank.getAliasMap();

        // check if the sender and receiver are aliases and get the real accounts
        if (this.bank.getAccountByIban(senderIban) == null) {
            senderAccount = this.bank.getAccountByIban(aliasMap.get(senderIban));
        } else {
            senderAccount = this.bank.getAccountByIban(senderIban);
        }

        if (this.bank.getAccountByIban(receiverIban) == null) {
            receiverAccount = this.bank.getAccountByIban(aliasMap.get(receiverIban));
        } else {
            receiverAccount = this.bank.getAccountByIban(receiverIban);
        }

        boolean isReceiverCommerciant = false;
        Commerciant commerciant = this.bank.getCommerciantByIban(receiverIban);
        // if the accounts are still not found, try to find the receiver in the commerciants list
        if (senderAccount == null || receiverAccount == null) {
            // if the receiver belongs to the commerciants list, set the flag to true

            // if the receiver is still not found, print an error message
            if (commerciant == null) {
                throw new JSONException(this.command, "userNotFound", this.output);
            } else {
                isReceiverCommerciant = true;
            }
        }

        double convertedAmount;
        if (!isReceiverCommerciant) {
            // convert the amount to the receiver's currency
            convertedAmount = CurrencyConverter.convert(senderAccount.getCurrency(),
                            receiverAccount.getCurrency(), amount);
        } else {
            // if the receiver is a commerciant, the amount is in the sender's currency
            convertedAmount = amount;
        }

        // get the sender user and the sender plan
        User senderUser = this.bank.getUserByEmail(email);
        Plan.PlanType userPlan = senderUser.getPlanType();

        // if the sender is a business account, get the plan of the business account's owner
        if (senderAccount.getAccountType().equals(Account.AccountType.BUSINESS)) {
            userPlan = senderAccount.getOwner().getPlanType();
        }

        // get the commission for the transaction
        double commission = Plan.getCommission(userPlan, amount,
                    senderAccount.getCurrency());

        // for a business account, check if the user spends in the limit of the account
        // for a personal account, this is always true
        boolean canPay
                = senderAccount.verifyTransaction(senderUser, -convertedAmount);

        // check if the sender has enough funds and if not, add an error transaction to the sender
        if (senderAccount.getBalance() < amount + commission) {
            Transaction transactionSender;
            transactionSender = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .build();
            senderAccount.addTransaction(transactionSender);
        } else if (canPay) {
            // withdraw the amount and the commision from the sender and deposit it to the receiver
            senderAccount.withdraw(amount + commission);

            // only deposit if the receiver is not a commerciant
            if (!isReceiverCommerciant) {
                receiverAccount.deposit(convertedAmount);
            }

            // handle the money transactions for the sender user, for business accounts
            senderAccount.handleMoneyTransactions(senderUser, -convertedAmount, commerciant);

            // if the receiver is a commerciant, calculate the cashback for this transaction
            if (isReceiverCommerciant) {
                // create a cashback context for the commerciant
                CashbackContext cashbackContext =
                        new CashbackContext(commerciant.getCashbackStrategy());

                String currency = senderAccount.getCurrency();

                // handle the cashback process
                cashbackContext.handleCashbackTransaction(senderUser, senderAccount,
                        commerciant, amount, convertedAmount, currency);

                // check if the user can upgrade its plan from silver to gold automatically
                if (Plan.checkIfCanUpgrade(senderUser)) {
                    // add an upgrade transaction to the account
                    Transaction transaction;
                    transaction = new Transaction.TransactionBuilder(timestamp,
                            "Upgrade plan", "upgradePlan")
                            .newPlanType(Plan.PlanType.GOLD.toString().toLowerCase())
                            .account(senderAccount.getIban())
                            .build();
                    senderAccount.addTransaction(transaction);
                }
            }

            // add the successful transactions to the sender and receiver
            Transaction transactionSender;
            transactionSender = new Transaction.TransactionBuilder(timestamp, description,
                    this.command.getCommand())
                    .senderIBAN(senderIban)
                    .receiverIBAN(receiverIban)
                    .amount(amount)
                    .currency(senderAccount.getCurrency())
                    .transferType("sent")
                    .build();
            senderAccount.addTransaction(transactionSender);

            if (receiverAccount != null) {
                Transaction transactionReceiver;
                transactionReceiver = new Transaction.TransactionBuilder(timestamp, description,
                        this.command.getCommand())
                        .senderIBAN(senderIban)
                        .receiverIBAN(receiverIban)
                        .amount(convertedAmount)
                        .currency(receiverAccount.getCurrency())
                        .transferType("received")
                        .build();
                receiverAccount.addTransaction(transactionReceiver);
            }
        }
    }
}
