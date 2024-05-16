package server.managers;


import lombok.Getter;
import server.Server;
import server.commands.*;
import server.commands.interfaces.Command;
import commons.exceptions.CommandCollectionZeroException;
import commons.exceptions.CommandValueException;
import commons.exceptions.StopServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * The CommandInvoker class is responsible for invoking commands based on their names.
 * It maintains a collection of registered commands and provides a method to invoke a command by its name.
 *
 * The CommandInvoker class is used in the server to handle incoming command requests and execute the corresponding commands.
 * It takes a Server object as a parameter in its constructor to set the server instance for the registered commands.
 *
 * The CommandInvoker class provides the following functionality:
 * - Registering commands: It has a registerCommand method to register multiple commands at once.
 * - Invoking commands: It has an invoke method that takes a command name as input and executes the corresponding command.
 *   The method splits the command name into parts and checks the number of parts to determine the type of command value expected.
 *   It then calls the execute method of the corresponding command with the appropriate value.
 * - Getting registered commands: It has a getCommands method that returns a list of all registered commands.
 *
 * Example usage:
 * CommandInvoker commandInvoker = new CommandInvoker(server);
 * String result = commandInvoker.invoke("show");
 * List<Command> commands = commandInvoker.getCommands();
 *
 * Note: The CommandInvoker class assumes that the registered commands implement the Command interface.
 *
 * @see Command
 * @see Server
 */
public class CommandInvoker {
    private final HashMap<String, Command> commands = new HashMap<>();
    @Getter
    private Server server;

    public CommandInvoker(Server server) {
        this.server = server;
        registerCommand(
                new Help(),
                new Info(),
                new Show(),
                new Add(),
                new Update(),
                new RemoveById(),
                new Clear(),
                new Save(),
                new ExecuteScript(),
                new Exit(),
                new AddIfMin(),
                new Shuffle(),
                new RemoveLower(),
                new AverageOfPrice(),
                new CountGreaterThanEvent(),
                new FilterLessThanType());
    }

    public String invoke(String commandName) throws CommandValueException, NullPointerException, CommandCollectionZeroException {
        String[] s = commandName.strip().split(" ");
        if(commandName.isEmpty()){
            return "";
        }
        switch (commands.get(s[0]).getValue()) {
            case NOTHING, ELEMENT -> {
                if (s.length == 1) {
                    return commands.get(s[0]).execute("");
                }
                throw new CommandValueException("unexpected values");
            }
            case VALUE, VALUE_ELEMENT -> {
                if (s.length == 2) {
                    return commands.get(s[0]).execute(s[1]);
                }
                throw new CommandValueException("wrong values");
            }
            default -> throw new NullPointerException("");
        }
    }

    private void registerCommand(Command... commandsToRegister) {
        for (Command command : commandsToRegister) {
            command.setServer(server);
            commands.put(command.getName(), command);
        }
    }

    public List<Command> getCommands() {
        return new ArrayList<>(this.commands.values());
    }
}
