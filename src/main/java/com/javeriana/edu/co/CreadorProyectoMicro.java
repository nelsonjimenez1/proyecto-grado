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
public class CreadorProyectoMicro {
    
    public String groupID;
    public String rutaInput;
    public String microName;
    public XMLUtils xmlU;
    
    public CreadorProyectoMicro(String microName) {
        xmlU = new XMLUtils();
        Properties properties= new Properties();
        try {
            File f = new File(System.getProperty("user.dir")+"\\configuracion.properties");            
            properties.load(new FileInputStream(f));
            groupID = properties.getProperty("GROUPID");
            rutaInput = properties.getProperty("INPUTPATH");
            this.microName = microName;
            crearCarpetasBasicas();
            crearCarpetasGroupID();
            generarPOM();
            crearResources();
            copiarCarpetasAuxiliares();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void crearCarpetasBasicas() {
        String[] carpetas = {this.microName, "src", "main"};
        String[] subCarpetas = {"java", "resources"};
        String ruta = "";
        
        File dirOutput = new File(System.getProperty("user.dir")+"\\output");
        dirOutput.mkdir();     
        for (String c : carpetas) {
            ruta += "\\" + c;
            File directorio = new File(System.getProperty("user.dir")+"\\output" + ruta);
            directorio.mkdir();            
        }
        
        for (String sc : subCarpetas) {
            File directorio = new File(System.getProperty("user.dir")+"\\output" + ruta + "\\" + sc);
            directorio.mkdir();  
        }   
    }
    
    public void crearCarpetasGroupID() {
        
        String[] split = groupID.split("\\.");
        String ruta = "";
        
        for (String s : split) {
            ruta += "\\" + s;
            File directorio = new File(System.getProperty("user.dir")+"\\output\\" + this.microName + "\\src\\main\\java" + ruta);
            directorio.mkdir();            
        }       
    }
    
    public void generarPOM() {
        Document dOutput = xmlU.openXMLFile(System.getProperty("user.dir")+"\\templates\\pom.xml");
        
        agregarDependencias(dOutput);
        agregarPlugins(dOutput);
        cambiarGroupId_ArtifactID(dOutput);
         
        xmlU.saveXML(dOutput, System.getProperty("user.dir")+"\\output\\"+microName+"\\pom.xml");
    }
    
    public void agregarDependencias(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rutaInput + "\\pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/dependencies/dependency");        
        
        for (Node nodo : nodos) {            
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }               
    }
    
    public void agregarPlugins(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rutaInput + "\\pom.xml");
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/plugins/plugin");        
        
        for (Node nodo : nodos) {            
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }               
    }

    private void cambiarGroupId_ArtifactID(Document dOutput) {
        Document dInput = xmlU.openXMLFile(rutaInput + "\\pom.xml");
        ArrayList<Node> nodoGroupId = xmlU.readXMLNodes(dInput, "/project/groupId");   
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(dInput, "/project/artifactId");   
        xmlU.removeNodes(dOutput, "/project/groupId");
        xmlU.removeNodes(dOutput, "/project/artifactId");
        nodoArtifactID.get(0).getChildNodes().item(0).setNodeValue(nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue() + "-" + microName);
        xmlU.insertNode(dOutput, "/project", nodoGroupId.get(0));
        xmlU.insertNode(dOutput, "/project", nodoArtifactID.get(0));
    }
    
    private void crearResources() {
        copiarDirectorioAOtro(rutaInput+"\\src\\main\\resources", System.getProperty("user.dir")+"\\output\\"+microName+"\\src\\main");
    }   
    
    private void copiarDirectorioAOtro(String origen, String destino) {
        File from = new File(origen);
        File to = new File(destino);
 
        try {
            FileUtils.copyDirectoryToDirectory(from, to);
        } catch (IOException ex) {
            Logger.getLogger(CreadorProyectoMicro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void copiarCarpetasAuxiliares(){
        ArrayList<File> lista = listarDirectorio(rutaInput+"\\src\\main");
        for(int i = 0; i < lista.size(); i++) {
            if(!lista.get(i).getName().equals("resources") && !lista.get(i).getName().equals("java")) {
                copiarDirectorioAOtro(rutaInput+"\\src\\main\\"+lista.get(i).getName(), System.getProperty("user.dir")+"\\output\\"+microName+"\\src\\main");
            }  
        }
    }
    
    private ArrayList<File> listarDirectorio(String dirName) {
        ArrayList<File> listaArchivos = new ArrayList<>();
        File f = new File(dirName);
        File[] listFiles = f.listFiles();
        for (int i = 0 ; i < listFiles.length; i++) {
            if (listFiles[i].isDirectory()) {
                listaArchivos.add(listFiles[i]);
            }
        }
        return listaArchivos;
    }
}
