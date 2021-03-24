/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import static com.javeriana.edu.co.CreateProyectMicroServices.fileSeparator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author PC
 */
public class Graph {

    private HashMap<String, Node> nodes;
    private HashMap<String, ArrayList<Edge>> edges;

    public Graph() { // Modified
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        loadNodes();
        loadConnections();

    }

    public void loadNodes() {

        String[] split = {System.getProperty("user.dir"), "input", "graphNodes.csv"};
        String csvFile = String.join(fileSeparator, split);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                Node node = new Node(data[0], data[1], data[2], data[3], data[4]);
                this.nodes.put(node.getId(), node);
                System.out.println(data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3] + ", " + data[4]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void loadConnections() {

        String[] split = {System.getProperty("user.dir"), "input", "graphEdges.csv"};
        String csvFile = String.join(fileSeparator, split);

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                Edge edge = new Edge(data[0], data[1], data[2]);
                if( this.edges.get(edge.getIdSrc()) == null )   
                    this.edges.put(edge.getIdSrc(),new ArrayList<>());   
                this.edges.get(edge.getIdSrc()).add(edge); 
                
                System.out.println(data[0] + ", " + data[1] + ", " + data[2]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // New
    public ArrayList<Node> getAllNodes() {
        return (ArrayList<Node>) this.nodes.values();
    }   
    
    // New
    public Node getNodeByNodeId(String nodeId) {
        return this.nodes.get(nodeId);
    }
    
    // New
    public ArrayList<Edge> getEdgesBySrcNodeId(String nodeId) {
        return this.edges.get(nodeId);
    }
    
    // New
    public ArrayList<Edge> getEdgesSameMicroserviceBySrcNodeId(String nodeId, String microservice) {
        
        ArrayList<Edge> edges = new ArrayList<>();
        for (Edge edge : getEdgesBySrcNodeId(nodeId)) {
            if(this.nodes.get(edge.getIdDest()).getMicroservice().equals(microservice)) {
                edges.add(edge);
            }
        }
        return edges;
    }
    
    // New
    public ArrayList<Node> getNodeMethodsBySrcNodeId(String nodeId) {
        ArrayList<Node> methods = new ArrayList<>();
        for (Edge edge : getEdgesBySrcNodeId(nodeId)) {
            if(edge.getTypeRelation().equals("Calls")) {
                methods.add(this.nodes.get(edge.getIdDest()));
            }
        }
        return methods;
    }   
    
    // New
    public ArrayList<Node> getNodeElementsSameMicroserviceBySrcNodeId(String type, String nodeId, String microservice) {
        ArrayList<Node> methods = new ArrayList<>();
        for (Edge edge : getEdgesSameMicroserviceBySrcNodeId(nodeId, microservice)) {
            if(edge.getTypeRelation().equals(type)) {
                methods.add(this.nodes.get(edge.getIdDest()));
            }
        }
        return methods;
    }
    
    // New
    public ArrayList<Node> getNodesByMicroservice(String microservice) {
        ArrayList<Node> result = new ArrayList<>();
        
        Collection<Node> nodesAux = this.getAllNodes();
        for (Node node : nodesAux) {
            if(node.getMicroservice().equals(microservice))
                result.add(node);
        }
        
        return result;
    }

    // New
    public ArrayList<Edge> getEdgesByDstNodeId(String nodeId) {
        ArrayList<Edge> result = new ArrayList<>();
        
        Collection<String> keys = this.nodes.keySet();
        for (String key : keys) {
            ArrayList<Edge> edgesAux = this.getEdgesBySrcNodeId(nodeId);
            for (Edge edge : edgesAux) {
                if(edge.getIdDest().equals(nodeId))
                    result.add(edge);
            }
        }
        
        return result;
    }
    
       
}
