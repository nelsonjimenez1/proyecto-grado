/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.TypeParameter;
import static com.javeriana.edu.co.CreateProjectMicroServices.fileSeparator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.BlockStatement;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author 
 */
public class CreateProjectMicroWeb {
    
    public Graph graph;
    public static String fileSeparator = File.separator;
    public List<String> microNames;

    public CreateProjectMicroWeb(Graph graph) {
        this.graph = graph;
        microNames = new ArrayList<>();
        copyFolder(); 
        generateServices();
    }
    
     private void copyFolder() {
        String[] split = {System.getProperty("user.dir"),"templates","microservices-web"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {System.getProperty("user.dir"),"output"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        copyAnotherDirectory(path,pathMicro);
    }
    
    
    
    private void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Un servicio por cada  microservicio
     */
    private void generateServices(){
        
         HashMap<String, ArrayList<Vertex>> controllers = graph.getControllers();      
         String[] split = {"output","microservices-web", "src","main","java","io","pivotal","microservices","services","web"};
         String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
         String pathComplete = pathGeneric + "WebService.java";
         
         for (String nameMicroService : controllers.keySet()) {
             
            String nameMicroserviceFirstUpperCase = nameMicroService.substring(0, 1).toUpperCase() + nameMicroService.substring(1, nameMicroService.length());
             try {
                 CompilationUnit newCuWebService =  StaticJavaParser.parse(new File(pathComplete));
                 System.out.println(pathComplete);
                 newCuWebService.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                     declaration.setName("Web"+nameMicroserviceFirstUpperCase+"Service");
                     declaration.getConstructors().forEach(contructor ->
                             contructor.setName("Web"+nameMicroserviceFirstUpperCase+"Service"));
                     addMethods( declaration, controllers.get(nameMicroService));
                     System.out.println("Controller "+nameMicroService);
                 });       
                 saveMicroservice(newCuWebService, pathGeneric + "Web"+nameMicroserviceFirstUpperCase+"Service.java");
             } catch (FileNotFoundException ex) {
                 Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }
    
    private void saveMicroservice(CompilationUnit cu, String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(cu.toString());
            myWriter.close();
        } catch(Exception e) {
            System.out.println("createClass: "+ e.getMessage());
        }  
    }
    
    // Recorre los controladores de un microservicio, y recorre los metodos de cada uno. Se llama a la función createMethod para agregarle el metodo al WebService 
    private void addMethods(ClassOrInterfaceDeclaration classWebService, List<Vertex> controllers ){
        
 
        for (Vertex controller : controllers) {
            try {
                String[] packageController = controller.getPackageName().split("\\.");
                
                String[] split = {"output",controller.getMicroservice(), "src","main","java"};
                String[] pathNew = concatV(split,packageController);
                String pathGeneric = String.join(fileSeparator, pathNew) + fileSeparator + controller.getName() +".java";
                CompilationUnit cuController =  StaticJavaParser.parse(new File(pathGeneric));
                System.out.println("Metodos" + controller.getName());
                cuController.findAll(MethodDeclaration.class).forEach(oldMethod -> {
                    MethodDeclaration newMethod = classWebService.addMethod(oldMethod.getName().toString(), getKeywords(oldMethod.getModifiers()));
                    createMethod(oldMethod, newMethod, cuController);
                });
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    
    private void createMethod(MethodDeclaration oldMethod, MethodDeclaration newMethod, CompilationUnit cuController) {
        newMethod.setType(getReturnTypeMethod(oldMethod));
        
        setParameters(oldMethod, newMethod);
        
        BlockStmt tryStatement = new BlockStmt();
        
        NodeList<CatchClause> catchClauses = new NodeList<>();
        CatchClause catchStmt = new CatchClause();
        catchStmt.setParameter(new Parameter(new TypeParameter("Exception"), "e")).setBody(new BlockStmt().addStatement(StaticJavaParser.parseStatement("System.out.println(e.getMessage());")));
        catchClauses.add(catchStmt);
        
        oldMethod.getAnnotations().forEach(annotation -> {
            if(annotation.getName().toString().contains("Get")) {
                
                String controllerUrl = getUrlController(cuController);
                String url = controllerUrl + annotation.toString().split("value")[1].split("\"")[1];
                String returnType = getReturnTypeClass(oldMethod);
                String parameters = getStringGetParameters(oldMethod);
                
                String returnStmt = "restTemplate.getForObject(serviceUrl + \"" + url + "\", " + returnType + parameters + ")";
                
                if(returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                }
                
                tryStatement.addStatement(new ReturnStmt(returnStmt));                 
                
            } else if (annotation.getName().toString().contains("Post")) {
                
                String controllerUrl = getUrlController(cuController);
                String url = controllerUrl + annotation.toString().split("value")[1].split("\"")[1];
                String returnType = getReturnTypeClass(oldMethod);                
                String parameters = getStringGetParameters(oldMethod);
                String postParameter = getPostParameter(oldMethod);
                String postParameterType = getPostParameterType(oldMethod);
                
                String header1 = "HttpHeaders headers = new HttpHeaders();";
                String header2 = "headers.setContentType(MediaType.APPLICATION_JSON);";
                String header3 = "HttpEntity<" + postParameterType + "> request = new HttpEntity<>(" + postParameter + ", headers);";
                                
                String returnStmt = "restTemplate.postForObject(serviceUrl + \"" + url + "\", request, " + returnType + parameters + ")";               
               
                if(returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                }
                
                tryStatement.addStatement(StaticJavaParser.parseStatement(header1));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header2));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header3));
                tryStatement.addStatement(new ReturnStmt(returnStmt));  
                
            }
        });       
        newMethod.setBody(new BlockStmt().addStatement(new TryStmt().setTryBlock(tryStatement).setCatchClauses(catchClauses)));
        System.out.println("METHOD: "+newMethod);
    }
    
