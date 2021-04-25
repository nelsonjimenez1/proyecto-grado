
package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;


public class CreateProjectMicroRegister {
    
    private FileUtilsProject utils;
    
    public CreateProjectMicroRegister()
    {   
        this.utils = new FileUtilsProject();
        this.copyFolder();  
    }
    
    private void copyFolder() {
      
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
