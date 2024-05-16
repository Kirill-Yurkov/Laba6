package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import commons.exceptions.StopServerException;
import commons.utilities.CommandValues;
/**
 * The Exit class represents a command to exit the program without saving to a file.
 * It implements the Command interface and provides the necessary methods to execute the command.
 * The command value is CommandValues.NOTHING.
 * The execute method stops the server by calling the stop method of the Server class.
 * The getName method returns the name of the command, which is "exit".
 * The description method returns a description of the command, which is "завершить программу (без сохранения в файл)".
 */
public class Exit implements Command {
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
        server.stop();
        return "Successfully";
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String description() {
        return "завершить программу (без сохранения в файл)";
    }
}
