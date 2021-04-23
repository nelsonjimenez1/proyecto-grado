/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Utils;

import com.github.javaparser.ast.CompilationUnit;
import com.javeriana.edu.co.CreateProjectMicroServices;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author prado
 */
public class FileUtilsProject {   

    public void deleteOutput() {
        try {
            FileUtils.deleteDirectory(new File("output"));
        } catch (IOException ex) {
            Logger.getLogger(FileUtilsProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveCompilationUnit(CompilationUnit cu, String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(cu.toString());
            myWriter.close();
        } catch (Exception e) {
            System.out.println("save: " + e.getMessage());
        }
    }        
    
}
