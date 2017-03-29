/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import static Test.myBug.getSubsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import madkit.kernel.AgentAddress;
import turtlekit.viewer.AbstractObserver;

/**
 *
 * @author Ximo
 */
public class auctioneer extends AbstractObserver {
private HashMap<AgentAddress,Plan> myTurtlemap = new HashMap(); 
private AgentAddress myAddress;
private HashMap<ArrayList<myBail>,Bid> maximumBids = new HashMap();
private boolean update;
private HashMap<ArrayList<myBail>,Bid> solution = new HashMap(); 
private int maxPlan = 4;
private int currentPlan;

    @Override
    protected void activate(){
        super.activate();
        createGroupIfAbsent("myturtles","myturtles");
        requestRole("myturtles","myturtles","auctioneer");
        this.myAddress = getAgentAddressIn("myturtles","myturtles","auctioneer");
        this.currentPlan = 1;
    }
    @Override
    protected void observe() {
        
        boolean closeMail = false;
        int agentsAlive;
        try{
        agentsAlive = getAgentsWithRole("myturtles","myturtles","bug").size();
        } catch (NullPointerException e){
        agentsAlive = 0;
        }
        while(!closeMail){            
            myMessage bids = new myMessage();
        bids = (myMessage) nextMessage();
            
            if (bids != null) {
                if( bids.myAddress == null){}else{
                this.myTurtlemap.put(bids.myAddress, bids.myPlan);
                }
                //logger.info("Received panorama message");
            } else {
                closeMail = true;
            }
            }
           
            if(this.myTurtlemap.size() == agentsAlive){
               this.maximumBids = getBestPermutations(this.myTurtlemap);
               //this.solution = DPWDP(this.maximumBids);
                }
    }

    private HashMap<ArrayList<myBail>, Bid> getBestPermutations(HashMap<AgentAddress, Plan> myTurtlemap) {
        HashMap<ArrayList<myBail>, Bid> tent_solution = new HashMap();
            for (AgentAddress address : myTurtlemap.keySet()){
                Plan currentPlan = new Plan();
                currentPlan = myTurtlemap.get(address);
                for (int i = 0; i < currentPlan.bailOrder.size() ; i++ ){
                    ArrayList<myBail> currentBundle = new ArrayList();
                    currentBundle.addAll(currentPlan.bailOrder.get(i));
                    
                    for(ArrayList<myBail> currentBL : tent_solution.keySet()){
                    if (currentBL.containsAll(currentBundle) && currentBL.size() == currentBundle.size()){
                        Bid bundleBid = new Bid();
                        bundleBid = tent_solution.get(currentBL);
                        if (bundleBid.priceBid < currentPlan.utility.get(i)){
                            bundleBid.myAddress = address;
                            bundleBid.positionArray = currentPlan.positions.get(i);
                            bundleBid.priceBid = currentPlan.utility.get(i);
                        }
                        tent_solution.replace(currentBL, bundleBid);
                        
                        this.update = true;
                        break;
                    }
                }    
                    if (update != true){
                            Bid bundleBid = new Bid();
                            bundleBid.myAddress = address;
                            bundleBid.positionArray = currentPlan.positions.get(i);
                            bundleBid.priceBid = currentPlan.utility.get(i);    
                            tent_solution.put(currentBundle,bundleBid);
                    }
                
                
            }
    }
            return tent_solution;
    }

    //private HashMap<ArrayList<myBail>,Bid> DPWDP(HashMap<ArrayList<myBail>, Bid> maximumBids1)
    private void DPWDP(HashMap<ArrayList<myBail>, Bid> maximumBids1) {
        HashMap<ArrayList<myBail>, Double> Pi = new HashMap();
        HashMap<ArrayList<myBail>, ArrayList<ArrayList<myBail>>> C = new HashMap();
        Set<ArrayList<myBail>> mySet = maximumBids1.keySet();
            mySet.removeIf(e -> e.size() != 1);
            for (ArrayList<myBail> currentBundle : mySet){
                Pi.put(currentBundle, maximumBids1.get(currentBundle).priceBid);
                ArrayList<ArrayList<myBail>> myBidArray = new ArrayList();
                myBidArray.add(currentBundle);
                C.put(currentBundle, myBidArray);
            }
        for (int i = 2; i <= this.maxPlan; i++){
            final int j = i;
            mySet = maximumBids1.keySet();
            mySet.removeIf(e -> e.size() != j);
            for (ArrayList<myBail> currentBundle : mySet){
                ArrayList<ArrayList<myBail>> sPrime = getSprime(currentBundle);
                for (ArrayList<myBail> eachSprime : sPrime){
                ArrayList<myBail> bundleToevaluate = new ArrayList();
                bundleToevaluate.addAll(currentBundle);
                bundleToevaluate.removeAll(eachSprime);
                Double piProvisional;
                piProvisional = Pi.get(bundleToevaluate) + Pi.get(eachSprime);
                if(!Pi.containsKey(currentBundle) || piProvisional > Pi.get(currentBundle)){
                    ArrayList<ArrayList<myBail>> Cprovisional = new ArrayList();
                    Cprovisional.add(eachSprime);
                    Cprovisional.add(bundleToevaluate);
                    Pi.put(currentBundle, piProvisional);
                    C.put(currentBundle, Cprovisional);
                }
                }
                if(Pi.get(currentBundle) < maximumBids1.get(currentBundle).priceBid){
                    Pi.put(currentBundle, maximumBids1.get(currentBundle).priceBid);
                    ArrayList<ArrayList<myBail>> Cprovisional = new ArrayList();
                    Cprovisional.add(currentBundle);
                    C.put(currentBundle,Cprovisional);
                }
                
            }
        } 
        
    }

    
    private ArrayList<ArrayList<myBail>> getSprime(ArrayList<myBail> currentBundle) {
        ArrayList<ArrayList<myBail>> solution = new ArrayList();
        for (int j = 1; j <= currentBundle.size()/2 ; j++){
        List<myBail> list = new ArrayList<myBail>();
        list.addAll(currentBundle);
        List<Set<myBail>> subset = getSubsets( list, j);
        ArrayList<ArrayList<myBail>> hola = new ArrayList(subset);
        solution.addAll(hola);
        }
        return solution;
    }
        
     public static <T> List<Set<T>> getSubsets(List<T> superSet, int k) {
    List<Set<T>> res = new ArrayList<>();
    getSubsets(superSet, k, 0, new HashSet<T>(), res);
    return res;
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
    }

