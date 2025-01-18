package org.poo.commands.accountoperations.businessoperations;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.accounts.Account;
import org.poo.accounts.BusinessAccount;
import org.poo.bankmanager.Bank;
import org.poo.commands.Command;
import org.poo.instances.CommandData;
import org.poo.instances.jsonexceptions.JSONException;
import org.poo.instances.User;

public class ChangeDepositLimit implements Command {
    private CommandData command;
    private Bank bank;
    private ArrayNode output;

    public ChangeDepositLimit(final CommandData command, final Bank bank, final ArrayNode output) {
        this.command = command;
        this.bank = bank;
        this.output = output;
    }

    /**
     * Executes the changeDepositLimit command.
     * @throws JSONException if the account is not a business account or the user is not the
     * owner of the account
     */
    @Override
    public void execute() throws JSONException {
        String email = this.command.getEmail();
        String iban = this.command.getAccount();
        double depositLimit = this.command.getAmount();
        int timestamp = this.command.getTimestamp();

        // get the account and user
        Account account = this.bank.getAccountByIban(iban);

        if (account == null) {
            return;
        }

        User user = this.bank.getUserByEmail(email);

        if (user == null) {
            return;
        }

        // if the account is not a business account, print an error
        if (!account.getAccountType().equals(Account.AccountType.BUSINESS)) {
            throw new JSONException(this.command, "notBusinessAccount", this.output);
        }

        // if the user is not the owner of the account, print an error
        if (!account.getOwner().getEmail().equals(email)) {
            throw new JSONException(this.command, "notOwnerOfAccount", this.output);
        }

        // change the deposit limit of the account
        BusinessAccount businessAccount = (BusinessAccount) account;
        businessAccount.setDepositLimit(depositLimit);
    }
}
