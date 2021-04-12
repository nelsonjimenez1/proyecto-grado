/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 *
 * @author prado
 */
public class CmdUtils {        
    
    public void doMvnPackage(String name) {
        try {
            // TODO: Diferenciar windows o linux
            String commandMvn = "cmd /c mvn package";

            Process p = Runtime.getRuntime().exec(commandMvn, null, new File("output", name));
            p.waitFor();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            System.out.println("--------------Command Running: " + commandMvn + "------------------------------");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
