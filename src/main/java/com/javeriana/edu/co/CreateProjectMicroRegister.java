
package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;


public class CreateProjectMicroRegister {
    
    private FileUtilsProject utils;
    
    public CreateProjectMicroRegister()
    {   
        this.utils = new FileUtilsProject();
        this.copyFolder();  
    }
    
    private void copyFolder() {
      
        String[] split = {System.getProperty("user.dir"),"templates","microservices-register"};
        String path = String.join(FileUtilsProject.FILE_SEPARATOR, split);
        String[] splitMicro = {System.getProperty("user.dir"),"output"};
        String pathMicro = String.join(FileUtilsProject.FILE_SEPARATOR, splitMicro);
        this.utils.copyAnotherDirectory(path,pathMicro);
    }
        
}
