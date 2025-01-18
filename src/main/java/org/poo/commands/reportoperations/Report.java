package org.poo.commands.reportoperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankmanager.Bank;
import org.poo.instances.jsonexceptions.JSONException;
import org.poo.transactions.PrintTransactionsJSON;
import org.poo.transactions.Transaction;
import java.util.ArrayList;

public class Report implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public Report(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Execute the report command.
     * @throws JSONException if the account is not found
     */
    @Override
    public void execute() throws JSONException {
        int startTimestamp = this.command.getStartTimestamp();
        int endTimestamp = this.command.getEndTimestamp();
        String accountIban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the account by IBAN
        Account account = this.bank.getAccountByIban(accountIban);

        // if the account is not found, return an error message
        if (account == null) {
            throw new JSONException(this.command, "accountNotFound", this.output);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("command", this.command.getCommand());
        ObjectNode outputNode = objectMapper.createObjectNode();

        outputNode.put("IBAN", account.getIban());
        outputNode.put("balance", account.getBalance());
        outputNode.put("currency", account.getCurrency());

        // create the transactions array and populate it with the transactions
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ArrayList<Transaction> userTransactions = new ArrayList<>();

        for (Transaction transaction : account.getTransactions()) {
            userTransactions.add(transaction);
        }

        // print the transactions in JSON format
        PrintTransactionsJSON.printReport(userTransactions, transactionsArray, startTimestamp,
                endTimestamp);

        outputNode.set("transactions", transactionsArray);
        result.set("output", outputNode);
        result.put("timestamp", timestamp);

        this.output.add(result);
    }
}
