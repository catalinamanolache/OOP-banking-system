package org.poo.commands.printOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.cards.Card;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;

public class PrintUsers implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public PrintUsers(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Execute the printUsers command.
     */
    @Override
    public void execute() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode usersArray = objectMapper.createArrayNode();

        // iterate through all users and print their information
        for (User user : this.bank.getUsers()) {
            ObjectNode userObject = objectMapper.createObjectNode();
            userObject.put("firstName", user.getFirstName());
            userObject.put("lastName", user.getLastName());
            userObject.put("email", user.getEmail());

            ArrayNode accountsArray = objectMapper.createArrayNode();
            for (Account account : user.getAccounts()) {
                ObjectNode accountObject = objectMapper.createObjectNode();
                accountObject.put("IBAN", account.getIban());

                accountObject.put("balance", account.getBalance());

                accountObject.put("currency", account.getCurrency());
                Account.AccountType accountType = account.getAccountType();
                accountObject.put("type", accountType.name().toLowerCase());

                ArrayNode cardsArray = objectMapper.createArrayNode();
                for (Card card : account.getCards()) {
                    ObjectNode cardObject = objectMapper.createObjectNode();
                    cardObject.put("cardNumber", card.getCardNumber());
                    if (card.getIsFrozen()) {
                        cardObject.put("status", "frozen");
                    } else {
                        cardObject.put("status", "active");
                    }
                    cardsArray.add(cardObject);
                }
                accountObject.set("cards", cardsArray);
                accountsArray.add(accountObject);
            }
            userObject.set("accounts", accountsArray);
            usersArray.add(userObject);
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", this.command.getCommand());
        result.set("output", usersArray);
        result.put("timestamp", this.command.getTimestamp());
        this.output.add(result);
    }
}
