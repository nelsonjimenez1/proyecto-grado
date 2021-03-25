/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

import static com.javeriana.edu.co.CreateProyectMicroServices.fileSeparator;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
        String[] split = {System.getProperty("user.dir"), "input", "graph.xsl"};
        String filename = String.join(fileSeparator, split);
        
        try {
            FileInputStream fis = new FileInputStream(filename);
            XSSFWorkbook workBook = new XSSFWorkbook(fis);
            XSSFSheet hssfsheetNodes = workBook.getSheetAt(0);
            Iterator rowIterator = hssfsheetNodes.rowIterator();    
            int row = 0;
            
            while(rowIterator.hasNext()) {
                
                XSSFRow hssfRow = (XSSFRow) rowIterator.next();
                
                if(row == 0) {
                    row = 1;
                    continue;
                }
                
                Iterator it = hssfRow.cellIterator();           
                Node node = new Node();

                int cell = 0;
                
                while(it.hasNext()) {
                    XSSFCell hssfCell = (XSSFCell) it.next();
                    
                    switch(cell){
                        case 0:
                            node.setId(hssfCell.toString());
                            break;
                        case 1:
                            node.setPackageName(hssfCell.toString());
                            break;
                        case 2:
                            node.setName(hssfCell.toString());
                            break;
                        case 3:
                            node.setLabel(hssfCell.toString());
                            break;
                        case 4:
                            node.setType(hssfCell.toString());
                            break;
                        case 5:
                            node.setSubType(hssfCell.toString());
                            break;
                        case 6:
                            node.setMicroservice(hssfCell.toString());
                            break;  
                    }                    
                    cell++;                    
                }       
                this.nodes.put(node.getId(), node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void loadConnections() {
        String[] split = {System.getProperty("user.dir"), "input", "graph.xsl"};
        String filename = String.join(fileSeparator, split);
        
        try {
            FileInputStream fis = new FileInputStream(filename);
            XSSFWorkbook workBook = new XSSFWorkbook(fis);
            XSSFSheet hssfsheetNodes = workBook.getSheetAt(1);
            Iterator rowIterator = hssfsheetNodes.rowIterator();    
            int row = 0;
            
            while(rowIterator.hasNext()) {
                
                XSSFRow hssfRow = (XSSFRow) rowIterator.next();
                
                if(row == 0) {
                    row = 1;
                    continue;
                }
                
                Iterator it = hssfRow.cellIterator();           
                Edge edge = new Edge();

                int cell = 0;
                
                while(it.hasNext()) {
                    XSSFCell hssfCell = (XSSFCell) it.next();
                    
                    switch(cell){
                        case 0:
                            edge.setIdSrc(hssfCell.toString());
                            break;
                        case 1:
                            edge.setIdDest(hssfCell.toString());
                            break;
                        case 2:
                            edge.setTypeRelation(hssfCell.toString());
                            break;
                        case 3:
                            edge.setLabel(hssfCell.toString());
                            break;
                    }                    
                    cell++;                    
                } 
                if( this.edges.get(edge.getIdSrc()) == null )   
                    this.edges.put(edge.getIdSrc(),new ArrayList<>());   
                this.edges.get(edge.getIdSrc()).add(edge);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
