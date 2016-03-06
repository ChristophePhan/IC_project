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
import org.apache.jena.rdf.model.Resource;
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
    
    // CONSTRUCTOR
    
    /**
     * Empty constructor of Statistic
     */
    public Statistic() {
        
        try {
            
            this._rm = new RequestManager();
            this._doid = "doid.owl";
            this._bpModel = this._rm.readFile(this._doid);
            
        } catch (IOException ex) {
            Logger.getLogger(Statistic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // Statistic()
    
    // FUNCTIONS
    
    /**
     * Test human diceases mathing beetween BioPortal and DBpedia
     * @throws IOException 
     */
    public void matchDiceases() throws IOException {
        
        // BioPortal request
        String queryBP = "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT DISTINCT ?label\n" +
                "WHERE {\n" +
                "   ?root rdfs:label ?label .\n" +
                "}";
        //ResultSet resultBP = this.getRm().bioPortalSparqlQuery(this.getBpModel(), queryBP);
        
        // Matching Map
        Map<String,List> matchMap = new HashMap<>();
        
        // Execute the query and obtain results
        Query query = QueryFactory.create(queryBP);
        try (QueryExecution qe = QueryExecutionFactory.create(query, this._bpModel)) {
            
            ResultSet resultBP = qe.execSelect();
            
            while (resultBP.hasNext()) {
            
                // BioPortal dicease found
                QuerySolution qs = resultBP.nextSolution();
                String dicease = qs.getLiteral("?label").toString()
                        .replaceAll("@en", "").toUpperCase()
                        .replaceAll("\\W", "")
                        .replaceAll(" ", " AND ");

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

                    System.out.println(service + " is DOWN -> DICEASE : " + dicease);

                }
            
            }
            
            // Print result
            this.printMatchDiceasesResult(matchMap);
        
        }
        
    } // matchDiceases()
    
    /**
     * Print match diceases results
     * @param results results from BioPortal and DBpedia
     */
    private void printMatchDiceasesResult(Map results) {
        
        int sum = 0;
        
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
                    
                    if (!resultsIterator.hasNext()) {
                        resultsFound += ", ";
                    }
                    
            }
            resultsFound += "]";
            sum += nbResultsFound;
            
            System.out.println(pair.getKey() + " (BioPortal) | " + nbResultsFound + " résultats correspondants (DBpedia) | Résultats " + resultsFound);
            
        }
        
        int average = -1;
        if (results.size() > 0) {
            average = sum/results.size();
        }
        System.out.println("\nNombre moyen de réultats DBpedia trouvés par rapport aux données BioPortal : " + average + "\n");
        
        System.out.println("----------------------------------------------------------\n");
        
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
    
} // class Statistic
