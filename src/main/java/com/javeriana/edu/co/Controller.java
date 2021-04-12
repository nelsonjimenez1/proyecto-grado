
package com.javeriana.edu.co;

import java.util.ArrayList;
import java.util.HashMap;


public class Controller {
    private Graph graph;
    private CreateProjectMicroRegister createProjectMicroRegister;
    private HashMap<String, CreateProjectMicroServices> hashMapMicroservice;
    private HashMap<String, Integer> hashMapPortMicroservice;

    public Controller() {
        this.graph = new Graph();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        this.hashMapMicroservice = new HashMap<>();
        this.hashMapPortMicroservice = new HashMap<>(); 
        this.travelArrayMicroservice();        
        CreateProjectMicroWeb newMicroWeb = new CreateProjectMicroWeb(this.graph);
    }
    
    public void travelArrayMicroservice() {
        ArrayList <String> list = graph.getListMicroservices();
        int portGeneric = 3333;
        
        for (String microName : list) {
            hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph,portGeneric));
            portGeneric++;
        }
    }
}
