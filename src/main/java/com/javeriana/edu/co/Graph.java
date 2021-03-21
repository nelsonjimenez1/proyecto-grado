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
import java.util.HashMap;

/**
 *
 * @author PC
 */
public class Graph {

    private HashMap<String, Node> nodes;
    private HashMap<String, ArrayList<Edge>> edges;

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();

    }

    public void loadNodes() {

        String[] split = {System.getProperty("user.dir"), "input", "graphNodes.csv"};
        String csvFile = String.join(fileSeparator, split);

        BufferedReader br = null;
        String line = "";
//Se define separador ","
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
//Se define separador ","
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
       
}
