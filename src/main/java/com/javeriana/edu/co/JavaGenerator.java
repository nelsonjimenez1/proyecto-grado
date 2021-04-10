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
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
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
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.utils.Pair;
import com.google.common.io.Files;
import static com.javeriana.edu.co.CreateProjectMicroServices.fileSeparator;
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
import jdk.nashorn.internal.ir.BlockStatement;

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
            File f = new File(System.getProperty("user.dir") + fileSeparator + "configuracion.properties");
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
            String[] splitRegistrationServer = {"templates", "RegistrationServer.java"};
            String[] splitRegistrationServerWrite = {"output", nameMicroService, "src", "main", "java", rooteGroupID, "services", "register", "RegistrationServer.java"};
            String path = String.join(fileSeparator, splitRegistrationServer);
            String pathWriteFile = String.join(fileSeparator, splitRegistrationServerWrite);
            CompilationUnit cu = StaticJavaParser.parse(new File(System.getProperty("user.dir"), path));
            cu.setPackageDeclaration(this.groupID + ".services.register");
            FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + fileSeparator + pathWriteFile);
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

    public void createClass(CompilationUnit originalCu, Vertex classNode, ArrayList<Vertex> methods, ArrayList<Vertex> fields, String rootDest) {
        try {
            System.out.println("createClass");
            CompilationUnit newCu = new CompilationUnit();

            originalCu.getImports().forEach(imp -> newCu.addImport(imp));
            newCu.setPackageDeclaration(originalCu.getPackageDeclaration().get());

            originalCu.findAll(ClassOrInterfaceDeclaration.class).forEach(declaration -> {
                if (declaration.getName().asString().equals(classNode.getName().trim())) {
                    ClassOrInterfaceDeclaration aux = newCu.addClass(declaration.getName().toString(), getKeywords(declaration.getModifiers()));
                    addComplemets(declaration, aux);
                    addAnnotations(declaration, aux);
                    if (fields.size() == 0) {
                        addFieldsToClass(declaration, aux);
                        addConstructors(declaration, aux);

                    } else {
                        addFieldsToClass(declaration, aux, fields);
                        addConstructors(declaration, aux, fields);

                    }
                    addMethodsToClass(declaration, aux, classNode, methods);
                    System.out.println("Ruta " + rootDest);
                    try {
                        FileWriter myWriter = new FileWriter(rootDest);
                        myWriter.write(newCu.toString());
                        myWriter.close();
                    } catch (Exception e) {
                        System.out.println("createClass: " + e.getMessage());
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
            if (constructor.getComment().isPresent()) {
                newConstructor.setComment(constructor.getComment().get());
            }
        });
    }

    public void addConstructors(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, ArrayList<Vertex> fieldsNodes) {

        ConstructorDeclaration newConstructor = newClass.addConstructor(getKeywords(originalClass.getModifiers()));
        originalClass.getConstructors().forEach(constructor -> {
            constructor.getParameters().forEach(parameter -> {
                for (Vertex fieldsNode : fieldsNodes) {
                    if (fieldsNode.getName().split("\\.")[1].equalsIgnoreCase(parameter.getNameAsString())) {
                        newConstructor.addParameter(parameter);
                    }
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

    public void addAnnotations(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        newClass.setAnnotations(originalClass.getAnnotations());
        if (originalClass.getComment().isPresent()) {
            newClass.setComment(originalClass.getComment().get());
        }
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
    private void addMethodsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass, Vertex classNode, ArrayList<Vertex> methods) {
        System.out.println("methods");
        originalClass.findAll(MethodDeclaration.class).forEach(method -> {

            System.out.println("MethodDeclaration name: " + method.getDeclarationAsString());
            String idVertex = methodValidate(methods, method);
            if (idVertex != null) {
                //TODO: VALIDAR si metodo hace llamada a otro metodo fuera del microservicio
                MethodDeclaration aux = newClass.addMethod(method.getName().toString(), getKeywords(method.getModifiers()));
                aux.setType(method.getType());
                aux.setThrownExceptions(method.getThrownExceptions());
                method.getParameters().forEach(parameter -> aux.addParameter(parameter));

                aux.setAnnotations(method.getAnnotations());
                if (method.getComment().isPresent()) {
                    aux.setComment(method.getComment().get());
                }

                ArrayList<Vertex> methodsDistinct = graph.getMethodsDistinctMicroservices(idVertex);

                if (methodsDistinct.size() != 0) {
                    createRepositoryStubClass(methodsDistinct, idVertex);
                } else {
                    aux.setBody(method.getBody().get());
                }
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
            if (fieldsContains(fieldsNodes, field)) {
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

    private void addFieldsToClass(ClassOrInterfaceDeclaration originalClass, ClassOrInterfaceDeclaration newClass) {
        System.out.println("fields");
        originalClass.findAll(FieldDeclaration.class).forEach(field -> {
            // TODO: Fields with Initializer?
            FieldDeclaration aux = newClass.addField(field.getVariables().get(0).getTypeAsString(), field.getVariables().get(0).getName().toString(), getKeywords(field.getModifiers()));
            aux.setAnnotations(field.getAnnotations());

            if (field.getComment().isPresent()) {
                aux.setComment(field.getComment().get());
            }
        });
    }

    private boolean fieldsContains(ArrayList<Vertex> fields, FieldDeclaration field) {
        boolean result = false;
        for (Vertex f : fields) {
            String split[] = f.getName().split("\\.");
            String a = split[1];
            String b = field.getVariables().get(0).getName().asString();
            if (split[1].equals(field.getVariables().get(0).getName().asString())) {
                result = true;
                break;
            }
        }
        return result;
    }

    // TODO: Validar cant. Parametros y tipo de cada uno
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

    private String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    void modifyMain(String originPath, String destinyPath) {
        try {
            CompilationUnit cuMain = StaticJavaParser.parse(new File(originPath));
            String import1 = "org.springframework.web.client.RestTemplate";
            String import2 = "org.springframework.cloud.client.loadbalancer.LoadBalanced";
            String import3 = "org.springframework.context.annotation.Bean";
            String import4 = "org.springframework.cloud.client.discovery.EnableDiscoveryClient";
            String import5 = ".services.registration.RegistrationServer";
            import5 = groupID + import5;
            cuMain.addImport(import1);
            cuMain.addImport(import2);
            cuMain.addImport(import3);
            cuMain.addImport(import4);
            cuMain.addImport(import5);
            ClassOrInterfaceDeclaration classMain = cuMain.findAll(ClassOrInterfaceDeclaration.class).get(0);
            classMain.addAnnotation("EnableDiscoveryClient");
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
            mdTemplate.addAnnotation("LoadBalanced");
            mdTemplate.addAnnotation("Bean");
            BlockStmt returnSmt = new BlockStmt();
            returnSmt.addStatement(new ReturnStmt("new RestTemplate()"));
            mdTemplate.setBody(returnSmt);
            try {
                FileWriter myWriter = new FileWriter(destinyPath);
                myWriter.write(cuMain.toString());
                myWriter.close();
            } catch (Exception e) {
                System.out.println("createClass: " + e.getMessage());
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JavaGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createRepositoryStubClass(ArrayList<Vertex> methodVertices, String idVertexDestMicro) {
        ArrayList<Vertex> createdParents = new ArrayList<>();
        Vertex vertexDestMicro = graph.getNodeByNodeId(idVertexDestMicro);
        for (Vertex methodVertex : methodVertices) {
            Vertex parent = graph.getParentByMethodId(methodVertex.getId());
            if (!createdParents.contains(parent)) {

                String[] origin = {this.rootInput, "src", "main", "java"};
                String[] originRight = methodVertex.getPackageName().split("\\.");
                origin = concatV(origin, originRight);
                String originPath = String.join(fileSeparator, origin) + fileSeparator + parent.getName() + ".java";
                String[] dest = {"output", vertexDestMicro.getMicroservice(), "src", "main", "java"};
                String[] destinyRight = parent.getPackageName().split("\\.");
                dest = concatV(dest, destinyRight);
                String destinyPath = String.join(fileSeparator, dest) + fileSeparator + parent.getName() + ".java";

                try {

                    CompilationUnit cuParent = StaticJavaParser.parse(new File(originPath));
                    CompilationUnit cuDestiny = new CompilationUnit();
                    cuDestiny.addImport("org.springframework.web.client.RestTemplate");
                    cuDestiny.addImport("org.springframework.cloud.client.loadbalancer.LoadBalanced");
                    cuDestiny.addImport("org.springframework.beans.factory.annotation.Autowired");

                    ClassOrInterfaceDeclaration classParent = cuParent.findAll(ClassOrInterfaceDeclaration.class).get(0);
                    ClassOrInterfaceDeclaration classDestiny = cuDestiny.addClass(classParent.getNameAsString());

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
                    classDestiny.addConstructor(getKeywords(modifiers));

                    List<MethodDeclaration> methods = classParent.getMethods();
                    for (MethodDeclaration method : methods) {
                        if (equalsMethod(method, methodVertex)) {
                            MethodDeclaration newMethod = classDestiny.addMethod(method.getNameAsString(), getKeywords(method.getModifiers()));
                            newMethod.setType(method.getType().toString().contains("Optional") ? method.getType().toString().split("<")[1].split(">")[0] : method.getType().toString());
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

                    FileWriter myWriter = new FileWriter(destinyPath);
                    myWriter.write(cuDestiny.toString());
                    myWriter.close();

                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        }
    }

    private String getMethodCallType(MethodDeclaration method) {
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

    private void createBody(MethodDeclaration oldMethod, MethodDeclaration newMethod, String callType) {
        BlockStmt tryStatement = new BlockStmt();
        NodeList<CatchClause> catchClauses = new NodeList<>();
        CatchClause catchStmt = new CatchClause();
        catchStmt.setParameter(new Parameter(new TypeParameter("Exception"), "e")).setBody(new BlockStmt().addStatement(StaticJavaParser.parseStatement("System.out.println(e.getMessage());")));
        catchClauses.add(catchStmt);

        switch (callType) {
            case "GET":

                String url = "URL + \"/interface/search/" + oldMethod.getNameAsString() + getStringUrlParameters(oldMethod) + "\"";
                String returnType = getReturnTypeClass(oldMethod);
                String parameters = getStringGetParameters(oldMethod);

                String returnStmt = "restTemplate.getForObject(" + url + ", " + returnType + parameters + ")";

                if (returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                }

                tryStatement.addStatement(new ReturnStmt(returnStmt));
                break;

            case "POST":

                url = "URL + \"/interface\"";
                returnType = getReturnTypeClass(oldMethod);
                //parameters = getStringGetParameters(oldMethod); Save con parametros además del objeto?
                String postParameter = getPostParameter(oldMethod);
                String postParameterType = getPostParameterType(oldMethod);

                String header1 = "HttpHeaders headers = new HttpHeaders();";
                String header2 = "headers.setContentType(MediaType.APPLICATION_JSON);";
                String header3 = "HttpEntity<" + postParameterType + "> request = new HttpEntity<>(" + postParameter + ", headers);";

                returnStmt = "restTemplate.postForObject(" + url + ", request, " + returnType + ")";

                /*if (returnType.contains("[]")) {
                    returnStmt = "Arrays.asList(" + returnStmt + ")";
                }*/ //Save puede devolver Arreglos?
                tryStatement.addStatement(StaticJavaParser.parseStatement(header1));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header2));
                tryStatement.addStatement(StaticJavaParser.parseStatement(header3));
                tryStatement.addStatement(new ReturnStmt(returnStmt));
                break;

            case "DELETE":

                url = "URL + \"/interface" + getStringUrlParameters(oldMethod) + "\"";
                ;
                parameters = getStringGetParameters(oldMethod);

                returnStmt = "restTemplate.delete(" + url + parameters + ")";

                tryStatement.addStatement(StaticJavaParser.parseStatement(returnStmt));
                break;

            default:
                System.out.println("Error in com.javeriana.edu.co.JavaGenerator.createBody(), No CallType");
        }

        newMethod.setBody(new BlockStmt().addStatement(new TryStmt().setTryBlock(tryStatement).setCatchClauses(catchClauses)));
    }

    private String getPostParameterType(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getTypeAsString();
            break;
        }
        return string;
    }

    private String getPostParameter(MethodDeclaration method) {
        String string = "";
        for (Parameter parameter : method.getParameters()) {
            string = parameter.getNameAsString();
            break;
        }
        return string;
    }

    private String getStringGetParameters(MethodDeclaration method) {
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

    private String getStringUrlParameters(MethodDeclaration method) {
        String string = "?";
        for (Parameter parameter : method.getParameters()) {
            for (AnnotationExpr annotation : parameter.getAnnotations()) {
                SingleMemberAnnotationExpr singleMembAnnot = (SingleMemberAnnotationExpr) annotation;
                if (annotation.toString().contains("Param")) {
                    string += singleMembAnnot.getMemberValue().toString() + "={" + parameter.getNameAsString() + "}&";
                }
            }
        }
        return string.substring(0, string.length() - 1);
    }

    private String getReturnTypeClass(MethodDeclaration method) {

        String returnType = method.getTypeAsString();

        if (returnType.contains("<") && returnType.contains(">")) { // TODO: Preguntar al profe: Collection<Collection<Entity>> ???
            String returnTypeAux = returnType.split("<")[1].split(">")[0];
            if(!returnType.contains("Optional"))
                returnTypeAux += "[]";
            returnType = returnTypeAux;
        }

        return returnType + ".class";
    }

    private static class MethodNameCollector extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            //necesario para visitar todos los nodos hijos
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }

    private boolean equalsMethod(MethodDeclaration method, Vertex m) {
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
        return result;
    }

    public void generateExposedRepository(Vertex vertex, String srcPath, String dstPath) {
        try {
            CompilationUnit cuRepo = StaticJavaParser.parse(new File(srcPath));
            String import1 = "org.springframework.data.rest.core.annotation.RepositoryRestResource";
            String annotation = "RepositoryRestResource";
            cuRepo.addImport(import1);
            cuRepo.findAll(ClassOrInterfaceDeclaration.class).forEach(cd -> {
                if (cd.getNameAsString().equals(vertex.getName())) {
                    NodeList<MemberValuePair> aux = new NodeList<>();
                    aux.add(new MemberValuePair("path", StaticJavaParser.parseExpression("\"interface\"")));
                    cd.addAnnotation(new NormalAnnotationExpr(new Name(annotation), aux));
                }
            });
            FileWriter myWriter = new FileWriter(dstPath);
            myWriter.write(cuRepo.toString());
            myWriter.close();

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }

    }

}
