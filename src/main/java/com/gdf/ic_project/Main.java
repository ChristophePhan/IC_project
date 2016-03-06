/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdf.ic_project;

import java.io.IOException;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

/**
 * Main
 * @author chris
 */
public class Main {
    
    public static void main(String[] args) throws IOException {
        
        Statistic s = new Statistic();
        s.matchDiceases();
        
    } // main(String[] args) throws IOException
    
    /**
     * Test BioPortal request
     * @throws IOException 
     */
    public static void testBioPortal() throws IOException {
        
        String doid = "doid.owl";
        RequestManager ic = new RequestManager();
        Model bpModel = ic.readFile(doid);
        
        String queryBP = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT DISTINCT ?root ?label\n" +
                "WHERE {\n" +
                "   ?root a owl:Class .\n" +
                "   ?root rdfs:label ?label .\n" +
                "}";
        System.out.println("\n-----------------------------------");
        System.out.println("-------- BIOPORTAL RESULTS --------");
        System.out.println("-----------------------------------\n");
        ResultSet resultBP = ic.bioPortalSparqlQuery(bpModel, queryBP);
        
    } // testBioPortal()
    
    /**
     * Test DBpedia request
     * @throws IOException 
     */
    public static void testDBpedia() throws IOException {
        
        RequestManager ic = new RequestManager();
        
        String queryDBP = "PREFIX dbo:<http://dbpedia.org/ontology/>\n" +
                "PREFIX : <http://dbpedia.org/resource/>\n" +
                "PREFIX pr:<http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                "SELECT DISTINCT ?s ?label WHERE {\n" +                              
                "?s rdfs:label ?label . \n"+
                "?label <bif:contains> \"" + "BONE AND DISEASE" + "\" .\n"+
                "}";
        System.out.println("\n-----------------------------------");
        System.out.println("--------- DBPEDIA RESULTS ---------");
        System.out.println("-----------------------------------\n");
        ResultSet resultDBP = ic.dbpediaSparqlQuery(queryDBP);
        
    } // testDBpedia()
    
} // class Main
