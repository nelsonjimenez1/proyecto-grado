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
public class JavaGenerador {

    public String groupID;
    public String rutaInput;

    public JavaGenerador() {

        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir") + "\\configuracion.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rutaInput = properties.getProperty("INPUTPATH");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void modificarRegiter(String nombreMicroServicio, String ruta) {
        try {
            
            CompilationUnit cu = StaticJavaParser.parse(new File(System.getProperty("user.dir") + "\\templates\\RegistrationServer.java"));
            cu.setPackageDeclaration(this.groupID + ".services.registration");
            FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "\\output\\" + nombreMicroServicio + "\\src\\main\\java" + ruta+ "\\services\\register\\RegistrationServer.java");
            myWriter.write(cu.toString());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGenerador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JavaGenerador.class.getName()).log(Level.SEVERE, null, ex);
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
