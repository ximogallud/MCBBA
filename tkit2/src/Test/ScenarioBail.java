/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Test.ScenarioBail.density_tasks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

/**
 *
 * @author Ximo
 */

class ScenarioBail {
    private Integer[] position_20bails_50width1rnd = {1635, 37, 1875, 421, 2479, 
                    493, 51, 1130, 747, 676, 
                    2406, 367, 1755, 6};
    
    private Integer[] position_30bails_50width1rnd = {2167, 688, 2463, 2116, 1258, 1058, 381,
                    576, 2209, 2418, 1036, 1047, 1654, 901, 1304, 60, 2352, 2468, 
                    1852, 1563, 1550, 2436, 2395, 702, 2259, 2112, 213, 569, 691,
                    2079};
    private Integer[] position_6bails_50width1rnd = {1635, 37, 1875, 421, 2479, 
                    493};
    private Integer[] position_20bails_50width2rnd = {183, 59, 30, 189, 51, 56, 6, 54,
                    66, 141, 116, 0, 127, 83, 184, 32, 145, 136, 13};
    
        private Integer[] position_20bails_50width3rnd = {2044, 1517, 2371, 389, 347, 2202, 
            438, 572, 342, 1553, 24, 1796, 1551, 1824, 145, 2393, 1141, 1423, 29, 1736};
    
    private Integer[] position_30bails_50width2rnd = {23, 121, 165, 
                    165, 37, 159, 194, 193, 26, 91, 70, 52, 41, 78, 34, 21, 32, 15,
                    85, 155, 110, 100, 5, 129, 132, 161, 197, 55, 22, 134, 27};
    
    private Integer[] position_6bails_10widthTEST ={0,1,2,3,4,5};
    
    private Integer[] position_4bails_10widthTEST = {2,3,4,5};
    private Integer[] position_2bails_6widthTEST = {2,3};
    private Integer[] position_4bails_8x6TEST = {40,47,0,7};
    private Integer[] position_4bails_8x6TEST2 = {24,38,2,16};
    
    private Double[] scores_20bails_50width1rnd = {187.455, 131.651, 67.7597, 
                        186.502, 160.825, 105.688, 2.32343, 125.893, 73.6295, 147.455, 
                        120.358, 188.971, 194.956, 149.055, 59.5389, 9.34645, 196.908, 
                        92.7041, 68.4271, 168.276};
    
    private Double[] scores_20bails_50width3rnd = {736.575, 948.555, 235.172, 730.699, 658.472, 505.262, 
        599.773, 709.149, 260.434, 516.996, 792.619, 596.485, 910.765, 332.697, 449.355,
        568.324, 873.018, 116.529, 55.7727, 91.6378};
    
    private Double[] scores_30bails_50width1rnd = {76.1379, 29.3561, 18.3347, 16.8708, 
                        44.2585, 124.319, 36.2681, 43.4239, 115.624, 138.057, 8.23456, 
                        119.305, 88.449, 31.43, 94.3445, 115.694, 156.155, 55.1528, 
                        160.419, 108.312, 123.31, 52.7077, 160.215, 48.9236, 3.71654, 
                        38.7977, 138.018, 108.835, 198.072, 142.555};
    
    private Double[] scores_20bails_50width2rnd ={23.3804, 3.8047,
                        85.7677, 196.812, 131.519, 55.1951, 8.85106, 6.5815,
                        191.651, 113.82, 91.8923, 72.5574, 18.8217, 86.9443,
                        69.2315, 99.8864, 44.0406, 178.302, 175.222, 83.2312};
     
    private Double[] scores_30bails_50width2rnd = {125.354, 74.9777, 32.521, 151.381, 108.96, 111.178,
                        9.54617, 193.825, 19.7408, 88.3937, 79.7297, 36.1412,
                        95.4962, 98.1606, 90.3255, 28.7008, 63.5924, 53.7836,
                        166.228, 76.4613, 26.1158, 180.452, 94.5206, 87.1437, 
                        198.043, 121.737, 144.111, 22.6784, 157.888, 103.67};
    
    private Double[] scores_6bails_10widthTEST ={200.0,150.0,100.0,75.0,50.0,25.0};
    
    private Double[] scores_4bails_10widthTEST = {210.0,150.0,120.0,90.0};
    
    private Double[] scores_2bails_6widthTEST = {100.0,100.0};
    private Double[] scores_6bails_50width = {553.801, 635.349, 599.741, 503.918, 206.539, 954.366};
    private Double[] scores_6bails_50widthHDev = {153.801, 135.349, 99.741, 703.918, 806.539, 954.366};
    private Double[] scores_4bails_8x6TEST = {200.0,200.0,200.0,200.0};
    private Double[] scores_4bails_8x6TEST2 = {1000.0,1000.0,1000.0,1000.0};
    
