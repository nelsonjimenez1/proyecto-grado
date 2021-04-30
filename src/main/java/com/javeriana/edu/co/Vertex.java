/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;
/**
 * This class creates and allows you to manipulate the vertex, taking into account the specifications of the input
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class Vertex {
    private String packageName;
    private String name;
    private String type;
    private String subType;
    private String id; 
    private String microservice;
    private String label;
    
    // Informacion adicional necesaria
    // atributos para duplicar la clase
    // Si se parte la clase
    // Especificaciones de divisiones

    /*
    public Node( String id, String packageName, String name, String type, String microservice) {
        this.id= id;
        this.packageName = packageName;
        this.name = name;
        this.type = type;
        this.microservice = microservice;
    }*/
    /**
     * Contructor
     */
    public Vertex() {
        
    }
    /**
     * Get the package name
     * @return package name
     */
    public String getPackageName() {
        return packageName;
    }
    /**
     * Get the name of vertex
     * @return name 
     */
    public String getName() {
        return name;
    }
    /**
     * Get the type of vertex
     * @return type sucha as fiel, class, Annotation, method
     */
    public String getType() {
        return type;
    }
    /**
     * Get the id of vertex
     * @return id
     */
    public String getId() {
        return id;
    }
    /**
     * Get the name of microservices
     * @return name microservices
     */
    
    public String getMicroservice() {
        return microservice;
    }
    /**
     * Modify the name package 
     * @param packageName new name package 
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    /**
     * Modify the name of vertex
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Modify the type of vertex
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * Modify the id 
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * Modify the microservice name
     * @param microservice the new microservice name 
     */
    public void setMicroservice(String microservice) {
        this.microservice = microservice;
    }
    /**
     * Get the sub type of vertex
     * @return sub type, for example:entity, controller, repository or "-" if it has no subtype
     */
    public String getSubType() {
        return subType;
    }
    /**
     * Modify the sub type of vertex
     * @param subType the new sub type 
     */
    public void setSubType(String subType) {
        this.subType = subType;
    }
    /**
     * Get label
     * @return labal of vertex
     */
    public String getLabel() {
        return label;
    }
    /**
     * Modify label 
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
