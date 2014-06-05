/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kta02.tsp;

import kta02.domein.Artikel;
import java.util.ArrayList;

public class Algoritm {

    public static ArrayList<Integer> tourImprovement(ArrayList<Artikel> article, int robotX, int robotY) {
        
        //* Declare route consist indexxes from Article
        ArrayList<Integer> route = new ArrayList<>();

        //* Add indexxes to orgRoute
        ArrayList<Integer> orgRoute = new ArrayList<>();
        for (int i = 0; i < article.size(); i++) {
            orgRoute.add(i);
        }

        //* Caculating variables
        int bestShotItem;
        float bestShotDistance;
        float distance;
        
        //Calculating route from Robot to Nearest Point
        bestShotItem = 0;
        bestShotDistance = 1000;
        for (int i = 0; i < orgRoute.size(); i++) {
            distance = Algoritm.calculateDistanceBetweenItems(0, 0, article.get(i).getLocatie().x, article.get(i).getLocatie().y);
            if (distance < bestShotDistance) {
                bestShotItem = i;
                bestShotDistance = distance;
            }
            
        }
        route.add(orgRoute.get(bestShotItem));
        orgRoute.remove(bestShotItem);

        //Calculate Rest for Robot
        int routeSize = orgRoute.size();
        for (int i = 0; i < routeSize; i++) {
            bestShotItem = 0;
            bestShotDistance = 1000;
            for (int y = 0; y < orgRoute.size(); y++) {

                distance = Algoritm.calculateDistanceBetweenItems(article.get(route.size() - 1).getLocatie().x, article.get(route.size() - 1).getLocatie().y, article.get(i).getLocatie().x, article.get(i).getLocatie().y);
                if (distance < bestShotDistance) {
                    bestShotItem = y;
                    bestShotDistance = distance;
                }
            }
            route.add(orgRoute.get(bestShotItem));
            orgRoute.remove(bestShotItem);
        }
        
        route = swapPoints(route, article);
        return route;
    }

    public static float calculateDistanceBetweenItems(int x1, int y1, int x2, int y2) {
        float distance;
        if(x1 == x2 && y1 == y2){
            return 0;
        }
        //Calculate Distance
        distance = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        return distance;
    }
    public static ArrayList<Integer> swapPoints(ArrayList<Integer> route, ArrayList<Artikel> article) {
        
        //* Calculation variables
        int swapValue;
        float distance1;
        float distance2;
        for (int i = 0; i < route.size() - 3; i++) {
            
            //* Calculate distance through the points
            distance1 = calculateDistanceBetweenItems(article.get(route.get(i)).getLocatie().x, article.get(route.get(i)).getLocatie().y, article.get(route.get(i + 1)).getLocatie().x, article.get(route.get(i + 1)).getLocatie().y)
                    + calculateDistanceBetweenItems(article.get(route.get(i + 1)).getLocatie().x, article.get(route.get(i + 1)).getLocatie().y, article.get(route.get(i + 2)).getLocatie().x, article.get(route.get(i + 2)).getLocatie().y)
                    + calculateDistanceBetweenItems(article.get(route.get(i + 2)).getLocatie().x, article.get(route.get(i + 2)).getLocatie().y, article.get(route.get(i + 3)).getLocatie().x, article.get(route.get(i + 3)).getLocatie().y);
            
            //* Calculate distance through the points ## WHILE swapped
            distance2 = calculateDistanceBetweenItems(article.get(route.get(i)).getLocatie().x, article.get(route.get(i)).getLocatie().y, article.get(route.get(i + 2)).getLocatie().x, article.get(route.get(i + 2)).getLocatie().y)
                    + calculateDistanceBetweenItems(article.get(route.get(i + 1)).getLocatie().x, article.get(route.get(i + 1)).getLocatie().y, article.get(route.get(i + 2)).getLocatie().x, article.get(route.get(i + 2)).getLocatie().y)
                    + calculateDistanceBetweenItems(article.get(route.get(i + 1)).getLocatie().x, article.get(route.get(i + 1)).getLocatie().y, article.get(route.get(i + 3)).getLocatie().x, article.get(route.get(i + 3)).getLocatie().y);
            
            //* If distance is shorter. Swap points.. 
            if (distance2 < distance1) {
                //* Swap the to indexxes
                swapValue = route.get(i + 1);
                route.set(i + 1, route.get(i + 2));
                route.set(i + 2, swapValue);
                
                //* If there was improvement, also repeat this process.
                route = swapPoints(route, article);
                return route;
            }
        }
        
        return route;
    }

}
