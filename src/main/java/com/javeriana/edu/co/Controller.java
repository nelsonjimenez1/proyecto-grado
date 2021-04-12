
package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.CmdUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class Controller {
    private Graph graph;
    private CreateProjectMicroRegister createProjectMicroRegister;
    private HashMap<String, CreateProjectMicroServices> hashMapMicroservice;
    private HashMap<String, Integer> hashMapPortMicroservice;
    private CmdUtils cmdUtils;

    public Controller() {
        this.cmdUtils = new CmdUtils();
        this.graph = new Graph();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        this.cmdUtils.doMvnPackage("microservices-register");
        this.hashMapMicroservice = new HashMap<>();
        this.hashMapPortMicroservice = new HashMap<>(); 
        this.travelArrayMicroservice();
        CreateProjectMicroWeb newMicroWeb = new CreateProjectMicroWeb(this.graph);
        this.cmdUtils.doMvnPackage("microservices-web");
    }
    
    public void travelArrayMicroservice() {
        ArrayList <String> list = graph.getListMicroservices();
        int portGeneric = 3333;     
        
        for (String microName : list) {
            hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph,portGeneric));
            this.cmdUtils.doMvnPackage(microName);
            portGeneric++;
        }
    }
}
