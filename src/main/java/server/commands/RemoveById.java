package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import commons.exceptions.CommandCollectionZeroException;
import commons.exceptions.CommandValueException;
import commons.patternclass.Ticket;
import commons.utilities.CommandValues;
/**
 * The RemoveById class represents a command that removes an element from the collection based on its ID.
 * It implements the Command interface and provides functionality for executing the command.
 *
 * Usage:
 * RemoveById removeById = new RemoveById();
 * removeById.setServer(server); // Set the server for the command
 * String result = removeById.execute(value); // Execute the command with the given value
 *
 * Example:
 * RemoveById removeById = new RemoveById();
 * removeById.setServer(server);
 * String result = removeById.execute("123");
 *
 * Command Details:
 * - Value: VALUE
 * - Name: remove_by_id
 * - Description: удалить элемент из коллекции по его id
 *
 * Exceptions:
 * - CommandValueException: Thrown if the value is not a valid long.
 * - CommandCollectionZeroException: Thrown if the collection is empty.
 *
 * Note: The server must be set before executing the command.
 */
public class RemoveById implements Command {
    private Server server;
    @Override
    public CommandValues getValue() {
        return CommandValues.VALUE;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String execute(String value) throws CommandValueException, CommandCollectionZeroException {
        long id;
        try {
            id = Long.parseLong(value);
        } catch (NumberFormatException ignored){
            throw new CommandValueException("long");
        }
        if(server.getListManager().getTicketList().isEmpty()){
            throw new CommandCollectionZeroException("collection is empty");
        }
        for(Ticket ticket: server.getListManager().getTicketList()){
            if(ticket.getId() == id){
                server.getListManager().remove(ticket);
                return "successfully";
            }
        }
        throw new CommandValueException("id not find");
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String description() {
        return "удалить элемент из коллекции по его id";
    }
}
