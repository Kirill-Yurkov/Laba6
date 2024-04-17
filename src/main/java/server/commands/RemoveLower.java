package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import server.exceptions.CommandCollectionZeroException;
import server.exceptions.StopCreateTicketException;
import server.exceptions.StopServerException;
import server.patternclass.Ticket;
import server.utilities.CommandValues;

import java.util.ArrayList;
import java.util.List;
/**
 * The RemoveLower class is a command implementation that removes all elements from the collection
 * that have a price lower than the specified ticket's price.
 * It implements the Command interface and provides methods to execute the command, set the server,
 * get the command value, get the command name, and get the command description.
 *
 * Usage:
 * RemoveLower removeLower = new RemoveLower();
 * removeLower.setServer(server); // Set the server for the command
 * String result = removeLower.execute(value); // Execute the command with the specified value
 * String name = removeLower.getName(); // Get the name of the command
 * String description = removeLower.description(); // Get the description of the command
 *
 * Example:
 * RemoveLower removeLower = new RemoveLower();
 * removeLower.setServer(server);
 * String result = removeLower.execute("100");
 * String name = removeLower.getName();
 * String description = removeLower.description();
 *
 * The RemoveLower command requires a server instance to be set before execution.
 * It creates a new ticket using the TicketCreator and compares the price of each ticket in the collection
 * with the price of the new ticket. If the ticket's price is lower, it is added to a removeList.
 * After iterating through all tickets, the removeList is used to remove the tickets from the collection.
 * The execute method returns "successfully" if the operation is completed without any exceptions.
 * If a StopCreateTicketException is caught during ticket creation, the execute method returns null.
 * The getName method returns the name of the command ("remove_lower").
 * The description method returns a description of the command ("удалить из коллекции все элементы, меньшие, чем заданный(по price)").
 */
public class RemoveLower implements Command {
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
    public String execute(String value) throws CommandCollectionZeroException {
        try {
           Ticket newTicket = server.getTicketCreator().createTicketGroup();
            List<Ticket> removeList = new ArrayList<>();
            if(server.getListManager().getTicketList().isEmpty()){
                throw new CommandCollectionZeroException("collection is zero");
            }
            for(Ticket ticket: server.getListManager().getTicketList()){
                if(ticket.getPrice()< newTicket.getPrice()){
                    removeList.add(ticket);
                }
            }
            for(Ticket ticket: removeList){
                server.getListManager().getTicketList().remove(ticket);
            }
            return "successfully";
        } catch (StopCreateTicketException e) {
            return null;
        }

    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String description() {
        return "удалить из коллекции все элементы, меньшие, чем заданный(по price)";
    }
}
