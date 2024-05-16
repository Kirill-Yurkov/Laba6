package client.managers;

import client.Client;
import client.exceptions.BadResponseException;
import client.utilities.Request;
import client.utilities.Response;
import client.utilities.ResponseException;
import client.utilities.Validator;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TCPClient {
    private final Client client;
    private static final int CONNECTION_RETRY_DELAY = 5000;
    private static final int MAX_CONNECTION_ATTEMPTS = 3;
    private static final Logger LOGGER = Logger.getLogger(TCPClient.class.getName());

    private Socket socket;
    private ObjectInputStream inPort;
    private BufferedOutputStream outConsole;
    private ObjectOutputStream outPort;

    public TCPClient(Client client){
        this.client = client;
    }
    public void openConnection() {
        if(checkConnection()){
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            client.getInputOutput().setReaderConsole(consoleInput);
            client.getInputOutput().setReaderPort(inPort);
            client.getInputOutput().setWriter(outConsole);
        } else{
            client.stop();
        }
    }
    private boolean checkConnection(){
        int attempts = 0;
        while (attempts < MAX_CONNECTION_ATTEMPTS) {
            try {
                socket = new Socket("localhost", 7777);
                inPort = new ObjectInputStream(socket.getInputStream());
                outPort = new ObjectOutputStream(socket.getOutputStream());
                LOGGER.info("Подключение к серверу выполнено. "+socket);
                return true;
            } catch (IOException e) {
                attempts++;
                LOGGER.warning("Не удалось подключиться к серверу. Повторная попытка через некоторое время...");
                try {
                    TimeUnit.MILLISECONDS.sleep(CONNECTION_RETRY_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        LOGGER.severe("Превышено количество попыток подключения. Завершение работы клиента.");
        return false;
    }

    public String getAnswer(Request request) throws BadResponseException {
        try {
            outPort.writeObject(request);
            outPort.flush();
            Object response = inPort.readObject();
            if (response instanceof Response ) {
                LOGGER.info("От сервера: " + response);
                return client.getCommandInvoker().invokeFromResponse((Response) response);
            } else if(response instanceof ResponseException) {
                LOGGER.info("Exception с сервера" + response);
                throw new BadResponseException(client.getCommandInvoker().invokeFromResponseException((ResponseException) response));
            }else {
                LOGGER.warning("Неверный формат ответа от сервера.");
                throw new BadResponseException("bad response");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new BadResponseException("bad gateway");
        }
    }
}
