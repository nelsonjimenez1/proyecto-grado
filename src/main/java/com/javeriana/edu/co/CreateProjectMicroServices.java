/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.io.Files;
import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.NodeList;

/**
 *
 * @author prado
 */
public class CreateProjectMicroServices {

    public String groupID;
    public String rootInput;
    public String microName;
    public XMLUtils xmlU;
    public JavaGenerator generator;
    public String rootGroupID;
    public static String fileSeparator = File.separator;
    public Graph graph;
    public Integer port;

    public CreateProjectMicroServices(String microName, Graph graph, int port) { // Modified
        xmlU = new XMLUtils();
        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir"), "configuracion.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rootInput = properties.getProperty("INPUTPATH");
            this.microName = microName;
            this.graph = graph;
            this.generator = new JavaGenerator(rootInput, graph); // Modified
            this.port = port;
            createBasicFolders();
            createFolderGroupID();
            createFolderAfterGroupID();
            createPOM();
            createResources();
            copyAuxiliaryFolders();
            generateFiles();
            updateRegister();
            copyJavaFiles();
            createApplicationYML();
            
            DockerGenerator dockerG = new DockerGenerator();
            dockerG.generateDockerFile(microName);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createBasicFolders() {
        String[] folders = {this.microName, "src", "main"};
        String[] subFolders = {"java", "resources"};
        String rout = "";

        File dirOutput = new File(System.getProperty("user.dir"), "output");
        dirOutput.mkdir();

        for (String c : folders) {

            rout += fileSeparator + c;
            File directory = new File(System.getProperty("user.dir"), "output" + rout);
            directory.mkdir();
        }

        for (String sc : subFolders) {
            File directory = new File(System.getProperty("user.dir"), "output" + rout + fileSeparator + sc);
            directory.mkdir();
        }
    }

    public void createFolderGroupID() {

        String[] split = groupID.split("\\.");
        String root = "";
        //String path = String.join(fileSeparator, split);

        String[] splitPath = {System.getProperty("user.dir"), "output", this.microName, "src", "main", "java"};
        String path = String.join(fileSeparator, splitPath);
        for (String s : split) {
            root += fileSeparator + s;
            File directory = new File(path + root);
            directory.mkdir();
        }

        this.rootGroupID = root;
        String[] splitPathDirectory = {System.getProperty("user.dir"), "output", this.microName, "src", "main", "java", root, "services"};
        String pathDirectory = String.join(fileSeparator, splitPathDirectory);
        File directory = new File(pathDirectory);
        directory.mkdir();
        directory = new File(pathDirectory + fileSeparator + "registration");
        directory.mkdir();
    }

    public void createPOM() {
        String[] splitPath = {System.getProperty("user.dir"), "templates", "pom.xml"};
        String path = String.join(fileSeparator, splitPath);
        Document dOutput = xmlU.openXMLFile(path);
        addProperties(dOutput);
        addDependencies(dOutput);
        addPlugins(dOutput);
        updateGroupId_ArtifactID(dOutput);

        String[] splitPathXml = {System.getProperty("user.dir"), "output", microName, "pom.xml"};
        String pathXml = String.join(fileSeparator, splitPathXml);
        xmlU.saveXML(dOutput, pathXml);
    }

    public void addDependencies(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator + "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/dependencies/dependency");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    public void addPlugins(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator + "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/plugins/plugin");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    private void updateGroupId_ArtifactID(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator + "pom.xml");
        ArrayList<Node> nodoGroupId = xmlU.readXMLNodes(dInput, "/project/groupId");
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(dInput, "/project/artifactId");
        xmlU.removeNodes(dOutput, "/project/groupId");
        xmlU.removeNodes(dOutput, "/project/artifactId");
        nodoArtifactID.get(0).getChildNodes().item(0).setNodeValue(nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue() + "-" + microName);
        xmlU.insertNode(dOutput, "/project", nodoGroupId.get(0));
        xmlU.insertNode(dOutput, "/project", nodoArtifactID.get(0));
    }

