package client.commands.interfaces;

import client.Client;
import client.exceptions.CommandCollectionZeroException;
import client.exceptions.CommandValueException;
import client.utilities.CommandValues;
import client.utilities.Request;

public interface Command {
    CommandValues getValue();
    void setClient(Client client);
    Request execute(String value) throws CommandValueException, CommandCollectionZeroException;
    String getName();
    String description();
}
