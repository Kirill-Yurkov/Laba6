package server;

import lombok.Getter;
import lombok.Setter;
import commons.exceptions.CommandCollectionZeroException;
import commons.exceptions.CommandValueException;
import commons.exceptions.FileException;
import commons.exceptions.StopServerException;
import server.managers.CommandInvoker;
import server.managers.FileManager;
import server.managers.ListManager;
import server.utilities.IdCounter;
import server.utilities.TicketCreator;

import java.io.*;

/**
 * The Server class represents a server that handles commands and manages various components.
 * It provides functionality for starting and stopping the server, as well as invoking commands.
 * The server consists of a CommandInvoker, FileManager, ListManager, IdCounter, and TicketCreator.
 * It also has a FileManager.ReaderWriter and FileManager.InputOutput for file operations and input/output handling.
 * The server can be started with or without a file, and it handles exceptions related to command execution.
 *
 * Usage:
 * Server server = new Server(reader, writer);
 * server.start(); // Start the server and handle commands from the console
 * server.start(file); // Start the server and handle commands from a file
 * server.stop(); // Stop the server
 *
 * Example:
 * Server server = new Server(reader, writer);
 * server.start();
 *
 */
@Getter
public class Server {
    private final CommandInvoker commandInvoker = new CommandInvoker(this);
    private final FileManager fileManager = new FileManager(this);
    private final ListManager listManager = new ListManager(this);
    private final IdCounter idCounter = new IdCounter(this);
    private final TicketCreator ticketCreator = new TicketCreator(this);
    private final FileManager.ReaderWriter readerWriter = fileManager.new ReaderWriter();
    private final FileManager.InputOutput inputOutput = fileManager.new InputOutput();
    private boolean serverOn;
    @Getter
    @Setter
    private boolean withFile = false;

    public Server(BufferedReader reader, BufferedOutputStream writer) {
        inputOutput.setReader(reader);
        inputOutput.setWriter(writer);
    }

    public static void main(String[] args) {
        Server server = new Server(new BufferedReader(new InputStreamReader(System.in)), new BufferedOutputStream(System.out));
        if (args.length == 1) {
            server.getFileManager().initializeFile(args[0]);
        }
        server.start();
    }
    public void stop() {
        serverOn = false;
        try {
            inputOutput.getReader().close();
            inputOutput.getWriter().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void start() {
        if (fileManager.initializeFile()) {
            serverOn = true;
            boolean isCommandWas = true;
            while (serverOn) {
                try {
                    if (isCommandWas){
                        inputOutput.outPut("Введите комманду (для справки используйте комманду help) \n~ ");
                    } else {
                        inputOutput.outPut("~ ");
                    }
                    String commandFromConsole = inputOutput.inPut();
                    if (commandFromConsole == null){
                        inputOutput.outPut("\nПолучен сигнал завершения работы.");
                        serverOn = false;
                        return;
                    }
                    if(commandFromConsole.isBlank() || commandFromConsole.isEmpty()){
                        isCommandWas = false;
                    } else {
                        isCommandWas = true;
                        String str = invoke(commandFromConsole);
                        if (str != null) {
                            inputOutput.outPut(str + "\n");
                            inputOutput.outPut("\n");
                        } else {
                            inputOutput.outPut("\n");
                        }
                    }
                } catch (StopServerException e) {
                    inputOutput.outPut("Command isn't valid: " + e.getMessage() + "\n");
                    inputOutput.outPut("\n");
                }
            }
        }
    }

    public void start(File file) {
        try {
            FileReader f = new FileReader(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(f);
            inputOutput.setReader(br);
            String commandFromConsole;
            while ((commandFromConsole = br.readLine()) != null) {
                try {
                    String str = invoke(commandFromConsole);
                    if (str != null) {
                        inputOutput.outPut(str + "\n");
                    } else {
                        inputOutput.outPut("\n");
                    }
                } catch (StopServerException e) {
                    inputOutput.outPut("Script isn't valid: " + e.getMessage() + "\n");
                    br.close();
                    withFile = false;
                    break;
                }
            }
            br.close();
        } catch (Exception e) {
            withFile = false;
            inputOutput.outPut("Script isn't valid: " + e.getMessage() + "\n");
        }
    }



    public String invoke(String commandName) throws StopServerException {
        try {
            return commandInvoker.invoke(commandName);
        } catch (CommandValueException e) {
            throw new StopServerException("incorrect value of command: " + e.getMessage());
        } catch (NullPointerException ignored) {
            throw new StopServerException("incorrect command");
        } catch (CommandCollectionZeroException e) {
            throw new StopServerException("command is useless: " + e.getMessage());
        }
    }
}
