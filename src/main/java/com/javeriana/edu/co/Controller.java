/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author nelso
 */
public class Controller {
    private Graph graph;
    private CreateProjectMicroRegister createProjectMicroRegister;
    private HashMap<String, CreateProjectMicroServices> hashMapMicroservice;
    private HashMap<String, Integer> hashMapPortMicroservice;

    public Controller() {
        this.graph = new Graph();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        this.hashMapMicroservice = new HashMap<>();
        this.travelArrayMicroservice();
        this.hashMapPortMicroservice = new HashMap<>(); 
        CreateProjectMicroWeb newMicroWeb = new CreateProjectMicroWeb(this.graph);
    }
    
    public void travelArrayMicroservice() {
        ArrayList <String> list = graph.getListMicroservices();
        int portGeneric = 3333;
        
        for (String microName : list) {
            //hashMapPortMicroservice.put(microName, portGeneric);
            hashMapMicroservice.put(microName, new CreateProjectMicroServices(microName, graph,portGeneric));
            portGeneric++;
        }
    }
}
