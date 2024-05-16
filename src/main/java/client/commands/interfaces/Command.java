package client.commands.interfaces;

import client.Client;
import client.exceptions.BadResponseException;
import client.exceptions.CommandCollectionZeroException;
import client.exceptions.CommandValueException;
import client.utilities.CommandValues;
import client.utilities.Request;

import java.util.ArrayList;

public interface Command {
    CommandValues getValue();
    void setClient(Client client);
    Request makeRequest(String value) throws CommandValueException, CommandCollectionZeroException;
    //String executeResponse(String answer) throws BadResponseException;
    String getName();
    String description();
}
