package server.managers;

import commons.exceptions.BadResponseException;
import commons.exceptions.ResponseException;
import commons.utilities.Request;
import commons.utilities.Response;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class TCPServer {
    private static final int PORT = 7777;
    public static final Logger LOGGER = Logger.getLogger(TCPServer.class.getName());

    static {
        try {
            LogManager.getLogManager().reset();
            LOGGER.setLevel(Level.ALL);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            LOGGER.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка настройки логгера: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.info("Сервер запущен и ожидает подключения на порту " + PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info("Клиент подключился: " + clientSocket.getInetAddress());
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Ошибка при подключении клиента: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Ошибка запуска сервера: " + e.getMessage(), e);
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try(ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){

            for(;;) {
                try {
                    Object inputObject = in.readObject();
                    if (inputObject instanceof Request) {
                        Request request = (Request) inputObject;
                        Response response = new Response(request.getName(), request.getName() );
                        out.writeObject(response);
                        out.flush();
                        TCPServer.LOGGER.info("Выполнена команда: " + response.getName() + " от клиента " + clientSocket);
                    } else {
                        System.out.println(inputObject);
                        out.writeObject(new ResponseException("WRONG", new BadResponseException("Неверный запрос")));
                        TCPServer.LOGGER.warning("Получен неверный объект от клиента " + clientSocket);
                    }
                }catch (EOFException ignored){
                    break;
                }
            }
        } catch (EOFException e){
            e.printStackTrace();
        }
        catch (IOException | ClassNotFoundException  e) {
            TCPServer.LOGGER.log(Level.SEVERE, "Ошибка в обработке клиента: " + e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                TCPServer.LOGGER.log(Level.SEVERE, "Ошибка при закрытии соединения с клиентом: " + e.getMessage(), e);
            }
            TCPServer.LOGGER.info("Клиент отключился: " + clientSocket.getInetAddress());
        }
    }
}