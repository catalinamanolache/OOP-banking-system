package org.poo.instances.jsonexceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.instances.CommandData;

public final class PrintJSONMessages {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private PrintJSONMessages() {

    }

    /**
     * Handles the errors that can occur in the commands, adding them to the output.
     * @param command the command that was executed
     * @param error the error that occurred
     * @param output the output of the program
     */
    public static void handleErrors(final CommandData command, final String error,
                                    final ArrayNode output) {
        switch (error) {
            case "accountNotFound":
                output.add(accountNotFound(command));
                break;
            case "userNotFound":
                output.add(userNotFound(command));
                break;
            case "cardNotFound":
                output.add(cardNotFound(command));
                break;
            case "notBusinessAccount":
                output.add(notBusinessAccount(command));
                break;
            case "notSavingsAccount":
                output.add(notSavingsAccount(command));
                break;
            case "notOwnerOfAccount":
                output.add(notOwnerOfAccount(command));
                break;
            case "failedDeleteAccount":
                output.add(failedDeleteAccount(command));
                break;
            default:
                break;
        }
    }

    /**
     * Creates a JSON object for the case when the account is not found.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode accountNotFound(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("description", "Account not found");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the user is not found.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode userNotFound(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("description", "User not found");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the card is not found.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode cardNotFound(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("description", "Card not found");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the account is not a business account.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode notBusinessAccount(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("description", "This is not a business account");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the account is not a savings account.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode notSavingsAccount(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        if (command.getCommand().equals("spendingsReport")) {
            outputNode.put("error", "This kind of report is not supported for "
                    + "a saving account");
        } else {
            outputNode.put("description", "This is not a savings account");
            outputNode.put("timestamp", command.getTimestamp());
        }
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the user is not the owner of the account.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode notOwnerOfAccount(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        if (command.getCommand().equals("changeDepositLimit")) {
            outputNode.put("description", "You must be owner in order to change"
                   + " deposit limit.");
        } else {
            outputNode.put("description", "You must be owner in order to change "
                    + "spending limit.");
        }
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the account couldn't be deleted.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode failedDeleteAccount(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("error", "Account couldn't be deleted - see"
                + " org.poo.transactions for details");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }

    /**
     * Creates a JSON object for the case when the account was successfully deleted.
     * @param command the command that was executed
     * @return the JSON object
     */
    public static ObjectNode successDeleteAccount(final CommandData command) {
        ObjectNode resultNode = OBJECT_MAPPER.createObjectNode();
        resultNode.put("command", command.getCommand());

        ObjectNode outputNode = OBJECT_MAPPER.createObjectNode();

        outputNode.put("success", "Account deleted");
        outputNode.put("timestamp", command.getTimestamp());
        resultNode.set("output", outputNode);
        resultNode.put("timestamp", command.getTimestamp());

        return resultNode;
    }
}
