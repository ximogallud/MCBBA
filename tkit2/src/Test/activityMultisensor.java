/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Ximo
 */
public class activityMultisensor {
    ArrayList<myBail> listOfBails = new ArrayList();
    HashMap<myBail, Integer> levelorder = new HashMap();
    HashMap<myBail, Integer> dependencyHash = new HashMap(); 
    HashMap<myBail, Integer> mutualDependent = new HashMap();
    
    DMatrix dmatrix = new DMatrix();
    TMatrix tmatrix = new TMatrix();
    public ArrayList<String> numSensors = new ArrayList();
    public int position;
    public int x;
    public int y;
    public Double lambda = 0.040;
    public Double maximum_score;
    public final Double INF = 100000.0;
    public static Double maxWaitingTime = 2.0;
    public activityMultisensor(ArrayList<String> numSensors){
        this.numSensors.addAll(numSensors);
    };
    public void assignFeatures(){
        Integer m = numSensors.size();
        int count = 0;
        for (int i = 1; i <= m; i++){
           List<Set<String>> subset = getSubsets( numSensors, i);
           for (int k = 0; k < subset.size(); k++){
           
           ArrayList<String> subset1 = new ArrayList(subset.get(k));
           Integer l = subset1.size();
            for(String sensor : subset1){
                        myBail droppedBail = new myBail();
                        droppedBail.score = maximum_score*alphafunction((double)l/m)/m;
                        ArrayList<String> sensorArray = new ArrayList();
                        sensorArray.add(sensor);
                        droppedBail.bail_sensor = sensorArray;
                        droppedBail.position = this.position;
                        droppedBail.x = this.x;
                        droppedBail.y = this.y;
                        droppedBail.myActivity = this;
                        droppedBail.lambda = this.lambda;
                        this.listOfBails.add(droppedBail);
                        this.levelorder.put(droppedBail,i);  
                        this.mutualDependent.put(droppedBail, count);
            }
            count++;
        }
    }
        //fill in the matrices
        for (myBail bail1 : this.listOfBails){
            int Nreq = 0;
            for (myBail bail2 : this.listOfBails){
                
                if (bail1 == bail2){
                    this.dmatrix.put(bail1, bail2, 0);
                    this.tmatrix.put(bail1, bail2, 0.0);
                } else if (mutualDependent.get(bail1) == mutualDependent.get(bail2)){
                    this.dmatrix.put(bail1, bail2, 1);
                    this.tmatrix.put(bail1, bail2, this.maxWaitingTime);
                    Nreq ++;
                } else {
                    this.dmatrix.put(bail1, bail2, -1);
                    this.tmatrix.put(bail1, bail2, INF);
                }
            }
            this.dependencyHash.put(bail1,Nreq);
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
    public ArrayList<myBail> getSameLevelBails(myBail bail) {
        ArrayList<myBail> solution = new ArrayList();
        Integer level = this.levelorder.get(bail);
        for (myBail bailEnquestio : listOfBails){
            if(this.levelorder.get(bailEnquestio) == level){
                solution.add(bailEnquestio);
            }
        }
        return solution;
    }
    
    private double alphafunction(double result) {
        if(numSensors.size() == 2){
        return 2*result*result/3 + result/3;//-2.58333*result + 12.0833*result*result - result*result*result*8.5;    
    }
        else if (numSensors.size() == 3){
          return result/4 + (3*result*result)/4  ;
        }
        else {
            return result;
        }
    
}
}
