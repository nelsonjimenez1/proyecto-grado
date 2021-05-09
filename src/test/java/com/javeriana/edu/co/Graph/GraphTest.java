/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nelso
 */
public class GraphTest {
    private Graph graph;

    public GraphTest() {
        graph = new Graph();
    }
    
    @Test
    @DisplayName("")
    public void testGetEdgesSameMicroserviceBySrcNodeId() {
        boolean sw = false;
        ArrayList<Edge> edgesSameMicroserviceBySrcNodeId = graph.getEdgesSameMicroserviceBySrcNodeId("owner.org.springframework.samples.petclinic.owner.OwnerController", "owner");
        if (edgesSameMicroserviceBySrcNodeId.size() == 9) {
            sw = true;
        }
        
        assertEquals(true, sw);
    }
   
    @Test
    @DisplayName("")
    public void testGetNodeMethodsBySrcNodeId() {
        boolean sw = false;
        ArrayList<Vertex> nodeMethodsBySrcNodeId = graph.getNodeMethodsBySrcNodeId("owner.org.springframework.samples.petclinic.owner.OwnerRepository");
        System.out.println("ayuda" + nodeMethodsBySrcNodeId.size());
    }

    @Test
    @DisplayName("")
    public void testGetNodeElementsSameMicroserviceBySrcNodeId() {

    }
    
    @Test
    @DisplayName("")
    public void testGetNodesByMicroservice() {

    }
    
    @Test
    @DisplayName("")
    public void testGetEdgesByDstNodeId() {
        
    }
   
    @Test
    @DisplayName("")
    public void testGetListMicroservices() {
      
    }

    @Test
    @DisplayName("")
    public void testGetControllers() {
       
    }

    @Test
    @DisplayName("")
    public void testGetEntitiesProjectWeb() {

    }
   
    @Test
    @DisplayName("")
    public void testGetEntitiesByMicroservice() {
     
    }

    @Test
    @DisplayName("")
    public void testGetMethodsByClassId() {

    }

    @Test
    @DisplayName("")
    public void testGetMethodsByMethodId() {

    }
   
    @Test
    @DisplayName("")
    public void testGetMethodsDistinctMicroservices() {

    }

    @Test
    @DisplayName("")
    public void testGetMainByMicroservice() {

    }
    
    @Test
    @DisplayName("")
    public void testGetParentByMethodId() {
 
    }
 
    @Test
    @DisplayName("")
    public void testGetFieldsByClassId() {
 
    }
  
    @Test
    @DisplayName("")
    public void testNeedExpose() {

    }
}
