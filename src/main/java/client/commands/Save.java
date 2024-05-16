package client.commands;

import client.commands.interfaces.Command;
import client.Client;
import client.utilities.CommandValues;
import client.utilities.Request;

/**
 * The Save class represents a command to save the collection to a file.
 * It implements the Command interface.
 *
 * This class has the following methods:
 * - getValue(): Returns the CommandValues enum value associated with this command.
 * - setServer(Client client): Sets the client instance for this command.
 * - execute(String value): Executes the save command by writing the collection to a file.
 * - getName(): Returns the name of the command.
 * - description(): Returns a description of the command.
 */
public class Save implements Command {
    private Client client;

    @Override
    public CommandValues getValue() {
        return CommandValues.NOTHING;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
    }
    @Override
    public Request makeRequest(String value) {
        return new Request(getName(),getValue(), null);
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String description() {
        return "<> сохранить коллекцию в файл";
    }
}
