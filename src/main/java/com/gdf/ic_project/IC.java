/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdf.ic_project;

import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

/**
 *
 * @author chris
 */
public class IC {
    
    private final String bioApikey;
    private final String bioService;
    private final String dbpediaService;
    private final String bioOnto;
    
    public IC(String bioApikey, String bioService, String dbpediaService, String bioOnto){
        this.bioApikey = bioApikey;
        this.bioService = bioService;
        this.dbpediaService = dbpediaService;
        this.bioOnto = bioOnto;
    }

    public QueryEngineHTTP sparqlBioQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.bioService, query);
        qexec.addParam("apikey", this.bioApikey);
        //ResultSet results = qexec.execSelect();
        return qexec;
    }
    
    public QueryEngineHTTP sparqlDbpediaQuery(String queryString){
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.dbpediaService, query);
        return qexec;
    }
    
    public void matchingEntities(){
        String queryBio = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT *"
                + "FROM <"+bioOnto+">"
                + "WHERE {"
                + "   ?s rdfs:label ?label ."
                + "}";
        
        QueryEngineHTTP qexec = this.sparqlBioQuery(queryBio);
        ResultSet res = qexec.execSelect();
        ArrayList<String> labels = new ArrayList();
        while(res.hasNext()){
            labels.add(res.next().get("label").toString());
            //System.out.println(res.next().get("label"));
        }
        qexec.close();
        int number = 0;
        for(String label: labels){
            String proper = label.replace(" ", "_");
            proper = proper.replace("'","_");
            proper = proper.replace(",","_");
            proper = proper.replace("/","_");
            proper = proper.replace("(","_");
            proper = proper.replace(")","_");
            proper = proper.replace(".","_");
            proper = proper.replaceAll("[0-9]*", "");
            proper = proper.replaceAll("ü", "u");
            label = label.replaceAll("[0-9]*", "");
            label = label.replaceAll("ü", "u");
            System.out.println(proper);
            String queryDBPedia = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT DISTINCT ?label WHERE {"
                + "?s rdfs:label ?label ."
                + "FILTER (lang(?label) = 'en')."
                + "?label <bif:contains> \'"+proper+"\' ."
                + "FILTER regex(str(?label), \"^"+label+"$\")."
                + "}";
            QueryEngineHTTP qexec2 = this.sparqlDbpediaQuery(queryDBPedia);
            ResultSet res2 = qexec2.execSelect();
            if(res2.hasNext()){
                number++;
                System.out.println(res2.next().get("label")+" aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            }
            qexec2.close();
            System.out.println("--------------------------------------------------");
        }
    }
}
