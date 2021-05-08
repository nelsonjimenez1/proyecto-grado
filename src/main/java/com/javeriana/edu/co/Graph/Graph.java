package com.javeriana.edu.co.Graph;

import com.javeriana.edu.co.Utils.ExcelUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class creates and allows you to manipulate the graph, taking into account the specifications of the input
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class Graph {

    private HashMap<String, Vertex> nodes;
    private HashMap<String, ArrayList<Edge>> edges;
    private ExcelUtils utils;

    /**
     * Constructor
     */
    public Graph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.utils = new ExcelUtils();
        this.utils.loadNodes(nodes);
        this.utils.loadConnections(edges);
    }
    /**
     * This function allows to obtain all the vertex of the graph.
     * @return an array of all vertex
     */
    public ArrayList<Vertex> getAllNodes() {
        return new ArrayList<Vertex>(this.nodes.values());
    }
    /**
     * This function allows you to obtain a node by the id
     * @param nodeId The ID contained in each node.
     * @return The vertex containing the ID
     */
    public Vertex getNodeByNodeId(String nodeId) {
        return this.nodes.get(nodeId);
    }

    /**
     * Allows you to obtain the nodes corresponding to a source ID.
     * @param nodeId The ID contained in each node is unique.
     * @return a list of edges
     */
    public ArrayList<Edge> getEdgesBySrcNodeId(String nodeId) {
        return this.edges.get(nodeId);
    }
    /**
     * Allows to obtain a list of nodes given the node ID and the name of the microservice.
     * @param nodeId The ID contained in each node.
     * @param microservice the name of the microservice
     * @return a list of edge
     */

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
    /**
     * Allows to obtain the nodes of type method from the origin given the ID of the nodes.
     * @param nodeId The ID contained in each node.
     * @return a list of vertex, the type method
     */
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
    /**
     * Allows to obtain the elements of the same microservice given the ID of a source node
     * @param type name of the type relation
     * @param nodeId The ID contained in each node.
     * @param microservice Name of the microservices
     * @return a list of vertex, the type method
     */
    public ArrayList<Vertex> getNodeElementsSameMicroserviceBySrcNodeId(String type, String nodeId, String microservice) {
        ArrayList<Vertex> methods = new ArrayList<>();
        for (Edge edge : getEdgesSameMicroserviceBySrcNodeId(nodeId, microservice)) {
            if (edge.getTypeRelation().equals(type)) {
                methods.add(this.nodes.get(edge.getIdDest()));
            }
        }
        return methods;
    }
    /**
     * Allows to obtain a list of vertices belonging to a microservice
     * @param microservice name of a microservices
     * @return a list of vertex
     */
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
    /**
     * Allows to obtain a list of edges associated to the node id.
     * @param nodeId The ID of a node
     * @return a list of edges
     */
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
    /**
     * Allows to obtain a list with the name of all the microservices
     * @return a list with the names of the microservices
     */
    public ArrayList<String> getListMicroservices() {
        ArrayList<String> listMicroservices = new ArrayList<>();
        for (Vertex node : this.getAllNodes()) {
            if (!listMicroservices.contains(node.getMicroservice())) {
                listMicroservices.add(node.getMicroservice());
            }
        }
        return listMicroservices;
    }
    /**
     * Allows to obtain HashMap, with the name of the microservice as key and a list of its Controllers
     * @return HashMap 
     */
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
    /**
     * Allows to obtain hashmap, with the entity name as key and a vertex corresponding to the entity.
     * @return HashMap
     */
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
    /**
     * Allows to obtain the entities for a given microservice
     * @param microService name of a microservices
     * @return A set of strings representing the imports
     */
    public Set<String> getEntitiesByMicroservice(String microService) {
        Set<String> exposedImports = new HashSet<String>();

        Collection<Vertex> nodesAux = this.getAllNodes();
        for (Vertex node : nodesAux) {
            if (node.getMicroservice().equals(microService) && node.getType().equalsIgnoreCase("Class") && node.getSubType().equalsIgnoreCase("entity")) {
                exposedImports.add(node.getPackageName() +  "." + node.getName());
            }
        }

        return exposedImports;
    }
    /**
     * Allows to obtain a list of vertex that represent the methods of a class.
     * @param classId id of a class
     * @return a list of vertex
     */
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
    /**
     * Allows to obtain the methods that call a given method given its id.
     * @param methodId id of a method
     * @return a list of vertex
     */
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
    /**
     * Allow to obtain the methods that are other microservices to which the class belongs.
     * @param methodId the id of a method
     * @return A list of vertex representing the methods
     */
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
    /**
     * Allows to obtain the main of a microservice given its name
     * @param nameMicro name of a microservices
     * @return vertex of the main
     */
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
    /**
     * Allows to get the parent of a method given the id of the method
     * @param id 
     * @return a vertex partent 
     */
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
    /**
     * Allows to obtain the list of vertices associated to a class.
     * @param classId the ID of a class 
     * @return A list of vertex
     */
    public ArrayList<Vertex> getFieldsByClassId(String classId) {
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
    /**
     * Allows to determine whether or not a repository should be exposed.
     * @param repositoryId ID the repository
     * @return true: if the repository is to be exposed
     *         false: if the repository should not be exposed
     */
    public boolean needExpose(String repositoryId) {
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
