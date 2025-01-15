package org.poo.commands.cardOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.bankManager.CurrencyConverter;
import org.poo.cards.Card;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.Plan;
import org.poo.instances.User;
import org.poo.transactions.Transaction;

public class CashWithdrawal implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public CashWithdrawal(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the cashWithdrawal command.
     */
    @Override
    public void execute() {
        String cardNumber = this.command.getCardNumber();
        double amount = this.command.getAmount();
        String email = this.command.getEmail();
        String location = this.command.getLocation();
        int timestamp = this.command.getTimestamp();

        User user = this.bank.getUserByEmail(email);
        ObjectMapper objectMapper = new ObjectMapper();

        // if the user is not found, print an error
        if (user == null) {
            System.out.println("User not found in cashWithdrawal");
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("command", this.command.getCommand());

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "User not found");

            objectNode.set("output", outputNode);

            objectNode.put("timestamp", timestamp);

            this.output.add(objectNode);
            return;
        }

        // get the account and card by card number
        Account account = this.bank.getAccountByCardNumber(cardNumber);
        Card card = this.bank.getCardByCardNumber(cardNumber);

        // if the account is a business account, check if the user is associated with the business
        // if not, set the account to null (the user is not allowed to withdraw money)
        if (account != null && account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                account = null;
            }
        }

        // if the account is not found or the card does not belong to the user, print an error
        if (account == null || !card.getOwner().equals(user)) {
            ObjectNode result = objectMapper.createObjectNode();
            result.put("command", this.command.getCommand());

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("timestamp", timestamp);
            outputNode.put("description", "Card not found");
            result.set("output", outputNode);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        // convert the amount to the account's currency, since we only withdraw RON
        double amountConverted = CurrencyConverter.convert("RON", account.getCurrency(),
                amount);
        Plan.PlanType userPlan = user.getPlanType();
        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            userPlan = account.getOwner().getPlanType();
        }
        // take the commission depending on the user's plan
        double commission = Plan.getCommission(userPlan, amountConverted,
                account.getCurrency());
//        double commission;
//        if (account.getAccountType().equals(Account.AccountType.BUSINESS)) {
//            // if the sender is a business account, get the commission from the owner's plan
//            commission = Plan.getCommission(account.getOwner().getPlanType(), amount,
//                    account.getCurrency());
//        } else {
//            commission = Plan.getCommission(user.getPlanType(), amount,
//                    account.getCurrency());
//        }


        // if the user doesn't have enough funds, print an error
        if (amountConverted + commission > account.getBalance()) {
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .error("Insufficient funds")
                    .build();
            account.addTransaction(transaction);
            System.out.println("Insufficient funds in cashWithdrawal");
            return;
        }

        // withdraw the amount and the commission and add a transaction
        account.withdraw(amountConverted + commission);

        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Cash withdrawal of " + amount, this.command.getCommand())
                .amount(amount)
                .build();
        account.addTransaction(transaction);
    }
}
