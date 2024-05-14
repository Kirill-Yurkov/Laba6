package client.managers;

import client.Client;

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
    private BufferedReader in;
    private BufferedOutputStream out;

    public TCPClient(Client client){
        this.client = client;
    }
    public void openConnection() {
        if(checkConnection()){
            try {
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
                client.getInputOutput().setReaderConsole(consoleInput);
                client.getInputOutput().setReaderPort(in);
                client.getInputOutput().setWriter(out);
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.severe( "Ошибка при выполнении команды: " + e.getMessage());
            }
        }

    }
    private boolean checkConnection(){
        int attempts = 0;
        while (attempts < MAX_CONNECTION_ATTEMPTS) {
            try {
                socket = new Socket("localhost", 7777);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedOutputStream(socket.getOutputStream());
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
}
