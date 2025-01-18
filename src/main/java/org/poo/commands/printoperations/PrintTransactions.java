package org.poo.commands.printoperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.commands.Command;
import org.poo.transactions.PrintTransactionsJSON;
import org.poo.transactions.Transaction;
import org.poo.instances.User;
import org.poo.instances.CommandData;
import org.poo.bankmanager.Bank;

import java.util.ArrayList;
import java.util.Comparator;

public class PrintTransactions implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public PrintTransactions(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the printTransactions command.
     */
    @Override
    public void execute() {
        String commandString = this.command.getCommand();
        String email = this.command.getEmail();
        int timestamp = this.command.getTimestamp();

        // get the user by email
        User user = this.bank.getUserByEmail(email);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode outputArray = objectMapper.createArrayNode();

        result.put("command", commandString);

        // get all transactions of the user and put them in a list
        ArrayList<Transaction> userTransactions = new ArrayList<>();
        for (Account userAccount : user.getAccounts()) {
            for (Transaction transaction : userAccount.getTransactions()) {
                userTransactions.add(transaction);
            }
        }

        // sort the transactions by timestamp
        userTransactions.sort(new Comparator<Transaction>() {
            @Override
            public int compare(final Transaction t1, final Transaction t2) {
                return Integer.compare(t1.getTimestamp(), t2.getTimestamp());
            }
        });

        // print the transactions in JSON format
        PrintTransactionsJSON.printTransactions(userTransactions, outputArray);

        result.set("output", outputArray);
        result.put("timestamp", timestamp);
        this.output.add(result);
    }
}