    private void createResources() {
        String[] splitOne = {rootInput, "src", "main", "resources"};
        String pathOne = String.join(fileSeparator, splitOne);
        String[] splitTwo = {System.getProperty("user.dir"), "output", microName, "src", "main"};
        String pathTwo = String.join(fileSeparator, splitTwo);
        copyAnotherDirectory(pathOne, pathTwo);
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

    private void copyFile(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyFileToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void copyAuxiliaryFolders() {
        ArrayList<File> list = listDirectory(rootInput + fileSeparator + "src" + fileSeparator + "main");
        String[] split = {rootInput, "src", "main"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {"output", microName, "src", "main"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals("resources") && !list.get(i).getName().equals("java")) {
                copyAnotherDirectory(path + fileSeparator + list.get(i).getName(), System.getProperty("user.dir") + fileSeparator + pathMicro);
            }
        }
    }

    private ArrayList<File> listDirectory(String dirName) {
        ArrayList<File> listFilesOrigin = new ArrayList<>();
        File f = new File(dirName);
        try {
            File[] listFiles = f.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    listFilesOrigin.add(listFiles[i]);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return listFilesOrigin;
    }

    private void createFileByRoot(String root) {
        try {
            File directorio = new File(root);
            directorio.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createFiles(ArrayList<String> rootList) {
        for (String root : rootList) {
            this.createFileByRoot(root);
        }
    }

    private void generateFiles() {
        ArrayList<String> list = new ArrayList<String>();
        String[] split = {"output", this.microName, "src", "main", "java", rootGroupID, "services", "registration", "RegistrationServer.java"};
        String path = String.join(fileSeparator, split);
        list.add(System.getProperty("user.dir") + fileSeparator + path);
        this.createFiles(list);
    }

    private void updateRegister() {
        this.generator.updateRegister(microName, rootGroupID);
    }

    private void createFolderAfterGroupID() {
        String[] splitPathDirectory = {this.rootInput, "src", "main", "java", this.rootGroupID};
        String pathDirectory = String.join(fileSeparator, splitPathDirectory);
        ArrayList<File> list = listDirectory(pathDirectory);
        String[] split = {"output", this.microName, "src", "main", "java", rootGroupID};
        String path = String.join(fileSeparator, split);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDirectory()) {
                String pathFolder = path + fileSeparator + list.get(i).getName();
                File directory = new File(pathFolder);
                directory.mkdir();
            }
        }
    }

    //Validar caso 3
    private void copyJavaFiles() {
        ArrayList<Vertex> list = graph.getNodesByMicroservice(microName);
        Vertex main = graph.getMainByMicroservice(microName);
        String[] original = {this.rootInput, "src", "main", "java"};
        String[] originRight = main.getPackageName().split("\\.");
        original = concatV(original, originRight);
        String originPath = String.join(fileSeparator, original) + fileSeparator + main.getName() + ".java";
        String[] dest = {"output", this.microName, "src", "main", "java"};
        String[] destinyRight = main.getPackageName().split("\\.");
        dest = concatV(dest, destinyRight);
        String destinyPath = String.join(fileSeparator, dest);
        destinyPath += fileSeparator + main.getName() + ".java";
        generator.modifyMain(originPath, destinyPath);

        for (Vertex vertex : list) {
            if (!vertex.getSubType().equals("SpringBootApplication")) {
                if (vertex.getType().equals("Class")) {
                    String[] origin = {this.rootInput, "src", "main", "java"};
                    originRight = vertex.getPackageName().split("\\.");
                    origin = concatV(origin, originRight);
                    originPath = String.join(fileSeparator, origin) + fileSeparator + vertex.getName() + ".java";
                    String[] destiny = {"output", this.microName, "src", "main", "java"};
                    destinyRight = vertex.getPackageName().split("\\.");
                    destiny = concatV(destiny, destinyRight);
                    destinyPath = String.join(fileSeparator, destiny);

                    if (vertex.getSubType().equalsIgnoreCase("Controller") || vertex.getSubType().equalsIgnoreCase("Service")) {
                        try {
                            CompilationUnit newCuWebService = StaticJavaParser.parse(new File(originPath));
                            ArrayList<Vertex> methods = graph.getMethodsByClassId(vertex.getId());
                            ArrayList<Vertex> fields = graph.getFieldsByClassId(vertex.getId());
                            this.generator.createClass(newCuWebService, vertex, methods, fields, destinyPath + fileSeparator + vertex.getName() + ".java");
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (vertex.getSubType().equalsIgnoreCase("Repository")) {
                        if(graph.needExpose(vertex.getId())) {                            
                            destinyPath += fileSeparator + vertex.getName() + ".java";
                            generator.generateExposedRepository(vertex, originPath, destinyPath);
                        } else {
                            copyFile(originPath, destinyPath);
                        }
                    }
                    else {
                        copyFile(originPath, destinyPath);
                    }
                }
            }

        }
    }

    private String[] concatV(String[] left, String[] right) {
        String[] result = new String[left.length + right.length];

        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);

        return result;
    }

    private void createApplicationYML() {
        String[] splitPathDirectory = {"output", microName, "src", "main", "resources", "application.yml"};
        String pathDirectory = String.join(fileSeparator, splitPathDirectory);
        File newFile = new File(pathDirectory);

        String[] splitPathDirectoryOldFile = {"templates", "application.yml"};
        String pathDirectoryOldFile = String.join(fileSeparator, splitPathDirectoryOldFile);
        File oldFile = new File(pathDirectoryOldFile);
        try {
            if (oldFile.exists()) {
                BufferedReader FileRead = new BufferedReader(new FileReader(oldFile));
                String nextLine;
                while ((nextLine = FileRead.readLine()) != null) {
                    if (nextLine.toUpperCase().trim().equals("    name: name-service".toUpperCase().trim())) {
                        writeFile(newFile, "    name: " + microName);
                    } else if (nextLine.toUpperCase().trim().equals("server.port: 0000".toUpperCase().trim())) {
                        writeFile(newFile, "server.port: " + port);
                    } else {
                        writeFile(newFile, nextLine);
                    }
                }
                FileRead.close();
            }
        } catch (Exception e) {
            System.out.println("CreateAplicationYML: " + e.getMessage());
        }
    }

    private void writeFile(File file, String line) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            fileWriter.write(line + "\r\n");
            fileWriter.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void deleteFile(File file) {
        try {
            if (file.exists()) {
                file.delete();
                System.out.println("File deleted");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }   

    private void addProperties(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator + "pom.xml");
        ArrayList<Node> nodes = xmlU.readXMLNodes(dInput, "/project/properties");
        NodeList childNodes = nodes.get(0).getChildNodes();
        List<Node> nodesArray = IntStream.range(0, childNodes.getLength())
        .mapToObj(childNodes::item)
        .collect(Collectors.toList());
        
        for (Node nodo : nodesArray) {
            xmlU.insertNode(dOutput, "/project/properties", nodo);
        }
    }
}
