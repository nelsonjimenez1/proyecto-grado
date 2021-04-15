package com.javeriana.edu.co;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class JavaGenerator {

    public String groupID;
    public Graph graph;
    public String rootInput;
    public FileUtilsProject fileUtilsProject;

    public JavaGenerator(Graph graph) {
        this.graph = graph;
        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir") + File.separator + "configuration.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rootInput = properties.getProperty("INPUTPATH");
            this.fileUtilsProject = new FileUtilsProject();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Modifier.Keyword[] getKeywords(NodeList<Modifier> modifiers) {
        Modifier.Keyword[] list = new Modifier.Keyword[modifiers.size()];
        int i = 0;
        for (Modifier modifier : modifiers) {
            list[i] = modifier.getKeyword();
            i++;
        }
        return list;
    }

    public String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    public String getPostParameterType(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getTypeAsString();
            break;
        }
        return string;
    }

    public String getPostParameter(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getNameAsString();
            break;
        }
        return string;
    }

    public String getReturnTypeClass(MethodDeclaration method) {

        String returnType = method.getTypeAsString();

        if (returnType.contains("<") && returnType.contains(">")) { 
            
            String split[] = returnType.split("<");
            String returnTypeAux = "";
            if(split.length == 2) {
                returnTypeAux = split[1].split(">")[0];
            }
            else if(split.length == 3) {
                returnTypeAux = split[2].split(">")[0];
            }

            if (returnType.contains("Collection") || returnType.contains("List"))  {
                returnTypeAux += "[]";
            } 
            returnType = returnTypeAux;
        }

        return returnType + ".class";
    }

    public String getStringGetParameters(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if (annotation.toString().contains("Param")) {
                    string += ", " + parameter.getNameAsString();
                }
            }
        }
        return string;
    }
}
