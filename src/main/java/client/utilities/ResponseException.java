package client.utilities;


import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
@Getter
public class ResponseException implements Serializable {
    private String name;
    private Exception exception ;
    public ResponseException(String name, Exception exception){
        this.name = name;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Response{" +
                "name='" + name + '\'' +
                ", answer='" + exception + '\'' +
                '}';
    }
}
