package org.poo.commands.accountOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.instances.*;
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

//        System.out.println("context " + this.bank.getSplitPaymentContext().getParticipants());

        // get the sender and receiver accounts
        Account senderAccount = this.bank.getAccountByIban(senderIban);
        Account receiverAccount = this.bank.getAccountByIban(receiverIban);

        if (senderAccount == null || receiverAccount == null) {
            // if the accounts are not found, check if the sender or receiver is an alias
            Map<String, String> aliasMap = this.bank.getAliasMap();
            senderAccount = this.bank.getAccountByIban(aliasMap.get(senderIban));
            receiverAccount = this.bank.getAccountByIban(aliasMap.get(receiverIban));

            // if the accounts are still not found, print an error and return
            if (senderAccount == null || receiverAccount == null) {
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
            }
        }

        // convert the amount to the receiver's currency
        double convertedAmount =
                CurrencyConverter.convert(senderAccount.getCurrency(),
                        receiverAccount.getCurrency(), amount);

        // check if the sender has enough funds and if not, add an error transaction to the sender
        if (senderAccount.getBalance() < amount) {
            Transaction transactionSender;
            transactionSender = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .build();
            senderAccount.addTransaction(transactionSender);
        } else {
            // take the commission depending on the sender user's plan
            User senderUser = this.bank.getUserByEmail(email);
            double commission = Plan.getCommission(senderUser.getPlanType(), amount,
                    senderAccount.getCurrency());

            // withdraw the amount from the sender and deposit it to the receiver
            senderAccount.withdraw(amount + commission);
            receiverAccount.deposit(convertedAmount);

//            System.out.print(this.command.getCommand() + " | took commission " + commission + " for email " + email +
//                    " timestamp " + timestamp);
//            System.out.print(" | new balance " + senderAccount.getBalance() + "\n");
            System.out.println("timestamp " +timestamp  + "  "+ email + " sent " + amount + " " + senderAccount.getCurrency() +
                    " to " + receiverAccount.getOwner()  + " and paid a commission of " + commission + " " +
                    senderAccount.getCurrency());

            // TODO:Pentru sendMoney cand destinatarul este un comerciant, se va lua currency-ul contului din care se face plata.
            Commerciant commerciant = this.bank.getCommerciantByIban(receiverIban);
            if (commerciant != null) {
                CashbackContext cashbackContext =
                        new CashbackContext(commerciant.getCashbackStrategy());
                String currency = senderAccount.getCurrency();

                // get the cashback discount benefit if the user reached the milestones for nrOfTransactions
                cashbackContext.useDiscountCashback(senderUser, senderAccount, commerciant, convertedAmount);

                // update the total spent for the commerciant
                senderAccount.updateTotalSpent(commerciant, amount, currency);

                // update the number of transactions for the commerciant
                senderAccount.updateNrOfTransactions(commerciant);

                cashbackContext.calculateFutureCashback(senderAccount, senderUser, commerciant);

                // get the cashback benefit for spending threshold
                cashbackContext.useCashback(senderUser, senderAccount, commerciant, convertedAmount);
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
                    .build();
            senderAccount.addTransaction(transactionSender);

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
