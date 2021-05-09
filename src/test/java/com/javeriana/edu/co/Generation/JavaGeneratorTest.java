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
public class JavaGeneratorTest {
    private JavaGenerator javaGenerator;

    public JavaGeneratorTest() {
        javaGenerator = new JavaGenerator();
    }
    
    @Test
    @DisplayName("CP10")
    public void testConcatV() {
        String[] array1 = {"left", "left"};
        String[] array2 = {"rigth", "right"};
        String[] concat = javaGenerator.concatV(array1, array2);
        boolean sw = true;
        for (int i = 0; i < concat.length; i++) {
            if (i < 2) {
                if (!array1[i].equals(concat[i])) {
                    sw = false;
                }
            } else {
                if (!array2[i-2].equals(concat[i])) {
                    sw = false;
                }
            }

        }

        assertEquals(true, sw);
    }
    
    @Test
    @DisplayName("CP11")
    public void testGetPostParameterType() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "Repository.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String postParameterType = javaGenerator.getPostParameterType(methods.get(1));
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
    @DisplayName("CP12")
    public void testGetPostParameter() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "Repository.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String postParameter = javaGenerator.getPostParameter(methods.get(1));
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
    @DisplayName("CP13")
    public void testGetReturnTypeClass() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "TestMethod.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String returnTypeClass = javaGenerator.getReturnTypeClass(methods.get(1));
            System.out.println(returnTypeClass);
            boolean sw = false;
            if (returnTypeClass.equals("Owner[].class")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    @DisplayName("CP14")
    public void testGetStringGetParameters() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "Repository.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String getParameters = javaGenerator.getStringGetParameters(methods.get(2));
            boolean sw = false;
            if (getParameters.equals(", id")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
