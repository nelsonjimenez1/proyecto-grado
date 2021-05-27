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
 * This class allows you make calls by console
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class ConsoleUtils {

    /**
     * This method lets run the "mvn package" command
     * 
     * @param name: string with a name project.
     */
    public void doMvnPackage(String name) {
        executeCommand(name, "mvn package");
    }

    /**
     *
     * This method allows to run commands on the console
     * 
     * @param name: string with a name project.
     * @param command: instruction for call in console;
     */
    public void executeCommand(String name, String command) {
        try {

            String osName = System.getProperty("os.name");
            String console = "";

            if (osName.toLowerCase().contains("windows")) {
                console = "cmd /c ";
            }

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

    /**
     *
     * This method allows to print the process result of command executing
     * 
     * @param process: process result to print.
     */
    public void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    
    public String getRegisterIP() {
        try {
            
            String osName = System.getProperty("os.name");
            String console = "";
            
            if(osName.toLowerCase().contains("windows"))
                console = "cmd /c ";          
            
            String commandMvn = console + "docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' register";
            Process p = Runtime.getRuntime().exec(commandMvn, null, new File("output", "microservices-register"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            if ((line = reader.readLine()) != null) {
                return line;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; //null
    }
    
}
