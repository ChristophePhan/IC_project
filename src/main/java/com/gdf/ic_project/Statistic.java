/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdf.ic_project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

/**
 * Statistic
 * @author Tristan
 */
public class Statistic {
    
    // VARIABLES
    
    private RequestManager _rm;
    private String _doid;
    private Model _bpModel;
    private int _limit;
    
    private Long _startTime;
    
    // CONSTRUCTOR
    
    /**
     * Empty constructor of Statistic
     */
    public Statistic() {
        
        try {
            
            this._rm = new RequestManager();
            this._doid = "doid.owl";
            this._bpModel = this._rm.readFile(this._doid);
            this._limit = 200;
            
        } catch (IOException ex) {
            Logger.getLogger(Statistic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // Statistic()
    
    // FUNCTIONS
    
    /**
     * Test human diceases mathing beetween BioPortal and DBpedia
     * /!\ LIMIT to first results !
     * @throws IOException 
     */
    public void matchDiceases() throws IOException {
        
        this.setStartTime(System.currentTimeMillis());
        
        // BioPortal request
        String queryBP = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT DISTINCT ?label\n" +
                "WHERE {\n" +
                "   ?root rdfs:label ?label .\n" +
                "} LIMIT " + this.getLimit();
        //ResultSet resultBP = this.getRm().bioPortalSparqlQuery(this.getBpModel(), queryBP);
        
        // Matching Map
        Map<String,List> matchMap = new HashMap<>();
        
        // Execute the query and obtain results
        Query query = QueryFactory.create(queryBP);
        try (QueryExecution qe = QueryExecutionFactory.create(query, this._bpModel)) {
            
            ResultSet resultBP = qe.execSelect();
            
            int sumNoResults = 0;
            int sumOfResults = 0;
            
            while (resultBP.hasNext()) {
                
                sumOfResults++;
            
                // BioPortal dicease found
                QuerySolution qs = resultBP.nextSolution();
                String dicease = qs.getLiteral("?label").toString().trim()
                        .replaceAll("@en", "").toUpperCase()
                        .replaceAll("[0-9]","")
                        .replaceAll("\\s+", " ").trim()
                        .replaceAll(" AND ", " ")
                        .replaceAll(" OR ", " ")
                        .replaceAll(" ", " AND ")
                        .replaceAll("/", " AND ")
                        .replaceAll("'", "")
                        .replaceAll(",", "")
                        .replaceAll("-", "")
                        .replaceAll("()", "");
                
                // DBpedia request
                String queryDBP = "PREFIX dbo:<http://dbpedia.org/ontology/>\n" +
                    "PREFIX : <http://dbpedia.org/resource/>\n" +
                    "PREFIX pr:<http://xmlns.com/foaf/0.1/>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
                    "SELECT DISTINCT ?s ?label WHERE {\n" +                              
                    "?s rdfs:label ?label . \n"+
                    "?label <bif:contains> \"" + dicease + "\" .\n"+
                    "}";
                //ResultSet resultDBP = this.getRm().dbpediaSparqlQuery(queryDBP);
                List dbpResults = new ArrayList<>();

                // Execute the query and obtain results
                String service = "http://dbpedia.org/sparql";
                try (QueryExecution qeDB = QueryExecutionFactory.sparqlService(service, queryDBP)) {

                    ResultSet resultDBP = qeDB.execSelect();
                    while (resultDBP.hasNext()) {

                        QuerySolution s = resultDBP.nextSolution();
                        dbpResults.add(s.getLiteral("?label").toString().replaceAll("@en", ""));

                    }
                    matchMap.put(dicease, dbpResults);

                } catch (QueryExceptionHTTP e) {

                    sumNoResults++;
                    System.out.println(service + " is DOWN -> DICEASE : " + dicease);

                }
            
            }
            
            // Print result
            this.printMatchDiceasesResult(matchMap, sumNoResults, sumOfResults);
            
        }
        
    } // matchDiceases()
    
    /**
     * Print match diceases results
     * @param results results from BioPortal and DBpedia
     * @param sumNoResults number of results impossible to test
     */
    private void printMatchDiceasesResult(Map results, int sumNoResults, int sumOfResults) {
        
        int sum = 0;
        int sumNullResults = 0;
        
        System.out.println("\n----------------------------------------------------------");
        System.out.println("- Correspondance entre les résultst BioPortal et DBpedia -");
        System.out.println("----------------------------------------------------------\n");
        
        Iterator it = results.entrySet().iterator();
        while (it.hasNext()) {
            
            Map.Entry pair = (Map.Entry)it.next();
            int nbResultsFound = 0;
            String resultsFound = "[";
            
            Iterator<String> resultsIterator = ((List<String>)pair.getValue()).iterator();
            while (resultsIterator.hasNext()) {
                
                    nbResultsFound++;
                    resultsFound += resultsIterator.next();
                    
                    if (resultsIterator.hasNext()) {
                        resultsFound += ", ";
                    }
                    
            }
            resultsFound += "]";
            sum += nbResultsFound;
            if (nbResultsFound == 0) {
                sumNullResults++;
            }
            
            System.out.println(pair.getKey() + " (BioPortal) | " + nbResultsFound + " résultats correspondants (DBpedia) | Résultats " + resultsFound);
            
        }
        
        int average = -1;
        if (results.size() > 0) {
            average = sum/results.size();
        }
        System.out.println("\nNombre de résultats testés : " + sumOfResults);
        System.out.println("Nombre moyen de réultats DBpedia trouvés par rapport aux données BioPortal : " + average);
        System.out.println("Nombre de résultats n'ayant aucune correspondance : " + sumNullResults);
        System.out.println("Nombre de résultats impossible à tester : " + sumNoResults);
        
        // ElapsedTime
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - this.getStartTime();
        System.out.println("\nTemps d'éxécution (matchDiseases function) : " + (elapsedTime*0.001) + " s\n");
        
    } // printMatchDiceasesResult(Map results) 
    
    // GETTER/SETTER

    public RequestManager getRm() {
        return _rm;
    }

    public void setRm(RequestManager _rm) {
        this._rm = _rm;
    }

    public String getDoid() {
        return _doid;
    }

    public void setDoid(String _doid) {
        this._doid = _doid;
    }

    public Model getBpModel() {
        return _bpModel;
    }

    public void setBpModel(Model _bpModel) {
        this._bpModel = _bpModel;
    }

    public Long getStartTime() {
        return _startTime;
    }

    public void setStartTime(Long _startTime) {
        this._startTime = _startTime;
    }

    public int getLimit() {
        return _limit;
    }

    public void setLimit(int _limit) {
        this._limit = _limit;
    }
    
} // class Statistic
