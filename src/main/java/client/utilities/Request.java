package client.utilities;


import java.io.Serializable;
import java.util.ArrayList;

public class Request implements Serializable {
    private String name;
    private CommandValues commandValues;
    private ArrayList<Object> params;
    public Request(String name, CommandValues commandValues, ArrayList<Object> params){
        this.name = name;
        this.commandValues = commandValues;
        this.params = params;
    }
}
