/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import static com.javeriana.edu.co.CreateProjectMicroServices.fileSeparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author 
 */
public class CreateProjectMicroWeb {
    
    public Graph graph;
    public static String fileSeparator = File.separator;
    public List<String> microNames;

    public CreateProjectMicroWeb(Graph graph) {
        this.graph = graph;
        microNames = new ArrayList<>();
        copyFolder(); 
        generateServices();
    }
    
     private void copyFolder() {
        String[] split = {System.getProperty("user.dir"),"templates","microservices-web"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {System.getProperty("user.dir"),"output"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        copyAnotherDirectory(path,pathMicro);
    }
    
    
    
    private void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Un servicio por cada  microservicio
     */
    private void generateServices(){
        
         HashMap<String, ArrayList<Vertex>> controllers = graph.getControllers();      
         String[] split = {"output","microservices-web", "src","main","java","io","pivotal","microservices","services","web"};
         String pathGeneric = String.join(fileSeparator, split) + fileSeparator + "WebService.java";
         
         for (String nameMicroService : controllers.keySet()) {
            
             try {
                 CompilationUnit newCu =  StaticJavaParser.parse(new File(pathGeneric));;
                 System.out.println(pathGeneric);
                 newCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                     declaration.setName("Web"+nameMicroService+"Service");
                     declaration.getConstructors().forEach(contructor ->
                             contructor.setName("Web"+nameMicroService+"Service"));
                     addMethods( declaration, controllers.get(nameMicroService));
                     System.out.println("Controller "+nameMicroService);
                 });
             } catch (FileNotFoundException ex) {
                 Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
          
         
    }
    private void addMethods(ClassOrInterfaceDeclaration classOrInterface,List<Vertex> controllers ){
        
 
        for (Vertex controller : controllers) {
            try {
                String[] packageController = controller.getPackageName().split("\\.");
                
                String[] split = {"output",controller.getMicroservice(), "src","main","java"};
                String[] pathNew = concatV(split,packageController);
                String pathGeneric = String.join(fileSeparator, pathNew) + fileSeparator + controller.getName() +".java";
                CompilationUnit newCu =  StaticJavaParser.parse(new File(pathGeneric));
                System.out.println("Metodos" + controller.getName());
                newCu.findAll(MethodDeclaration.class).forEach( m -> System.out.println(m.toString()));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private ArrayList<File> listDirectory(String dirName) {
        ArrayList<File> listFilesOrigin = new ArrayList<>();
        File f = new File(dirName);
        File[] listFiles = f.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                listFilesOrigin.add(listFiles[i]);
            }
        }
        return listFilesOrigin;
    }
    private String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }
    
}
