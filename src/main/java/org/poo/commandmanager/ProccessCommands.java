package org.poo.commandmanager;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.commands.Command;
import org.poo.fileio.CommandInput;
import org.poo.bankmanager.Bank;
import org.poo.instances.CommandData;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public final class ProccessCommands {
    private ProccessCommands() {
    }

    /**
     * Class that processes the commands from the input file.
     * @param commands array of commands
     * @param bank bank object
     * @param output output array
     */
    public static void processCommands(final CommandInput[] commands, final Bank bank,
                                       final ArrayNode output) {
        List<CommandData> commandDataArray = new ArrayList<>();

        // add all the commands to the commandDataArray
        for (CommandInput command : commands) {
            commandDataArray.add(new CommandData(command));
        }

        // create the command invoker
        CommandInvoker invoker = new CommandInvoker();

        // reset the random generator
        Utils.resetRandom();

        // process the commands
        for (CommandInput commandInput : commands) {
            CommandData commandData = new CommandData(commandInput);
            Command command = CommandFactory.getCommand(commandData, bank, output);

            // for invalid commands, skip them
            if (command == null) {
                continue;
            }

            // add the command to the invoker
            invoker.addCommand(command);
        }

        // execute the commands
        invoker.executeCommands();
    }
}
