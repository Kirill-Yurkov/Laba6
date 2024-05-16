package client;

import client.exceptions.*;
import client.managers.CommandInvoker;
import client.managers.InputOutput;
import client.managers.TCPClient;
import client.utilities.TicketCreator;
import lombok.Getter;
import lombok.Setter;

import java.io.*;

@Getter
public class Client {
    private final CommandInvoker commandInvoker = new CommandInvoker(this);
    private final TicketCreator ticketCreator = new TicketCreator(this);
    private final InputOutput inputOutput = new InputOutput();
    private final TCPClient tcpClient = new TCPClient(this);
    private boolean clientOn;
    @Setter
    private boolean withFile = false;

    public Client(BufferedReader readerConsole, ObjectInputStream readerPort, BufferedOutputStream writer ) {
        inputOutput.setReaderConsole(readerConsole);
        inputOutput.setReaderPort(readerPort);
        inputOutput.setWriter(writer);
    }


    public static void main(String[] args) {
        Client client = new Client(null, null, null);
        client.start();
    }

    public void stop() {
        clientOn = false;
        try {
            inputOutput.getReaderConsole().close();
            inputOutput.getReaderPort().close();
            inputOutput.getWriter().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize(){
        clientOn = true;
        tcpClient.openConnection();
    }

    public void start() {
        boolean isCommandWas = true;
        initialize();
        while (clientOn) {
            try {
                if (isCommandWas){
                    inputOutput.outPut("Введите комманду (для справки используйте комманду help) \n~ ");
                } else {
                    inputOutput.outPut("~ ");
                }
                String commandFromConsole = inputOutput.inPutConsole();
                if (commandFromConsole == null){
                    inputOutput.outPut("\nПолучен сигнал завершения работы.");
                    stop();
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

    public void start(File file) {
        try {
            FileReader f = new FileReader(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(f);
            inputOutput.setReaderConsole(br);
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



    private String invoke(String commandName) throws StopServerException {
        try {
            return commandInvoker.invoke(commandName);
        } catch (CommandValueException e) {
            throw new StopServerException("incorrect value of command: " + e.getMessage());
        } catch (NullPointerException ignored) {
            throw new StopServerException("incorrect command");
        } catch (CommandCollectionZeroException e) {
            throw new StopServerException("command is useless: " + e.getMessage());
        } catch (BadResponseException e) {
            throw new StopServerException("problem with answer from server: " + e.getMessage());
        }
    }
}
