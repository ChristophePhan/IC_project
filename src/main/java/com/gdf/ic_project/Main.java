/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdf.ic_project;

/**
 *
 * @author chris
 */
public class Main {
    public static void main(String[] args) {

        String bioApikey = "b905a8d6-9ca8-44be-8613-f3ef1fdab3a9";
        String bioService = "http://sparql.bioontology.org/sparql";
        String dbpediaService = "http://dbpedia.org/sparql";
        
        String bioOnto = "http://bioportal.bioontology.org/ontologies/DOID";
        IC ic = new IC(bioApikey, bioService, dbpediaService, bioOnto);
        ic.matchingEntities();
    }
}
