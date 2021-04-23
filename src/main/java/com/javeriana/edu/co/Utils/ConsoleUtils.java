/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author prado
 */
public class ConsoleUtils {

    public void doMvnPackage(String name) {
        executeCommand(name, "mvn package");
    }

    public void executeCommand(String name, String command) {
        try {
            
            String osName = System.getProperty("os.name");
            String console = "";
            
            if(osName.toLowerCase().contains("windows"))
                console = "cmd /c ";
            
            String commandMvn = console + command;

            System.out.println("---------------------------------------------------");
            System.out.println("Executing command: \"" + command + "\" in: \"output" + File.separator + name + "\"");
            System.out.println("---------------------------------------------------");
            Process p = Runtime.getRuntime().exec(commandMvn, null, new File("output", name));
            printResults(p);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