    private String getPostParameterType(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if(annotation.toString().contains("RequestBody")) {
                    string = parameter.getTypeAsString();
                }
            }
        }
        return string;
    }
    
    private String getPostParameter(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if(annotation.toString().contains("RequestBody")) {
                    string = parameter.getNameAsString();
                }
            }
        }
        return string;
    }
    
    private void setParameters(MethodDeclaration oldMethod, MethodDeclaration newMethod) {        
        for (Parameter parameter : oldMethod.getParameters()) {           
           Parameter aux = parameter.clone();
           aux.getAnnotations().clear();
           newMethod.addParameter(aux);
        }                          
    }
    
    private String getStringGetParameters(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if(annotation.toString().contains("PathVariable")) {
                    string += ", " + parameter.getNameAsString();
                }
            }
        }
        return string;
    }
    
    private String getReturnTypeMethod (MethodDeclaration method) {
        String returnType = method.getTypeAsString();        
        if(returnType.contains("ResponseEntity")){
            returnType = returnType.substring(15, returnType.length()-1);            
        }        
        return returnType;
    }
    
    private String getReturnTypeClass (MethodDeclaration method) {
        
        String returnType = getReturnTypeMethod(method);
        
        if(returnType.contains("<") && returnType.contains(">")) { // TODO: Preguntar al profe: Collection<Collection<Entity>> ???
            returnType = returnType.split("<")[1].split(">")[0];
            returnType += "[]";
        }
        
        return returnType + ".class";
    }
    
    // TODO: Mejorar la forma en la que se obtienen la url
    private String getUrlController(CompilationUnit cu) {
        String url = "";
        for (AnnotationExpr annotation : cu.findAll(ClassOrInterfaceDeclaration.class).get(0).getAnnotations()) {
            if(annotation instanceof SingleMemberAnnotationExpr ) {
                //System.out.println("VALUE: " +((SingleMemberAnnotationExpr) annotation).getMemberValue());
                url = annotation.toString().split("\"")[1];
            }
        }
        return url;
    }
    
    private ArrayList<File> listDirectory(String dirName) {
        ArrayList<File> listFilesOrigin = new ArrayList<>();
        File f = new File(dirName);
        File[] listFiles = f.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                listFilesOrigin.add(listFiles[i]);
            }
        }
        return listFilesOrigin;
    }
    private String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }
    
}