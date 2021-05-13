/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Utils;

import com.github.javaparser.ast.CompilationUnit;
import com.javeriana.edu.co.Creation.CreateProjectMicroServices;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * This class allows you to manipulate the project files
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class FileUtilsProject {   

    /**
     * This method deletes the files from the ouput folder, in case the folder has them.
     */
    public void deleteOutput() {
        try {
            FileUtils.deleteDirectory(new File("output"));
        } catch (IOException ex) {
            Logger.getLogger(FileUtilsProject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * This method allows you to copy files or folders to another directory.
     * @param origin directory location from which the information will be copied
     * @param destiny directory location where the information will be pasted
     */
    public void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     /**
     * This method allows you to write the compilationUnit
     * @param cu directory location from which the information will be copied
     * @param path path  where the compilationUnit information will be written 
     */
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
