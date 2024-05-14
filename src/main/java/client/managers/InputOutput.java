package client.managers;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class InputOutput {
    @Getter
    private BufferedReader readerConsole;
    @Getter
    private BufferedOutputStream writer;
    @Getter
    private BufferedReader readerPort;
    private String lastOut = "";

    public void setReaderPort(BufferedReader readerPort) {
        try {
            this.readerPort.close();
        } catch (IOException ignored) {}
        this.readerPort = readerPort;
    }
    public void setReaderConsole(BufferedReader readerConsole) {
        try {
            this.readerConsole.close();
        } catch (IOException ignored) {}
        this.readerConsole = readerConsole;
    }
    public void setWriter(BufferedOutputStream writer) {
        try{
            this.writer.close();
        } catch (IOException ignored){}
        this.writer = writer;
    }

    public String inPutConsole() {
        try {
            return readerConsole.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String inPutPort(){
        try{
            return  readerPort.readLine();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void outPut(String text) {
        try {
            if(!lastOut.equals("\n") || !text.equals("\n")){
                writer.write(text.getBytes());
                writer.flush();
                lastOut = text;
            }
        } catch (IOException ignored) {
        }
    }
}