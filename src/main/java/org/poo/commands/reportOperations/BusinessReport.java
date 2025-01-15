package org.poo.commands.reportOperations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankManager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.Commerciant;
import org.poo.instances.User;

import java.util.*;

public class BusinessReport implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public BusinessReport(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    @Override
    public void execute() {
        String type = this.command.getType();
        int startTimestamp = this.command.getStartTimestamp();
        int endTimestamp = this.command.getEndTimestamp();
        String iban = this.command.getAccount();
        int timestamp = this.command.getTimestamp();

        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            // TODO: "Account not found"
            System.out.println("Account not found in business report");
            return;
        }

        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            // TODO: "Account is not of type business"
            System.out.println("Account is not of type business");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode output = objectMapper.createObjectNode();
        output.put("command", this.command.getCommand());
        output.put("timestamp", timestamp);

        BusinessAccount businessAccount = (BusinessAccount) account;
        double balance = businessAccount.getBalance();
        String currency = businessAccount.getCurrency();
        double spendingLimit = businessAccount.getSpendingLimit();
        double depositLimit = businessAccount.getDepositLimit();

        ObjectNode report = objectMapper.createObjectNode();
        report.put("IBAN", iban);
        report.put("balance", balance);
        report.put("currency", currency);
        report.put("spending limit", spendingLimit);
        report.put("deposit limit", depositLimit);

        if (type.equals("transaction")) {
            List<User> managers = businessAccount.getUserMap()
                    .get(BusinessAccount.UserType.MANAGER);
            List<User> employees = businessAccount.getUserMap()
                    .get(BusinessAccount.UserType.EMPLOYEE);

            double totalSpent = 0;
            double totalDeposited = 0;

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
        } else {
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
            User owner = businessAccount.getOwner();

            for (Map.Entry<Commerciant, Double> entry : sortedCommerciantsMap.entrySet()) {
                ObjectNode commerciantNode = objectMapper.createObjectNode();
                Commerciant commerciant = entry.getKey();
                List<User> usersWhoSpentAtCommerciant =
                        businessAccount.getUsersWhoSpentAtCommerciant(commerciant);

                if (usersWhoSpentAtCommerciant.size() == 1
                        && usersWhoSpentAtCommerciant.get(0).equals(owner)) {
                    continue;
                }

                commerciantNode.put("commerciant", commerciant.getCommerciant());

                ArrayNode managersArray = objectMapper.createArrayNode();
                ArrayNode employeesArray = objectMapper.createArrayNode();
                List<String> sortedEmployees = new ArrayList<>();
                List<String> sortedManagers = new ArrayList<>();

                for (User user : usersWhoSpentAtCommerciant) {
                    BusinessAccount.UserType userType = businessAccount.getUserType(user);
                    Map<Commerciant, Integer> nrOfTransactionsMap
                            = businessAccount.getNrOfTransactionsBusiness().get(user);
                    int nrOfTransactions = nrOfTransactionsMap.get(commerciant);

                    if (userType == null) {
                        continue;
                    }
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
                    double totalSpentAtCommerciant
                            = businessAccount.getTotalSpentAtCommerciantByUser(commerciant, user);
                    commerciantNode.put("total received", totalSpentAtCommerciant);
                }

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
        }
        output.set("output", report);
        this.output.add(output);
    }
}
