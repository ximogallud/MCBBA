/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

/**
 *
 * @author Ximo
 */
public class ScenarioBug {
    private Integer[] position_3bugs_50width1rnd = {1905, 1178, 725};
    private Integer[] position_3bugs_50width2rnd = {1763, 2142, 58};
    private Integer[] position_2bugs_50width1rnd = {1763, 2142};
    private Integer[] position_3bugs_50width3rnd = {702, 1920, 910};
    private Integer[] position_4bugs_50width1rnd = {702, 1920, 910, 45};
    private Integer[] position_2bugs_10widthTEST = {9,90};
    private Integer[] position_2bugs_10widthTESTB = {0,9};
    private Integer[] position_2bugs_6widthTESTB = {0,5};
    private Integer[] position_3bugs_10widthTESTB = {0,8,9};
    private Integer[] position_4bugs_8x6TEST = {44,3,31,19};
    private Integer[] position_3bugs_8x6TEST = {44,25,4};
    private Integer[] position_6bugs_50width1rnd = {702, 1920, 910, 45, 78, 321};
    private Integer[] position_9bugs_50width1rnd = {702, 1920, 910, 45, 78, 321, 1496, 1001,781};
    private Integer[] position_9bugs_50width1TEST = {702, 702,702, 1920, 1920,1920, 910,910,910};
    private Integer[] position_6bugs_50width1TEST = {702, 702, 1920, 1920, 910,910};
    private Integer[] position_3bugs_50width1TEST = {702, 1920,910};
    
    public int number_of_bugs;
    public ArrayList possible_sensors = new ArrayList();
    public HashMap<Integer,ArrayList<String>> actual_sensors = new HashMap();
    public ArrayList<Integer> positions = new ArrayList();
    public ArrayList<Double> money = new ArrayList();
    public ArrayList<Double> cost_movement = new ArrayList();
    public ArrayList<Double> cost_photo = new ArrayList();

    
    public enum density_bugs {high,medium,low,three,two};
    public enum sensors_dispersion_bug {high,low};
    public enum degree_of_modularity_bug {three,two,one};
    public enum cost_of_moving {high,low,superlow};
    public enum cost_of_moving_dispersion {high,low,superlow};
    public enum photo_cost {high, low,superlow};
    public enum photo_cost_dispersion {high,low,superlow};
    public enum resources_quantity {high,low,superlow};
    public enum resources_quantity_dispersion{high,low};
    public density_bugs DensBug;
    public sensors_dispersion_bug SensDisp;
    public degree_of_modularity_bug Mod;
    public cost_of_moving CostMov;
    public cost_of_moving_dispersion CostDisp;
    public photo_cost PhotoCost;
    public photo_cost_dispersion PhotoCostDisp;
    public resources_quantity ResQuan;
    public resources_quantity_dispersion ResQuanDisp;
    
