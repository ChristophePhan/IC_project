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
 *
 * @author chris
 */
public class Main {
    public static void main(String[] args) throws IOException{
        //String dbpedia = "doid2.owl";
        String doid = "doid.owl";
        IC ic = new IC();
        //Model m1 = ic.readFile(dbpedia);
        Model m2 = ic.readFile(doid);
        
        String queryString = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT ?s ?label"
                + "WHERE {"
                + "   ?s a owl:Class ."
                + "   ?s rdfs:label ?label ."
                + "}";
   
        ResultSet result = ic.sparqlQuert(m2, queryString);
        System.out.println(result.next());
    }
}
