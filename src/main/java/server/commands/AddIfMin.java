package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import server.exceptions.CommandCollectionZeroException;
import server.exceptions.CommandValueException;
import server.exceptions.StopCreateTicketException;
import server.exceptions.StopServerException;
import server.patternclass.Ticket;
import server.utilities.CommandValues;
/**
 * The 'AddIfMin' class represents a command that adds a new element to the collection if its value is smaller than the value of the smallest element in the collection.
 *
 * Attributes:
 * - server: Server (required) - The server object that contains the list manager and ticket creator.
 *
 * Methods:
 * - getValue(): CommandValues - Returns the value of the command.
 * - setServer(Server server): void - Sets the server object.
 * - execute(String value): String - Executes the command by creating a new ticket and checking if its price is smaller than the smallest price in the collection. Returns "successfully" if the ticket is added, throws CommandValueException if the price is greater than the smallest price, and throws CommandCollectionZeroException if the collection is empty.
 * - getName(): String - Returns the name of the command.
 * - description(): String - Returns the description of the command.
 */
public class AddIfMin implements Command {
    private Server server;
    @Override
    public CommandValues getValue() {
        return CommandValues.ELEMENT;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String execute(String value) throws  CommandValueException, CommandCollectionZeroException {
        if(server.getListManager().getTicketList().isEmpty()){
            throw new CommandCollectionZeroException("collection is zero");
        }
        try {
            Ticket ticket = server.getTicketCreator().createTicketGroup();
            int mini = Integer.MAX_VALUE;
            for (Ticket localTicket : server.getListManager().getTicketList()) {
                if (localTicket.getPrice() != null && localTicket.getPrice()<mini) {
                    mini = localTicket.getPrice();
                }
            }
            if(ticket.getPrice()<mini){
                ticket.setId(server.getIdCounter().getIdForTicket(ticket));
                if(ticket.getEvent()!=null){
                    ticket.getEvent().setId(server.getIdCounter().getIdForEvent(ticket.getEvent()));
                }
                return "successfully";
            }
        } catch (StopCreateTicketException e) {
            return null;
        }
        throw new CommandValueException("price more than minimal");
    }

    @Override
    public String getName() {
        return "add_if_min";
    }

    @Override
    public String description() {
        return "добавить новый элемент в коллекцию, если его значение меньше (price), чем у наименьшего элемента этой коллекции";
    }
}
