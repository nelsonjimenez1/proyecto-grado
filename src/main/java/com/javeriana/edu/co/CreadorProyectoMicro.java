/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void crearCarpetasBasicas() {
        String[] carpetas = {this.microName, "src", "main"};
        String[] subCarpetas = {"java", "resources"};
        String ruta = "";
        
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
        ArrayList<Node> nodos = xmlU.readXMLNodes(dInput, "/project/dependencies/dependency");        
        
        for (Node nodo : nodos) {            
            xmlU.insertNode(dOutput, "/project/dependencies", nodo);
        }               
    }
}
