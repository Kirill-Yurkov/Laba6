package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import commons.utilities.CommandValues;
/**
 * The Help class represents a command that provides help and information about available commands.
 * It implements the Command interface.
 *
 * The Help class has the following functionality:
 * - Getting the command value: It returns the CommandValues.NOTHING value, indicating that no value is expected for this command.
 * - Setting the server: It sets the server instance for the command.
 * - Executing the command: It generates a string that contains information about all available commands.
 * - Getting the command name: It returns the name of the command, which is "help".
 * - Getting the command description: It returns a string that describes the purpose of the command.
 *
 * Example usage:
 * Help helpCommand = new Help();
 * helpCommand.setServer(server);
 * String result = helpCommand.execute("");
 * String commandName = helpCommand.getName();
 * String commandDescription = helpCommand.description();
 *
 * Note: The Help class assumes that the server instance is set before executing the command.
 *
 * @see Command
 * @see CommandValues
 * @see Server
 */
public class Help implements Command {
    private Server server;

    @Override
    public CommandValues getValue() {
        return CommandValues.NOTHING;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }
    @Override
    public String execute(String value){
        StringBuilder str = new StringBuilder();
        for(Command command: server.getCommandInvoker().getCommands()){
            str.append("\n").append(command.getName()).append(" : ").append(command.description());
        }
        return str.toString();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String description() {
        return "вывести справку по доступным командам";
    }
}
