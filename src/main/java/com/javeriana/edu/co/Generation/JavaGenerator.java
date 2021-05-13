package com.javeriana.edu.co.Generation;

import com.javeriana.edu.co.Graph.Graph;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class offers functions for manipulate java code elements.
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class JavaGenerator {

    public String groupID;
    public Graph graph;
    public String rootInput;
    public FileUtilsProject fileUtilsProject;

    public JavaGenerator() {
        this.fileUtilsProject = new FileUtilsProject();
    }
    
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

     /**
     * This method get the keys from a modifiers list.
     * 
     * @param modifiers modifiers list.
     * @return modifier keyword list.
     */
    public Modifier.Keyword[] getKeywords(NodeList<Modifier> modifiers) {
        Modifier.Keyword[] list = new Modifier.Keyword[modifiers.size()];
        int i = 0;
        for (Modifier modifier : modifiers) {
            list[i] = modifier.getKeyword();
            i++;
        }
        return list;
    }
     /**
     * This method concatenates two string Vectors 
     * 
     * @param left vector that makes up the concatenation left part.
     * @param rigth vector that makes up the concatenation right part.
     * @return String vector gererated.
     */
    public String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }
    
     /**
     * This method gets the parameter type of a function declaration and return it as a string
     * 
     * @param method metod declatation information
     * @return string with parameter type
     */
    public String getPostParameterType(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getTypeAsString();
            break;
        }
        return string;
    }

     /**
     * This method gets the parameter of a function declaration and return as a string
     * 
     * @param method metod declatation information
     * @return string with parameter name
     */
    public String getPostParameter(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getNameAsString();
            break;
        }
        return string;
    }

    /**
     * This method identifies the return type of a function declaration  and returns the return type class as a String
     * 
     * @param method metod declatation information
     * @return string with return type class
     */
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
    /**
     * This method identifies the return type of a function declaration  and returns the return type class
     * 
     * @param method metod declatation information
     * @return string with parameter name
     */
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
