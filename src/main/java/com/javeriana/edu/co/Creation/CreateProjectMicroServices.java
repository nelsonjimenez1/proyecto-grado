package com.javeriana.edu.co.Creation;

import com.javeriana.edu.co.Generation.JavaGeneratorMicroservices;
import com.javeriana.edu.co.Graph.Vertex;
import com.javeriana.edu.co.Graph.Graph;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
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
 * This class creates the n microservices with the necessary files generating
 * and copying.
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class CreateProjectMicroServices {

    public String groupID;
    public String rootInput;
    public String microName;
    public XMLUtils xmlU;
    public JavaGeneratorMicroservices generator;
    public String rootGroupID;
    public Graph graph;
    public Integer port;

    /**
     * Constructor
     */
    public CreateProjectMicroServices() {

    }

    /**
     * Constructor 
     * In this costructor, class attributes are initialized to be
     * used in this class functions.
     *
     * @param microName name for the microservice to going to be created
     * @param graph class with the Graph information
     * @param port port number used for microservice creation
     */
    public CreateProjectMicroServices(String microName, Graph graph, int port) {
        this.xmlU = new XMLUtils();
        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir"), "configuration.properties");
            properties.load(new FileInputStream(f));
            this.groupID = properties.getProperty("GROUPID");
            this.rootInput = properties.getProperty("INPUTPATH");
            this.microName = microName;
            this.graph = graph;
            this.generator = new JavaGeneratorMicroservices(graph);
            this.port = port;
            this.init();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method calls the other class functions for the microservice creation.
     */
    private void init() {
        System.out.println("--------------------------------------");
        System.out.println("creating project for " + this.microName);
        this.createBasicFolders();
        this.createFolderGroupID();
        this.createFolderAfterGroupID();
        this.createPOM();
        this.createResources();
        this.copyAuxiliaryFolders();
        this.generateFiles();
        this.updateRegister();
        this.partitionFiles();
        this.createApplicationYML();
        System.out.println("project creation finished");
        System.out.println("--------------------------------------");
    }

    /**
     * This method creates the microservice basic folders and places them in the right
     * position.
     */
    public void createBasicFolders() {
        String[] folders = {this.microName, "src", "main"};
        String[] subFolders = {"java", "resources"};
        String rout = "";

        File dirOutput = new File(System.getProperty("user.dir"), "output");
        dirOutput.mkdir();

        for (String c : folders) {

            rout += File.separator + c;
            File directory = new File(System.getProperty("user.dir"), "output" + rout);
            directory.mkdir();
        }

        for (String sc : subFolders) {
            File directory = new File(System.getProperty("user.dir"), "output" + rout + File.separator + sc);
            directory.mkdir();
        }
        System.out.println("basic folders created");
    }

    /**
     * This method creates the microservice groupID folders and places them in the right
     * position.
     */
    public void createFolderGroupID() {

        String[] split = groupID.split("\\.");
        String root = "";

        String[] splitPath = {System.getProperty("user.dir"), "output", this.microName, "src", "main", "java"};
        String path = String.join(File.separator, splitPath);
        for (String s : split) {
            root += File.separator + s;
            File directory = new File(path + root);
            directory.mkdir();
        }

        this.rootGroupID = root;
        String[] splitPathDirectory = {System.getProperty("user.dir"), "output", this.microName, "src", "main", "java", root, "services"};
        String pathDirectory = String.join(File.separator, splitPathDirectory);
        File directory = new File(pathDirectory);
        directory.mkdir();
        directory = new File(pathDirectory + File.separator + "registration");
        directory.mkdir();
        System.out.println("groupId folder created");
    }

    /**
     * This method generates the microservice POM.xml file and places it in the right
     * location.
     *
     */
    public void createPOM() {
        String[] splitPath = {System.getProperty("user.dir"), "templates", "pom.xml"};
        String path = String.join(File.separator, splitPath);
        Document dOutput = xmlU.openXMLFile(path);
        addProperties(dOutput);
        addDependencies(dOutput);
        addPlugins(dOutput);
        updateGroupId_ArtifactID(dOutput);

        String[] splitPathXml = {System.getProperty("user.dir"), "output", microName, "pom.xml"};
        String pathXml = String.join(File.separator, splitPathXml);
        xmlU.saveXML(dOutput, pathXml);
        System.out.println("POM file created");
    }

    /**
     * This method extract the necessary dependencies from a template to copy them into the
     * POM.xml file.
     *
     * @param dOuput represent the microservice POM.xml file
     */
    public void addDependencies(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + File.separator + "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/dependencies/dependency");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    /**
     * This method extract the necessary plugins from a template to copy them into the
     * POM.xml file.
     *
     * @param dOuput represent the microservice POM.xml file
     */
    public void addPlugins(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + File.separator + "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/plugins/plugin");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    /**
     * This method gets original groupID and artifactID and modifies to update in
     * microservice POM.xml file.
     *
     * @param dOuput represent the microservice POM.xml file
     */
    private void updateGroupId_ArtifactID(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + File.separator + "pom.xml");
        ArrayList<Node> nodoGroupId = xmlU.readXMLNodes(dInput, "/project/groupId");
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(dInput, "/project/artifactId");
        xmlU.removeNodes(dOutput, "/project/groupId");
        xmlU.removeNodes(dOutput, "/project/artifactId");
        nodoArtifactID.get(0).getChildNodes().item(0).setNodeValue(nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue() + "-" + microName);
        xmlU.insertNode(dOutput, "/project", nodoGroupId.get(0));
        xmlU.insertNode(dOutput, "/project", nodoArtifactID.get(0));
    }

    /**
     * This method copy the resource folder from the original project and paste it into the
     * microservice
     *
     */
    private void createResources() {
        String[] splitOne = {rootInput, "src", "main", "resources"};
        String pathOne = String.join(File.separator, splitOne);
        String[] splitTwo = {System.getProperty("user.dir"), "output", microName, "src", "main"};
        String pathTwo = String.join(File.separator, splitTwo);
        copyAnotherDirectory(pathOne, pathTwo);
    }

    /**
     * This method allows you to copy a folder from a directory into another one.
     *
     * @param origin directory location from which the information will be
     * copied
     * @param destiny directory location where the information will be pasted
     */
    public void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows copying files from a directory into another one.
     */
    private void copyFile(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyFileToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method allows copying folders from Original´project main folder directory into
     * microservice main folder directory.
     */
    private void copyAuxiliaryFolders() {
        ArrayList<File> list = listDirectory(rootInput + File.separator + "src" + File.separator + "main");
        String[] split = {rootInput, "src", "main"};
        String path = String.join(File.separator, split);
        String[] splitMicro = {"output", microName, "src", "main"};
        String pathMicro = String.join(File.separator, splitMicro);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals("resources") && !list.get(i).getName().equals("java")) {
                copyAnotherDirectory(path + File.separator + list.get(i).getName(), System.getProperty("user.dir") + File.separator + pathMicro);
            }
        }
        System.out.println("Auxiliary folders created");
    }

    /**
     * This method identifies in a directory the files or folders that it has and returns
     * the files and directories found.
     *
     * @param dirName directory path.
     * @return List of the Directry files.
     */
    public ArrayList<File> listDirectory(String dirName) {
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

    /**
     * This method creates in a specific path a new file.
     *
     * @param root specific path.
     */
    private void createFileByRoot(String root) {
        try {
            File directorio = new File(root);
            directorio.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method creates a set of files according to a root list.
     *
     * @param rootList root list for the files creation
     */
    public void createFiles(ArrayList<String> rootList) {
        for (String root : rootList) {
            this.createFileByRoot(root);
        }
    }

    /**
     * This method generates a set of files based on a specified path.
     */
    private void generateFiles() {
        ArrayList<String> list = new ArrayList<String>();
        String[] split = {"output", this.microName, "src", "main", "java", rootGroupID, "services", "registration", "RegistrationServer.java"};
        String path = String.join(File.separator, split);
        list.add(System.getProperty("user.dir") + File.separator + path);
        this.createFiles(list);
    }

    /**
     * This method adds the RegistrationServer.java class to the microservice project.
     */
    private void updateRegister() {
        this.generator.addRegisterClass(microName, rootGroupID);
    }

    /**
     * This method creates folders into microservice groupID folder.
     */
    private void createFolderAfterGroupID() {
        String[] splitPathDirectory = {this.rootInput, "src", "main", "java", this.rootGroupID};
        String pathDirectory = String.join(File.separator, splitPathDirectory);
        ArrayList<File> list = listDirectory(pathDirectory);
        String[] split = {"output", this.microName, "src", "main", "java", rootGroupID};
        String path = String.join(File.separator, split);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isDirectory()) {
                String pathFolder = path + File.separator + list.get(i).getName();
                File directory = new File(pathFolder);
                directory.mkdir();
            }
        }
        System.out.println("folders into groupId folder created");
    }

    /**
     * This method takes the file with the SpringBootApplication notation from the original
     * project and modifies it to generate the one from the
     * microservice. Generates the microservice class and the file partition by
     * javaGeneratormicroservices class and if necessary, generate the class to
     * expose a repository and its configuration class.
     */
    private void partitionFiles() {
        ArrayList<Vertex> list = graph.getNodesByMicroservice(microName);
        Vertex main = graph.getMainByMicroservice(microName);
        String[] original = {this.rootInput, "src", "main", "java"};
        String[] originRight = main.getPackageName().split("\\.");
        original = concatV(original, originRight);
        String originPath = String.join(File.separator, original) + File.separator + main.getName() + ".java";
        String[] dest = {"output", this.microName, "src", "main", "java"};
        String[] destinyRight = main.getPackageName().split("\\.");
        dest = concatV(dest, destinyRight);
        String destinyPath = String.join(File.separator, dest);
        destinyPath += File.separator + main.getName() + ".java";
        this.generator.modifyMain(originPath, destinyPath);

        boolean needExpose = false;

        for (Vertex vertex : list) {
            if (!vertex.getId().equals(main.getId())) {
                if (vertex.getType().equals("Class")) {
                    String[] origin = {this.rootInput, "src", "main", "java"};
                    originRight = vertex.getPackageName().split("\\.");
                    origin = concatV(origin, originRight);
                    originPath = String.join(File.separator, origin) + File.separator + vertex.getName() + ".java";
                    String[] destiny = {"output", this.microName, "src", "main", "java"};
                    destinyRight = vertex.getPackageName().split("\\.");
                    destiny = concatV(destiny, destinyRight);
                    destinyPath = String.join(File.separator, destiny);

                    try {
                        CompilationUnit newCuWebService = StaticJavaParser.parse(new File(originPath));
                        ArrayList<Vertex> methods = graph.getMethodsByClassId(vertex.getId());
                        ArrayList<Vertex> fields = graph.getFieldsByClassId(vertex.getId());
                        this.generator.createClass(newCuWebService, vertex, methods, fields, destinyPath + File.separator + vertex.getName() + ".java");
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found - CopyJavaFiles: " + ex.getMessage());
                        Logger.getLogger(CreateProjectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (vertex.getSubType().equalsIgnoreCase("Repository") && graph.needExpose(vertex.getId())) {
                        destinyPath += File.separator + vertex.getName() + ".java";
                        needExpose = true;
                        this.generator.generateExposedRepository(vertex, originPath, destinyPath);
                    }
                }
            }
        }

        if (needExpose) {
            this.generator.generateExposedConfiguration(this.graph.getEntitiesByMicroservice(this.microName), this.microName);
        }
    }

    /**
     * This method concatenate two string Vectors
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
     * This method generates the microservice application.yml file and places it in the
     * right location.
     */
    private void createApplicationYML() {
        String[] splitPathDirectory = {"output", microName, "src", "main", "resources", "application.yml"};
        String pathDirectory = String.join(File.separator, splitPathDirectory);
        File newFile = new File(pathDirectory);

        String[] splitPathDirectoryOldFile = {"templates", "application.yml"};
        String pathDirectoryOldFile = String.join(File.separator, splitPathDirectoryOldFile);
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
            System.out.println("YML file created");
        } catch (Exception e) {
            System.out.println("CreateAplicationYML: " + e.getMessage());
        }
    }

    /**
     * This method writes a File with a string received by parameter. If file doesnt exist, the method creates it.
     * @param file: The file where it will be written.
     * @param line: The string that will be written.
     */
    public void writeFile(File file, String line) {
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
    
     /**
     * This method adds properties to the microservice POM.xml file.
     */
    private void addProperties(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + File.separator + "pom.xml");
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
