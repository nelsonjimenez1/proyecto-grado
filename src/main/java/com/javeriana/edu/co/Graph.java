package com.javeriana.edu.co;

import com.javeriana.edu.co.Utils.ExcelUtils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Graph {

    private HashMap<String, Vertex> nodes;
    private HashMap<String, ArrayList<Edge>> edges;
    private ExcelUtils utils;

    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.utils = new ExcelUtils();
        this.utils.loadNodes(nodes);
        this.utils.loadConnections(edges);

    }

    public ArrayList<Vertex> getAllNodes() {
        return new ArrayList<Vertex>(this.nodes.values());
    }

    public Vertex getNodeByNodeId(String nodeId) {
        return this.nodes.get(nodeId);
    }

    public ArrayList<Edge> getEdgesBySrcNodeId(String nodeId) {
        return this.edges.get(nodeId);
    }

    public ArrayList<Edge> getEdgesSameMicroserviceBySrcNodeId(String nodeId, String microservice) {

        ArrayList<Edge> edgesSameMicro = new ArrayList<>();
        ArrayList<Edge> edges = getEdgesBySrcNodeId(nodeId);
        if (edges != null) {
            for (Edge edge : edges) {
                if (this.nodes.get(edge.getIdDest()).getMicroservice().equals(microservice)) {
                    edgesSameMicro.add(edge);
                }
            }
        }
        return edgesSameMicro;
    }

    public ArrayList<Vertex> getNodeMethodsBySrcNodeId(String nodeId) {
        ArrayList<Vertex> methods = new ArrayList<>();
        ArrayList<Edge> edges = getEdgesBySrcNodeId(nodeId);
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getTypeRelation().equals("Has")
                        && this.nodes.get(edge.getIdDest()).getType().equalsIgnoreCase("Method")) {
                    methods.add(this.nodes.get(edge.getIdDest()));
                }
            }
        }
        return methods;
    }

    public ArrayList<Vertex> getNodeElementsSameMicroserviceBySrcNodeId(String type, String nodeId, String microservice) {
        ArrayList<Vertex> methods = new ArrayList<>();
        for (Edge edge : getEdgesSameMicroserviceBySrcNodeId(nodeId, microservice)) {
            if (edge.getTypeRelation().equals(type)) {
                methods.add(this.nodes.get(edge.getIdDest()));
            }
        }
        return methods;
    }

    public ArrayList<Vertex> getNodesByMicroservice(String microservice) {
        ArrayList<Vertex> result = new ArrayList<>();

        Collection<Vertex> nodesAux = this.getAllNodes();
        for (Vertex node : nodesAux) {
            if (node.getMicroservice().equals(microservice)) {
                result.add(node);
            }
        }

        return result;
    }

    public ArrayList<Edge> getEdgesByDstNodeId(String nodeId) {
        ArrayList<Edge> result = new ArrayList<>();

        Collection<String> keys = this.nodes.keySet();
        for (String key : keys) {
            ArrayList<Edge> edgesAux = this.getEdgesBySrcNodeId(key);
            if (edgesAux != null) {
                for (Edge edge : edgesAux) {
                    if (edge.getIdDest().equals(nodeId)) {
                        result.add(edge);
                    }
                }
            }

        }

        return result;
    }

    public ArrayList<String> getListMicroservices() {
        ArrayList<String> listMicroservices = new ArrayList<>();
        for (Vertex node : this.getAllNodes()) {
            if (!listMicroservices.contains(node.getMicroservice())) {
                listMicroservices.add(node.getMicroservice());
            }
        }
        return listMicroservices;
    }

    public HashMap<String, ArrayList<Vertex>> getControllers() {
        HashMap<String, ArrayList<Vertex>> controllers = new HashMap();
        for (String microName : getListMicroservices()) {
            controllers.put(microName, new ArrayList<>());
        }
        for (Vertex node : nodes.values()) {
            if (node.getSubType().equalsIgnoreCase("Controller")) {
                controllers.get(node.getMicroservice()).add(node);
            }
        }
        return controllers;
    }

    public HashMap<String, Vertex> getEntitiesProjectWeb() {
        HashMap<String, Vertex> entities = new HashMap();

        for (Vertex node : nodes.values()) {
            if (node.getSubType().equalsIgnoreCase("Controller")) {
                for (Edge edge : this.edges.get(node.getId())) {
                    if (edge.getTypeRelation().equalsIgnoreCase("Uses Class")) {
                        entities.put(this.nodes.get(edge.getIdDest()).getName(), this.nodes.get(edge.getIdDest()));
                    }
                }
            }
        }
        return entities;
    }
    
    public Set<String> getEntitiesByMicroservice(String microService) {
        Set<String> exposedImports = new HashSet<String>();

        Collection<Vertex> nodesAux = this.getAllNodes();
        for (Vertex node : nodesAux) {
            if (node.getMicroservice().equals(microService) && node.getSubType().equalsIgnoreCase("entity")) {
                exposedImports.add(node.getPackageName() +  "." + node.getName());
            }
        }

        return exposedImports;
    }

    public ArrayList<Vertex> getMethodsByClassId(String classId) {
        ArrayList<Vertex> methodByClass = new ArrayList<>();
        if (getNodeByNodeId(classId).getType().equalsIgnoreCase("Class")) {

            ArrayList<Edge> edges = getEdgesBySrcNodeId(classId);
            if (edges != null) {
                Vertex aux;
                for (Edge edge : edges) {
                    if (edge.getTypeRelation().equalsIgnoreCase("Has method")) {
                        aux = getNodeByNodeId(edge.getIdDest());
                        if (aux.getType().equalsIgnoreCase("Method")) {
                            methodByClass.add(aux);
                        }
                    }
                }
            }

        }
        return methodByClass;
    }

    public ArrayList<Vertex> getMethodsByMethodId(String methodId) {
        ArrayList<Vertex> calls = new ArrayList<>();
        if (getNodeByNodeId(methodId).getType().equalsIgnoreCase("Method")) {

            ArrayList<Edge> edges = getEdgesBySrcNodeId(methodId);
            if (edges != null) {
                Vertex aux;
                for (Edge edge : edges) {
                    if (edge.getTypeRelation().equalsIgnoreCase("Calls")) {
                        aux = getNodeByNodeId(edge.getIdDest());
                        if (aux.getType().equalsIgnoreCase("Method")) {
                            calls.add(aux);
                        }
                    }
                }
            }

        }
        return calls;
    }

    public ArrayList<Vertex> getMethodsDistinctMicroservices(String methodId) {
        ArrayList<Vertex> calls = getMethodsByMethodId(methodId);
        ArrayList<Vertex> returns = new ArrayList<>();
        Vertex method = getNodeByNodeId(methodId);
        for (Vertex vertex : calls) {
            if (!vertex.getMicroservice().equalsIgnoreCase(method.getMicroservice())) {
                returns.add(vertex);
            }
        }
        return returns;
    }

    public Vertex getMainByMicroservice(String nameMicro) {
        ArrayList<Vertex> nodes = getNodesByMicroservice(nameMicro);
        for (Vertex vertex : nodes) {
            ArrayList<Edge> edges = getEdgesBySrcNodeId(vertex.getId());
            if (edges != null) {
                for (Edge edge : edges) {
                    if (edge.getTypeRelation().equalsIgnoreCase("Has Annotation")) {
                        if (edge.getIdDest().contains("org.springframework.boot.autoconfigure.SpringBootApplication")) {
                            return vertex;
                        }
                    }
                }
            }
        }
        return null;
    }

    public Vertex getParentByMethodId(String id) {
        ArrayList<Edge> edges = getEdgesByDstNodeId(id);
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getTypeRelation().equalsIgnoreCase("Has Method")) {
                    return getNodeByNodeId(edge.getIdSrc());
                }
            }
        }
        return null;
    }

    ArrayList<Vertex> getFieldsByClassId(String classId) {
        ArrayList<Vertex> fieldsByClass = new ArrayList<>();
        if (getNodeByNodeId(classId).getType().equalsIgnoreCase("Class")) {

            ArrayList<Edge> edges = getEdgesBySrcNodeId(classId);
            if (edges != null) {
                Vertex aux;
                for (Edge edge : edges) {
                    if (edge.getTypeRelation().equalsIgnoreCase("Has field")) {
                        aux = getNodeByNodeId(edge.getIdDest());
                        if (aux.getType().equalsIgnoreCase("Field")) {
                            fieldsByClass.add(aux);
                        }
                    }
                }
            }

        }
        return fieldsByClass;
    }

    boolean needExpose(String repositoryId) {
        boolean result = false;
        ArrayList<Edge> edges = getEdgesBySrcNodeId(repositoryId);
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getTypeRelation().equalsIgnoreCase("has method")) {
                    ArrayList<Edge> edgesMethod = getEdgesByDstNodeId(edge.getIdDest());
                    if(edgesMethod != null) {
                        for (Edge edgeM : edgesMethod) {
                            if (edgeM.getTypeRelation().equalsIgnoreCase("calls")) {
                                Vertex src = getNodeByNodeId(edgeM.getIdSrc());
                                Vertex dst = getNodeByNodeId(edgeM.getIdDest());
                                if (!src.getMicroservice().equals(dst.getMicroservice())) {
                                    result = true;
                                    break;
                                }
                            }
                        }
                        if (result) {
                            break;
                        }
                    }
                    
                }

            }
        }
        return result;
    }
}
