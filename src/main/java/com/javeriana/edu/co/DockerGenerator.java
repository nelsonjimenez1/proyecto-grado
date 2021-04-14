package com.javeriana.edu.co;

import static com.javeriana.edu.co.CreateProjectMicroServices.fileSeparator;
import com.javeriana.edu.co.Utils.XMLUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 *
 * @author Gustavo Rivera
 */
public class DockerGenerator {
    public static int lineJump = 4; 
    public static String image ="openjdk:8-jre";
    
    public XMLUtils xmlU;
    DockerGenerator(){
        xmlU = new XMLUtils();
    }
    public void generateDockerFile(String microName){
        String[] split = {"output", microName, "Dockerfile"};
        List<String> lines = new ArrayList<>(); 
        String pathGeneric = String.join(fileSeparator, split);
        File file = new File(pathGeneric); 
        String from = "FROM " + image; 
        String add = "ADD " + "target"+ "/"  +getADDText(microName) + " "+ "app.jar"; 
        String expose = "EXPOSE " + getPort(microName); 
        String coment ="# Optional default command\n" + "# ENTRYPOINT [\"java\",\"-jar\",\"/app.jar\",\"reg\"]";
        lines.add(from);
        lines.add(add);
        lines.add(expose); 
        lines.add(coment); 
        write(file, lines);
    }
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
    public String getADDText(String microName){
        Document d = xmlU.openXMLFile(System.getProperty("user.dir") +fileSeparator+"output"+fileSeparator + microName + fileSeparator+ "pom.xml");
        ArrayList<Node> nodoVersion = xmlU.readXMLNodes(d, "/project/version");
        ArrayList<Node> nodoArtifactID = xmlU.readXMLNodes(d, "/project/artifactId");
        ArrayList<Node> nodoPackaging = xmlU.readXMLNodes(d,"/project/packaging");
        String version = nodoVersion.get(0).getChildNodes().item(0).getNodeValue(); 
        String artifactId = nodoArtifactID.get(0).getChildNodes().item(0).getNodeValue();
        String packaging = nodoPackaging.get(0).getChildNodes().item(0).getNodeValue();
        String add = artifactId +"-"+version+"."+packaging;
        return add;
    }
    public String getPort(String microName){
        String port ="";  
        String[] split = {"output", microName, "src", "main", "resources", "application.yml"};
        String path= String.join(fileSeparator, split);
        BufferedReader br;
        File fileApplication = new File(path);
        FileReader fr;
        try {
            fr = new FileReader(fileApplication);
            if(fileApplication.exists()){
            br = new BufferedReader(fr);
            String line; 
            String [] textElements;
            int cont = 0; 
            do {
                line = br.readLine();
                if(line != null && line.length() > 0){
                cont = 0; 
                textElements = line.split(" ");
                    if(textElements.length > 1){
                        if(textElements[0].equalsIgnoreCase("server.port:")){
                            port = textElements[1]; 
                        }
                    }
                }else{
                    cont ++; 
                }
            }while(cont < lineJump );   
            
        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DockerGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DockerGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return port; 
    }

}
