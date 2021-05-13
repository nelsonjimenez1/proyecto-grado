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
 * This class has its attributes,the classes in charge of generating each part
 * of the microservices application and starts their execution
 *
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
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
     * Contructor
     *
     * @param port: port number used for microservices creation
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

    /**
     * This method allows start microservie-register, n-microsevices, microservices-web
     * creation and their dokersfiles
     *
     * @param microName the microservice name
     * @param port port where it will be exposed
     */
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
     * This method reads the name of each of the n microservices and based on them, a port
     * is assigned, the microservice is created and its respective dockerFile is
     * generated
     */
    public void travelArrayMicroservice() {
        ArrayList<String> list = graph.getListMicroservices();

        for (String microName : list) {
            hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph, portGeneric));
            this.consoleUtils.doMvnPackage(microName);
            this.dockerG.generateDockerFile(microName, portGeneric);
            portGeneric++;

            System.out.println("--------------------------------------");
            System.out.println("microservice: " + microName + " construction finished");
            System.out.println("--------------------------------------");

        }
    }
}
