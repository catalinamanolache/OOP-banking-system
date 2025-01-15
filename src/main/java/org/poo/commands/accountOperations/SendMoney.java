package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.commands.accountOperations.cashbackInstances.CashbackContext;
import org.poo.instances.CommandData;
import org.poo.instances.Commerciant;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.transactions.Transaction;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;

import java.util.Map;

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
     */
    @Override
    public void execute() {
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

        // if the accounts are still not found, try to find the receiver in the commerciants list
        if (senderAccount == null || receiverAccount == null) {
            // if the receiver belongs to the commerciants list, set the flag to true
            Commerciant receiverCommerciant = this.bank.getCommerciantByIban(receiverIban);

            // if the receiver is still not found, print an error message
            if (receiverCommerciant == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode resultNode = objectMapper.createObjectNode();

                resultNode.put("command", this.command.getCommand());

                ObjectNode outputNode = objectMapper.createObjectNode();

                outputNode.put("description", "User not found");
                outputNode.put("timestamp", timestamp);
                resultNode.put("timestamp", timestamp);
                resultNode.set("output", outputNode);
                this.output.add(resultNode);
                return;
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

        // compute the commission depending on the sender user's plan
        User senderUser = this.bank.getUserByEmail(email);

        Plan.PlanType userPlan = senderUser.getPlanType();
        if (senderAccount.getAccountType().equals(Account.AccountType.BUSINESS)) {
            userPlan = senderAccount.getOwner().getPlanType();
        }
        double commission;
        commission = Plan.getCommission(userPlan, amount,
                    senderAccount.getCurrency());

        Commerciant commerciant = this.bank.getCommerciantByIban(receiverIban);

        // for a business account, check if the user spends in the limit of the account
        // for a personal account, this is always true
        boolean canPay
                = senderAccount.verifyMoneyTransaction(senderUser, -convertedAmount, commerciant);

        if (senderAccount.getIban().equals("RO68POOB1848616931405475") && !isReceiverCommerciant) {
            System.out.println("sendMoney timestamp " + timestamp + "  " + email + " sent " + amount + " " + senderAccount.getCurrency() +
                    " to " + receiverAccount + " and paid a commission of " + commission + " " +
                    senderAccount.getCurrency() + " sender plan " + senderUser.getPlanType() + " balance " + senderAccount.getBalance());
        }

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

            senderAccount.handleMoneyTransactions(senderUser, -convertedAmount, commerciant);

//            System.out.print(this.command.getCommand() + " | took commission " + commission + " for email " + email +
//                    " timestamp " + timestamp);
//            System.out.print(" | new balance " + senderAccount.getBalance() + "\n");
//            System.out.println("timestamp " +timestamp  + "  "+ email + " sent " + amount + " " + senderAccount.getCurrency() +
//                    " to " + receiverAccount.getOwner().getEmail() + " in currency " + receiverAccount.getCurrency()+ " and paid a commission of " + commission + " " +
//                    senderAccount.getCurrency() + " sender plan " + senderUser.getPlanType());

            // if the receiver is a commerciant, calculate the cashback for this transaction
            if (isReceiverCommerciant) {
                System.out.println("balance before cashback " + senderAccount.getBalance());
                CashbackContext cashbackContext =
                        new CashbackContext(commerciant.getCashbackStrategy());

                String currency = senderAccount.getCurrency();

                // get the cashback discount benefit for nrOfTransactions
                cashbackContext.useDiscount(senderUser, senderAccount,
                        commerciant, amount);

                // update the total spent for the commerciant
                senderAccount.updateTotalSpent(commerciant, amount, currency);

                // update the number of transactions for the commerciant
                senderAccount.updateNrOfTransactions(commerciant);

                // calculate the future cashback for the sender
                cashbackContext.calculateCashback(senderAccount, senderUser, commerciant);

                // get the cashback benefit for spending threshold
                cashbackContext.useCashback(senderAccount, commerciant, amount);

                // check if the user can upgrade its plan from silver to gold automatically
                if (senderAccount.equals(Plan.PlanType.SILVER)) {
                    Plan.checkIfCanUpgrade(senderUser);
                }

                System.out.println("balance after cashback " + senderAccount.getBalance());
                if (senderAccount.getIban().equals("RO68POOB1848616931405475")) {
                    System.out.println("COMERCIANT sendMoney timestamp " + timestamp + "  " + email + " sent " + amount + " " + senderAccount.getCurrency() +
                            " to " + receiverAccount + " and paid a commission of " + commission + " " +
                            senderAccount.getCurrency() + " sender plan " + senderUser.getPlanType() + " balance " + senderAccount.getBalance());
                }
            }



            // add the transactions to the sender and receiver
            Transaction transactionSender;
            transactionSender = new Transaction.TransactionBuilder(timestamp, description,
                    this.command.getCommand())
                    .senderIBAN(senderIban)
                    .receiverIBAN(receiverIban)
                    .amount(amount)
                    .currency(senderAccount.getCurrency())
                    .transferType("sent")
                    .commerciant(receiverAccount != null ? receiverAccount.getOwner().getEmail() : null)
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
