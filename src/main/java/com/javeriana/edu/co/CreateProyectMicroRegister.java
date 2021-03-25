/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import static com.javeriana.edu.co.CreateProyectMicroServices.fileSeparator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Gustavo Rivera
 */
public class CreateProyectMicroRegister {
    
    CreateProyectMicroRegister()
    {   
        copyFolder();  
    }
    private void copyFolder() {
      
        String[] split = {System.getProperty("user.dir"),"templates","microservices-register"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {System.getProperty("user.dir"),"output"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        copyAnotherDirectory(path,pathMicro);
    }
    
    
//    private ArrayList<File> listDirectory(String dirName) {
//        ArrayList<File> listFilesOrigin = new ArrayList<>();
//        File f = new File(dirName);
//        File[] listFiles = f.listFiles();
//        for (int i = 0; i < listFiles.length; i++) {
//            if (listFiles[i].isDirectory()) {
//                listFilesOrigin.add(listFiles[i]);
//            }
//        }
//        return listFilesOrigin;
//    }
    
    
    private void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProyectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
