package org.example;

import server.Server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(new BufferedReader(new InputStreamReader(System.in)), new BufferedOutputStream(System.out));
        if(args.length==1){
            server.getFileManager().initializeFile(args[0]);
        }
        server.start();
    }
}
