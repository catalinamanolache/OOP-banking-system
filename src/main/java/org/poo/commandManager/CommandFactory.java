package org.poo.commandManager;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.commands.Command;
import org.poo.commands.accountOperations.businessAccountOperations.AddNewBusinessAssociate;
import org.poo.commands.accountOperations.businessAccountOperations.ChangeDepositLimit;
import org.poo.commands.accountOperations.businessAccountOperations.ChangeSpendingLimit;
import org.poo.commands.accountOperations.splitPaymentOperations.AcceptSplitPayment;
import org.poo.commands.accountOperations.splitPaymentOperations.RejectSplitPayment;
import org.poo.commands.accountOperations.splitPaymentOperations.SplitPayment;
import org.poo.commands.accountOperations.savingsAccountOperations.AddInterest;
import org.poo.commands.accountOperations.savingsAccountOperations.ChangeInterestRate;
import org.poo.commands.accountOperations.savingsAccountOperations.WithdrawSavings;
import org.poo.commands.cardOperations.CheckCardStatus;
import org.poo.commands.cardOperations.CashWithdrawal;
import org.poo.commands.cardOperations.PayOnline;
import org.poo.commands.cardOperations.CreateCard;
import org.poo.commands.cardOperations.CreateOneTimeCard;
import org.poo.commands.cardOperations.DeleteCard;
import org.poo.commands.printOperations.PrintUsers;
import org.poo.commands.printOperations.PrintTransactions;
import org.poo.commands.reportOperations.BusinessReport;
import org.poo.commands.reportOperations.Report;
import org.poo.commands.reportOperations.SpendingsReport;
import org.poo.bankManager.Bank;
import org.poo.instances.CommandData;
import org.poo.commands.accountOperations.AddAccount;
import org.poo.commands.accountOperations.AddFunds;
import org.poo.commands.accountOperations.SetMinimumBalance;
import org.poo.commands.accountOperations.DeleteAccount;
import org.poo.commands.accountOperations.SetAlias;
import org.poo.commands.accountOperations.UpgradePlan;
import org.poo.commands.accountOperations.SendMoney;


public final class CommandFactory {
    private CommandFactory() {
    }

    /**
     * Return the command object based on the command input.
     * @param command the command
     * @param bank the bank object which contains all the users' data
     * @param output the output array
     * @return the command
     */
    public static Command getCommand(final CommandData command, final Bank bank,
                                     final ArrayNode output) {
        return switch (command.getCommand()) {
            case "printUsers" -> new PrintUsers(command, bank, output);
            case "addAccount" -> new AddAccount(command, bank);
            case "addFunds" -> new AddFunds(command, bank);
            case "createCard" -> new CreateCard(command, bank);
            case "printTransactions" -> new PrintTransactions(command, bank, output);
            case "createOneTimeCard" -> new CreateOneTimeCard(command, bank);
            case "deleteAccount" -> new DeleteAccount(command, bank, output);
            case "deleteCard" -> new DeleteCard(command, bank);
            case "setMinimumBalance" -> new SetMinimumBalance(command, bank);
            case "checkCardStatus" -> new CheckCardStatus(command, bank, output);
            case "payOnline" -> new PayOnline(command, bank, output);
            case "sendMoney" -> new SendMoney(command, bank, output);
            case "setAlias" -> new SetAlias(command, bank);
            case "splitPayment" -> new SplitPayment(command, bank);
            case "addInterest" -> new AddInterest(command, bank, output);
            case "changeInterestRate" -> new ChangeInterestRate(command, bank, output);
            case "report" -> new Report(command, bank, output);
            case "spendingsReport" -> new SpendingsReport(command, bank, output);
            case "withdrawSavings" -> new WithdrawSavings(command, bank, output);
            case "upgradePlan" -> new UpgradePlan(command, bank, output);
            case "cashWithdrawal" -> new CashWithdrawal(command, bank, output);
            case "addNewBusinessAssociate" -> new AddNewBusinessAssociate(command, bank);
            case "changeSpendingLimit" -> new ChangeSpendingLimit(command, bank, output);
            case "changeDepositLimit" -> new ChangeDepositLimit(command, bank, output);
            case "acceptSplitPayment" -> new AcceptSplitPayment(command, bank, output);
            case "rejectSplitPayment" -> new RejectSplitPayment(command, bank, output);
            case "businessReport" -> new BusinessReport(command, bank, output);
            default -> null;
        };
    }
}
