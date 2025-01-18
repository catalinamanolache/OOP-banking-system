package org.poo.commandmanager;

import com.fasterxml.jackson.databind.node.ArrayNode;

import org.poo.commands.Command;
import org.poo.commands.accountoperations.businessoperations.AddNewBusinessAssociate;
import org.poo.commands.accountoperations.businessoperations.ChangeDepositLimit;
import org.poo.commands.accountoperations.businessoperations.ChangeSpendingLimit;
import org.poo.commands.accountoperations.splitpaymentoperations.AcceptSplitPayment;
import org.poo.commands.accountoperations.splitpaymentoperations.RejectSplitPayment;
import org.poo.commands.accountoperations.splitpaymentoperations.SplitPayment;
import org.poo.commands.accountoperations.savingsoperations.AddInterest;
import org.poo.commands.accountoperations.savingsoperations.ChangeInterestRate;
import org.poo.commands.accountoperations.savingsoperations.WithdrawSavings;
import org.poo.commands.cardoperations.CheckCardStatus;
import org.poo.commands.cardoperations.CashWithdrawal;
import org.poo.commands.cardoperations.PayOnline;
import org.poo.commands.cardoperations.CreateCard;
import org.poo.commands.cardoperations.CreateOneTimeCard;
import org.poo.commands.cardoperations.DeleteCard;
import org.poo.commands.printoperations.PrintUsers;
import org.poo.commands.printoperations.PrintTransactions;
import org.poo.commands.reportoperations.BusinessReport;
import org.poo.commands.reportoperations.Report;
import org.poo.commands.reportoperations.SpendingsReport;
import org.poo.bankmanager.Bank;
import org.poo.instances.CommandData;
import org.poo.commands.accountoperations.AddAccount;
import org.poo.commands.accountoperations.AddFunds;
import org.poo.commands.accountoperations.SetMinimumBalance;
import org.poo.commands.accountoperations.DeleteAccount;
import org.poo.commands.accountoperations.SetAlias;
import org.poo.commands.accountoperations.UpgradePlan;
import org.poo.commands.accountoperations.SendMoney;


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
