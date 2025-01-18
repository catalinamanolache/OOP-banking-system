package org.poo.instances.jsonexceptions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.instances.CommandData;

public final class JSONException extends Exception {
    private CommandData commandData;
    private String error;
    private ArrayNode output;

    public JSONException(final CommandData commandData, final String error,
                         final ArrayNode output) {
        super(error);
        this.commandData = commandData;
        this.error = error;
        this.output = output;
    }

    public CommandData getCommandData() {
        return this.commandData;
    }

    public String getError() {
        return this.error;
    }

    public ArrayNode getOutput() {
        return this.output;
    }
}
