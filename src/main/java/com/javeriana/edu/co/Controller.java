package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.ConsoleUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller {

    private Graph graph;
    private CreateProjectMicroRegister createProjectMicroRegister;
    private HashMap<String, CreateProjectMicroServices> hashMapMicroservice;
    private HashMap<String, Integer> hashMapPortMicroservice;
    private ConsoleUtils consoleUtils;
    private DockerGenerator dockerG;
    private String registerIP;

    public Controller() {
        this.dockerG = new DockerGenerator();
        this.consoleUtils = new ConsoleUtils();
        this.graph = new Graph();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        this.consoleUtils.executeCommand("microservices-register", "docker network create microservices-net");
        this.consoleUtils.doMvnPackage("microservices-register");
        this.dockerG.generateDockerFile("microservices-register", 1111);
        this.consoleUtils.executeCommand("microservices-register", "docker build -t microservices-register-image .");
        this.consoleUtils.executeCommand("microservices-register", "docker run --name register --network microservices-net -dp 1111:1111 microservices-register-image java -jar app.jar");
        this.registerIP = this.consoleUtils.getRegisterIP();
        this.registerIP = this.registerIP.substring(1, this.registerIP.length()-1);
        this.hashMapMicroservice = new HashMap<>();
        this.hashMapPortMicroservice = new HashMap<>();
        this.travelArrayMicroservice();
        CreateProjectMicroWeb newMicroWeb = new CreateProjectMicroWeb(this.graph);
        this.consoleUtils.doMvnPackage("microservices-web");
        this.dockerG.generateDockerFile("microservices-web", 2222);
        this.consoleUtils.executeCommand("microservices-web", "docker build -t microservices-web-image .");
        this.consoleUtils.executeCommand("microservices-web", "docker run --network microservices-net -dp 2222:2222 microservices-web-image java -jar app.jar   --registration.server.hostname=" + registerIP);
    }

    public void travelArrayMicroservice() {
        ArrayList<String> list = graph.getListMicroservices();
        int portGeneric = 3333;

        for (String microName : list) {
            hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph, portGeneric));
            this.consoleUtils.doMvnPackage(microName);
            this.dockerG.generateDockerFile(microName, portGeneric);
            this.consoleUtils.executeCommand(microName, "docker build -t " + microName.toLowerCase() + "-image .");
            this.consoleUtils.executeCommand(microName, "docker run --network microservices-net -dp " + portGeneric + ":" + portGeneric + " " + microName + "-image java -jar app.jar  --registration.server.hostname=" + registerIP);
            portGeneric++;
        }
    }
}
