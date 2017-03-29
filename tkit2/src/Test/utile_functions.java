/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import madkit.kernel.AgentAddress;
import madkit.message.ObjectMessage;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import madkit.kernel.Agent;

/**
 *
 * @author Ximo
 */
public class utile_functions {
    Agent dummy_agent;


public ArrayList<String> assignRandomSensors(int[] amount_of_sensors, 
        double[] probabilities, ArrayList<String> types_of_sensors){
    
    ArrayList<String> result = new ArrayList<String>();
    
    EnumeratedIntegerDistribution randomizer;
    Random random_number = new Random();
    randomizer = new EnumeratedIntegerDistribution(amount_of_sensors,probabilities);
    int quantity;
    quantity = randomizer.sample();
    Collections.shuffle(types_of_sensors, new Random());
    for (int i = 0; i < quantity; i++){
    result.add(types_of_sensors.get(i));
    }
          
   return result;
}

public Double assignRandomScore(){
        Double score;
        score = Math.random();
        return score;
}

/*public Panorama dismeElPanorama(AgentAddress scenario) {
    Panorama myPanorama = new Panorama();
    
         ObjectMessage<ArrayList<ArrayList<String>>> sensor_tasks = 
                 (ObjectMessage<ArrayList<ArrayList<String>>>)
                 dummy_agent.sendMessageAndWaitForReply(scenario,
                         new ObjectMessage("sensor_tasks_array?"));
         
         myPanorama.sensor_tasks = (ArrayList<ArrayList<String>>) sensor_tasks.getContent();
         
        ObjectMessage<ArrayList<Position>> position_tasks = 
                 (ObjectMessage<ArrayList<Position>>)
                 dummy_agent.sendMessageAndWaitForReply(scenario,
                         new ObjectMessage("position_tasks_array?"));
         
         myPanorama.position_tasks = (ArrayList<Position>) position_tasks.getContent();
         
           ObjectMessage<ArrayList<Double>> score_tasks = 
                 (ObjectMessage<ArrayList<Double>>)
                 dummy_agent.sendMessageAndWaitForReply(scenario,
                         new ObjectMessage("score_tasks_array?"));
         
         myPanorama.score_tasks = (ArrayList<Double>) score_tasks.getContent();
        
     return myPanorama;          
    }
*/

public LinkedHashMap<Integer, Double> sortHashMapByValues(
        HashMap<Integer, Double> passedMap) {
    List<Integer> id_tasks = new ArrayList<>(passedMap.keySet());
    List<Double> utilities = new ArrayList<>(passedMap.values());
    Collections.sort(id_tasks);
    Collections.sort(utilities);

    LinkedHashMap<Integer, Double> sortedMap =
        new LinkedHashMap<>();

    Iterator<Double> valueIt = utilities.iterator();
    while (valueIt.hasNext()) {
        Double val = valueIt.next();
        Iterator<Integer> keyIt = id_tasks.iterator();

        while (keyIt.hasNext()) {
            Integer key = keyIt.next();
            Double comp1 = passedMap.get(key);
            Double comp2 = val;

            if (comp1.equals(comp2)) {
                keyIt.remove();
                sortedMap.put(key, val);
                break;
            }
        }
    }
    return sortedMap;
}


   
}
