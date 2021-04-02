/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
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
            //test(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateRegister(String nameMicroService, String rooteGroupID) {
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
    
    
    public void test(String nodeId) {
        
        Vertex classNode = new Vertex();
        classNode.setName("Pet");
        /*
        String[] split = {rootInput, "src", "main", "java"};
        String[] split2 = classNode.getPackageName().split("\\.");        
        split = concatV(split, split2);
        String[] split3 = {classNode.getName()};    
        split = concatV(split, split3);
        
        String path = String.join(fileSeparator, split);*/
        System.out.println("Iniciando test");
        String path = "C:\\Users\\nelso\\Documents\\TG\\spring-petclinic-master\\src\\main\\java\\org\\springframework\\samples\\petclinic\\owner\\Pet.java";
        
        try {
            CompilationUnit originalCu = StaticJavaParser.parse(new File(path));
            //createClass(originalCu, classNode, null, null);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    // New
    // No orientarlo al grafo 
    public void createClass(CompilationUnit originalCu, Vertex classNode, ArrayList<Vertex> methods, ArrayList<Vertex> fields) {
        try {            
            System.out.println("createClass");
            CompilationUnit newCu = new CompilationUnit();         
            
            originalCu.getImports().forEach(imp -> newCu.addImport(imp));            
            newCu.setPackageDeclaration(originalCu.getPackageDeclaration().get());
            
            originalCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                if(declaration.getName().asString().equals(classNode.getName().trim())) {
                    ClassOrInterfaceDeclaration aux = newCu.addClass(declaration.getName().toString(), getKeywords(declaration.getModifiers()));
                    addConstructors(declaration, aux);
                    addComplemets(declaration, aux);
                    addAnnotations(declaration, aux);
                    addFieldsToClass(declaration, aux, fields);
                    addMethodsToClass(declaration, aux, classNode, methods);                    
                    try {
                        FileWriter myWriter = new FileWriter("Pet.java");
                        myWriter.write(newCu.toString());
                        myWriter.close();
                    } catch(Exception e) {
                        System.out.println("createClass: "+ e.getMessage());
                    }                    
                }                                                     
            });
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void createClass(CompilationUnit originalCu, Vertex classNode, ArrayList<Vertex> methods, String rootDest) {
        try {            
            System.out.println("createClass");
            CompilationUnit newCu = new CompilationUnit();         
            
            originalCu.getImports().forEach(imp -> newCu.addImport(imp));            
            newCu.setPackageDeclaration(originalCu.getPackageDeclaration().get());
            
            originalCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                if(declaration.getName().asString().equals(classNode.getName().trim())) {
                    ClassOrInterfaceDeclaration aux = newCu.addClass(declaration.getName().toString(), getKeywords(declaration.getModifiers()));
                    addConstructors(declaration, aux);
                    addComplemets(declaration, aux);
                    addAnnotations(declaration, aux);
                    addFieldsToClass(declaration, aux);
                    addMethodsToClass(declaration, aux, classNode, methods); 
                    System.out.println("Ruta " + rootDest);
                    try {
                        FileWriter myWriter = new FileWriter(rootDest);
                        myWriter.write(newCu.toString());
                        myWriter.close();
                    } catch(Exception e) {
                        System.out.println("createClass: "+ e.getMessage());
                    }                    
                }                                                     
            });
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    // TODO: Modificar Constructor para que solo inicialice y reciba los atributos que son indicados en el grafo? -> Caso de generar nosotros constructores
    // TODO: Filtrar por lista de constructores? -> No generar xD
    public void addConstructors(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) { 
        ConstructorDeclaration newConstructor = newClass.addConstructor(getKeywords(originalClass.getModifiers()));
        originalClass.getConstructors().forEach(constructor -> {
            newConstructor.setParameters(constructor.getParameters());
            newConstructor.setBody(constructor.getBody());
            newConstructor.setAnnotations(constructor.getAnnotations());
            if(constructor.getComment().isPresent())
                newConstructor.setComment(constructor.getComment().get());
        });
    }
    
    public void addAnnotations(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        newClass.setAnnotations(originalClass.getAnnotations());
        if(originalClass.getComment().isPresent())
                newClass.setComment(originalClass.getComment().get());
    }
    
    public void addComplemets(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        newClass.setExtendedTypes(originalClass.getExtendedTypes());
        newClass.setImplementedTypes(originalClass.getImplementedTypes());
    }
        
    public Keyword[] getKeywords(NodeList<Modifier> modifiers) {
        Keyword[] list = new Keyword[modifiers.size()];
        int i = 0;
        for (Modifier modifier : modifiers) {
            list[i] = modifier.getKeyword();
            i++;
        }
        return list;
    }
    
    // New
    // TODO1: Filtrar por una lista de metodos
    private void addMethodsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, Vertex classNode, ArrayList<Vertex> methods) {
        System.out.println("methods");
        originalClass.findAll(MethodDeclaration.class).forEach(method -> {
            
            System.out.println("MethodDeclaration name: " + method.getDeclarationAsString());
            if(methodValidate(methods, method)) {
                //TODO: VALIDAR si metodo hace llamada a otro metodo fuera del microservicio
                MethodDeclaration aux = newClass.addMethod(method.getName().toString(), getKeywords(method.getModifiers()));
                aux.setType(method.getType());
                aux.setThrownExceptions(method.getThrownExceptions());
                method.getParameters().forEach(parameter -> aux.addParameter(parameter));
                aux.setBody(method.getBody().get());
                aux.setAnnotations(method.getAnnotations());
                if(method.getComment().isPresent())
                    aux.setComment(method.getComment().get());
            }            
        });
    }
    
    // New
    // TODO: Filtrar por una lista de fields
    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, ArrayList<Vertex> fieldsNodes) {
        System.out.println("fields");
        originalClass.findAll(FieldDeclaration.class).forEach(field -> {
            // TODO: Fields with Initializer?
            System.out.println("FieldDeclaration name: " + field.getVariables().get(0).getName());
            if(fieldsContains(fieldsNodes, field)) {
                FieldDeclaration aux = newClass.addField(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), getKeywords(field.getModifiers()));
                aux.setAnnotations(field.getAnnotations());
                if(field.getComment().isPresent())
                    aux.setComment(field.getComment().get());
            }            
        });
    }
    
    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        System.out.println("fields");
        originalClass.findAll(FieldDeclaration.class).forEach(field -> {
            // TODO: Fields with Initializer?
            FieldDeclaration aux = newClass.addField(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), getKeywords(field.getModifiers()));
            aux.setAnnotations(field.getAnnotations());

            if(field.getComment().isPresent())
                aux.setComment(field.getComment().get());                  
        });
    }
    private boolean fieldsContains(ArrayList<Vertex> fields, FieldDeclaration field) {
        boolean result = false;
        /*for(Node f: fields) {
            if(f.getName().equals(field.getVariables().get(0).getName())) {
                result = true;
                break;
            }
        }*/
        return true;
    }
    
    // TODO: Validar cant. Parametros y tipo de cada uno
    private boolean methodValidate(ArrayList<Vertex> methods, MethodDeclaration method) {
        //ResolvedMethodDeclaration rmd = method.resolve();
        //System.out.println("Signature: " + rmd.getQualifiedSignature());  
        String[] splitSignature ;
        String[] splitVertex;
        String[] parameterSignature;
        String[] parameterVertex;
        String nameVertex;
        splitSignature = method.getSignature().asString().split("\\(|\\)");
        if(splitSignature.length > 1){
            parameterSignature = splitSignature[1].split(",");
            System.out.println("Split Signature "+ parameterSignature[0]);
        }else{
            parameterSignature = null; 
        }            
        ArrayList<Boolean> flags = new ArrayList<>(); 
        for(Vertex m: methods) {
            splitVertex = m.getName().split("\\(|\\)"); 
            nameVertex = splitVertex[0].split("\\.")[1];
            if(splitVertex.length >1 )
                parameterVertex = splitVertex[1].split(",");
            else
                parameterVertex = null;
            boolean result = true;
            if(nameVertex.equals(splitSignature[0])){
                if(parameterSignature != null && parameterVertex!= null){
                    if(parameterVertex.length == parameterSignature.length){
                        for (int i = 0; i < parameterVertex.length; i++) {
                            String[] splitAux = parameterVertex[i].split("\\.");
                            String parameterType = splitAux[splitAux.length-1].trim();
                            if(!parameterType.equals(parameterSignature[i].trim())){
                                result = false; 
                                break; 
                            } 
                        }
                    }
                } else{
                    if((parameterVertex != null && parameterSignature == null) ||(parameterVertex == null && parameterSignature != null) ){
                        result = false;
                    }
                }
            } else{
                result = false;
            }
            flags.add(result);  
         }
        System.out.println("RETORNO: " +flags.contains(true));
        return flags.contains(true);
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
