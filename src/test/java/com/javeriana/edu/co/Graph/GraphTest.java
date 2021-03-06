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
    @DisplayName("CP20")
    public void testGetEdgesSameMicroserviceBySrcNodeId() {
        ArrayList<Edge> edgesSameMicroserviceBySrcNodeId = graph.getEdgesSameMicroserviceBySrcNodeId("owner.org.springframework.samples.petclinic.owner.OwnerController", "owner");
        assertEquals(9, edgesSameMicroserviceBySrcNodeId.size());
    }
   
    @Test
    @DisplayName("CP21")
    public void testGetNodeElementsSameMicroserviceBySrcNodeId() {
        ArrayList<Vertex> nodeElementsSameMicroserviceBySrcNodeId = graph.getNodeElementsSameMicroserviceBySrcNodeId("Has Method", "vet.org.springframework.samples.petclinic.vet.VetRepository", "vet");
        assertEquals(1, nodeElementsSameMicroserviceBySrcNodeId.size());
    }
    
    @Test
    @DisplayName("CP22")
    public void testGetNodesByMicroservice() {
        ArrayList<Vertex> nodeByNodeId = graph.getNodesByMicroservice("vet");
        assertEquals(13, nodeByNodeId.size());
    }
    
    @Test
    @DisplayName("CP23")
    public void testGetEdgesByDstNodeId() {
        ArrayList<Edge> edgesByDstNodeId = graph.getEdgesByDstNodeId("owner.org.springframework.samples.petclinic.owner.OwnerRepository.findById(Integer)");
        assertEquals(2, edgesByDstNodeId.size());
    }
   
    @Test
    @DisplayName("CP24")
    public void testGetListMicroservices() {
        ArrayList<String> listMicroservices = graph.getListMicroservices();
        assertEquals(5, listMicroservices.size());
    }

    @Test
    @DisplayName("CP25")
    public void testGetControllers() {
        HashMap<String, ArrayList<Vertex>> controllers = graph.getControllers();
        assertEquals(5, controllers.size());
    }

    @Test
    @DisplayName("CP26")
    public void testGetEntitiesProjectWeb() {
        HashMap<String, Vertex> entitiesProjectWeb = graph.getEntitiesProjectWeb();
        assertEquals(10, entitiesProjectWeb.size());
    }
   
    @Test
    @DisplayName("CP27")
    public void testGetEntitiesByMicroservice() {
        Set<String> entitiesByMicroservice = graph.getEntitiesByMicroservice("vet");
        assertEquals(6, entitiesByMicroservice.size());
    }

    @Test
    @DisplayName("CP28")
    public void testGetMethodsByClassId() {
        ArrayList<Vertex> methodsByClassId = graph.getMethodsByClassId("owner.org.springframework.samples.petclinic.owner.OwnerController");
        assertEquals(5, methodsByClassId.size());
    }

    @Test
    @DisplayName("CP29")
    public void testGetMethodsByMethodId() {
        ArrayList<Vertex> methodsByMethodId = graph.getMethodsByMethodId("pet1.org.springframework.samples.petclinic.owner.PetController.update(int, int, Pet)");
        assertEquals(1, methodsByMethodId.size());
    }
   
    @Test
    @DisplayName("CP30")
    public void testGetMethodsDistinctMicroservices() {
        ArrayList<Vertex> methodsDistinctMicroservices = graph.getMethodsDistinctMicroservices("pet1.org.springframework.samples.petclinic.owner.PetController.update(int, int, Pet)");
        assertEquals(1, methodsDistinctMicroservices.size());
    }

    @Test
    @DisplayName("CP31")
    public void testGetMainByMicroservice() {
        Vertex mainByMicroservice = graph.getMainByMicroservice("pet1");
        assertEquals("PetClinicApplication", mainByMicroservice.getName());
    }
    
    @Test
    @DisplayName("CP32")
    public void testGetParentByMethodId() {
        Vertex parentByMethodId = graph.getParentByMethodId("visit.org.springframework.samples.petclinic.owner.VisitController.create(Visit, int, int)");
        assertEquals("VisitController", parentByMethodId.getName());
    }
 
    @Test
    @DisplayName("CP33")
    public void testGetFieldsByClassId() {
        ArrayList<Vertex> fieldsByClassId = graph.getFieldsByClassId("pet1.org.springframework.samples.petclinic.owner.PetController");
        assertEquals(2, fieldsByClassId.size());
    }
  
    @Test
    @DisplayName("CP34")
    public void testNeedExpose() {
        boolean needExpose = graph.needExpose("owner.org.springframework.samples.petclinic.owner.OwnerRepository");
        assertEquals(true, needExpose);
    }
}
