package org.poo.commandManager;

import org.poo.commands.Command;

import java.util.ArrayList;
import java.util.List;

public final class CommandInvoker {
    private List<Command> commandQueue = new ArrayList<>();

    /**
     * Adds a command to the queue.
     * @param command the command to be added
     */
    public void addCommand(final Command command) {
        commandQueue.add(command);
    }

    /**
     * Executes all commands in the queue.
     */
    public void executeCommands() {
        for (Command command : commandQueue) {
            command.execute();
        }
        commandQueue.clear();
    }
}

