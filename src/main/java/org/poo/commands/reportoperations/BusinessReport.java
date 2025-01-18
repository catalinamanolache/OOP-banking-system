package org.poo.commands.reportoperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankmanager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

public class BusinessReport implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public BusinessReport(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the businessReport command.
     */
    @Override
    public void execute() {
        String type = this.command.getType();
        int startTimestamp = this.command.getStartTimestamp();
        int endTimestamp = this.command.getEndTimestamp();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        // you cannot generate a report for a non-business account
        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode outputNode = objectMapper.createObjectNode();
        outputNode.put("command", this.command.getCommand());
        outputNode.put("timestamp", timestamp);

        ObjectNode report;

        // generate the report based on the type of statistics requested
        if (type.equals("transaction")) {
            report = getTransactionsReport(account);
        } else {
            report = getCommerciantsReport(account);
        }

        outputNode.set("output", report);
        this.output.add(outputNode);
    }

    /**
     * Generates a transaction report for a business account.
     * @param account the account to generate the report for
     * @return the transaction report node
     */
    private ObjectNode getTransactionsReport(final Account account) {
        ObjectMapper objectMapper = new ObjectMapper();
        BusinessAccount businessAccount = (BusinessAccount) account;

        double balance = businessAccount.getBalance();
        String currency = businessAccount.getCurrency();
        double spendingLimit = businessAccount.getSpendingLimit();
        double depositLimit = businessAccount.getDepositLimit();

        ObjectNode report = objectMapper.createObjectNode();
        report.put("IBAN", account.getIban());
        report.put("balance", balance);
        report.put("currency", currency);
        report.put("spending limit", spendingLimit);
        report.put("deposit limit", depositLimit);

        // get the managers and employees from the business account
        List<User> managers = businessAccount.getUserMap()
                .get(BusinessAccount.UserType.MANAGER);
        List<User> employees = businessAccount.getUserMap()
                .get(BusinessAccount.UserType.EMPLOYEE);

        double totalSpent = 0;
        double totalDeposited = 0;

        // for each manager, get the total spent and deposited and add them to the report
        ArrayNode managersArray = objectMapper.createArrayNode();
        for (User manager : managers) {
            ObjectNode managerNode = objectMapper.createObjectNode();
            double spentByManager = businessAccount.getTotalSpentByUser(manager);
            double depositedByManager = businessAccount.getTotalDepositedByUser(manager);
            totalSpent += spentByManager;
            totalDeposited += depositedByManager;

            managerNode.put("username", manager.getLastName()
                    + " " + manager.getFirstName());
            managerNode.put("spent", spentByManager);
            managerNode.put("deposited", depositedByManager);
            managersArray.add(managerNode);
        }
        report.set("managers", managersArray);

        // for each employee, get the total spent and deposited and add them to the report
        ArrayNode employeesArray = objectMapper.createArrayNode();
        for (User employee : employees) {
            ObjectNode employeeNode = objectMapper.createObjectNode();
            double spentByEmployee = businessAccount.getTotalSpentByUser(employee);
            double depositedByEmployee = businessAccount.getTotalDepositedByUser(employee);
            totalSpent += spentByEmployee;
            totalDeposited += depositedByEmployee;

            employeeNode.put("username", employee.getLastName() + " "
                    + employee.getFirstName());
            employeeNode.put("spent", spentByEmployee);
            employeeNode.put("deposited", depositedByEmployee);
            employeesArray.add(employeeNode);
        }
        report.set("employees", employeesArray);

        report.put("total spent", totalSpent);
        report.put("total deposited", totalDeposited);
        report.put("statistics type", "transaction");

        return report;
    }

    /**
     * Generates a commerciant report for a business account.
     * @param account the account to generate the report for
     * @return the commerciant report node
     */
    private ObjectNode getCommerciantsReport(final Account account) {
        ObjectMapper objectMapper = new ObjectMapper();
        BusinessAccount businessAccount = (BusinessAccount) account;
        ObjectNode report = objectMapper.createObjectNode();

        double balance = businessAccount.getBalance();
        String currency = businessAccount.getCurrency();
        double spendingLimit = businessAccount.getSpendingLimit();
        double depositLimit = businessAccount.getDepositLimit();

        report.put("IBAN", account.getIban());
        report.put("balance", balance);
        report.put("currency", currency);
        report.put("spending limit", spendingLimit);
        report.put("deposit limit", depositLimit);

        // get the total spent at each commerciant and sort it ascending by commerciant name
        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        Map<Commerciant, Double> totalSpentAtCommerciantMap
                = businessAccount.getTotalSpentAtCommerciantsMap();
        Map<Commerciant, Double> sortedCommerciantsMap
                = new TreeMap<>(Comparator.comparing(Commerciant::getCommerciant));

        for (Map.Entry<Commerciant, Double> entry : totalSpentAtCommerciantMap.entrySet()) {
            if (entry.getValue() == 0) {
                continue;
            }

            Commerciant commerciant = entry.getKey();
            double totalSpentAtCommerciant = entry.getValue();

            if (commerciant == null) {
                continue;
            }

            sortedCommerciantsMap.put(commerciant, totalSpentAtCommerciant);
        }

        // get the business account owner
        User owner = businessAccount.getOwner();

        for (Map.Entry<Commerciant, Double> entry : sortedCommerciantsMap.entrySet()) {
            ObjectNode commerciantNode = objectMapper.createObjectNode();
            Commerciant commerciant = entry.getKey();

            // create a list of users who spent at each commerciant
            List<User> usersWhoSpentAtCommerciant =
                    businessAccount.getUsersWhoSpentAtCommerciant(commerciant);

            // if only the owner spent at the commerciant, skip it
            if (usersWhoSpentAtCommerciant.size() == 1
                    && usersWhoSpentAtCommerciant.getFirst().equals(owner)) {
                continue;
            }

            commerciantNode.put("commerciant", commerciant.getCommerciant());

            ArrayNode managersArray = objectMapper.createArrayNode();
            ArrayNode employeesArray = objectMapper.createArrayNode();
            List<String> sortedEmployees = new ArrayList<>();
            List<String> sortedManagers = new ArrayList<>();

            // create a list of employees and managers who spent at each commerciant
            for (User user : usersWhoSpentAtCommerciant) {
                BusinessAccount.UserType userType = businessAccount.getUserType(user);
                Map<Commerciant, Integer> nrOfTransactionsMap
                        = businessAccount.getNrOfTransactionsBusiness().get(user);
                int nrOfTransactions = nrOfTransactionsMap.get(commerciant);

                if (userType == null) {
                    continue;
                }

                // for each user, add them to the according list based on their user type
                String name = user.getLastName() + " " + user.getFirstName();
                if (userType.equals(BusinessAccount.UserType.EMPLOYEE)) {
                    for (int i = 0; i < nrOfTransactions; i++) {
                        sortedEmployees.add(name);
                    }
                } else {
                    for (int i = 0; i < nrOfTransactions; i++) {
                        sortedManagers.add(name);
                    }
                }

                // add the total spent at the commerciant by the user
                double totalSpentAtCommerciant
                        = businessAccount.getTotalSpentAtCommerciantByUser(commerciant, user);

                commerciantNode.put("total received", totalSpentAtCommerciant);
            }

            // sort the lists of employees and managers and add them to the report
            Collections.sort(sortedEmployees);
            Collections.sort(sortedManagers);

            for (String employee : sortedEmployees) {
                employeesArray.add(employee);
            }

            for (String manager : sortedManagers) {
                managersArray.add(manager);
            }

            commerciantNode.set("managers", managersArray);
            commerciantNode.set("employees", employeesArray);

            commerciantsArray.add(commerciantNode);
        }
        report.set("commerciants", commerciantsArray);
        report.put("statistics type", "commerciant");
        return report;
    }
}
