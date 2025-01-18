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


public class SpendingsReport implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public SpendingsReport(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the spendingsReport command.
     *
     */
    @Override
    public void execute() throws JSONException {
        int startTimestamp = this.command.getStartTimestamp();
        int endTimestamp = this.command.getEndTimestamp();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the account which the report is requested for
        Account account = this.bank.getAccountByIban(iban);

        // if the account is not found, print an error message
        if (account == null) {
            throw new JSONException(this.command, "accountNotFound", this.output);
        }

        // if the account is a saving account, print an error message
        if (account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            throw new JSONException(this.command, "notSavingsAccount", this.output);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        ObjectNode outputNode = objectMapper.createObjectNode();
        result.put("command", this.command.getCommand());
        outputNode.put("IBAN", account.getIban());

        outputNode.put("balance", account.getBalance());

        outputNode.put("currency", account.getCurrency());

        // create the transactions and commerciants arrays
        ArrayNode transactionsArray = objectMapper.createArrayNode();
        ArrayNode commerciantsArray = objectMapper.createArrayNode();

        // print the spendings report
        PrintTransactionsJSON.printSpendingsReport(account.getTransactions(),
                transactionsArray, commerciantsArray, startTimestamp, endTimestamp);

        outputNode.set("transactions", transactionsArray);
        outputNode.set("commerciants", commerciantsArray);
        result.set("output", outputNode);
        result.put("timestamp", timestamp);

        this.output.add(result);
    }
}
