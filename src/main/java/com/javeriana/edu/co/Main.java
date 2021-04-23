package com.javeriana.edu.co;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {        

        if (args.length > 0) {

            Properties properties = new Properties();
            try {
                File f = new File(System.getProperty("user.dir") + File.separator + "configuration.properties");
                properties.load(new FileInputStream(f));                
                if(args[0] != null && args[1] != null) {
                    properties.setProperty("INPUTPATH", args[0]);
                    properties.setProperty("GROUPID", args[1]);
                } else {
                    throw new Exception("No enough parameters, please give the 'input path' and the 'groupId'");
                }
                properties.store(new FileOutputStream(f), null);
                ParitionCotroller c = new ParitionCotroller(args[2]);
                System.out.println("--------------------------------------");
                System.out.println("Transformation Finished");
                System.out.println("--------------------------------------");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No enough parameters, please give the 'input path' and the 'groupId'");        
        }
        
    }
}
