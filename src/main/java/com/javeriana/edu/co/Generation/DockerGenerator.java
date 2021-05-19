package com.javeriana.edu.co.Generation;

import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class creates and allows you to create the dockerfile
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class DockerGenerator {
    public static int lineJump = 4; 
    public static String image ="openjdk:8-alpine";
    
    public XMLUtils xmlU;
    /**
     * Constructor
     */
    public DockerGenerator(){
        xmlU = new XMLUtils();
    }
    /**
     * Allows to create the docker file given the microservice name and the port
     * @param microName the microservice name
     * @param port port where it will be exposed
     */
    public void generateDockerFile(String microName, int port){
        String[] split = {"output", microName, "Dockerfile"};
        List<String> lines = new ArrayList<>(); 
        String pathGeneric = String.join(File.separator, split);
        File file = new File(pathGeneric); 
        String from = "FROM " + image; 
        String add = "ADD " + "target"+ "/"  +getADDText(microName) + " "+ "app.jar"; 
        String expose = "EXPOSE " + port; 
        String coment ="# Optional default command\n" + "# ENTRYPOINT [\"java\",\"-jar\",\"/app.jar\",\"reg\"]";
        lines.add(from);
        lines.add(add);
        lines.add(expose); 
        lines.add(coment); 
        write(file, lines);   
        System.out.println("DokerFile was generated for " + microName);
        
    }
    /**
     * allows you to write to a file 
     * @param file file to write
     * @param lines lines to write 
     */
    public void write(File file, List<String> lines) {
        BufferedWriter bw;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file, true));
            for (String string : lines) {
                bw.write(string + "\n");
            }
            bw.close();

        } catch (Exception e) {
            Logger.getLogger(DockerGenerator.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    /**
     * Allows you to obtain the necessary information to add the .jar
     * @param microName microservice name
     * @return 
     */
    public String getADDText(String microName){
        Document d = xmlU.openXMLFile(System.getProperty("user.dir") + File.separator +"output"+ File.separator + microName + File.separator + "pom.xml");
        ArrayList<Node> nodoVersion = xmlU.readXMLNodes(d, "/project/version");
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(d, "/project/artifactId");
        ArrayList<Node> nodoPackaging = xmlU.readXMLNodes(d,"/project/packaging");
        String version = nodoVersion.get(0).getChildNodes().item(0).getNodeValue(); 
        String artifactId = nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue();
        String packaging = nodoPackaging.get(0).getChildNodes().item(0).getNodeValue();
        String add = artifactId +"-"+version+"."+packaging;
        return add;
    }
}
