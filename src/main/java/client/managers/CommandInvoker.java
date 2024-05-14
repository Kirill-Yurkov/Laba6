package client.managers;

import client.Client;
import client.commands.*;
import client.commands.interfaces.Command;
import client.exceptions.*;
import lombok.Getter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandInvoker {
    private final HashMap<String, Command> commands = new HashMap<>();
    @Getter
    private Client client;

    public CommandInvoker(Client client) {
        this.client = client;
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
            command.setClient(client);
            commands.put(command.getName(), command);
        }
    }

    public List<Command> getCommands() {
        return new ArrayList<>(this.commands.values());
    }
}

