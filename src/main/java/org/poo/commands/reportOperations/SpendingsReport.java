package org.poo.commands.reportOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.instances.CommandData;
import org.poo.commands.Command;
import org.poo.bankManager.Bank;
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
    public void execute() {
        int startTimestamp = this.command.getStartTimestamp();
        int endTimestamp = this.command.getEndTimestamp();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        // get the account which the report is requested for
        Account account = this.bank.getAccountByIban(iban);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        ObjectNode outputNode = objectMapper.createObjectNode();

        result.put("command", this.command.getCommand());

        // if the account is not found, print an error message
        if (account == null) {
            outputNode.put("description", "Account not found");
            outputNode.put("timestamp", timestamp);
            result.set("output", outputNode);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        // if the account is a saving account, print an error message
        if (account.getAccountType().equals(Account.AccountType.SAVINGS)) {
            outputNode.put("error", "This kind of report is not supported for a saving account");
            result.set("output", outputNode);
            result.put("timestamp", timestamp);
            this.output.add(result);
            return;
        }

        outputNode.put("IBAN", account.getIban());

        String formatted = String.format("%.2f", account.getBalance());
        double formattedBalance = Double.parseDouble(formatted);
        outputNode.put("balance", formattedBalance);

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