    public ScenarioBug (int i){
        switch (i){
            //Random fixed Position, random sensors 1
            case 1:
                Collections.addAll(this.positions, this.position_3bugs_50width1rnd);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList1 = new ArrayList<String>();
                myList1.add("MW");
                ArrayList<String> myList2 = new ArrayList<String>();
                myList2.add("IR");
                ArrayList<String> myList21 = new ArrayList<String>();
                //myList21.add("IR");
                myList21.add("MW");
                this.actual_sensors.put(0, myList1);
                this.actual_sensors.put(1,myList2);
                this.actual_sensors.put(2,myList21);
                Double[] myMoney = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney);
                Double[] myCost = {1.5,1.5,1.5};
                Collections.addAll(this.cost_movement,myCost);
                Double[] myPhotoCost = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost);
            // Case 1.1 : Random variable Positions, use  random option from Turtle sensors 1 --->
            //Supersensors, cost movement is a lot;
            break;
            case 2:
                
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList3 = new ArrayList<String>();
                myList3.add("SMW");
                ArrayList<String> myList4 = new ArrayList<String>();
                myList4.add("SIR");
               
                    
                this.actual_sensors.put(0, myList3);
                this.actual_sensors.put(1,myList3);
                this.actual_sensors.put(2,myList4);
                Double[] myMoney2 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney2);
                Double[] myCost2 = {3.0,3.0,3.0};
                Collections.addAll(this.cost_movement,myCost2);
                Double[] myPhotoCost2 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost2);
                         
            //Two type sensors, cost movement is a lot;
             break;   
            case 3:
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList5 = new ArrayList<String>();
                myList5.add("MW");
                myList5.add("IR");
                
                this.actual_sensors.put(0, myList5);
                this.actual_sensors.put(1,myList5);
                this.actual_sensors.put(2,myList5);
                Double[] myMoney3 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney3);
                Double[] myCost3 = {3.0,3.0,3.0};
                Collections.addAll(this.cost_movement,myCost3);
                Double[] myPhotoCost3 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost3);
             break;   
            //Agile agents, but no good sensors;   
            case 4: 
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList6 = new ArrayList<String>();
                myList6.add("IMW");
                ArrayList<String> myList7 = new ArrayList<String>();
                myList7.add("IIR");
                
                this.actual_sensors.put(0, myList6);
                this.actual_sensors.put(1,myList6);
                this.actual_sensors.put(2,myList7);
                Double[] myMoney4 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney4);
                Double[] myCost4 = {1.5,1.5,1.5};
                Collections.addAll(this.cost_movement,myCost4);
                Double[] myPhotoCost4 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost4);
             break;   
               
            //Heterogeneous costs / normal sensors    
            case 5: 
                
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList8 = new ArrayList<String>();
                myList8.add("MW");
                ArrayList<String> myList9 = new ArrayList<String>();
                myList9.add("IR");
             
                this.actual_sensors.put(0, myList8);
                this.actual_sensors.put(1,myList8);
                this.actual_sensors.put(2,myList9);
                Double[] myMoney5 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney5);
                Double[] myCost5 = {0.75,1.5,3.0};
                Collections.addAll(this.cost_movement,myCost5);
                Double[] myPhotoCost5 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost5);
             break;   
            case 6: 
                Collections.addAll(this.positions,this.position_2bugs_6widthTESTB);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList10 = new ArrayList<String>();
                ArrayList<String> myList13 = new ArrayList<String>();
                myList13.add("MW");
                myList10.add("IR");
             
                this.actual_sensors.put(0, myList10);
                this.actual_sensors.put(1, myList13);
                Double[] myMoney6 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney6);
                Double[] myCost6 = {1.0,1.0,1.0};
                Collections.addAll(this.cost_movement,myCost6);
                Double[] myPhotoCost6 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost6);
            break;   
            //TEST
            case 7:
                Collections.addAll(this.positions,this.position_3bugs_10widthTESTB);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList11;
                myList11 = new ArrayList<>();
                ArrayList<String> myListnothing;
               myListnothing = new ArrayList<>();
               myListnothing.add("MW");
                myList11.add("MW");
                myList11.add("IR");
                ArrayList<String> myList12;
                myList12 = new ArrayList<>();
                myList12.add("IR");
                this.actual_sensors.put(0, myList12);
                this.actual_sensors.put(1, myListnothing );
                this.actual_sensors.put(2, myList11);
                Double[] myMoney7 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney7);
                Double[] myCost7 = {1.0,1.0,1.0};
                Collections.addAll(this.cost_movement,myCost7);
                Double[] myPhotoCost7 = {5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost7);
                break;
            case 8:
                Collections.addAll(this.positions,position_4bugs_8x6TEST);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList81 = new ArrayList<String>();
              
                myList81.add("MW");
                
                ArrayList<String> myList82 = new ArrayList<String>();
                
                myList82.add("IR");
                
                this.actual_sensors.put(0, myList82);
                this.actual_sensors.put(1, myList82 );
                this.actual_sensors.put(2, myList81);
                this.actual_sensors.put(3, myList81);
                /*this.actual_sensors.put(4, myList82);
                this.actual_sensors.put(5, myList82 );
                this.actual_sensors.put(6, myList81);
                this.actual_sensors.put(7, myList81);*/
                
                Double[] myMoney8 = {1250.0,1250.0,1250.0,1250.0};//,1250.0,1250.0,1250.0,1250.0};
                Collections.addAll(this.money,myMoney8);
                Double[] myCost8 = {1.5,1.5,1.5,1.5};//1.5,1.5,1.5,1.5};
                Collections.addAll(this.cost_movement,myCost8);
                Double[] myPhotoCost8 = {5.0,5.0,5.0,5.0};//,5.0,5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost8);
                break;
                
                case 9:
                Collections.addAll(this.positions, this.position_3bugs_8x6TEST);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList91 = new ArrayList<String>();
                myList91.add("MW");
                ArrayList<String> myList92 = new ArrayList<String>();
                myList92.add("IR");
                ArrayList<String> myList93 = new ArrayList<String>();
                //myList21.add("IR");
                myList93.add("AB");
                this.actual_sensors.put(0, myList91);
                this.actual_sensors.put(1,myList92);
                this.actual_sensors.put(2,myList93);
                Double[] myMoney9 = {125.0,125.0,125.0};
                Collections.addAll(this.money,myMoney9);
                Double[] myCost9 = {0.5,0.5,0.5};
                Collections.addAll(this.cost_movement,myCost9);
                Double[] myPhotoCost9 = {2.0,2.0,2.0};
                Collections.addAll(this.cost_photo,myPhotoCost9);
                break;
                
                case 10:
                Collections.addAll(this.positions,this.position_4bugs_50width1rnd);
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList101 = new ArrayList<String>();
              
                myList101.add("MW");
                
                ArrayList<String> myList102 = new ArrayList<String>();
                
                myList102.add("IR");
                ArrayList<String> myList103 = new ArrayList<String>();
                
                myList103.add("AB");
                
                
                this.actual_sensors.put(0, myList102);
                this.actual_sensors.put(1, myList101 );
                this.actual_sensors.put(2, myList103);
                this.actual_sensors.put(3, myList102);
                Double[] myMoney10 = {1250.0,1250.0,1250.0,1250.0};
                Collections.addAll(this.money,myMoney10);
                Double[] myCost10 = {1.0,1.0,1.0,1.0};
                Collections.addAll(this.cost_movement,myCost10);
                Double[] myPhotoCost10 = {5.0,5.0,5.0,5.0};
                Collections.addAll(this.cost_photo,myPhotoCost10);
                break;
                case 11:
                    Collections.addAll(this.positions,this.position_2bugs_10widthTEST);
                this.actual_sensors = new HashMap<>();
                this.actual_sensors = new HashMap<>();
                ArrayList<String> myList110 = new ArrayList<String>();
                myList110.add("MW");
                myList110.add("IR");
                
                this.actual_sensors.put(0, myList110);
                this.actual_sensors.put(1,myList110);
                
                Double[] myMoney11 = {2500.0,2500.0};
                Collections.addAll(this.money,myMoney11);
                Double[] myCost11 = {3.0,3.0};
                Collections.addAll(this.cost_movement,myCost11);
                Double[] myPhotoCost11 = {10.0,10.0};
                Collections.addAll(this.cost_photo,myPhotoCost11);
             break;   
    }
}
     public ScenarioBug (density_bugs DensBug, sensors_dispersion_bug SensDisp, degree_of_modularity_bug Mod,
             cost_of_moving CostMov, cost_of_moving_dispersion CostDisp, photo_cost PhotoCost,
             photo_cost_dispersion PhotoCostDisp, resources_quantity ResQuan, resources_quantity_dispersion ResQuanDisp){
            this.DensBug = DensBug;
            this.SensDisp = SensDisp;
            this.Mod = Mod;
            this.CostDisp = CostDisp;
            this.PhotoCost = PhotoCost;
            this.ResQuan = ResQuan;
            this.PhotoCostDisp = PhotoCostDisp;
         
         switch (DensBug){
             case high:
                 this.number_of_bugs = 9;
                 Collections.addAll(this.positions, this.position_9bugs_50width1TEST);
                 break;
             case medium:
                 this.number_of_bugs = 6;
                 Collections.addAll(this.positions, this.position_6bugs_50width1TEST);
                 break;
             case low:
                 this.number_of_bugs = 4;
                 Collections.addAll(this.positions, position_4bugs_50width1rnd);
                 break;
             case three:
                 this.number_of_bugs = 3;
                 Collections.addAll(this.positions, position_3bugs_50width1TEST);
                 break;
             case two:
                 this.number_of_bugs = 2;
                 Collections.addAll(this.positions, position_2bugs_50width1rnd);
                 
         }
         if (SensDisp == sensors_dispersion_bug.high){
             if(Mod == degree_of_modularity_bug.three){
                 for (int i = 0; i < (int) this.number_of_bugs*0.6; i++){
                     ArrayList<String> sensors = new ArrayList();
                     sensors.add("IR");
                     this.actual_sensors.put(i,sensors);
                 }
                
                for (int i = (int) (this.number_of_bugs*0.6) ; i < (int) this.number_of_bugs*0.8; i++){
                   ArrayList<String> sensors = new ArrayList();
                     sensors.add("MW");
                     this.actual_sensors.put(i,sensors);
                    
                }
                for (int i = (int) (this.number_of_bugs*0.8) ; i < (int) this.number_of_bugs; i++){
                   ArrayList<String> sensors = new ArrayList();
                     sensors.add("AB");
                     this.actual_sensors.put(i,sensors);
                    
                }
             } else if (Mod == degree_of_modularity_bug.two) {
                 for (int i = 0; i < (int) this.number_of_bugs*0.7; i++){
                     ArrayList<String> sensors = new ArrayList();
                     sensors.add("IR");
                     this.actual_sensors.put(i,sensors);
                 }
                 for (int i = (int) (this.number_of_bugs*0.7); i < (int) this.number_of_bugs; i++){
                     ArrayList<String> sensors = new ArrayList();
                     sensors.add("MW");
                     this.actual_sensors.put(i,sensors);
                 }
                 
             } else {
                 for (int i = 0; i < this.number_of_bugs; i++){
                     ArrayList<String> sensors = new ArrayList();
                     sensors.add("IR");
                     this.actual_sensors.put(i,sensors);
                 }
             }
         } else {
         ArrayList<String> sensors = new ArrayList();
         sensors.add("IR");
         
         int modularity;
         switch(Mod){
             case three:
                 modularity = 3;
                 sensors.add("MW");
                 sensors.add("AB");
                 break;
             case two:
                 modularity = 2;
                 sensors.add("MW");
                 break;
             default:
                 modularity = 1;
                 break;
         }
         for (int i = 0; i < this.number_of_bugs; i++){
             ArrayList<String> sensorsBug = new ArrayList();
             int a = i % modularity;
             sensorsBug.add(sensors.get(a));
             this.actual_sensors.put(i,sensorsBug);
         }
         }
        for (int i = 0; i < this.number_of_bugs; i++){
            Double cost;
            Double cost_photo;
            Double resources;
        if (CostMov == cost_of_moving.high){
            cost = 4.0;
        } else if (CostMov == cost_of_moving.low){
            cost = 2.0;
        } else{
            cost = 1.3333333333;
        }
        if (PhotoCost == photo_cost.high){
            cost_photo = 8.0;
        } else if (PhotoCost == photo_cost.low){
            cost_photo = 4.0;
        } else {
            cost_photo = 2.6666666666667;
        }
        if (ResQuan == resources_quantity.high){
            resources = 700.0;
        } else if (ResQuan == resources_quantity.low){
            resources = 350.0;
        } else{
            resources = 233.33333333333;
        }
        this.money.add(resources);
        this.cost_photo.add(cost_photo);
        this.cost_movement.add(cost);
        }
        
         if(CostDisp == cost_of_moving_dispersion.high){
             
         modifyVector(this.cost_movement, true);
         } else {
             modifyVector(this.cost_movement, false);
         }
         if (PhotoCost == photo_cost.high){
             modifyVector(this.cost_photo, true);
         } else {
             modifyVector(this.cost_photo, false);
         }
         if (ResQuanDisp == resources_quantity_dispersion.high){
             modifyVector(this.money, true);
         } else {
             modifyVector(this.money, false);
         }
         
     }
         
 private void modifyVector(ArrayList<Double> cost_photo, boolean b) {
ArrayList<Double> factors = new ArrayList();
        
         if(b == true){
            
            factors.add(0.50);
            factors.add(1.0);
            factors.add(2.0);
         } else {
                    factors.add(1.0);
                    factors.add(1.0);
                    factors.add(1.0);
                    }
            double[] probVector = new double[3];
            probVector[0] = 0.4;
            probVector[1] = 0.2;
            probVector[2] = 0.4;
            int[] map = new int[3];
            map[0] = 0;
            map[1] = 1;
            map[2] = 2;
            Double sum = 0.0;
            for (int i = 0; i < this.number_of_bugs; i++){
                sum = sum + cost_photo.get(i);
            }
            Double sum_2 = 0.0;
            for (int i = 0; i < this.number_of_bugs; i++){
                EnumeratedIntegerDistribution randomizerPosition;            
                randomizerPosition = new EnumeratedIntegerDistribution(map,probVector);
                int solution = randomizerPosition.sample();
                cost_photo.add(i, cost_photo.get(i)*factors.get(solution));
                sum_2 = sum_2 + cost_photo.get(i);
            }
            for (int i = 0; i < this.number_of_bugs; i++){
                cost_photo.add(i, cost_photo.get(i)*sum/sum_2);
            }
        }
     }
         
