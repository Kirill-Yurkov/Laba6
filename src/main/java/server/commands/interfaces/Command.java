package server.commands.interfaces;

import server.Server;
import commons.exceptions.CommandCollectionZeroException;
import commons.exceptions.CommandValueException;
import commons.exceptions.StopServerException;
import commons.utilities.CommandValues;

public interface Command {
    CommandValues getValue();
    void setServer(Server server);
    String execute(String value) throws CommandValueException, CommandCollectionZeroException;
    String getName();
    String description();
}
