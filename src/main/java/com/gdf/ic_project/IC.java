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
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

/**
 *
 * @author chris
 */
public class IC {

    public Model readFile(String inputFileName) throws IOException {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();
        
        // use the FileManager to find the input file
        try (InputStream in = FileManager.get().open(inputFileName)) {
            if (in == null) {
                throw new IllegalArgumentException("File: " + inputFileName + " not found");
            }
        }
        // read file
        return model.read(inputFileName);
    }

    public ResultSet sparqlQuert(Model model, String queryString) throws FileNotFoundException, IOException {

        Query query = QueryFactory.create(queryString);

        ResultSet results;
        // Execute the query and obtain results
        try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
            results = qe.execSelect();
            // Output query results
            ResultSetFormatter.out(System.out, results, query);
        }
        return results;
    }
}
