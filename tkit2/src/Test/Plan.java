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
class Plan {
    ArrayList<ArrayList<myBail>> bailOrder = new ArrayList();
    ArrayList<ArrayList<Double[]>> positions = new ArrayList();
    ArrayList<Double> utility = new ArrayList();
    ArrayList<ArrayList<Double>> arrival_times = new ArrayList();
    ArrayList<ArrayList<Double>> fracutility = new ArrayList();
    HashMap<ArrayList<String>,Double> utilityCoalitions = new HashMap();
    ArrayList<HashMap<ArrayList<String>,ArrayList<Double>>> fracutilityCoalitions = new ArrayList();
}
