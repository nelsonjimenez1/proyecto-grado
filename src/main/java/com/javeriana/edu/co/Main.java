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
    public static String fileSeparator = File.separator;

    public static void main(String[] args) throws Exception {

        //cambiar ruta
        String borrame = "C:\\Users\\nelso\\Documents\\TG\\spring-petclinic-master"; //Nelson
        //String borrame = "C:\\\\Users\\\\PC\\\\Desktop\\\\spring-petclinic-master"; //Santos
        //String borrame = "C:\\Tools\\spring-petclinic-master"; //Gustavo
        //String borrame = "C:\\Users\\prado\\OneDrive\\Documentos\\TG\\spring-petclinic-master"; //Sebastián

        if (/*args.length > 0*/true) {

            Properties properties = new Properties();
            try {
                File f = new File(System.getProperty("user.dir") + fileSeparator + "configuracion.properties");
                properties.load(new FileInputStream(f));
                properties.setProperty("INPUTPATH", borrame);
                properties.store(new FileOutputStream(f), null);
                //if(args[0] != null)                
                //properties.setProperty("INPUTPATH", args[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Controller c = new Controller();
        System.out.println("ToyFuncionando guiño guiño");
    }
}
