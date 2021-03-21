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
public class Node {
    private String packageName;
    private String name;
    private String type;
    private String id; 
    private String microservice;

    public Node( String id, String packageName, String name, String type, String microservice) {
        this.id= id;
        this.packageName = packageName;
        this.name = name;
        this.type = type;
        this.microservice = microservice;
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

}
