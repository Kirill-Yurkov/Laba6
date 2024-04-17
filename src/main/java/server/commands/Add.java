package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import server.exceptions.StopCreateTicketException;
import server.patternclass.Ticket;
import server.utilities.CommandValues;
/**
 * The 'Add' class is responsible for adding a new element to the collection.
 * It implements the 'Command' interface and provides the necessary methods to execute the command.
 * The 'Add' command takes a value as input and creates a new 'Ticket' object based on the provided value.
 * The created ticket is then added to the collection using the 'ListManager' class.
 * If the server is running with a file, the ticket is created with additional information and assigned unique IDs using the 'IdCounter' class.
 * The 'Add' command returns a success message if the ticket is successfully created and added to the collection.
 * If there is an exception during the ticket creation process, the command returns null.
 *
 * The 'Add' class has the following methods:
 * - getValue(): Returns the value of the command, which is 'ELEMENT' in this case.
 * - setServer(Server server): Sets the server instance for the command.
 * - execute(String value): Executes the 'Add' command by creating a new ticket and adding it to the collection.
 *                          Returns a success message if the ticket is added successfully, or null if there is an exception.
 * - getName(): Returns the name of the command, which is 'add' in this case.
 * - description(): Returns a description of the command, which is "добавить новый элемент в коллекцию" (add a new element to the collection) in this case.
 */
public class Add implements Command {
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
    public String execute(String value) {
        Ticket ticket = null;
        try {
            if(server.isWithFile()){
                ticket = server.getTicketCreator().createTicketGroup(true);
                ticket.setId(server.getIdCounter().getIdForTicket(ticket));
                if(ticket.getEvent()!=null){
                    ticket.getEvent().setId(server.getIdCounter().getIdForEvent(ticket.getEvent()));
                }
            } else{
                ticket = server.getTicketCreator().createTicketGroup();
                ticket.setId(server.getIdCounter().getIdForTicket(ticket));
                if(ticket.getEvent()!=null){
                    ticket.getEvent().setId(server.getIdCounter().getIdForEvent(ticket.getEvent()));
                }
            }

            server.getListManager().add(ticket);
            return "Successfully created";
        } catch (StopCreateTicketException e) {
            return null;
        }


    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String description() {
        return "добавить новый элемент в коллекцию";
    }
}
