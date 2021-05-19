
package com.javeriana.edu.co.Creation;

import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;
import org.apache.commons.io.FileUtils;

/**
 * This class creates the microservice register
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class CreateProjectMicroRegister {
    
    private FileUtilsProject utils;
    /**
     * Constructor
     */
    public CreateProjectMicroRegister()
    {   
        this.utils = new FileUtilsProject();
    }
    
    /**
     * Allows copying files and folders from the register template
     */
    public void copyFolder() {
      
        String[] split = {System.getProperty("user.dir"),"templates","microservices-register"};
        String path = String.join(File.separator, split);
        String[] splitMicro = {System.getProperty("user.dir"),"output"};
        String pathMicro = String.join(File.separator, splitMicro);
        this.utils.copyAnotherDirectory(path,pathMicro);
        System.out.println("--------------------------------------");
        System.out.println("microservices-register construction finished");
        System.out.println("--------------------------------------"); 
    }        
}
