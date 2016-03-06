/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdf.ic_project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.util.FileManager;

/**
 * IC
 * @author chris
 */
public class IC {

    /**
     * Load OWL file from BioPortal
     * @param inputFileName name of OWL file
     * @return OWL file loaded
     * @throws IOException 
     */
    public Model readFile(String inputFileName) throws IOException {
        
        // Create an empty model
        Model model = ModelFactory.createDefaultModel();
        
        // Use the FileManager to find the input file
        try (InputStream in = FileManager.get().open(inputFileName)) {
            if (in == null) {
                throw new IllegalArgumentException("File: " + inputFileName + " not found");
            }
        }
        
        // Read file
        return model.read(inputFileName);
        
    } // readFile(String inputFileName) throws IOException

    /**
     * Return results from BioPortal request
     * @param model OWL data
     * @param queryString String request
     * @return set of BioPortal request results
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public ResultSet bioPortalSparqlQuery(Model model, String queryString) throws FileNotFoundException, IOException {

        Query query = QueryFactory.create(queryString);

        ResultSet results;
        // Execute the query and obtain results
        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            results = qe.execSelect();
            // Output query results
            ResultSetFormatter.out(System.out, results, query);
        }
        return results;
        
    } // bioPortalSparqlQuery(Model model, String queryString) throws FileNotFoundException, IOException
    
    /**
     * Return results from DBpedia request
     * @param queryString String request
     * @return set of DBpedia request results
     */
    public ResultSet dbpediaSparqlQuery(String queryString) {
        
        String service = "http://dbpedia.org/sparql";
        ResultSet rs = null;
        try (QueryExecution qe = QueryExecutionFactory.sparqlService(service, queryString)) {
            
            rs = qe.execSelect();
            while (rs.hasNext()) {

                QuerySolution s = rs.nextSolution();
                System.out.println(s.getLiteral("?label").toString().replaceAll("@en", ""));

            }
            
        } catch (QueryExceptionHTTP e) {
            
            System.out.println(service + " is DOWN");
            
        }
        
        return rs;
        
    } // dbpediaSparqlQuery(String queryString)
    
} // class IC
