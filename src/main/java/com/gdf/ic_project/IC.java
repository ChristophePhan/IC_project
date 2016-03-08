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
    
    private String bioApikey;
    private String bioService;
    private String dbpediaService;
    private String bioOnto;
    private int limit;
    
    public IC(String bioApikey, String bioService, String dbpediaService, String bioOnto){
        this.bioApikey = bioApikey;
        this.bioService = bioService;
        this.dbpediaService = dbpediaService;
        this.bioOnto = bioOnto;
        this.limit = 200;
    }
    
    public IC(String bioApikey, String bioService, String dbpediaService, String bioOnto, int limit){
        this.bioApikey = bioApikey;
        this.bioService = bioService;
        this.dbpediaService = dbpediaService;
        this.bioOnto = bioOnto;
        this.limit = limit;
    }

    public ResultSet sparqlBioQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.bioService, query);
        qexec.addParam("apikey", this.bioApikey);
        ResultSet results = qexec.execSelect();
        return results;
    }
    
    public ResultSet sparqlDbpediaQuery(String queryString){
        Query query = QueryFactory.create(queryString);
        QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.dbpediaService, query);
        ResultSet results = qexec.execSelect();
        return results;
    }
    
    public void matchingEntities(){
        long startTime = System.currentTimeMillis();
        
        String queryBio = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT *"
                + "FROM <"+bioOnto+">"
                + "WHERE {"
                + "   ?s rdfs:label ?label ."
                + "} LIMIT" + this.limit;
        
        ResultSet res1 = this.sparqlBioQuery(queryBio);
        ArrayList<String> labels = new ArrayList();
        while(res1.hasNext()){
            labels.add(res1.next().get("label").toString());
            //System.out.println(res.next().get("label"));
        }
        int number = 0;
        int noMatch = 0;
        for(String label: labels){
            String proper = label.replace(" ", "_");
            proper = proper.replace("'","_");
            proper = proper.replace(",","_");
            proper = proper.replace("/","_");
            proper = proper.replace("(","_");
            proper = proper.replace(")","_");
            proper = proper.replace(".","_");
            proper = proper.replaceAll("[0-9]*", "");
            proper = proper.replace("ü", "u");
            proper = proper.replace("<", "");
            proper = proper.replace(">", "");
            proper = proper.replace("-", "_");
            proper = proper.replace("+", "_");
            proper = proper.replace(";", "_");
            label = label.replace("ü", "u");
            label = label.replace("+", ".");
            //System.out.println(proper);
            //System.out.println(label+" bbbbbbbbbbbbb");
            String queryDBPedia = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT DISTINCT ?label WHERE {"
                + "?s rdfs:label ?label ."
                + "FILTER (lang(?label) = 'en')."
                + "?label <bif:contains> \'"+proper+"\' ."
                + "FILTER regex(str(?label), \"^(?i)"+label+"$\")."
                + "}";
            ResultSet res2 = this.sparqlDbpediaQuery(queryDBPedia);
            if(res2.hasNext()){
                number++;
                //System.out.println(res2.next().get("label")+" aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            } else {
                noMatch++;
            }
            //System.out.println("--------------------------------------------------");
        }
        double average = ((double)number/(double)labels.size())*100.0;
        System.out.println("\nPourcentage de correspondance exacte trouvé sur DBPedia par rapport à l'ontologie DOID : "+ average+"%");
        System.out.println("Nombre de résultats testés : "+ this.limit);
        /**
         * /!\ il est possible que le filtre regex ne match pas alors qu'il y a un résultat qui correspond 
         */
        System.out.println("Nombre de résultats sans correspondance : "+noMatch);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("\nTemps d'éxécution (matchingEntities function) : " + (elapsedTime*0.001) + " s\n");
    }
    
    public void matchingExactEntities(){
        long startTime = System.currentTimeMillis();
        
        String queryBio = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT *"
                + "FROM <"+bioOnto+">"
                + "WHERE {"
                + "   ?s rdfs:label ?label ."
                + "} LIMIT" + this.limit;
        
        ResultSet res1 = this.sparqlBioQuery(queryBio);
        ArrayList<String> labels = new ArrayList();
        while(res1.hasNext()){
            labels.add(res1.next().get("label").toString());
            //System.out.println(res.next().get("label"));
        }
        int number = 0;
        int noMatch = 0;
        for(String label: labels){
            String proper = label.replace(" ", "_");
            proper = proper.replace("'","_");
            proper = proper.replace(",","_");
            proper = proper.replace("/","_");
            proper = proper.replace("(","_");
            proper = proper.replace(")","_");
            proper = proper.replace(".","_");
            proper = proper.replaceAll("[0-9]*", "");
            proper = proper.replace("ü", "u");
            proper = proper.replace("<", "");
            proper = proper.replace(">", "");
            proper = proper.replace("-", "_");
            proper = proper.replace("+", "_");
            proper = proper.replace(";", "_");
            label = label.replace("ü", "u");
            label = label.replace("+", ".");
            //System.out.println(proper);
            //System.out.println(label+" bbbbbbbbbbbbb");
            String queryDBPedia = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT DISTINCT ?label WHERE {"
                + "?s rdfs:label ?label ."
                + "FILTER (lang(?label) = 'en')."
                + "?label <bif:contains> \'"+proper+"\' ."
                + "FILTER regex(str(?label), \"^(?i)"+label+"$\")."
                + "}";
            ResultSet res2 = this.sparqlDbpediaQuery(queryDBPedia);
            if(res2.hasNext()){
                res2.next();
                if(!res2.hasNext()) {
                    number++;
                }
                //System.out.println(res2.next().get("label")+" aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            } else {
                noMatch++;
            }
            //System.out.println("--------------------------------------------------");
        }
        double average = ((double)number/(double)labels.size())*100.0;
        System.out.println("\nPourcentage de correspondance exacte trouvé sur DBPedia par rapport à l'ontologie DOID : "+ average+"%");
        System.out.println("Nombre de résultats testés : "+ this.limit);
        /**
         * /!\ il est possible que le filtre regex ne match pas alors qu'il y a un résultat qui correspond 
         */
        System.out.println("Nombre de résultats sans correspondance : "+noMatch);
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("\nTemps d'éxécution (matchingEntities function) : " + (elapsedTime*0.001) + " s\n");
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
