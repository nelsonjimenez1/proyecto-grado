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

    public Controller() {
        this.graph = new Graph();
        this.createProjectMicroRegister = new CreateProjectMicroRegister();
        this.hashMapMicroservice = new HashMap<>();
        this.travelArrayMicroservice();
    }
    
    public void travelArrayMicroservice() {
        ArrayList <String> list = graph.getListMicroservices();
        
        for (String string : list) {
            hashMapMicroservice.put(string, new CreateProjectMicroServices(string, graph));
        }
    }
}
