/*package Test;

import java.awt.Color;
import static java.awt.Color.blue;
import static java.awt.Color.yellow;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.logging.Level;
import madkit.kernel.AgentAddress;
import turtlekit.kernel.Patch;
import turtlekit.kernel.Turtle;
import turtlekit.kernel.TurtleKit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import Test.myBugCBBA;
import static java.lang.Math.abs;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import madkit.kernel.Message;
  
    public class myBugCBBA extends Turtle { 
        
        //ownVariablesTurtle
        private ArrayList possible_sensors;
        public final ArrayList<String> actual_sensors = new ArrayList();
        public Double score = 0.0;
        public Double money;
        public Double cost_movement;
        public Double cost_photo = 5.0;
        private myBail myTargetBail;
        private AgentAddress myAddress;
        private Plan myPlan;
        private Schedule currentSchedule = new Schedule();
        private Schedule auxSchedule = new Schedule();
        private HashMap<myBail,ArrayList<myBail>> order = new HashMap();
        private HashMap<myBail,ArrayList<Double[]>> coordinates = new HashMap();
        private ArrayList<myBail> myBundle = new ArrayList();
        private ArrayList<myBail> def_position = new ArrayList();
        private ArrayList<Double[]> def_coordinates = new ArrayList();
        private HashMap<myBail,Boolean> hvector = new HashMap();
        private HashMap<myBail,Double> myBids = new HashMap();
        private HashMap<myBail,Double> mymatesBids = new HashMap();
        private HashMap<myBail,AgentAddress> winners = new HashMap();
        private HashMap<AgentAddress, Integer> timer = new HashMap();
        private HashMap<AgentAddress, HashMap <AgentAddress, Integer>> timer_map = new HashMap();
        private HashMap<AgentAddress, HashMap<myBail,AgentAddress>> winMap = new HashMap();
        private ArrayList<Double> bundlePlainScore = new ArrayList();
        private final int numPlans = 2;
        
        // Variables from the scenario
        private Patch[] patchGrid;
        private HashMap<Integer, myBail> myBailsmap;
        private int dropoff_ut_value;
        private ArrayList<Integer> Position_bails; //in the Grid's counting method 
        private ArrayList<myBail> totalBailsavailable;
        private ArrayList<AgentAddress> scenario_address;
        private HashMap<AgentAddress,HashMap<myBail,Double>> myTurtlemap = new HashMap(); 
        private ArrayList<myBail> otherTurtlesBails = new ArrayList();
        private HashMap<AgentAddress,Boolean> agreement_code = new HashMap();
        
        //Scheduling variables
        private int count_convergence = 0;
        private final int maximum_counts_convergence = 20;
        private int number_iterations = 0;
        private int time = 0;
        private ArrayList<Double> solucio_mestraUtil;
        private HashMap<HashMap<Integer,Double>,myBail> utilities;
        private ArrayList<Double> solucio_mestraScore;
        private ArrayList<HashMap<myBail,Double>> history = new ArrayList();
        
        //Dummy objects
         private utile_functions myFunctions = new utile_functions();
        
	protected void activate() {
		super.activate();
                setLogLevel(Level.FINEST);
                //Starting array of types of sensors
               /* possible_sensors = new ArrayList<>();
                possible_sensors.add("MW"); //sensor type 1
                possible_sensors.add("IR"); //sensor type 2
                
                //assignSensorsRandomly(); //Starting quantity and type of sensors per each turtle(random)
		setNextAction("start"); //First action of the turtle (like a machine state)
                setColor(Color.red); //Turtle is color green in the viewer
                //money = getWorldWidth()*2.5;
                //dropoff_ut_value = getWorldWidth()/5;
                //cost_movement = (double) 150*0.5/getWorldWidth();
		//randomLocation(); //Assign radom initial position to the turtles    
	}
        
        public String start(){
            //wiggle();
            createGroupIfAbsent("myturtles","myturtles");
            requestRole("myturtles","myturtles","bug");
            logger.info("I started");
            return("searchForPanorama");
        }
    
        public String listen(){
           behaviorMessage action = new behaviorMessage();
           action = (behaviorMessage) purgeMailbox();
           return action.whatTodo;
        }
        
        public String searchForPanorama(){
            //Request update from scenario
            this.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
            myMessage hi = new myMessage();
            hi.things_to_say = "helloscenario";
            this.scenario_address = (ArrayList<AgentAddress>)
                    getAgentsWithRole("myturtles","myturtles","scenario"); //get other turtles what they are doing
            
            sendMessage (scenario_address.get(0), hi);
            boolean received = false;       
            myMessage m = (myMessage) purgeMailbox();
            logger.info("I checked my mail");
            try{
                
                m.hash.isEmpty();
                this.patchGrid = m.patchGrid;
                this.myBailsmap = m.hash;
                this.Position_bails = new ArrayList(this.myBailsmap.keySet());
                this.totalBailsavailable = new ArrayList (this.myBailsmap.values());
                logger.info("I received the message");
                return("sitAndThink"); 
                
            } catch (NullPointerException e){}
                        
                return ("searchForPanorama");                                         
                        
        }
        
        //first phase of CBBA
        public String sitAndThink () {
            //Initialize
           if (myBids.isEmpty()){
                bundlePlainScore.add(0.0);
                for (myBail bb : totalBailsavailable){
                    myBids.put(bb, 0.0);
                    
                }
            }
            if (mymatesBids.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    mymatesBids.put(bb,0.0);
                }
            }
            if (hvector.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    hvector.put(bb, true);
                }
            }             

            
           firstPhase();
           logger.info("exited first phase");
        return("secondPhase");
        }        
        public String secondPhase(){  
       // phase 2
            
            myMessage sendMyNumbers = new myMessage();
            sendMyNumbers.SP.winners.putAll(this.winners);
            sendMyNumbers.SP.maximumBids.putAll(mymatesBids);
            sendMyNumbers.SP.myAddress = this.myAddress;
            sendMyNumbers.SP.myTimer.putAll(timer);
            broadcastMessage("myturtles","myturtles","bug", sendMyNumbers);
            boolean closeMail = false;
            //myTurtlemap.clear();
            //avoid doing same thing
            while(!closeMail){            
            myMessage m = (myMessage) nextMessage();
            
            if (m!= null){
                this.myTurtlemap.put(m.SP.myAddress, m.SP.maximumBids);
                this.timer.put(m.SP.myAddress, this.time);
                this.winMap.put(m.SP.myAddress, m.SP.winners);
                this.timer_map.put(m.SP.myAddress, m.SP.myTimer);
                logger.info("Received panorama message");
                closeMail = true;
            } else {
                closeMail = true;
            }
            }
           
            if(this.myTurtlemap.keySet().contains(null) || this.myTurtlemap.size() == 0){
                return ("secondPhase");
            }
            time++;
           /* Integer i;
            i = getAgentsWithRole("myturtles","myturtles","bug").size();
            try{
            if ((this.myTurtlemap.containsKey(null)||this.myTurtlemap.size() == 0) && i != null){
                    return ("secondPhase");
                } else{
                   if (this.myTurtlemap.size() == i){
                   closeMail = true; 
                }
            }            
            } catch (IndexOutOfBoundsException e){
                   return("secondPhase");
            }
            }*/
            //check
            //retrieve other turtle's values
            /*logger.info("Comparison phase");
            this.number_iterations ++;
            AgentAddress senderWinner;
            AgentAddress myWinner;
            for ( AgentAddress address : myTurtlemap.keySet()){
                for (myBail bail : myTurtlemap.get(address).keySet()){
                    
                senderWinner = winMap.get(address).get(bail);
                myWinner = this.winners.get(bail);
                if(senderWinner == address){
                    if(myWinner == myAddress){
                        if(myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)){
                        update(bail,address);
                        
                        }
                    }
                    else if(myWinner != myAddress && myWinner != address && myWinner != null){
                        if(myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                || this.timer_map.get(address).get(myWinner) > this.timer.get(myWinner)){
                        update(bail,address);
                        
                        }
                    } else {
                        update(bail,address);
                        
                    }
                }
                else if(senderWinner == myAddress){
                    if(myWinner == address ){
                        reset(bail);
                    } else if (myWinner != myAddress && myWinner != address && myWinner != null){
                        if(this.timer_map.get(address).get(myWinner) > this.timer.get(myWinner)){
                            reset(bail);
                        }
                    }else{}
                    
                } else if (senderWinner != myAddress && senderWinner != address && senderWinner != null){
                    if(myWinner == myAddress){
                        if(myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                && this.timer_map.get(address).get(senderWinner) > this.timer.get(senderWinner))
                        {
                            update(bail,address);
                            
                        }
                    }  else if (myWinner == address){
                        
                        if(this.timer_map.get(address).get(senderWinner) > this.timer.get(senderWinner)){
                            update(bail,address);
                            
                        } else {
                            
                        }
                    } else if( myWinner != myAddress && myWinner != address && myWinner != senderWinner
                            && myWinner != null){
                        if(this.timer_map.get(address).get(senderWinner) > this.timer.get(senderWinner) && 
                                this.timer_map.get(address).get(myWinner) > this.timer.get(myWinner)){
                            update(bail,address);
                            
                        }
                        if(this.timer_map.get(address).get(senderWinner) > this.timer.get(senderWinner) &&
                                myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)){
                            update(bail,address);
                            
                        }
                        if(this.timer_map.get(address).get(myWinner) > this.timer.get(myWinner) &&
                                this.timer.get(senderWinner) > this.timer_map.get(address).get(senderWinner)){
                            reset(bail);
                        }
                    } else {
                        if(this.timer_map.get(address).get(senderWinner) > this.timer.get(senderWinner)){
                            update(bail,address);
                            
                        }
                    }
                    
                    } else {
                        if(myWinner == address){
                            update(bail,address);
                            
                        } else if (myWinner != address && myWinner != myAddress && myWinner != null){
                            if(this.timer_map.get(address).get(myWinner) > this.timer.get(myWinner) ){
                                update(bail,address);
                                
                            }
                        }
                    }
                }
            }
           //check releases:
          for (int i = 0; i < myBundle.size() ; i++){
              myBail bailTC = myBundle.get(i);
              if(winners.get(bailTC) != myAddress){
                  for (int u = i; u < myBundle.size(); u++){
                                        int pos = def_position.indexOf(myBundle.get(u));
                                        def_coordinates.remove(pos);
                                        def_position.remove(myBundle.get(u));
                                        bundlePlainScore.remove(i+1);
                                    }
                                    break;                                  
                                }
          }
          for (int i = 0; i < myBundle.size(); i++){
              myBail bailTC2 = myBundle.get(i);
              if(winners.get(bailTC2) != myAddress){
                    for (int r = (i+1); r < myBundle.size(); r++){
                    mymatesBids.put(myBundle.get(r), 0.0);
                    winners.put(myBundle.get(r), null);
                    }
              }
          }
          myBundle.retainAll(def_position);
          HashMap<myBail,Double> auxmymatesBids2 = new HashMap();
           auxmymatesBids2.putAll(mymatesBids);
            history.add(auxmymatesBids2);
            logger.info(history.get(history.size()-1).values().toString());
            //check convergence:
            if (history.size() == 1){
                return ("sitAndThink");
            } else if(history.get(history.size()-1).equals(history.get(history.size()-2))){
                this.count_convergence++;
                
                if(count_convergence > this.maximum_counts_convergence){
                currentSchedule.bailsToeat.addAll(def_position);
                currentSchedule.positionsToEat.addAll(def_coordinates);
                logger.info("I go to agreement phase");
                return("agreementPhase");
                } else {
                    
                return("sitAndThink");
                } 
        } else {
                this.count_convergence = 0;
                return("sitAndThink");
            }
 }
            
                
        public String agreementPhase(){
            int count = 0;
            myMessage myAgreement = new myMessage();
            requestRole("myturtles","myturtles","agreementPhase");
            myAgreement.myBundle.addAll(myBundle);
            myAgreement.agreement = true;
            try{
            broadcastMessage("myturtles","myturtles","agreementPhase", myAgreement);
            } catch (NullPointerException e){}      
            myMessage action = new myMessage();
            action =  (myMessage) purgeMailbox();
                     
            Integer i = getAgentsWithRole("myturtles","myturtles","agreementPhase").size();
            
            
            while (count < 5*i){
                if (action != null && action.myBundle != null && action.agreement == true){
                 agreement_code.put(action.myaddress, action.agreement);
            } else {
                    count++;
                    action = (myMessage) purgeMailbox();
                }
                if (agreement_code.size() == i && !agreement_code.containsValue(false)){
                    return ("goForBail");
                }
            }
            
            return ("agreementPhase");
            
        }
        
                
        public String goForBail(){
            
            /*myMessage m = (myMessage) purgeMailbox();
            if (m!= null){
                 myMessage data = new myMessage();
                 data.bailIEat = this.myTargetBail;
                 data.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
                 sendReply(m, data);
            } */
           /* Double[] position;
            try{
                position = currentSchedule.positionsToEat.get(0);
            
            } catch (NullPointerException e){
                return("doNoting");
            }

            if (distance(position[0],position[1]) <= 1){
                moveTo(position[0],position[1]);
                logger.info ("Got Bail!");
                return ("eatBail");  
            } else {
           
            double direction = towards(position[0],position[1]);
            setHeading(direction);
            fd(1);
            this.money = this.money - this.cost_movement;
            if(this.money <= 0){
                logger.info("I do not have enough money, I stopped!");
                return ("doNothing");
            }
            return ("goForBail");
            }            
        }
        /*public String finishAgreement(){
            int j = getAvailableBails().size();
            if(j != 0){
                int count = 0;
            myMessage myAgreement = new myMessage();
            myAgreement.myBundle.addAll(myBundle);
            myAgreement.agreement = true;
            broadcastMessage("myturtles","myturtles","bug", myAgreement);
            myMessage action = new myMessage();
            action =  (myMessage) purgeMailbox();
                     
            int i = getAgentsWithRole("myturtles","myturtles","bug").size();
            
            
            while (count < 5*i){
                if (action != null && action.myBundle != null && action.agreement == true){
                 agreement_code.put(action.myaddress, action.agreement);
            } else {
                    count++;
                    action = (myMessage) purgeMailbox();
                }
                if (agreement_code.size() == i && !agreement_code.containsValue(false)){
                    return ("goForBail");
                }
            }
            
            return ("agreementPhase");
            
            }
            
        }*/
       /* public String eatBail() {
            Patch patchBail = getPatchAt(myTargetBail.x - xcor(),myTargetBail.y - ycor());
            patchBail.setColor(blue);
            this.money = this.money - this.cost_photo;
            myMessage m = new myMessage();
            m.bailIate = myTargetBail;
            this.score = this.score + myTargetBail.score;
            m.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
            sendMessage(this.scenario_address.get(0), m);
            
            try{
                currentSchedule.bailsToeat.remove(0);
                currentSchedule.positionsToEat.remove(0);
                myTargetBail = currentSchedule.bailsToeat.get(0);
                return ("goForBail");
            } catch (IndexOutOfBoundsException e){
                int j = getAvailableBails().size();
                if (j != 0){
                return ("sitAndThink");
                } else {
                    logger.info("no more bails available");
                    return ("doNothing");
                }
            }
            
    }
        public String doNothing(){
            return ("doNothing");
        }
        
public Double getUtility (Double xbail, Double ybail, Double xev, Double yev,
            Double xorigin, Double yorigin, Double score_bails,ArrayList sensor_tasks){

    int i = 0;
    double alpha = 0;
    switch (sensor_tasks.size()){
        case 1: 
            if (sensor_tasks.get(0) == "MW"){
                alpha = 0.2;
            }
            if (sensor_tasks.get(0) == "IR"){
                alpha = 0.6;
            }
            break;
        case 2:
            alpha = 1;
            break;
    }
    
    Double ev_point = sqrt((xbail - xev)*(xbail - xev) + (ybail - yev)*(ybail - yev));
    Double distance;
    alpha = 1;
    distance = (Double) sqrt((xbail - xorigin)*(xbail - xorigin) + (ybail - yorigin)*(ybail - yorigin));
    Double utility = score_bails*alpha*deltaK(ev_point)//(1/(1 + exp((ev_point) - dropoff_ut_value))) 
            - this.cost_movement*(distance - ev_point);

    return utility;
            }

        public static void main(String[] args) {
        executeThisTurtle(0
                        ,TurtleKit.Option.envDimension.toString(),"10,10"
                        ,TurtleKit.Option.viewers.toString(), BailViewer.class.getName()+";"+ScoreViewer.class.getName()+";"+moneyViewer.class.getName()
                        ,TurtleKit.Option.environment.toString(), myEnvironment.class.getName()
                        ,TurtleKit.Option.noWrap.toString()

                        ,TurtleKit.Option.startSimu.toString());    

}

   /* private void assignSensorsRandomly() {
                int[] number_of_sensors = {1,2};
                double[] probabilities = {0.7,0.3};
                this.actual_sensors = (ArrayList<String>) myFunctions.assignRandomSensors(number_of_sensors, 
                probabilities, possible_sensors);

    }*/

   /* private Plan planAhead(int i) {
        HashMap<Integer,myBail> setBails = new HashMap<>();
        setBails = getAvailableBails();
        List<myBail> list = new ArrayList<myBail>();
        list.addAll(setBails.values());
        List<Set<myBail>> subset = getSubsets( list, i);
        int j = 0;
        HashMap<Integer,ArrayList<myBail>> solution = new HashMap();
        for (int k = 0; k < subset.size(); k++){
           ArrayList<myBail> subset1 = new ArrayList(subset.get(k));
           ArrayList<ArrayList<myBail>> anotherArray = new ArrayList();
           ArrayList<ArrayList<myBail>> myArray = new ArrayList();
           myArray = permute(subset1, subset1.size(), anotherArray);
           for (int y = 0; y < myArray.size(); y++){
               solution.put(j, myArray.get(y));
               j++;
           }
        }
        return evaluatePermutations(solution);
        }
            //get all permutations
            
            /*myBail[] bailArray = setBails.values().toArray(new myBail[setBails.values().size()]);
            ArrayList<myBail> auxiliary = new ArrayList<>();
            total_permutations = getPermutations(bailArray, i, total_permutations, 0,auxiliary);
            //evaluate permutations*/
   
   /* private HashMap<Integer, myBail> getAvailableBails() {
            HashMap<Integer,myBail> setBails = new HashMap<>();
            for (int j = 0; j < totalBailsavailable.size(); j++){
                
             Patch  patchBEQ = new Patch();  
             patchBEQ = (Patch) (this.patchGrid[Position_bails.get(j)]);
             myBail bailEnQuestio = totalBailsavailable.get(j);
              
                if (!Collections.disjoint(actual_sensors,
                        bailEnQuestio.bail_sensor)&& patchBEQ.getColor() == yellow)
                {
                   setBails.put(Position_bails.get(j), bailEnQuestio);
                }
            }    
            return setBails;
    } 
    
    /*public static <T> List<Set<T>> getSubsets(List<T> superSet, int k) {
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
}*/

  /* static <T> ArrayList<ArrayList<T>> permute(ArrayList<T> arr, int k, ArrayList<ArrayList<T>> solution){
        if (k == 1){
            ArrayList<T> auxiliary = new ArrayList(arr);
            solution.add(auxiliary);
        } else {
            for(int i = 0; i < k ; i++){
            permute(arr,k-1, solution);
            if (k % 2 == 1){
                swap (arr, 0, k - 1);
            } else {
                 swap(arr, i, k - 1);
            }
           
        }
        }
        return solution;
    }
    private static <T> void swap(ArrayList<T> setBails, int k, int i) {
        T t;
        t = setBails.get(k);
        setBails.set(k, setBails.get(i));
        setBails.set(i, t);
    }

    private Plan evaluatePermutations(HashMap<Integer, ArrayList<myBail>> total_permutations) {
        Plan sample_plan = new Plan();

        //Phase 1 : calculate all the scores
        for (int i = 0; i < total_permutations.size(); i++){
        ArrayList<Double[]> optimum_position = new ArrayList<>();
        double utility = 0;
        List<myBail> setB = total_permutations.get(i);
        Double xsub1 = getX();
        Double ysub1 = getY();
            for (int k = 0; k < setB.size(); k++){
                ArrayList common_sensors = new ArrayList();
                common_sensors.addAll(actual_sensors);
                common_sensors.retainAll(setB.get(k).bail_sensor);
                Double optUtility = 0.0;
                
                //initialize xsum
                Double xsum =  ((double)setB.get(k).x - xsub1)/(getWorldWidth()/2);
                //initialize ysum
                Double ysum = ((double) setB.get(k).y - ysub1)/(getWorldWidth()/2);
                Double xpointOpt = null;
                Double ypointOpt = null;
                Double xorigin;
                Double yorigin;
                if(optimum_position.size() == 0){
                        xorigin = getX();
                        yorigin = getY();
                    } else {
                        xorigin = optimum_position.get(k-1)[0];
                        yorigin = optimum_position.get(k-1)[1];
                    }
                for (int j = 0; j < (getWorldWidth()/2 + 1); j++){
                    Double utilitysingle;
                    utilitysingle = getUtility ((double) setB.get(k).x,(double) setB.get(k).y, 
                        xsub1, ysub1, xorigin,yorigin, setB.get(k).score,common_sensors); 
                    if (utilitysingle > optUtility || xpointOpt == null){
                        optUtility = utilitysingle;
                        xpointOpt = xsub1;
                        ypointOpt = ysub1;
                    } 
                        xsub1 = xsub1 + xsum;
                        ysub1 = ysub1 + ysum;
                }
                Double[] point = {xpointOpt,ypointOpt};
                utility = utility + optUtility;
                optimum_position.add(k, point);
                xsub1 = xpointOpt;
                ysub1 = ypointOpt;
            }
            
            sample_plan.bailOrder.add((ArrayList<myBail>) setB);
            sample_plan.positions.add(optimum_position);
            double auxUtility;
            auxUtility = utility;
            sample_plan.utility.add(auxUtility);
            
        }
            //Phase 2 : order the plan
            HashMap<Double,ArrayList<myBail>> link1 = new HashMap<>();
            HashMap<Double,ArrayList<Double[]>> link2 = new HashMap<>();
            for (int h = 0; h < sample_plan.utility.size(); h++){
                link1.put(sample_plan.utility.get(h), sample_plan.bailOrder.get(h));
                link2.put(sample_plan.utility.get(h), sample_plan.positions.get(h));
            }
            Collections.sort(sample_plan.utility, Collections.reverseOrder());        
            for (int h = 0; h < sample_plan.utility.size(); h++){
                sample_plan.bailOrder.set(h, link1.get(sample_plan.utility.get(h)));
                sample_plan.positions.set(h, link2.get(sample_plan.utility.get(h)));  
            
        }
            return sample_plan;
    }

    private Schedule calculateIncreaseUtility(ArrayList<myBail> bundle, 
            myBail bailEnquestio, Double previous_bid) {
            Schedule mySch = new Schedule();
            mySch.bailsToeat.addAll(bundle);
        if (bundle.contains(bailEnquestio)){
            mySch.TotalScore = 0.0;
            return mySch;
        } else {
            mySch.bailsToeat.add(bailEnquestio);
            HashMap<Integer,ArrayList<myBail>> solution = new HashMap<>();
            ArrayList<ArrayList<myBail>> anotherArray = new ArrayList();
            ArrayList<ArrayList<myBail>> myArray = new ArrayList();
            myArray = permute(mySch.bailsToeat, mySch.bailsToeat.size(), anotherArray);
            for (int y = 0; y < myArray.size(); y++){
               solution.put(y, myArray.get(y));
           }
           Plan  this_plan = new Plan();
           this_plan = evaluatePermutations(solution);
           Double increase;
           increase = this_plan.utility.get(0) - previous_bid;
           if (increase > 0){
               mySch.bailsToeat = this_plan.bailOrder.get(0);
               mySch.positionsToEat = this_plan.positions.get(0);
               mySch.TotalScore = increase;
               return mySch;
           } else {
               mySch.TotalScore = 0.0;
               return mySch;
           }
        }


    }
   private void release (myBail bail){ 
    if(myBundle.contains(bail)){
                            for (int y = 0; y < myBundle.size(); y++){
                                if (myBundle.get(y) == bail){
                                    
                            }
                        }
    myBundle.retainAll(def_position);
   }
   }
   private void update(myBail bail, AgentAddress address){
       mymatesBids.put(bail,myTurtlemap.get(address).get(bail));
      this.winners.put(bail, this.winMap.get(address).get(bail));
   }
    
    private void firstPhase(){
         HashMap<Integer,myBail> myBailsavailable = new HashMap();
         myBailsavailable = getAvailableBails();
         while (myBundle.size()< numPlans && hvector.containsValue(true))
            { //order.clear();
              //coordinates.clear();
                for ( myBail bailEnquestio : totalBailsavailable ){
                    auxSchedule = calculateIncreaseUtility(myBundle, bailEnquestio, 
                            bundlePlainScore.get(myBundle.size()));
                    myBids.put(bailEnquestio, auxSchedule.TotalScore);
                    order.put(bailEnquestio,auxSchedule.bailsToeat);
                    coordinates.put(bailEnquestio, auxSchedule.positionsToEat);
                    if(myBids.get(bailEnquestio) > mymatesBids.get(bailEnquestio)
                            && myBailsavailable.values().contains(bailEnquestio)){
                        hvector.put(bailEnquestio, true);
                        //mymatesBids.put(bailEnquestio, myBids.get(bailEnquestio));
                    } else {
                        hvector.put(bailEnquestio, false);
                    }
                }
                //decision
                myBail optimum = new myBail();
                optimum = null;
                Double winningBid = 0.0;
                for (myBail aa : myBailsavailable.values()){
                    if (hvector.get(aa) == true){
                        if (optimum == null || myBids.get(aa) > winningBid){
                            optimum = aa;
                            winningBid = myBids.get(aa);
                        }
                    }
                }
                if (optimum != null){
                    myBail auxiliaryBail = new myBail();
                    auxiliaryBail = optimum;
                    myBundle.add(auxiliaryBail);
                    bundlePlainScore.add(winningBid);
                    def_position = order.get(auxiliaryBail);
                    def_coordinates = coordinates.get(auxiliaryBail);
                    winners.put(auxiliaryBail, myAddress);
                    mymatesBids.put(auxiliaryBail, winningBid);
                    //hvector.put(optimum,false);
            }
                
         
            }
         
    }

    private void reset(myBail bail) {
      mymatesBids.put(bail,0.0);
      this.winners.put(bail, null);
      
    }

    private double deltaK(Double ev_point) {
            if (abs(ev_point) < 1E-5){
                return 1.0;
            } else {
                return 0.0;
            }
    }
    }*/

