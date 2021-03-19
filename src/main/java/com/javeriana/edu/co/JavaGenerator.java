/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
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
    public String routInput;
    public static String fileSeparator = File.separator;
    public JavaGenerator() {

        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir") + fileSeparator +"configuracion.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            routInput = properties.getProperty("INPUTPATH");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRegiter(String nameMicroService, String routeGroupID) {
        try {
            String[] splitRegistrationServer = {"templates","RegistrationServer.java"};
            String[] splitRegistrationServerWrite = {"output",nameMicroService,"src","main","java",routeGroupID,"services","register","RegistrationServer.java"};
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

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            //necesario para visitar todos los nodos hijos
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}
