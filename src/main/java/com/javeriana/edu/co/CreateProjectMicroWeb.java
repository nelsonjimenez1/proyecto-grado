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
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import static com.javeriana.edu.co.CreateProjectMicroServices.fileSeparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
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
        generateControllers();
        moveEntities();
    }

    private void copyFolder() {
        String[] split = {System.getProperty("user.dir"), "templates", "microservices-web"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {System.getProperty("user.dir"), "output"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        copyAnotherDirectory(path, pathMicro);
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
     * Un servicio por cada microservicio
     */
    private void generateServices() {
        HashMap<String, ArrayList<Vertex>> controllers = graph.getControllers();
        String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web"};
        String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
        String pathComplete = pathGeneric + "WebService.java";

        for (String nameMicroService : controllers.keySet()) {

            String nameMicroserviceFirstUpperCase = nameMicroService.substring(0, 1).toUpperCase() + nameMicroService.substring(1, nameMicroService.length());
            try {
                CompilationUnit newCuWebService = StaticJavaParser.parse(new File(pathComplete));
                System.out.println(pathComplete);
                newCuWebService.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                    declaration.setName("Web" + nameMicroserviceFirstUpperCase + "Service");
                    declaration.getConstructors().forEach(contructor
                            -> contructor.setName("Web" + nameMicroserviceFirstUpperCase + "Service"));
                    addMethodsServices(declaration, controllers.get(nameMicroService));

                    System.out.println("Controller " + nameMicroService);
                });
                saveMicroservice(newCuWebService, pathGeneric + "Web" + nameMicroserviceFirstUpperCase + "Service.java");
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
        } catch (Exception e) {
            System.out.println("createClass: " + e.getMessage());
        }
    }

    // Recorre los controladores de un microservicio, y recorre los metodos de cada uno. Se llama a la función createMethod para agregarle el metodo al WebService 
    private void addMethodsServices(ClassOrInterfaceDeclaration classWebService, List<Vertex> controllers) {
        for (Vertex controller : controllers) {
            try {
                String[] packageController = controller.getPackageName().split("\\.");

                String[] split = {"output", controller.getMicroservice(), "src", "main", "java"};
                String[] pathNew = concatV(split, packageController);
                String pathGeneric = String.join(fileSeparator, pathNew) + fileSeparator + controller.getName() + ".java";
                CompilationUnit cuController = StaticJavaParser.parse(new File(pathGeneric));
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
            if (annotation.getName().toString().contains("Get")) {

                String controllerUrl = getUrlController(cuController);
                String url = controllerUrl + annotation.toString().split("value")[1].split("\"")[1];
                String returnType = getReturnTypeClass(oldMethod);
                String parameters = getStringGetParameters(oldMethod);

                String returnStmt = "restTemplate.getForObject(serviceUrl + \"" + url + "\", " + returnType + parameters + ")";

                if (returnType.contains("[]")) {
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

                if (returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                }

                tryStatement.addStatement(StaticJavaParser.parseStatement(header1));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header2));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header3));
                tryStatement.addStatement(new ReturnStmt(returnStmt));

            }
        });
        newMethod.setBody(new BlockStmt().addStatement(new TryStmt().setTryBlock(tryStatement).setCatchClauses(catchClauses)));
        System.out.println("METHOD: " + newMethod);
    }

    private String getPostParameterType(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if (annotation.toString().contains("RequestBody")) {
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
                if (annotation.toString().contains("RequestBody")) {
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
                if (annotation.toString().contains("PathVariable")) {
                    string += ", " + parameter.getNameAsString();
                }
            }
        }
        return string;
    }

    private String getReturnTypeMethod(MethodDeclaration method) {
        String returnType = method.getTypeAsString();
        if (returnType.contains("ResponseEntity")) {
            returnType = returnType.substring(15, returnType.length() - 1);
        }
        return returnType;
    }

    private String getReturnTypeClass(MethodDeclaration method) {

        String returnType = getReturnTypeMethod(method);

        if (returnType.contains("<") && returnType.contains(">")) { // TODO: Preguntar al profe: Collection<Collection<Entity>> ???
            returnType = returnType.split("<")[1].split(">")[0];
            returnType += "[]";
        }

        return returnType + ".class";
    }

    private String getUrlController(CompilationUnit cu) {
        String url = "";
        for (AnnotationExpr annotation : cu.findAll(ClassOrInterfaceDeclaration.class).get(0).getAnnotations()) {
            if (annotation.toString().contains("RequestMapping")) {
                if (annotation instanceof NormalAnnotationExpr) {

                    NormalAnnotationExpr annotationNormal = (NormalAnnotationExpr) annotation;
                    NodeList<MemberValuePair> pairs = annotationNormal.getPairs();
                    for (MemberValuePair pair : pairs) {
                        if (pair.getName().toString().equalsIgnoreCase("Value")) {
                            url = pair.getValue().toString();
                        }
                    }
                } else if (annotation instanceof SingleMemberAnnotationExpr) {
                    url = annotation.toString().split("\"")[1];
                }
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

    private void generateControllers() {
        HashMap<String, ArrayList<Vertex>> controllers = graph.getControllers();
        String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web"};
        String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
        String pathComplete = pathGeneric + "WebController.java";

        for (String nameMicroService : controllers.keySet()) {

            String nameControllerFirstUpperCase = nameMicroService.substring(0, 1).toUpperCase() + nameMicroService.substring(1, nameMicroService.length());
            try {
                CompilationUnit newCuWebController = StaticJavaParser.parse(new File(pathComplete));
                newCuWebController.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                    declaration.setName("Web" + nameControllerFirstUpperCase + "Controller");
                    declaration.getConstructors().forEach(constructor -> {
                        constructor.setName("Web" + nameControllerFirstUpperCase + "Controller");
                        constructor.getParameter(0).setType("Web" + nameControllerFirstUpperCase + "Service");
                    });
                    declaration.getFields().forEach(field -> {
                        field.setAllTypes(new TypeParameter("Web" + nameControllerFirstUpperCase + "Service"));
                    });

                    addMethodsControllers(declaration, controllers.get(nameMicroService));
                    System.out.println("Controller " + nameMicroService);
                });
                saveMicroservice(newCuWebController, pathGeneric + "Web" + nameControllerFirstUpperCase + "Controller.java");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
            modifyWebServer(nameMicroService);
        }
        deleteOldMethodsWebServer();
        deleteOldTemplates();
    }

    private void addMethodsControllers(ClassOrInterfaceDeclaration classWebController, ArrayList<Vertex> controllers) {
        for (Vertex controller : controllers) {
            try {
                String[] packageController = controller.getPackageName().split("\\.");

                String[] split = {"output", controller.getMicroservice(), "src", "main", "java"};
                String[] pathNew = concatV(split, packageController);
                String pathGeneric = String.join(fileSeparator, pathNew) + fileSeparator + controller.getName() + ".java";
                CompilationUnit cuController = StaticJavaParser.parse(new File(pathGeneric));
                System.out.println("Metodos" + controller.getName());
                cuController.findAll(MethodDeclaration.class).forEach(oldMethod -> {
                    MethodDeclaration newMethod = classWebController.addMethod(oldMethod.getName().toString(), getKeywords(oldMethod.getModifiers()));
                    String url = getUrlController(cuController);
                    createMethodController(oldMethod, newMethod, url);
                });
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void createMethodController(MethodDeclaration methodController, MethodDeclaration methodWebController, String urlController) {

        methodWebController.setType(methodController.getType());
        methodWebController.setThrownExceptions(methodController.getThrownExceptions());
        methodController.getParameters().forEach(parameter -> methodWebController.addParameter(parameter));
        methodController.getAnnotations().forEach(annotation -> {
            if (annotation.toString().contains("Mapping")) {
                if (annotation instanceof NormalAnnotationExpr) {

                    NormalAnnotationExpr annotationNormal = (NormalAnnotationExpr) annotation;
                    NodeList<MemberValuePair> pairs = annotationNormal.getPairs();
                    for (MemberValuePair pair : pairs) {
                        if (pair.getName().toString().equalsIgnoreCase("Value")) {
                            System.out.println("url:" + urlController + " : " + pair.getValue().toString());
                            pair.setValue(StaticJavaParser.parseExpression("\"" + urlController + pair.getValue().toString().substring(1, pair.getValue().toString().length())));
                        }
                    }

                    methodWebController.addAnnotation(annotationNormal);
                } else if (annotation instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr annotationSingle = (SingleMemberAnnotationExpr) annotation;
                    System.out.println("url:" + urlController + " : " + annotationSingle.getMemberValue());
                    annotationSingle.setMemberValue(StaticJavaParser.parseExpression("\"" + urlController + annotationSingle.getMemberValue().toString().substring(1, annotationSingle.getMemberValue().toString().length())));
                    methodWebController.addAnnotation(annotationSingle);
                }
            } else {
                methodWebController.addAnnotation(annotation);
            }
        });
        if (methodController.getComment().isPresent()) {
            methodWebController.setComment(methodController.getComment().get());
        }

        String string = "HttpStatus code = HttpStatus.NOT_FOUND;";
        String string2 = getReturnTypeMethod(methodController) + " aux = null;";
        String string3 = "aux = this.service." + methodWebController.getNameAsString() + "(" + getStringParametersController(methodController) + ");";
        String string4 = "code = HttpStatus.OK;";
        String stringReturn = "ResponseEntity.status(code).body(aux)";
        BlockStmt tryStatement = new BlockStmt();
        NodeList<CatchClause> catchClauses = new NodeList<>();
        CatchClause catchStmt = new CatchClause();
        catchStmt.setParameter(new Parameter(new TypeParameter("Exception"), "e")).setBody(new BlockStmt().addStatement(StaticJavaParser.parseStatement("System.out.println(e.getMessage());")));
        catchClauses.add(catchStmt);

        tryStatement.addStatement(StaticJavaParser.parseStatement(string3));
        tryStatement.addStatement(StaticJavaParser.parseStatement(string4));
        methodWebController.setBody(new BlockStmt().addStatement(StaticJavaParser.parseStatement(string)).addStatement(StaticJavaParser.parseStatement(string2)).addStatement(new TryStmt().setTryBlock(tryStatement).setCatchClauses(catchClauses)).addStatement(new ReturnStmt(stringReturn)));
    }

    private String getStringParametersController(MethodDeclaration method) {
        String string = "";
        int cont = 0;
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                if (cont == 0) {
                    string += parameter.getNameAsString();
                    cont++;
                } else {
                    string += ", " + parameter.getNameAsString();
                }
            }
        }
        return string;
    }
    
    private void modifyWebServer(String nameMicroService){
        String serviceURL = "SERVICE_URL"; 
        String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web"};
        String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
        String pathComplete = pathGeneric + "WebServer.java";
        String url = nameMicroService.toUpperCase() + "_" + serviceURL;
        String nameMethodService = nameMicroService+"Service";
        
        try {
            CompilationUnit cuWebServer = StaticJavaParser.parse(new File(pathComplete));
            cuWebServer.findAll(ClassOrInterfaceDeclaration.class).forEach(webServer -> {
                FieldDeclaration field =  webServer.getFieldByName(serviceURL).get(); 
                webServer.addFieldWithInitializer(String.class, url ,StaticJavaParser.parseExpression("\"http://" + nameMicroService.toUpperCase() + "\""),getKeywords(field.getModifiers()));
                MethodDeclaration methodService = webServer.getMethodsByName("service").get(0);
                MethodDeclaration newMethodService = webServer.addMethod(nameMethodService,getKeywords(methodService.getModifiers())); 
                createNewWebMethodService(methodService, newMethodService, nameMicroService, url);
                
                MethodDeclaration methodController = webServer.getMethodsByName("controller").get(0);
                MethodDeclaration newMethodController = webServer.addMethod(nameMicroService+"Controller",getKeywords(methodController.getModifiers())); 
                createNewWebMethodController(methodController, newMethodController, nameMicroService, nameMethodService);
            });
            saveMicroservice(cuWebServer, pathComplete);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createNewWebMethodService(MethodDeclaration oldMethodService, MethodDeclaration newMethodService, String nameMicroService, String url){
        String name = "Web" + nameMicroService.substring(0,1).toUpperCase() +nameMicroService.substring(1,nameMicroService.length())+ "Service";  
        newMethodService.setType(name);
        newMethodService.addAnnotation(oldMethodService.getAnnotation(0));
        BlockStmt body = new BlockStmt(); 
        body.addStatement(new ReturnStmt("new " + name +"(" + url + ")"));
        newMethodService.setBody(body); 
    }
    
    private void createNewWebMethodController(MethodDeclaration oldMethodController, MethodDeclaration newMethodController, String nameMicroService, String nameService){
        String name = "Web" + nameMicroService.substring(0,1).toUpperCase() +nameMicroService.substring(1,nameMicroService.length()) +"Controller";  
        newMethodController.setType(name);
        newMethodController.addAnnotation(oldMethodController.getAnnotation(0));
        BlockStmt body = new BlockStmt(); 
        body.addStatement(new ReturnStmt("new " + name +"(" + nameService +"()"+ ")"));
        newMethodController.setBody(body); 
    }
    
    private void deleteOldMethodsWebServer(){
        String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web"};
        String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
        String pathComplete = pathGeneric + "WebServer.java";
        try {
            CompilationUnit cuWebServer = StaticJavaParser.parse(new File(pathComplete));
            cuWebServer.findAll(ClassOrInterfaceDeclaration.class).forEach(webServer -> {
                webServer.remove(webServer.getMethodsByName("service").get(0));
                webServer.remove(webServer.getMethodsByName("controller").get(0));
                webServer.remove(webServer.getFieldByName("SERVICE_URL").get());
            });
            saveMicroservice(cuWebServer, pathComplete);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void deleteOldTemplates(){
        String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web"};
        String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
        String pathController = pathGeneric + "WebController.java";
        String pathService = pathGeneric + "WebService.java";
        File file = new File(pathController);
        file.delete();
        File fileService = new File(pathService);
        fileService.delete();
    }
    
    private void moveEntities() {
        try {
            Properties properties = new Properties();
            HashMap<String, Vertex> entities = graph.getEntitiesProjectWeb();
            File f = new File(System.getProperty("user.dir"), "configuracion.properties");
            properties.load(new FileInputStream(f));            
            String groupIdWeb = "io.pivotal.microservices.services.web";
            for (String name : entities.keySet()) {                                
                String originalGroupId = properties.getProperty("GROUPID");
                String groupID = entities.get(name).getPackageName();
                String[] split2 = groupID.split("\\.");
                String root = "";
                for (String s : split2) {
                    root += fileSeparator + s;
                }
                String[] rootInput = {properties.getProperty("INPUTPATH"), "src", "main", "java", root, name + ".java"};
                String path = String.join(fileSeparator, rootInput);
                CompilationUnit cuEntity = StaticJavaParser.parse(new File(path));
                cuEntity.setPackageDeclaration(groupIdWeb); 
                NodeList<Node> importsRemove = new NodeList<>();
                cuEntity.getImports().forEach(imp -> {
                    if(imp.getNameAsString().contains(originalGroupId))
                        importsRemove.add(imp);
                });
                importsRemove.forEach(imp -> {
                    cuEntity.remove(imp);
                });
                String[] split = {"output", "microservices-web", "src", "main", "java", "io", "pivotal", "microservices", "services", "web", name+".java"};
                String pathGeneric = String.join(fileSeparator, split) + fileSeparator;
                saveMicroservice(cuEntity, pathGeneric);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
