/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ximo
 */
public class myBail {
    public Double score;
    public ArrayList<String> bail_sensor;
        public List sensors;
        public int position;
        public int x;
        public int y;
        public Double tmin;
        public Double tmax;
        public activityMultisensor myActivity;
        private utile_functions myFunctions = new utile_functions();
        public ScenarioBail scenarioType;    
        public Double lambda;

    
    public myBail(){
        /*sensors = new ArrayList<>();
        sensors.add("MW"); //sensor type 1
        sensors.add("IR"); //sensor type 2
        //setRandomScore();
        int[] amount_of_sensors = {1,2};
        double[] probabilities = {0.7,0.3};
        
        this.bail_sensor = myFunctions.assignRandomSensors(amount_of_sensors, 
                probabilities, (ArrayList<String>) sensors);*/
        
    }
    
    public void setRandomScore(){
        this.score = Math.random()*200;
    }
    
}
