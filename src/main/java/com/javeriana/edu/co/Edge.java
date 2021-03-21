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
public class Edge {
    
    private String idSrc;
    private String idDest;
    private String typeRelation;

    public Edge(String idSrc, String idDest, String typeRelation) {
        this.idSrc = idSrc;
        this.idDest = idDest;
        this.typeRelation = typeRelation;
    }

    public String getIdSrc() {
        return idSrc;
    }

    public String getIdDest() {
        return idDest;
    }

    public String getTypeRelation() {
        return typeRelation;
    }

    
}
