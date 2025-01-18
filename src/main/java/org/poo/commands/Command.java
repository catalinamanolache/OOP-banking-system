package org.poo.commands;

import org.poo.instances.jsonexceptions.JSONException;

public interface Command {
    /**
     * Executes the command.
     * @throws JSONException if an error occurs and prints the error message
     */
    void execute() throws JSONException;
}
