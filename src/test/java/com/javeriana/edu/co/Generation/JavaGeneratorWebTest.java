/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Generation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nelso
 */
public class JavaGeneratorWebTest {

    private JavaGeneratorWeb javaGeneratorWeb;

    public JavaGeneratorWebTest() {
        javaGeneratorWeb = new JavaGeneratorWeb();
    }

    @Test
    @DisplayName("")
    public void testGetPostParameterType() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String postParameterType = javaGeneratorWeb.getPostParameterType(methods.get(1));
            boolean sw = false;
            if (postParameterType.equals("Owner")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("")
    public void testGetPostParameter() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String postParameter = javaGeneratorWeb.getPostParameter(methods.get(1));
            boolean sw = false;
            if (postParameter.equals("owner")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("")
    public void testGetStringGetParameters() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String getParameters = javaGeneratorWeb.getStringGetParameters(methods.get(2));
            boolean sw = false;
            if (getParameters.equals(", ownerId")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("")
    public void testGetReturnTypeMethod() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String getReturnType = javaGeneratorWeb.getReturnTypeMethod(methods.get(3));
            boolean sw = false;
            if (getReturnType.equals("Owner")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("duda")
    public void testGetStringParametersController() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            String parametersController = javaGeneratorWeb.getUrlController(cu);
            System.out.println(parametersController);
            boolean sw = false;
            if (parametersController.equals("\"/api\"")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
