/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;
import madkit.kernel.AgentAddress;

/**
 *
 * @author Ximo
 */
public class comparator {
    HashMap<String,AgentAddress> winnersSender = new HashMap();
    HashMap<String,AgentAddress> winnersReceiver = new HashMap();
    public ArrayList<String> sensorsextraction(AgentAddress address){
        ArrayList<String> sensorsIParticipate = new ArrayList();
        for (String sensor : winnersSender.keySet()){
            if(winnersSender.get(sensor) == address){
                sensorsIParticipate.add(sensor);
            } 
        }
        return sensorsIParticipate;
    }
    
}
