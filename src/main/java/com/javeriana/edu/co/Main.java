package com.javeriana.edu.co;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.visitor.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.resolution.types.ResolvedType;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {        

        if (args.length > 0) {

            Properties properties = new Properties();
            try {
                File f = new File(System.getProperty("user.dir") + File.separator + "configuration.properties");
                properties.load(new FileInputStream(f));                
                if(args[0] != null && args[1] != null) {
                    properties.setProperty("INPUTPATH", args[0]);
                    properties.setProperty("GROUPID", args[1]);
                } else {
                    throw new Exception("No enough parameters, please give the 'input path' and the 'groupId'");
                }
                properties.store(new FileOutputStream(f), null);
                Controller c = new Controller();
                System.out.println("--------------------------------------");
                System.out.println("Transformation Finished");
                System.out.println("--------------------------------------");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("No enough parameters, please give the 'input path' and the 'groupId'");        
        }
        
    }
}
