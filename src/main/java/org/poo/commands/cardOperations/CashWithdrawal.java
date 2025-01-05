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

    @Override
    public void execute() {
        String cardNumber = this.command.getCardNumber();
        double amount = this.command.getAmount();
        String email = this.command.getEmail();
        String location = this.command.getLocation();
        int timestamp = this.command.getTimestamp();

        User user = this.bank.getUserByEmail(email);
        ObjectMapper objectMapper = new ObjectMapper();

        if (user == null) {
            // TODO: transaction or output "User not found"
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

        Account account = this.bank.getAccountByCardNumber(cardNumber);
        Card card = this.bank.getCardByCardNumber(cardNumber);

        if (account != null && account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            BusinessAccount businessAccount = (BusinessAccount) account;
            if (!businessAccount.isUserAssociatedWithBusiness(user)) {
                account = null;
            }
        }

        if (account == null || !card.getOwner().equals(user)) {
            System.out.println("Account not found in cashWithdrawal");
            // TODO: transaction or output "Account not found"/ "Card not found"??
            ObjectNode result = objectMapper.createObjectNode();
            result.put("command", this.command.getCommand());

            ObjectNode output = objectMapper.createObjectNode();
            output.put("timestamp", timestamp);
            output.put("description", "Card not found");
            result.set("output", output);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        double amountConverted = CurrencyConverter.convert("RON", account.getCurrency(),
                amount);

        // take the commission depending on the user's plan
        double commission = Plan.getCommission(user.getPlanType(), amountConverted,
                account.getCurrency());

//        System.out.println("Total amount " + (amountConverted + commission) + " RON");
//        System.out.println("Fee " + commission + " RON");
//        System.out.println("Converted amount " + amountConverted + " " + account.getCurrency());
//        System.out.println("Account balance " + account.getBalance() + " " + account.getCurrency());

        if (amountConverted + commission > account.getBalance()) {
            // TODO: transaction or output "Insufficient funds"
            Transaction transaction;
            transaction = new Transaction.TransactionBuilder(timestamp,
                    "Insufficient funds", this.command.getCommand())
                    .error("Insufficient funds")
                    .build();
            account.addTransaction(transaction);
            System.out.println("Insufficient funds in cashWithdrawal");
            return;
        }

        account.withdraw(amountConverted + commission);

        System.out.print(this.command.getCommand() + " paid " + amountConverted + account.getCurrency() + " account " + account.getIban() + " took commission " + commission + " for email " + email +
                " timestamp " + timestamp);
        System.out.print(" | new balance " + account.getBalance() + "\n");

        Transaction transaction;
        transaction = new Transaction.TransactionBuilder(timestamp,
                "Cash withdrawal of " + amount, this.command.getCommand())
                .amount(amount)
                .build();
        account.addTransaction(transaction);
//        System.out.println("CashWithdrawal");
    }
}
