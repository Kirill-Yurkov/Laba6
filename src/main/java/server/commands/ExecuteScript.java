package server.commands;

import server.Server;
import server.commands.interfaces.Command;
import commons.exceptions.CommandValueException;
import commons.exceptions.StopServerException;
import commons.utilities.CommandValues;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
/**
 * The ExecuteScript class represents a command that reads and executes a script from a specified file.
 * The script contains commands in the same format as the user enters them in interactive mode.
 *
 * This class implements the Command interface and provides the following methods:
 * - getValue(): Returns the value of the command.
 * - setServer(Server server): Sets the server for the command.
 * - execute(String filePath): Executes the script from the specified file.
 * - checkFilePermission(String filePath): Checks the file permission for the specified file.
 * - getName(): Returns the name of the command.
 * - description(): Returns the description of the command.
 *
 * The ExecuteScript class is used in the server application to handle the "execute_script" command.
 * This command allows the server to read and execute a script from a file, which can be useful for automating tasks or running multiple commands at once.
 * The execute() method reads the script file, checks the file permission, and executes the commands in the script.
 * If the file has recursion, a CommandValueException is thrown.
 * If the file is not found or the file permission is denied, a CommandValueException is thrown.
 *
 * Example usage:
 * ExecuteScript executeScript = new ExecuteScript();
 * executeScript.setServer(server);
 * String result = executeScript.execute(filePath);
 *
 * Note: This class assumes that the Server class and the Command interface are already defined.
 */
public class ExecuteScript implements Command {
    private Server server;
    private static Set<String> fileSet = new HashSet<>();

    @Override
    public CommandValues getValue() {
        return CommandValues.VALUE;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String execute(String filePath) throws CommandValueException {

        if (checkFilePermission(filePath)) {
            if(!fileSet.contains(filePath)){
                fileSet.add(filePath);
                server.setWithFile(true);
                server.start(new File(filePath));
                server.getInputOutput().setReader(new BufferedReader(new InputStreamReader(System.in)));
                fileSet.remove(filePath);
                server.setWithFile(false);
                return null;
            } else {
                server.setWithFile(false);
                throw new CommandValueException("file has recursion");
            }

        }else {
            server.setWithFile(false);
            throw new CommandValueException("error");
        }

    }
    private boolean checkFilePermission(String filePath) throws  CommandValueException {
        try {
            File file = new File(filePath);
            FileReader f = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(f);

        } catch (FileNotFoundException e) {
            throw new CommandValueException("file not found");
        } catch (SecurityException e) {
            throw new CommandValueException("file permission denied");
        }
        return true;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String description() {
        return "cчитать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }
}
