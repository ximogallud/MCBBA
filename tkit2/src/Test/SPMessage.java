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
public class SPMessage {
    HashMap<myBail,Double> maximumBids = new HashMap();
    HashMap<myBail,AgentAddress> winners = new HashMap();
    AgentAddress myAddress;
    HashMap<myBail,Integer> myTimer = new HashMap();
    HashMap<myBail,Double> arrival_time_winners = new HashMap();
    HashMap<myBail,HashMap<AgentAddress,Integer>> myTimercoal = new HashMap();
    HashMap<myBail,ArrayList<AgentAddress>> winnerscoal = new HashMap();
    HashMap<myBail,HashMap<AgentAddress,Double>> maximumBidscoal = new HashMap();
    ArrayList<String> mySensors = new ArrayList();
}
