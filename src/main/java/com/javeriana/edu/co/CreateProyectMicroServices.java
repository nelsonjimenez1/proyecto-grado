/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.google.common.io.Files;
import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author prado
 */
public class CreateProyectMicroServices {

    public String groupID;
    public String rootInput;
    public String microName;
    public XMLUtils xmlU;
    public JavaGenerator generator;
    public String rootGroupID;
    public static String fileSeparator = File.separator;

    public CreateProyectMicroServices(String microName, Graph graph) { // Modified
        xmlU = new XMLUtils();
        Properties properties = new Properties();
        try {
            File f = new File(System.getProperty("user.dir"),"configuracion.properties");
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rootInput = properties.getProperty("INPUTPATH");
            this.microName = microName;
            this.generator = new JavaGenerator(rootInput, graph); // Modified
            createBasicFolders();
            createFolderGroupID();
            createPOM();
            createResources();
            copyAuxiliaryFolders();
            generateFiles();
            updateRegister();
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
            
            rout +=  fileSeparator+ c;
            File directory = new File(System.getProperty("user.dir"), "output" +rout);
            directory.mkdir();
        }

        for (String sc : subFolders) {
            File directory = new File(System.getProperty("user.dir") , "output" + rout  + fileSeparator+ sc);
            directory.mkdir();
        }
    }

    public void createFolderGroupID() {

        String[] split = groupID.split("\\.");
        String root = "";
        //String path = String.join(fileSeparator, split);
        
        String[] splitPath = {System.getProperty("user.dir"),"output" ,this.microName,"src","main","java"};
        String path = String.join(fileSeparator, splitPath);
        for (String s : split) {
            root += fileSeparator + s;
            File directory = new File(path+ root);
            directory.mkdir();
        }
        
        this.rootGroupID = root;
        String[] splitPathDirectory = {System.getProperty("user.dir"),"output" ,this.microName,"src","main","java",root,"services"};
        String pathDirectory = String.join(fileSeparator, splitPathDirectory);
        File directory = new File(pathDirectory);
        directory.mkdir();
        directory = new File(pathDirectory+ fileSeparator+ "register");
        directory.mkdir();
    }

    public void createPOM() {
        String[] splitPath = {System.getProperty("user.dir"), "templates" ,"pom.xml"};
        String path = String.join(fileSeparator, splitPath);
        Document dOutput = xmlU.openXMLFile(path);

        addDependencies(dOutput);
        addPlugins(dOutput);
        updateGroupId_ArtifactID(dOutput);
        
        String[] splitPathXml = {System.getProperty("user.dir"),"output" ,microName,"pom.xml"};
        String pathXml = String.join(fileSeparator, splitPathXml);
        xmlU.saveXML(dOutput, pathXml);
    }

    public void addDependencies(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator+ "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/dependencies/dependency");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    public void addPlugins(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator+ "pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/plugins/plugin");

        for (Node nodo : nodos) {
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }
    }

    private void updateGroupId_ArtifactID(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rootInput + fileSeparator+ "pom.xml");
        ArrayList<Node> nodoGroupId = xmlU.readXMLNodes(dInput, "/project/groupId");
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(dInput, "/project/artifactId");
        xmlU.removeNodes(dOutput, "/project/groupId");
        xmlU.removeNodes(dOutput, "/project/artifactId");
        nodoArtifactID.get(0).getChildNodes().item(0).setNodeValue(nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue() + "-" + microName);
        xmlU.insertNode(dOutput, "/project", nodoGroupId.get(0));
        xmlU.insertNode(dOutput, "/project", nodoArtifactID.get(0));
    }

    private void createResources() {
        String[] splitOne = {rootInput,"src","main","resources"};
        String pathOne = String.join(fileSeparator, splitOne);
        String[] splitTwo = {System.getProperty("user.dir"),"output", microName,"src","main"};
        String pathTwo = String.join(fileSeparator, splitTwo);
        copyAnotherDirectory(pathOne, pathTwo);
    }

    private void copyAnotherDirectory(String origin, String destiny) {
        File from = new File(origin);
        File to = new File(destiny);

        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreateProyectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void copyAuxiliaryFolders() {
        ArrayList<File> list = listDirectory(rootInput +fileSeparator+"src"+fileSeparator+"main");
        String[] split = {rootInput,"src","main"};
        String path = String.join(fileSeparator, split);
        String[] splitMicro = {"output",microName,"src","main"};
        String pathMicro = String.join(fileSeparator, splitMicro);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals("resources") && !list.get(i).getName().equals("java")) {
                copyAnotherDirectory(path + fileSeparator+ list.get(i).getName(), System.getProperty("user.dir") +fileSeparator+ pathMicro);
            }
        }
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

    private void createFileByRoot(String root) {
        try {
            File directorio = new File(root);
            directorio.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(CreateProyectMicroServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void createFiles(ArrayList<String>rootList){
        for (String root : rootList) {
            this.createFileByRoot(root);
        }
    }
    private void generateFiles(){
        ArrayList<String> list = new ArrayList<String>();
        String[] split = {"output",this.microName,"src","main","java",rootGroupID,"services","register","RegistrationServer.java"};
        String path = String.join(fileSeparator, split);
        list.add(System.getProperty("user.dir")+fileSeparator+  path); 
        this.createFiles(list);
    }
    private void updateRegister(){
        this.generator.updateRegiter(microName, rootGroupID);
    }
}
