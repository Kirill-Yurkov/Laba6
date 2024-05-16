package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import commons.exceptions.CommandCollectionZeroException;
import commons.exceptions.CommandValueException;
import commons.exceptions.StopCreateTicketExceptionByClient;
import commons.patternclass.Ticket;
import commons.utilities.CommandValues;
/**
 * The 'Update' class represents a command that updates the value of an element in the collection
 * with the specified ID.
 *
 * Attributes:
 * - server: Server - The server object that contains the collection and other necessary components.
 *
 * Methods:
 * - getValue(): CommandValues - Returns the value of the command.
 * - setServer(Server server): void - Sets the server object.
 * - execute(String value): String - Executes the command with the specified value.
 * - getName(): String - Returns the name of the command.
 * - description(): String - Returns the description of the command.
 */
public class Update implements Command {
    private Server server;
    @Override
    public CommandValues getValue() {
        return CommandValues.VALUE_ELEMENT;
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
            throw new CommandCollectionZeroException("collection is zero");
        }
        for(Ticket ticket: server.getListManager().getTicketList()){
            if (ticket.getId() == id){
                server.getListManager().remove(ticket);
                try {
                    Ticket newTicket = server.getTicketCreator().createTicketGroup();
                    newTicket.setId(id);
                    if(newTicket.getEvent()!=null){
                        newTicket.getEvent().setId(server.getIdCounter().getIdForEvent(newTicket.getEvent()));
                    }
                    server.getListManager().add(newTicket);
                    return "successfully";
                } catch (StopCreateTicketExceptionByClient e) {
                    return null;
                }
            }
        }
        throw new CommandValueException("id not find");
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String description() {
        return "обновить значение элемента коллекции, id которого равен заданному";
    }
}
