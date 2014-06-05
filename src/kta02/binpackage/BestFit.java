/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kta02.binpackage;

import java.util.ArrayList;
import java.util.Collections;
import kta02.domein.Bestelling;

/**
 *
 * @author Solid
 */
public class BestFit {
    
    public static ArrayList<ArrayList<Integer>> BestFit(Bestelling bestelling, ArrayList<Integer> currentRoute){
        
        //* Declare the weight of the bins. And the bins
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<ArrayList<Integer>> bins = new ArrayList<>();
        
        //* Initialize the route and reverse it in order of the TSP Robot
        ArrayList<Integer> route = new ArrayList<>(currentRoute);
        Collections.reverse(route);
        
        //* The best bin to put the Package in
        int bestBin;
        
        //* Start with two bins and one binsize
        sizes.add(0);
        sizes.add(0);
        bins.add(new ArrayList<Integer>());
        bins.add(new ArrayList<Integer>());
        
        //*Looping thourgh the route of items
        for(int item = 0; item < route.size(); item ++){
            bestBin = 0;
            
            //* Let's see if there is a bin not full enough to place the item in
            for(int bin = (bins.size()-2); bin < bins.size(); bin ++){
                if(sizes.get(bin) < sizes.get(bestBin)){
                    bestBin = bin;
                }
            }

            //* Check if there is enough space in a bin. If so: place it in the bin.
            if((sizes.get(bestBin) + bestelling.getArtikelen().get(route.get(item)).getSize()) <= 30){

                //* Add item to the bin with index **Bin
                sizes.set(bestBin, (sizes.get(bestBin) + bestelling.getArtikelen().get(route.get(item)).getSize()));
                bins.get(bestBin).add(route.get(item));
            
            //* If we looped through all the bins, and none good found... Create one. And place the item in it
            }else{

                //* Create new bin, and new binsize
                sizes.add(0);
                bins.add(new ArrayList<Integer>());

                //* Add item to the just created bin and binsize
                sizes.set(sizes.size()-1, sizes.get(sizes.size()-1) + bestelling.getArtikelen().get(route.get(item)).getSize());
                bins.get((bins.size()-1)).add(route.get(item));
            }
        }

        return bins;
    }
    
}
