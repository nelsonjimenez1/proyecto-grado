/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co;

/**
 *
 * @author PC
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
    
    public Vertex() {
        
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
    
    public String getMicroservice() {
        return microservice;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMicroservice(String microservice) {
        this.microservice = microservice;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
