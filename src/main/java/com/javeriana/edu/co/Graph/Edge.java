/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javeriana.edu.co.Graph;

/**
 * This class creates and allows you to manipulate the edge  , taking into account the specifications of the input
 * @author Nelson David Jimenez Ortiz
 * @author Santos David Nuñez Villamil
 * @author Juan Sebastián Prado Valero
 * @author Gustavo Antonio Rivera Delgado
 */
public class Edge {
    
    private String idSrc;
    private String idDest;
    private String typeRelation;
    private String label;

    /**
     * Modify the source Id
     * @param idSrc the new source Id
     */
    public void setIdSrc(String idSrc) {
        this.idSrc = idSrc;
    }
    /**
     * modify the destination Id
     * @param idDest the new Destination Id
     */
    public void setIdDest(String idDest) {
        this.idDest = idDest;
    }
    /**
     * Modify the type relation
     * @param typeRelation the new type relation
     */
    public void setTypeRelation(String typeRelation) {
        this.typeRelation = typeRelation;
    }
    /**
     * Modify the label
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }
    /**
     * Get the label
     * @return the label
     */
    public String getLael(){
      return this.label;
    }
    /**
     * Constructor
     */
    public Edge(){
        
    }
    /**
     * Constructor
     * @param idSrc ID Source
     * @param idDest ID Destination
     * @param typeRelation type of the relation
     * @param label label
     */
    public Edge(String idSrc, String idDest, String typeRelation, String label) {
        this.idSrc = idSrc;
        this.idDest = idDest;
        this.typeRelation = typeRelation;
        this.label = label;
    }
    /**
     * Get the id source
     * @return the id source
     */
    public String getIdSrc() {
        return idSrc;
    }
    /**
     * Get the id destination
     * @return id destination
     */
    public String getIdDest() {
        return idDest;
    }
    /**
     * Get the type relation
     * @return type relation
     */
    public String getTypeRelation() {
        return typeRelation;
    }  
}
