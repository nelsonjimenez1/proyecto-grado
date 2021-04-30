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
    private String label;


    public void setIdSrc(String idSrc) {
        this.idSrc = idSrc;
    }

    public void setIdDest(String idDest) {
        this.idDest = idDest;
    }

    public void setTypeRelation(String typeRelation) {
        this.typeRelation = typeRelation;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLael(){
      return   this.label;
    }
    public Edge(){
        
    }
    public Edge(String idSrc, String idDest, String typeRelation, String label) {
        this.idSrc = idSrc;
        this.idDest = idDest;
        this.typeRelation = typeRelation;
        this.label = label;
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
