/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.io.Files;
import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author PC
 */
public class JavaGenerator {

    public String groupID;
    public String rootInput;
    public static String fileSeparator = File.separator;
    private Graph graph; // Modified
    
    public JavaGenerator(String rootInput, Graph graph) { // Modified

        this.rootInput = rootInput;
        this.graph = graph;
        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir") + fileSeparator +"configuracion.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rootInput = properties.getProperty("INPUTPATH");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRegiter(String nameMicroService, String rooteGroupID) {
        try {
            String[] splitRegistrationServer = {"templates","RegistrationServer.java"};
            String[] splitRegistrationServerWrite = {"output",nameMicroService,"src","main","java",rooteGroupID,"services","register","RegistrationServer.java"};
            String path = String.join(fileSeparator, splitRegistrationServer);
            String pathWriteFile = String.join(fileSeparator, splitRegistrationServerWrite);
            CompilationUnit cu = StaticJavaParser.parse(new File(System.getProperty("user.dir") , path));
            cu.setPackageDeclaration(this.groupID + ".services.register");
            FileWriter myWriter = new FileWriter(System.getProperty("user.dir")+ fileSeparator+ pathWriteFile);
            myWriter.write(cu.toString());
            myWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(JavaGenerator.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        } 
    }
    
    // New
    // No orientarlo al grafo
    public void createClass(String classId, Node classNode, ArrayList<Node> methods) {
        
        String[] split = {rootInput, "src", "main", "java"};
        String[] split2 = classNode.getPackageName().split("\\.");
        
        split = concatV(split, split2);        
        String[] split3 = {classNode.getName()};        
        split = concatV(split, split3);
        
        String path = String.join(fileSeparator, split);                
        
        try {
            CompilationUnit originalCu = StaticJavaParser.parse(new File(path));            
            CompilationUnit newCu = new CompilationUnit();
            
            originalCu.getImports().forEach(imp -> newCu.addImport(imp));            
            newCu.setPackageDeclaration(originalCu.getPackageDeclaration().get());            
            
            originalCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                
                if(declaration.getClass().getName().equals(classNode.getName())) {
                    ClassOrInterfaceDeclaration aux = newCu.addClass(declaration.getClass().getName());                    
                    addMethodsToClass(declaration, aux, classNode, methods);
                }                                                     
            });
            
        } catch (Exception e) {
            
        }
    }
    
    // New    
    private void addMethodsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, Node classNode, ArrayList<Node> methods) {
        
        originalClass.findAll(MethodDeclaration.class).forEach(n -> {
            // TODO
        });
        
        for (Node method : methods) {
            
        }
    }
    
    // New
    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, Node classNode) {
        ArrayList<Node> fields = this.graph.getNodeElementsSameMicroserviceBySrcNodeId("Has", classNode.getId(), classNode.getMicroservice());
        
        originalClass.findAll(FieldDeclaration.class).forEach(n -> {
            // TODO
        });
        
        for (Node field : fields) {
            
        }
    }
    
    private String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            //necesario para visitar todos los nodos hijos
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}
