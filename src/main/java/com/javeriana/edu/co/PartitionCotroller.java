package com.javeriana.edu.co;

import com.javeriana.edu.co.Creation.CreateProjectMicroServices;
import com.javeriana.edu.co.Creation.CreateProjectMicroWeb;
import com.javeriana.edu.co.Creation.CreateProjectMicroRegister;
import com.javeriana.edu.co.Generation.DockerGenerator;
import com.javeriana.edu.co.Graph.Graph;
import com.javeriana.edu.co.Utils.ConsoleUtils;
import com.javeriana.edu.co.Utils.FileUtilsProject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author nelso
 */
public class PartitionCotroller {

    private Graph graph;
    private CreateProjectMicroRegister createProjectMicroRegister;
    private HashMap<String, CreateProjectMicroServices> hashMapMicroservice;
    private HashMap<String, Integer> hashMapPortMicroservice;
    private ConsoleUtils consoleUtils;
    private DockerGenerator dockerG;
    private String registerIP;
    private FileUtilsProject fileUtilsProject;
    private int portGeneric;

    /**
     *
     * @param port
     */
    public PartitionCotroller(String port) {
        this.portGeneric = Integer.parseInt(port);
        this.fileUtilsProject = new FileUtilsProject();
        this.dockerG = new DockerGenerator();
        this.consoleUtils = new ConsoleUtils();
        this.graph = new Graph();
        this.fileUtilsProject.deleteOutput();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        init();

    }

    private void init() {
        this.createProjectMicroRegister.copyFolder();
        this.consoleUtils.doMvnPackage("microservices-register");
        this.dockerG.generateDockerFile("microservices-register", 1111);
        this.hashMapMicroservice = new HashMap<>();
        this.hashMapPortMicroservice = new HashMap<>();
        this.travelArrayMicroservice();
        CreateProjectMicroWeb newMicroWeb = new CreateProjectMicroWeb(this.graph);
        this.consoleUtils.doMvnPackage("microservices-web");
        this.dockerG.generateDockerFile("microservices-web", 2222); 
    }

    /**
     *
     */
    public void travelArrayMicroservice() {
        ArrayList<String> list = graph.getListMicroservices();

        for (String microName : list) {
            hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph, portGeneric));
            this.consoleUtils.doMvnPackage(microName);
            this.dockerG.generateDockerFile(microName, portGeneric);
            portGeneric++;
        }
    }
}
