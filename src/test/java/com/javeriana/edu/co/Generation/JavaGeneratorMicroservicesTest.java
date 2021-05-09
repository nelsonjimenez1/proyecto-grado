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
public class JavaGeneratorMicroservicesTest {

    private JavaGeneratorMicroservices javaGeneratorMicroservices;

    public JavaGeneratorMicroservicesTest() {
        javaGeneratorMicroservices = new JavaGeneratorMicroservices();
    }

    @Test
    @DisplayName("")
    public void testGetMethodCallType() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "Repository.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String callType = javaGeneratorMicroservices.getMethodCallType(methods.get(0));
            boolean sw = false;
            if (callType.equals("GET")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    @DisplayName("duda")
    public void testGetStringUrlParameters() {
        String path = System.getProperty("user.dir") + File.separator + "tests" + File.separator + "Repository.java";
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(path));
            ClassOrInterfaceDeclaration classParent = cu.findAll(ClassOrInterfaceDeclaration.class).get(0);
            List<MethodDeclaration> methods = classParent.getMethods();
            String urlParameters = javaGeneratorMicroservices.getStringUrlParameters(methods.get(0));
            boolean sw = false;
            if (urlParameters.equals("?lastName={lastName}")) {
                sw = true;
            }
            assertEquals(true, sw);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorMicroservicesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