    public List<Double> scores = new ArrayList();
    public List<Integer> positions = new ArrayList();
    public int number_of_bails;
    private utile_functions myFunctions = new utile_functions();
    public HashMap<Integer, ArrayList<String>> bail_sensor = new HashMap<Integer,ArrayList<String>>(); 

    private void adjustMean(List<Double> scores, double d) {
 Double sum = 0.0;
        for (int i = 0; i < scores.size();  i++){
                sum = sum + scores.get(i);
            }
            for (int i = 0; i < scores.size(); i++){
            scores.set(i, scores.get(i)*d/sum);
            
            }    }
    //HashMap<Integer,myBail> panorama = new HashMap<>();
    public enum density_tasks {high,low};
    public enum spatial_concentration {high,low};
    public enum degree_of_modularity {three,two,oneCompthree,oneComptwo};
    public enum score_dispersion{high,low};
    public enum sensors_dispersion{high,low};
    public density_tasks DensTask;
    public spatial_concentration SpatConc;
    public degree_of_modularity Mod;
    public score_dispersion DispScore;
    public sensors_dispersion DispSens;
    
    
    
    public ScenarioBail (int number){
       
        switch (number){
            //Everything random type 1
            case 1:
                //Random scores & positions
                
                Collections.addAll(this.scores, scores_20bails_50width1rnd);
                Collections.addAll(this.positions, this.position_20bails_50width1rnd);
    
                //Random sensors
                for (int i = 0; i < 8; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                    this.bail_sensor.put(i, myList);
                }
                for (int i = 8; i < 17; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                    
                }
                for (int i = 17; i < 19; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                }
            break;    
            //Everything random type 2
            case 2:
                 //Random scores
                Collections.addAll(scores, scores_20bails_50width2rnd);
                Collections.addAll(positions, position_20bails_50width2rnd);
                //Random sensors
                for (int i = 0; i < 5; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                this.bail_sensor.put(i, myList);}
                
                for (int i = 5; i < 14; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("IR");
                this.bail_sensor.put(i, myList);
                    
                }
                for (int i = 14; i < 20; i++){
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                }
            //50 % Tasks with a lot of benefit, and 50% tasks with poor benefit
            //Random sensors  
            break;
            case 3:
                Collections.addAll(this.scores,this.scores_20bails_50width1rnd);
                for (int i = 0; i < 10; i++){
                    this.scores.set(i, this.scores.get(i)*0.2);
                }
                //Random sensors
                Collections.addAll(this.positions,this.position_20bails_50width2rnd);
                //Random sensors
                for (int i = 0; i < 5; i++){
                      ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                
                    this.bail_sensor.put(i, myList);
                }
                for (int i = 5; i < 14; i++){
                      ArrayList<String> myList = new ArrayList<String>();
                
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                    
                }
                for (int i = 14; i < 20; i++){
                      ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                }
                //All tasks are doable
                //All sensors are MW+IR
            break;
            case 4:
                Collections.addAll(this.scores, this.scores_20bails_50width3rnd);
                Collections.addAll(this.positions, this.position_20bails_50width3rnd);
                //Random sensors
                
                for (int i = 0; i < 20; i++){
                       ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                myList.add("IR");
                myList.add("AB");
                    this.bail_sensor.put(i, myList); 
                }
            break;
            case 5:
                Collections.addAll(this.scores,scores_6bails_10widthTEST);
                Collections.addAll(this.positions,this.position_6bails_10widthTEST);
                for (int w = 0; w < 6; w++){
                    ArrayList<String> myArray = new ArrayList<String>();
                    myArray.add("MW");
                    myArray.add("IR");
                    this.bail_sensor.put(w, myArray );
                }
                
            break;
            //TEST
            case 6:
                Collections.addAll(this.scores,scores_4bails_10widthTEST);
                Collections.addAll(this.positions,this.position_4bails_10widthTEST);
                for (int w = 0; w < 4; w++){
                    ArrayList<String> myArray = new ArrayList<String>();
                    myArray.add("MW");
                    myArray.add("IR");
                    this.bail_sensor.put(w, myArray );
                }
            break;
            case 7:
                Collections.addAll(this.scores,scores_4bails_8x6TEST2);
                Collections.addAll(this.positions,this.position_4bails_8x6TEST2);
                for (int w = 0; w < 4; w++){
                    ArrayList<String> myArray = new ArrayList<String>();
                    myArray.add("MW");
                    myArray.add("IR");
                    //myArray.add("AB");
                    this.bail_sensor.put(w, myArray );
                }
            break;
            case 8:
                Collections.addAll(this.scores,scores_4bails_8x6TEST);
                Collections.addAll(this.positions,this.position_4bails_8x6TEST);
                for (int w = 0; w < 4; w++){
                    ArrayList<String> myArray = new ArrayList<String>();
                    myArray.add("MW");
                    myArray.add("IR");
                    myArray.add("AB");
                    this.bail_sensor.put(w, myArray );
                }
            break;
            case 9:
                 Collections.addAll(this.scores, this.scores_20bails_50width3rnd);
                Collections.addAll(this.positions, this.position_20bails_50width1rnd);
                for (int w = 0; w < 20; w++){
                    ArrayList<String> myArray = new ArrayList<String>();
                    myArray.add("MW");
                    myArray.add("IR");
                    myArray.add("AB");
                    this.bail_sensor.put(w, myArray );
                }
            break;
           
    }
    }
    public ScenarioBail(int number, Double mean){
        
        
        for (int i = 0; i < number/3; i++){
                int v = (int) (Math.random()*2499);
                        myBail droppedBail = new myBail();
                        this.scores.add(setRandomScore(mean));
                        this.positions.add(v);
                        
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                    this.bail_sensor.put(i, myList);
                }
                for (int i = number/3 ; i < number*3/4; i++){
                    int v = (int) (Math.random()*2499);
                        myBail droppedBail = new myBail();
                        this.scores.add(setRandomScore(mean));
                        this.positions.add(v);
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
                    
                }
                for (int i = number*3/4; i < number; i++){int v = (int) (Math.random()*2499);
                        myBail droppedBail = new myBail();
                        this.scores.add(setRandomScore(mean));
                        this.positions.add(v);
                ArrayList<String> myList = new ArrayList<String>();
                myList.add("MW");
                myList.add("IR");
                    this.bail_sensor.put(i, myList);
        
    }
}
     public ScenarioBail(density_tasks DensTask, spatial_concentration SpatConc,
             degree_of_modularity Mod, score_dispersion DispScore, sensors_dispersion DispSens, boolean random,
     int scenarioWidth){
         //assignment
         this.DensTask = DensTask;
         this.DispScore = DispScore;
         this.DispSens = DispSens;
         this.Mod = Mod;
         this.SpatConc = SpatConc;
         
         switch (DensTask){
             case high:
                 this.number_of_bails = 30;
                 break;
             case low:
                 this.number_of_bails = 6;
                 break;
         }
         if (SpatConc == spatial_concentration.high){
             if (random == false){
                 if (number_of_bails == 30){
                 Collections.addAll(this.positions,this.position_30bails_50width1rnd);
                 } else {
                 Collections.addAll(this.positions,this.position_6bails_50width1rnd);
                 }
             } else {
                    
                int v1 = (int) (Math.random()*(scenarioWidth-16));
                int v2 = (int) (Math.random()*(scenarioWidth-16));
                v2 = v2*scenarioWidth;
                ArrayList<Integer> concentrPoints = new ArrayList();
                for (int i = 0; i < 15; i++){
                    int k = v2 + scenarioWidth*i;
                    for (int j = k + v1; j < k + v1 + 15 ; j++){
                        concentrPoints.add(j);
                    }
                }
                ArrayList<Integer> allPoints = new ArrayList();
                ArrayList<Integer> diffPoints = new ArrayList();
                for (int i = 0; i < scenarioWidth*scenarioWidth; i++){
                    allPoints.add(i);
                }
                diffPoints.addAll(allPoints);
                diffPoints.removeAll(concentrPoints);
                EnumeratedIntegerDistribution randomizer;
                Double probDiff = (double) 0/diffPoints.size();
                Double probConc = (double) 1/concentrPoints.size();
                double[] probVector = new double[allPoints.size()];
                for (int i = 0; i < allPoints.size(); i ++){
                    if(diffPoints.contains(i)){
                        probVector[i] = probDiff;
                    } else {
                        probVector[i] = probConc;
                    }
                }
                int[] allPointsarray = allPoints.stream().mapToInt(i -> i).toArray();
                EnumeratedIntegerDistribution randomizerPosition;            
                randomizerPosition = new EnumeratedIntegerDistribution(allPointsarray,probVector);
                for (int u = 0; u < this.number_of_bails; u++) {
                    this.positions.add(randomizerPosition.sample());
                            
                } 
             }              
         } else {
              if (random == false){
                 if (number_of_bails == 30){
                 Collections.addAll(this.positions,this.position_30bails_50width1rnd);
                 } else {
                 Collections.addAll(this.positions,this.position_6bails_50width1rnd);
                 }
              } else {
             for (int u = 0; u< this.number_of_bails; u++){
                 int v1 = (int) (Math.random()*(scenarioWidth*scenarioWidth-1));
                 this.positions.add(v1);
             }       
         }
              }
         Double high;
         Double low;
         high = 1200.0;
         low = 300.0;
            /*if (Mod == degree_of_modularity.two || Mod == degree_of_modularity.oneComptwo){
                high = 800.0;
                low = 200.0;
            } else {
                high = 1200.0;
                low = 300.0;      
            }*/
         if (DispScore == score_dispersion.high){
           
            if (random == false){
                
                if(this.number_of_bails == 30){
                    for (int u = 0; u < 6; u++){
                        this.scores_30bails_50width1rnd[u] = this.scores_30bails_50width1rnd[u] + high;
                    }
                        Collections.addAll(this.scores,scores_30bails_50width1rnd);
                } else {
                        Collections.addAll(this.scores,this.scores_6bails_50widthHDev);
                }
            } else {
            int numHigh = (int) ((int) this.number_of_bails*0.2);
            int v = (int) (Math.random()*(this.number_of_bails-numHigh));
            ArrayList<Integer> bailsHigh = new ArrayList<>();
            Double sum = 0.0;
            for (int y = 0; y < this.number_of_bails; y++){
                this.scores.add(Math.random()*low);
                
            }
            for (int i = v; i < v + numHigh; i++){
                this.scores.add(i, Math.random()*low + high);
            
            }
           
            }
            adjustMean(this.scores,30000.0);
         } else {
             if(random == false){
                 if(this.number_of_bails == 30){
                        Collections.addAll(this.scores,scores_30bails_50width1rnd);
                } else {
                        Collections.addAll(this.scores,this.scores_6bails_50width);
                }
             } else {
                    for (int j = 0; j < this.number_of_bails; j++){
                        this.scores.add(Math.random()*(high-low) + high/2);
                    }
                    
         }
             adjustMean(this.scores,30000.0);
         }
         ArrayList<String> sensors = new ArrayList();
         sensors.add("IR");
        
         
         switch(Mod){
             case three:
                 sensors.add("MW");
                 sensors.add("AB");
                 break;
             case two:
                 sensors.add("MW");
                 break;
             default:
                 break;
         }
         
         int count = 0;
         if (DispSens == sensors_dispersion.high){
             for (int j = 1; j <= sensors.size(); j++){
                  List<Set<String>> subset = getSubsets( sensors, j);
                  EnumeratedIntegerDistribution randR;            
                   double[] probVec = new double[subset.size()];
                   int[] mapSubset = new int[subset.size()];
                   for (int t = 0; t < subset.size(); t++){
                       probVec[t] = (double)1/subset.size();
                       mapSubset[t] = t;
                   }
                   randR = new EnumeratedIntegerDistribution(mapSubset,probVec);
                  for (int y = 0; y < (int)this.number_of_bails/sensors.size(); y++){
                      Set<String> thisset = subset.get(randR.sample());
                      ArrayList<String> thisarray = new ArrayList();
                      thisarray.addAll(thisset);
                      this.bail_sensor.put(count, thisarray);
                      count++;
                  }
         }
         } else {
             for (int j = 0; j < this.number_of_bails; j++){
                 this.bail_sensor.put(count,sensors);
                 count++;
             }
         }
     }
  
     private static <T> void getSubsets(List<T> superSet, int k, int idx, Set<T> current,List<Set<T>> solution) {
    //successful stop clause
    if (current.size() == k) {
        solution.add(new HashSet<>(current));
        return;
    }
    //unseccessful stop clause
    if (idx == superSet.size()) return;
    T x = superSet.get(idx);
    current.add(x);
    //"guess" x is in the subset
    getSubsets(superSet, k, idx+1, current, solution);
    current.remove(x);
    //"guess" x is not in the subset
    getSubsets(superSet, k, idx+1, current, solution);
}
    public static <T> List<Set<T>> getSubsets(List<T> superSet, int k) {
    List<Set<T>> res = new ArrayList<>();
    getSubsets(superSet, k, 0, new HashSet<T>(), res);
    return res;
}
     public Double setRandomScore(Double mean){
         
        return Math.random()*mean;
    }
}
    
    