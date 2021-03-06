package com.javeriana.edu.co;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * This class has the run file of this tool, taking into account the input
 * parameters
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class Main {

    /**
     * This method get the imputpath and the group id of the monolith and create
     * the controller that starts the microservices generation
     * 
     * @param args: array containing the input parameters
     */
    public static void main(String[] args) {

        if (args.length > 0) {

            Properties properties = new Properties();
            try {
                File f = new File(System.getProperty("user.dir") + File.separator + "configuration.properties");
                properties.load(new FileInputStream(f));
                if (args[0] != null && args[1] != null && args[2] != null) {
                    properties.setProperty("INPUTPATH", args[0]);
                    properties.setProperty("GROUPID", args[1]);
                } else {
                    throw new Exception("No enough parameters, please give the 'input path', 'groupId' and 'port'");
                }
                properties.store(new FileOutputStream(f), null);
                if (Integer.parseInt(args[2]) > 2222) {
                    PartitionCotroller c = new PartitionCotroller(args[2]);
                    System.out.println("--------------------------------------");
                    System.out.println("Transformation Finished");
                    System.out.println("--------------------------------------");
                } else {
                    throw new Exception("Port parameter must be greater than 2222");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No enough parameters, please give the 'input path' and the 'groupId'");
        }
    }
}
