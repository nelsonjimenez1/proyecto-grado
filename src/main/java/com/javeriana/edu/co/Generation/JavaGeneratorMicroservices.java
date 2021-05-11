package com.javeriana.edu.co.Generation;

import com.javeriana.edu.co.Graph.Vertex;
import com.javeriana.edu.co.Graph.Graph;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.TypeParameter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class generates the .java code files of the microservices, taking into
 * account the specifications of the graph
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class JavaGeneratorMicroservices extends JavaGenerator {

    public JavaGeneratorMicroservices() {
        super();
    }

    public JavaGeneratorMicroservices(Graph graph) {
        super(graph);
    }

    /**
     * This method adds the RegistrationServer.java class to the microservice
     * project
     *
     * @param nameMicroService: represents the name of the microservice that is
     * being generated
     * @param pathGroupID: represents the path where files begin in the format:
     * "example/example2/.../exampleN"
     */
    public void addRegisterClass(String nameMicroService, String pathGroupID) {
        try {
            String[] splitRegistrationServer = {"templates", "RegistrationServer.java"};
            String[] splitRegistrationServerWrite = {"output", nameMicroService, "src", "main", "java", pathGroupID, "services", "registration", "RegistrationServer.java"};
            String path = String.join(File.separator, splitRegistrationServer);
            String pathWriteFile = String.join(File.separator, splitRegistrationServerWrite);
            CompilationUnit cu = StaticJavaParser.parse(new File(System.getProperty("user.dir"), path));
            cu.setPackageDeclaration(this.groupID + ".services.registration");
            this.fileUtilsProject.saveCompilationUnit(cu, System.getProperty("user.dir") + File.separator + pathWriteFile);
        } catch (IOException ex) {
            Logger.getLogger(JavaGeneratorWeb.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        }
    }

    /**
     * This method modifies the Main Class (which has the annotation
     * @SpringBootApplication) in order to add necessary imports, annotations
     * and methods
     *
     * @param sourcePath: represents the path of the main class in the original
     * project
     * @param destinyPath: represents the path where the main class is going to
     * be located in the microservice project
     */
    public void modifyMain(String sourcePath, String destinyPath) {
        try {
            CompilationUnit cuMain = StaticJavaParser.parse(new File(sourcePath));
            String import1 = "org.springframework.web.client.RestTemplate";
            String import2 = "org.springframework.cloud.client.loadbalancer.LoadBalanced";
            String import3 = "org.springframework.context.annotation.Bean";
            String import4 = "org.springframework.cloud.client.discovery.EnableDiscoveryClient";
            String import5 = ".services.registration.RegistrationServer";
            String import6 = "org.springframework.context.annotation.ComponentScan";
            import5 = groupID + import5;
            cuMain.addImport(import1);
            cuMain.addImport(import2);
            cuMain.addImport(import3);
            cuMain.addImport(import4);
            cuMain.addImport(import5);
            cuMain.addImport(import6);
            ClassOrInterfaceDeclaration classMain = cuMain.findAll(ClassOrInterfaceDeclaration.class).get(0);
            classMain.addAnnotation(new MarkerAnnotationExpr("EnableDiscoveryClient"));
            List<MethodDeclaration> methods = cuMain.findAll(MethodDeclaration.class);
            for (MethodDeclaration method : methods) {
                if (method.getNameAsString().equalsIgnoreCase("main")) {
                    BlockStmt oldBody = method.getBody().get();
                    BlockStmt newBody = new BlockStmt().addStatement(new IfStmt(StaticJavaParser.parseExpression("System.getProperty(RegistrationServer.REGISTRATION_SERVER_HOSTNAME) == null"),
                            StaticJavaParser.parseStatement("System.setProperty(RegistrationServer.REGISTRATION_SERVER_HOSTNAME, \"localhost\");"), null));
                    oldBody.getStatements().forEach(stmt -> {
                        newBody.addStatement(stmt);
                    });
                    method.setBody(newBody);
                    break;
                }
            }
            MethodDeclaration mdTemplate = classMain.addMethod("restTemplate");
            mdTemplate.setType("RestTemplate");
            mdTemplate.addAnnotation(new MarkerAnnotationExpr("LoadBalanced"));
            mdTemplate.addAnnotation(new MarkerAnnotationExpr("Bean"));
            BlockStmt returnSmt = new BlockStmt();
            returnSmt.addStatement(new ReturnStmt("new RestTemplate()"));
            mdTemplate.setBody(returnSmt);
            this.fileUtilsProject.saveCompilationUnit(cuMain, destinyPath);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGeneratorWeb.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method creates the class of the input node depending of the input
     * methods and fields that remain
     *
     * @param originalCu: represents the CompilationUnit of the original java
     * file of the class
     * @param classVertex: represents the vertex of the class in the graph
     * @param methods: represents the vertex methods that remain in the class
     * @param fields: represents the vertex fields that remain in the class
     * @param destPath: represents the path where the class is going to be
     * located in the microservice project
     */
    public void createClass(CompilationUnit originalCu, Vertex classVertex, ArrayList<Vertex> methods, ArrayList<Vertex> fields, String destPath) {
        try {
            System.out.println("createClass: " + classVertex.getName());
            CompilationUnit newCu = new CompilationUnit();

            originalCu.getImports().forEach(imp -> newCu.addImport(imp));
            newCu.setPackageDeclaration(originalCu.getPackageDeclaration().get());

            originalCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                if (declaration.getName().asString().equals(classVertex.getName().trim())) {
                    ClassOrInterfaceDeclaration aux = newCu.addClass(declaration.getName().toString(), getKeywords(declaration.getModifiers()));
                    addComplemets(declaration, aux);
                    addAnnotations(declaration, aux);
                    if (!classVertex.getSubType().equalsIgnoreCase("Repository")) {
                        if (fields.isEmpty()) {
                            addFieldsToClass(declaration, aux);
                            addConstructors(declaration, aux);
                        } else {
                            addFieldsToClass(declaration, aux, fields);
                            addConstructors(declaration, aux, fields);
                        }

                    } else {
                        aux.setInterface(true);
                    }

                    if (methods.isEmpty()) {
                        addMethodsToClass(declaration, aux, classVertex);
                    } else {
                        addMethodsToClass(declaration, aux, methods, classVertex);
                    }

                    this.fileUtilsProject.saveCompilationUnit(newCu, destPath);
                }
            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method adds the constructors from one ClassOrInterfaceDeclaration to
     * another
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the constructors
     */
    private void addConstructors(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        originalClass.getConstructors().forEach(constructor -> {
            ConstructorDeclaration newConstructor = newClass.addConstructor(getKeywords(constructor.getModifiers()));
            newConstructor.setParameters(constructor.getParameters());
            newConstructor.setBody(constructor.getBody());
            newConstructor.setAnnotations(constructor.getAnnotations());
            if (constructor.getComment().isPresent()) {
                newConstructor.setComment(constructor.getComment().get());
            }
        });
    }

    /**
     * This method adds the constructors from one ClassOrInterfaceDeclaration to
     * another, and modify them depending of the fields that remain in the new
     * class
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the constructors
     * @param fieldsNodes: represents the vertex fields that remain in the new
     * class
     */
    private void addConstructors(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, ArrayList<Vertex> fieldsNodes) {

        originalClass.getConstructors().forEach(constructor -> {
            ConstructorDeclaration newConstructor = newClass.addConstructor(getKeywords(constructor.getModifiers()));
            constructor.getParameters().forEach(parameter -> {
                String fieldName = getFieldName(constructor.getBody(), parameter.getNameAsString());
                if (fieldName != null) {
                    for (Vertex fieldsNode : fieldsNodes) {
                        if (fieldsNode.getName().split("\\.")[1].equalsIgnoreCase(fieldName)) {
                            newConstructor.addParameter(parameter);
                        }
                    }
                } else {
                    throw new RuntimeException("The parameter doesn't exist");
                }               
            });

            BlockStmt blockStmt = new BlockStmt();
            NodeList<Statement> statements = constructor.getBody().getStatements();
            for (Statement statement : statements) {
                for (Vertex fieldsNode : fieldsNodes) {
                    if (statement.toString().contains(fieldsNode.getName().split("\\.")[1])) {
                        blockStmt.addStatement(statement);
                    }
                }

            }

            newConstructor.setBody(blockStmt);
            newConstructor.setAnnotations(constructor.getAnnotations());
            if (constructor.getComment().isPresent()) {
                newConstructor.setComment(constructor.getComment().get());
            }
        });
    }

    /**
     * This method returns the field name equivalent to the parameter name from
     * the Constructor Body
     *
     * @param body: The body of the constructor
     * @param parameterName: The name of the parameter
     * @return: Result: The name of the parameter Null: If the field doesn't
     * exist
     *
     */
    private String getFieldName(BlockStmt body, String parameterName) {
        String result = null;
        NodeList<Statement> statements = body.getStatements();
        for (Statement stmt : statements) {
            if (stmt.toString().contains(parameterName) && stmt.toString().contains("=") && !stmt.toString().contains("==")) {
                String[] split = stmt.toString().split("=");
                if (split.length == 2) {
                    result = split[0].substring(0, split[0].length() - 1).trim();
                    split = result.split("\\.");
                    result = split[split.length - 1];
                    break;
                }
            }
        }
        return result;
    }

    /**
     * This method adds the annotations from one ClassOrInterfaceDeclaration to
     * another
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the annotations
     */
    private void addAnnotations(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        newClass.setAnnotations(originalClass.getAnnotations());
        if (originalClass.getComment().isPresent()) {
            newClass.setComment(originalClass.getComment().get());
        }
    }

    /**
     * This method adds the complements (Extended and Implemented types) from
     * one ClassOrInterfaceDeclaration to another
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the complements
     */
    private void addComplemets(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        newClass.setExtendedTypes(originalClass.getExtendedTypes());
        newClass.setImplementedTypes(originalClass.getImplementedTypes());
    }

    /**
     * This method adds the methods from one ClassOrInterfaceDeclaration to
     * another
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the methods
     * @param classVertex: represents the vertex of the new class in the graph
     */
    private void addMethodsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, Vertex classVertex) {
        originalClass.findAll(MethodDeclaration.class).forEach(method -> {

            MethodDeclaration aux = newClass.addMethod(method.getName().toString(), getKeywords(method.getModifiers()));
            aux.setType(method.getType());
            aux.setThrownExceptions(method.getThrownExceptions());
            method.getParameters().forEach(parameter -> aux.addParameter(parameter));

            aux.setAnnotations(method.getAnnotations());
            if (method.getComment().isPresent()) {
                aux.setComment(method.getComment().get());
            }

            if (!classVertex.getSubType().equalsIgnoreCase("Repository")) {
                aux.setBody(method.getBody().get());
            } else {
                aux.setBody(null);
            }
        });
    }

    /**
     * This method adds the methods from one ClassOrInterfaceDeclaration to
     * another depending of the methods that remain in the new class
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the methods
     * @param methods: represents the vertex methods that remain in the new
     * class
     * @param classVertex: represents the vertex of the new class in the graph
     */
    private void addMethodsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, ArrayList<Vertex> methods, Vertex classVertex) {
        originalClass.findAll(MethodDeclaration.class).forEach(method -> {

            String idVertex = methodValidate(methods, method);
            if (idVertex != null) {
                MethodDeclaration aux = newClass.addMethod(method.getName().toString(), getKeywords(method.getModifiers()));
                aux.setType(method.getType());
                aux.setThrownExceptions(method.getThrownExceptions());
                method.getParameters().forEach(parameter -> aux.addParameter(parameter));

                aux.setAnnotations(method.getAnnotations());
                if (method.getComment().isPresent()) {
                    aux.setComment(method.getComment().get());
                }

                ArrayList<Vertex> methodsDistinct = graph.getMethodsDistinctMicroservices(idVertex);

                if (!methodsDistinct.isEmpty()) {
                    createRepositoryStubClasses(methodsDistinct, idVertex);
                }

                if (!classVertex.getSubType().equalsIgnoreCase("Repository")) {
                    aux.setBody(method.getBody().get());
                } else {
                    aux.setBody(null);
                }
            }
        });
    }

    /**
     * This method adds the fields from one ClassOrInterfaceDeclaration to
     * another
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the methods
     */
    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        System.out.println("fields");
        originalClass.findAll(FieldDeclaration.class).forEach(field -> {

            FieldDeclaration aux;
            if (field.getVariable(0).getInitializer().isPresent()) {
                aux = newClass.addFieldWithInitializer(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), field.getVariable(0).getInitializer().get(), getKeywords(field.getModifiers()));
            } else {
                aux = newClass.addField(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), getKeywords(field.getModifiers()));
            }

            aux.setAnnotations(field.getAnnotations());

            if (field.getComment().isPresent()) {
                aux.setComment(field.getComment().get());
            }
        });
    }

    /**
     * This method adds the fields from one ClassOrInterfaceDeclaration to
     * another depending of the fields that remain in the new class
     *
     * @param originalClass: represents the original ClassOrInterfaceDeclaration
     * @param newClass: represents the new ClassOrInterfaceDeclaration to add
     * the methods
     * @param fields: represents the vertex fields that remain in the new class
     */
    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, ArrayList<Vertex> fields) {
        System.out.println("fields");
        originalClass.findAll(FieldDeclaration.class).forEach(field -> {
            System.out.println("FieldDeclaration name: " + field.getVariables().get(0).getName());
            if (fieldsContains(fields, field)) {
                FieldDeclaration aux;
                if (field.getVariable(0).getInitializer().isPresent()) {
                    aux = newClass.addFieldWithInitializer(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), field.getVariable(0).getInitializer().get(), getKeywords(field.getModifiers()));
                } else {
                    aux = newClass.addField(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), getKeywords(field.getModifiers()));
                }
                aux.setAnnotations(field.getAnnotations());
                if (field.getComment().isPresent()) {
                    aux.setComment(field.getComment().get());
                }
            }
        });
    }

    /**
     * This method indicates if a FieldDeclaration is contained in an array of
     * Vertex fields
     *
     * @param fields: represents the array of Vertex fields
     * @param field: represents the FieldDeclaration to find
     * @return True: if the field is contained in the fields False: if the field
     * isn't contained in the fields
     */
    private boolean fieldsContains(ArrayList<Vertex> fields, FieldDeclaration field) {
        boolean result = false;
        for (Vertex f : fields) {
            String split[] = f.getName().split("\\.");
            if (split[1].equals(field.getVariables().get(0).getName().asString())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This method validates if a MethodDeclaration is contained in an array of
     * Vertex methods
     *
     * @param methods: represents an array of Vertex methods
     * @param method: represents the MethodDeclaration to validate
     * @return The ID of the Vertex that represents the MethodDeclaration: if
     * there is a Vertex that represents the method Null: if there isn't a
     * Vertex that represents the method
     */
    private String methodValidate(ArrayList<Vertex> methods, MethodDeclaration method) {
        String[] splitSignature;
        String[] splitVertex;
        String[] parameterSignature;
        String[] parameterVertex;
        String nameVertex;
        splitSignature = method.getSignature().asString().split("\\(|\\)");
        if (splitSignature.length > 1) {
            parameterSignature = splitSignature[1].split(",");
        } else {
            parameterSignature = null;
        }
        ArrayList<Boolean> flags = new ArrayList<>();
        for (Vertex m : methods) {
            splitVertex = m.getName().split("\\(|\\)");
            nameVertex = splitVertex[0].split("\\.")[1];
            if (splitVertex.length > 1) {
                parameterVertex = splitVertex[1].split(",");
            } else {
                parameterVertex = null;
            }
            boolean result = true;
            if (nameVertex.equals(splitSignature[0])) {
                if (parameterSignature != null && parameterVertex != null) {
                    if (parameterVertex.length == parameterSignature.length) {
                        for (int i = 0; i < parameterVertex.length; i++) {
                            String[] splitAux = parameterVertex[i].split("\\.");
                            String parameterType = splitAux[splitAux.length - 1].trim();
                            if (!parameterType.equals(parameterSignature[i].trim())) {
                                result = false;
                                break;
                            }
                        }
                    }
                } else {
                    if ((parameterVertex != null && parameterSignature == null) || (parameterVertex == null && parameterSignature != null)) {
                        result = false;
                    }
                }
            } else {
                result = false;
            }
            flags.add(result);
        }
        int cont = 0;
        for (Boolean flag : flags) {
            if (flag) {
                return methods.get(cont).getId();
            }
            cont++;
        }
        return null;
    }

    /**
     * This method creates the Repository Stub Classes of the repositories
     * located in other microservices
     *
     * @param methodVertices: represents the methods Vertices (of other
     * microservice) that are called from one method of the microservice
     * @param idMethodVertex: represents the id of the method vertex that calls
     * the methods vertices
     */
    private void createRepositoryStubClasses(ArrayList<Vertex> methodVertices, String idMethodVertex) {
        ArrayList<Vertex> createdParents = new ArrayList<>();
        Vertex vertexDestMicro = graph.getNodeByNodeId(idMethodVertex);
        for (Vertex methodVertex : methodVertices) {
            Vertex parent = graph.getParentByMethodId(methodVertex.getId());
            if (!createdParents.contains(parent) && parent != null) {

                String[] origin = {this.rootInput, "src", "main", "java"};
                String[] originRight = methodVertex.getPackageName().split("\\.");
                origin = concatV(origin, originRight);
                String originPath = String.join(File.separator, origin) + File.separator + parent.getName() + ".java";
                String[] dest = {"output", vertexDestMicro.getMicroservice(), "src", "main", "java"};
                String[] destinyRight = parent.getPackageName().split("\\.");
                dest = concatV(dest, destinyRight);
                String destinyPath = String.join(File.separator, dest) + File.separator + parent.getName() + ".java";

                try {

                    CompilationUnit cuParent = StaticJavaParser.parse(new File(originPath));
                    CompilationUnit cuDestiny = new CompilationUnit();
                    cuDestiny.setPackageDeclaration(parent.getPackageName());
                    cuDestiny.addImport("org.springframework.web.client.RestTemplate");
                    cuDestiny.addImport("org.springframework.cloud.client.loadbalancer.LoadBalanced");
                    cuDestiny.addImport("org.springframework.beans.factory.annotation.Autowired");
                    cuDestiny.addImport("org.springframework.stereotype.Component");

                    cuParent.getImports().forEach(imp -> {
                        cuDestiny.addImport(imp);
                    });

                    ClassOrInterfaceDeclaration classParent = cuParent.findAll(ClassOrInterfaceDeclaration.class).get(0);
                    ClassOrInterfaceDeclaration classDestiny = cuDestiny.addClass(classParent.getNameAsString());

                    classDestiny.addAnnotation(new MarkerAnnotationExpr("Component"));

                    NodeList<Modifier> modifiers = new NodeList<>();
                    modifiers.add(Modifier.publicModifier());

                    FieldDeclaration fdDestiny = classDestiny.addField("RestTemplate", "restTemplate", getKeywords(modifiers));
                    fdDestiny.addAnnotation(new MarkerAnnotationExpr("Autowired"));
                    fdDestiny.addAnnotation(new MarkerAnnotationExpr("LoadBalanced"));

                    NodeList<Modifier> modifiers2 = new NodeList<>();
                    modifiers2.add(Modifier.publicModifier());
                    modifiers2.add(Modifier.finalModifier());
                    modifiers2.add(Modifier.staticModifier());
                    classDestiny.addFieldWithInitializer("String", "URL", StaticJavaParser.parseExpression("\"" + "http://" + parent.getMicroservice().toUpperCase() + "\""), getKeywords(modifiers2));

                    List<MethodDeclaration> methods = classParent.getMethods();
                    for (MethodDeclaration method : methods) {
                        if (equalsMethod(method, methodVertex)) {
                            MethodDeclaration newMethod = classDestiny.addMethod(method.getNameAsString(), getKeywords(method.getModifiers()));
                            newMethod.setType(method.getType().toString());
                            method.getParameters().forEach(parameter -> {
                                Parameter p = parameter.clone();
                                p.getAnnotations().clear();
                                newMethod.addParameter(p);
                            });

                            String callType = getMethodCallType(method);

                            createBody(method, newMethod, callType);
                            createdParents.add(methodVertex);
                            break;
                        }
                    }

                    this.fileUtilsProject.saveCompilationUnit(cuDestiny, destinyPath);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * This method gives the method REST call type when exposing a repository
     * depending on the name of the method and this reference:
     * https://github.com/spring-projects/spring-data-rest/blob/main/src/main/asciidoc/repository-resources.adoc
     *
     * @param method: represents the MethodDeclaration of the repository which
     * has the methods to expose
     * @return The REST call type required to call the method
     */
    public String getMethodCallType(MethodDeclaration method) {
        String callType = "";

        if (method.getNameAsString().equals("findAll") || method.getNameAsString().equals("findByID")) {
            callType = "GET";
        } else if (method.getNameAsString().equals("save")) {
            callType = "POST";
        } else if (method.getNameAsString().equals("delete")) {
            callType = "DELETE";
        }

        if (callType.equals("")) {
            NodeList<AnnotationExpr> annotations = method.getAnnotations();
            for (AnnotationExpr annotation : annotations) {
                if (annotation.toString().contains("Query")) {
                    callType = "GET";
                    break;
                }
            }
        }

        return callType;
    }

    /**
     * This method creates the body of the method that is going to do a REST
     * call to a Repository in another microservice
     *
     * @param oldMethod: represents the MethodDeclaration of the method in the
     * repository that is going to be called
     * @param newMethod: represents the MethodDeclaration of the method that is
     * going to call the oldMethod
     * @param callType: represents the type of the REST call from the newMethod
     * to the oldMethod
     */
    private void createBody(MethodDeclaration oldMethod, MethodDeclaration newMethod, String callType) {
        BlockStmt tryStatement = new BlockStmt();
        NodeList<CatchClause> catchClauses = new NodeList<>();
        CatchClause catchStmt = new CatchClause();
        catchStmt.setParameter(new Parameter(new TypeParameter("Exception"), "e")).setBody(new BlockStmt().addStatement(StaticJavaParser.parseStatement("System.out.println(e.getMessage());")).addStatement(new ReturnStmt("null")));
        catchClauses.add(catchStmt);

        switch (callType) {
            case "GET":

                String url = "URL + \"/interface/search/" + oldMethod.getNameAsString() + getStringUrlParameters(oldMethod) + "\"";
                String returnType = getReturnTypeClass(oldMethod);
                String parameters = getStringGetParameters(oldMethod);

                String returnStmt = "restTemplate.getForObject(" + url + ", " + returnType + parameters + ")";

                if (returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                } else if (oldMethod.getTypeAsString().contains("Optional")) {
                    returnStmt = "Optional.of(" + returnStmt + ")";
                }

                tryStatement.addStatement(new ReturnStmt(returnStmt));
                break;

            case "POST":

                url = "URL + \"/interface\"";
                returnType = getReturnTypeClass(oldMethod);
                String postParameter = getPostParameter(oldMethod);
                String postParameterType = getPostParameterType(oldMethod);

                String header1 = "HttpHeaders headers = new HttpHeaders();";
                String header2 = "headers.setContentType(MediaType.APPLICATION_JSON);";
                String header3 = "HttpEntity<" + postParameterType + "> request = new HttpEntity<>(" + postParameter + ", headers);";

                returnStmt = "restTemplate.postForObject(" + url + ", request, " + returnType + ")";

                if (returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                } else if (oldMethod.getNameAsString().contains("Optional")) {
                    returnStmt = "Optional.of(" + returnStmt + ")";
                }

                tryStatement.addStatement(StaticJavaParser.parseStatement(header1));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header2));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header3));
                tryStatement.addStatement(new ReturnStmt(returnStmt));
                break;

            case "DELETE":

                url = "URL + \"/interface" + getStringUrlParameters(oldMethod) + "\"";
                parameters = getStringGetParameters(oldMethod);

                returnStmt = "restTemplate.delete(" + url + parameters + ")";

                tryStatement.addStatement(StaticJavaParser.parseStatement(returnStmt));
                break;

            default:
                System.out.println("Error in com.javeriana.edu.co.JavaGenerator.createBody(), No CallType");
        }

        newMethod.setBody(new BlockStmt().addStatement(new TryStmt().setTryBlock(tryStatement).setCatchClauses(catchClauses)));
    }

    /**
     * This method gives the String with the parameters required to do the REST
     * call
     *
     * @param method: represents the method which is going to be called
     * @return A String with the parameters in the correct structure for the
     * REST call
     */
    public String getStringUrlParameters(MethodDeclaration method) {
        String string = "?";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                SingleMemberAnnotationExpr singleMembAnnot = (SingleMemberAnnotationExpr) annotation;
                if (annotation.toString().contains("Param")) {
                    string += singleMembAnnot.getMemberValue().toString().substring(1, singleMembAnnot.getMemberValue().toString().length() - 1) + "={" + parameter.getNameAsString() + "}&";
                }
            }
        }
        return string.substring(0, string.length() - 1);
    }

    /**
     * This method indicates if a MethodDeclaration is equals to a method Vertex
     *
     * @param method: represents the MethodDeclaration to compare
     * @param methodVertex: represents the method Vertex to compare
     * @return True: The inputs are the same False: The inputs are different
     */
    private boolean equalsMethod(MethodDeclaration method, Vertex methodVertex) {
        String[] splitSignature;
        String[] splitVertex;
        String[] parameterSignature;
        String[] parameterVertex;
        String nameVertex;
        splitSignature = method.getSignature().asString().split("\\(|\\)");
        if (splitSignature.length > 1) {
            parameterSignature = splitSignature[1].split(",");
        } else {
            parameterSignature = null;
        }
        splitVertex = methodVertex.getName().split("\\(|\\)");
        nameVertex = splitVertex[0].split("\\.")[1];
        if (splitVertex.length > 1) {
            parameterVertex = splitVertex[1].split(",");
        } else {
            parameterVertex = null;
        }
        boolean result = true;
        if (nameVertex.equals(splitSignature[0])) {
            if (parameterSignature != null && parameterVertex != null) {
                if (parameterVertex.length == parameterSignature.length) {
                    for (int i = 0; i < parameterVertex.length; i++) {
                        String[] splitAux = parameterVertex[i].split("\\.");
                        String parameterType = splitAux[splitAux.length - 1].trim();
                        if (!parameterType.equals(parameterSignature[i].trim())) {
                            result = false;
                            break;
                        }
                    }
                }
            } else {
                if ((parameterVertex != null && parameterSignature == null) || (parameterVertex == null && parameterSignature != null)) {
                    result = false;
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    /**
     * This method generates the exposed repository in the microservice, adding
     * required imports and annotations
     *
     * @param repoVertex: represents the vertex of the repository in the graph
     * @param srcPath: represents the path of the original repository file
     * @param dstPath: represents the path where the repository is going to be
     * generated
     */
    public void generateExposedRepository(Vertex repoVertex, String srcPath, String dstPath) {

        try {
            String exposedClass = "";
            CompilationUnit cuRepo = StaticJavaParser.parse(new File(srcPath));
            String import1 = "org.springframework.data.rest.core.annotation.RepositoryRestResource";
            String annotation = "RepositoryRestResource";
            cuRepo.addImport(import1);
            List<ClassOrInterfaceDeclaration> classes = cuRepo.findAll(ClassOrInterfaceDeclaration.class);

            for (ClassOrInterfaceDeclaration cd : classes) {
                if (cd.getNameAsString().equals(repoVertex.getName())) {
                    NodeList<MemberValuePair> aux = new NodeList<>();
                    aux.add(new MemberValuePair("path", StaticJavaParser.parseExpression("\"interface\"")));
                    cd.addAnnotation(new NormalAnnotationExpr(new Name(annotation), aux));

                    exposedClass = cd.getExtendedTypes().toString().split("<")[1].split(",")[0];
                }
            }

            this.fileUtilsProject.saveCompilationUnit(cuRepo, dstPath);

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    /**
     * This method generates the exposed configuration required for the exposed
     * repositories
     *
     * @param imports: represents a Set with the imports of the required classes
     * to expose
     * @param microName: represents the microservice name where it is going to
     * be generated
     */
    public void generateExposedConfiguration(Set<String> imports, String microName) {

        if (!imports.isEmpty()) {

            String[] splitTemplate = {"templates", "RestConfig.java"};
            String path = String.join(File.separator, splitTemplate);

            String groupIdFS = String.join(File.separator, this.groupID.split("\\."));
            String[] splitDst = {"output", microName, "src", "main", "java", groupIdFS, "RestConfig.java"};
            String pathDst = String.join(File.separator, splitDst);

            try {
                CompilationUnit restConfigCU = StaticJavaParser.parse(new File(path));
                restConfigCU.setPackageDeclaration(groupID);
                MethodDeclaration method = restConfigCU.findAll(ClassOrInterfaceDeclaration.class).get(0).getMethods().get(0);
                String statement = "config.exposeIdsFor(";
                BlockStmt body = new BlockStmt();
                for (String imp : imports) {
                    if (!(imp.contains("(") && imp.contains(")"))) {
                        String[] split = imp.split("\\.");
                        String className = split[split.length - 1];
                        String classStmt = statement + className + ".class);";
                        body.addStatement(StaticJavaParser.parseStatement(classStmt));
                        restConfigCU.addImport(imp);
                    }
                    
                }
                method.setBody(body);

                this.fileUtilsProject.saveCompilationUnit(restConfigCU, pathDst);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(JavaGeneratorMicroservices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
