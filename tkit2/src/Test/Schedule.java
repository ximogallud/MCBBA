/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ximo
 */
class Schedule {
    ArrayList<myBail> bailsToeat = new ArrayList();
    ArrayList<Double[]> positionsToEat =  new ArrayList ();
    ArrayList<Double> arrival_times = new ArrayList();
    Double TotalScore;
    HashMap<ArrayList<String>,Double> TotalScoreCoalitions = new HashMap();

    void clear() {
        bailsToeat.clear();
        positionsToEat.clear();
        arrival_times.clear();
        TotalScore = 0.0;
        TotalScoreCoalitions.clear();
    }
}
