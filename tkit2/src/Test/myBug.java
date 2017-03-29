package Test;

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
  
    public class myBug extends Turtle { 
        
        //ownVariablesTurtle
        private ArrayList possible_sensors;
        public ArrayList<String> actual_sensors = new ArrayList();
        public Double score = 0.0;
        public Double money;
        public Double cost_movement;
        public Double cost_photo = 5.0;
        public Double initialMoney;
        private myBail myTargetBail;
        private Plan myPlan;
        private AgentAddress myAddress;
        private Schedule currentSchedule = new Schedule();
        private int numPlans = 4;
        
        // Variables from the scenario
        private Patch[] patchGrid;
        private HashMap<Integer, myBail> myBailsmap;
        private int dropoff_ut_value;
        private ArrayList<Integer> Position_bails; //in the Grid's counting method 
        private ArrayList<myBail> totalBailsavailable;
        private ArrayList<AgentAddress> scenario_address;
        private HashMap<AgentAddress,Plan> myTurtlemap = new HashMap(); 
        private ArrayList<myBail> otherTurtlesBails = new ArrayList();
        
        //Scheduling variables
        private ArrayList<Double> solucio_mestraUtil;
        private HashMap<HashMap<Integer,Double>,myBail> utilities;
        private ArrayList<Double> solucio_mestraScore;
        private int convergence_count = 0;
        private int convergencemax = 5;
        
        //Dummy objects
         private utile_functions myFunctions = new utile_functions();
        
	protected void activate() {
		super.activate();
                setLogLevel(Level.FINEST);
                //Starting array of types of sensors
                /*possible_sensors = new ArrayList<>();
                possible_sensors.add("MW"); //sensor type 1
                possible_sensors.add("IR"); //sensor type 2
                
                assignSensorsRandomly(); //Starting quantity and type of sensors per each turtle(random)*/
		setNextAction("start"); //First action of the turtle (like a machine state)
                setColor(Color.red); //Turtle is color green in the viewer
                /*money = getWorldWidth()*2.5;
                dropoff_ut_value = getWorldWidth()/5;
                cost_movement = (double) 150*0.5/getWorldWidth();*/
		//randomLocation(); //Assign radom initial position to the turtles    
	}
        
        public String start(){
            wiggle();
            createGroupIfAbsent("myturtles","myturtles");
            requestRole("myturtles","myturtles","bug");
            this.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
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
            myMessage hi = new myMessage();
            hi.things_to_say = "helloscenario";
            this.scenario_address = (ArrayList<AgentAddress>)
                    getAgentsWithRole("myturtles","myturtles","scenario"); //get other turtles what they are doing
            
            sendMessage (scenario_address.get(0), hi);
            boolean received = false;       
            myMessage m = (myMessage) nextMessage();
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
        
        public String sitAndThink () {
         this.myPlan = planAhead(numPlans);
         return ("decideWhatToDo");
        }
        
        public String decideWhatToDo (){
            int count = 0;
            int agentsAlive;
            try{
            agentsAlive = getAgentsWithRole("myturtles","myturtles","bug").size();
            } catch (NullPointerException e){
            agentsAlive = 0;
            }
            myMessage request = new myMessage();
            this.myTargetBail = null;
            request.myAddress = this.myAddress;
            request.myPlan = this.myPlan;
            broadcastMessage("myturtles","myturtles","bug", request);
            boolean closeMail = false;
            //myTurtlemap.clear();
            //avoid doing same thing
            
             while(!closeMail){            
            myMessage m = new myMessage();
                    m = (myMessage) nextMessage();
            
            if (m!= null) {
                if( m.myAddress == null){}else{
                this.myTurtlemap.put(m.myAddress, m.myPlan);
                }
                //logger.info("Received panorama message");
            } else {
                closeMail = true;
            }
            }
           
            if(this.myTurtlemap.size() != agentsAlive){
                if(this.myTurtlemap.size() > agentsAlive){
                    this.myTurtlemap.clear();
                }
                return ("decideWhatToDo");
            } else {
           //retrieve other turtle's values
            while ( myPlan.bailOrder.size() != 0){
            ArrayList<myBail> tentative_bails = new ArrayList<myBail>();
            tentative_bails.addAll(this.myPlan.bailOrder.get(0));
            if (this.myPlan.utility.get(0) <= 0.01 || this.myPlan.utility.get(0) == null){
                    logger.info("No more utile tasks to do");
                    return ("doNothing");
                }
            
            for ( AgentAddress bugAddress : this.myTurtlemap.keySet()){
                Plan planmates = myTurtlemap.get(bugAddress);
            if (Collections.disjoint(tentative_bails,planmates.bailOrder.get(0))){
                count++;
            } else{
               
               if (planmates.bailOrder.get(0).size() == myPlan.bailOrder.get(0).size()){
                   if(planmates.utility.get(0) < myPlan.utility.get(0)){
                    count++;
                    } else {
                        count = 0;
                        while(!Collections.disjoint(myPlan.bailOrder.get(0),planmates.bailOrder.get(0))){
                            int fr = 0;
                        try{
                            removeSchedule();
                            fr++;
                            logger.info("Removal" + fr);
                        } catch (NullPointerException e){
                            logger.info("No more tasks to do");
                            return("doNothing");
                        }
                        }
                        break;
                    }
           
                } else {
                   ArrayList<myBail> commonBails = new ArrayList();
                   Double sumUtility = 0.0;
                        commonBails.addAll(tentative_bails);
                        commonBails.retainAll(planmates.bailOrder.get(0));
                        for (int i = 0; i < commonBails.size(); i++){
                            myBail bail = commonBails.get(i);
                            int index = tentative_bails.indexOf(bail);
                            sumUtility = sumUtility + myPlan.fracutility.get(0).get(index);
                        }
                        if (sumUtility < planmates.utility.get(0)){
                           removeSchedule();
                           break;
                        }else {
                            count++;
                        }
                } 
                    
                    
                }
               
            
            
            
            }
            if (count == agentsAlive){
                this.convergence_count++;
                if (this.convergence_count < this.convergencemax){
                    this.myTurtlemap.clear();
                    return("decideWhatToDo");
                } else {
                this.currentSchedule.bailsToeat = tentative_bails;
                this.currentSchedule.positionsToEat = (this.myPlan.positions.get(0));
                this.myTargetBail = tentative_bails.get(0);
                request.myAddress = this.myAddress;
                request.myPlan = this.myPlan;
                broadcastMessage("myturtles","myturtles","bug", request);
                return ("goForBail");               
            }
            }
                          
            }
            logger.info("I didn't win any bid");
            this.numPlans--;
            return ("sitAndThink")  ;  
        }
            
        }
    
    

        
                
        public String goForBail(){
            Double[] position = currentSchedule.positionsToEat.get(0);
            myMessage m = new myMessage();
                    m = (myMessage) nextMessage();
            
            if (m!= null) {
            myMessage request = new myMessage();
            request.myAddress = this.myAddress;
            request.myPlan = this.myPlan;
            broadcastMessage("myturtles","myturtles","bug", request);
                //logger.info("Received panorama message");
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
        public String eatBail() {
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
                int availableSize = getAvailableBails().values().size();
                if (availableSize == 0){
                    return ("doNothing");
                }
                if (numPlans <= availableSize){
                return ("sitAndThink");
            } else {
                    this.numPlans = availableSize;
                    return ("sitAndThink");
                }
            
            }
        }
        public String doNothing(){
            logger.info("Me vou pa mi casa tete");
            killAgent(this);
            return ("doNothing");
        }
        
public Double getUtility (Double xbail, Double ybail, Double xev, Double yev,
            Double score_bails,ArrayList sensor_tasks){

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
    distance = (Double) distanceNoWrap(xbail,ybail);
    Double utility = score_bails*alpha*(1/(1 + exp((ev_point) - dropoff_ut_value))) 
            - this.cost_movement*(distance - ev_point);

    return utility;
            }

        public static void main(String[] args) {
        executeThisTurtle(0
                        ,TurtleKit.Option.envDimension.toString(),"50,50"
                        ,TurtleKit.Option.viewers.toString(), BailViewer.class.getName()+";"+ScoreViewer.class.getName()+";"+moneyViewer.class.getName()
                        ,TurtleKit.Option.environment.toString(), myEnvironment.class.getName()
                        ,TurtleKit.Option.noWrap.toString()

                        ,TurtleKit.Option.startSimu.toString());    

}

    private void assignSensorsRandomly() {
                int[] number_of_sensors = {1,2};
                double[] probabilities = {0.7,0.3};
                this.actual_sensors = (ArrayList<String>) myFunctions.assignRandomSensors(number_of_sensors, 
                probabilities, possible_sensors);

    }

    private Plan planAhead(int i) {
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
   
    private HashMap<Integer, myBail> getAvailableBails() {
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

   static <T> ArrayList<ArrayList<T>> permute(ArrayList<T> arr, int k, ArrayList<ArrayList<T>> solution){
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
        ArrayList<Double> fut = new ArrayList<>();
        double utility = 0;
        List<myBail> setB = total_permutations.get(i);
        Double xsub1 = getX();
        Double ysub1 = getY();
            for (int k = 0; k < setB.size(); k++){
                ArrayList common_sensors = actual_sensors;
                common_sensors.retainAll(setB.get(k).bail_sensor);
                Double optUtility = 0.0;
                //initialize xsum
                Double xsum =  ((double)setB.get(k).x - xsub1)/(getWorldWidth()/2);
                //initialize ysum
                Double ysum = ((double) setB.get(k).y - ysub1)/(getWorldWidth()/2);
                Double xpointOpt = null;
                Double ypointOpt = null;
                for (int j = 0; j < (getWorldWidth()/2 + 1); j++){
                    Double utilitysingle;
                    utilitysingle = getUtility ((double) setB.get(k).x,(double) setB.get(k).y, 
                        xsub1, ysub1, setB.get(k).score,common_sensors); 
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
                fut.add(k, optUtility);
                optimum_position.add(k, point);
                xsub1 = xpointOpt;
                ysub1 = ypointOpt;
            }
            
            sample_plan.bailOrder.add((ArrayList<myBail>) setB);
            sample_plan.positions.add(optimum_position);
            sample_plan.utility.add(utility);
            sample_plan.fracutility.add(fut);
            
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
    private void removeSchedule(){
    this.myPlan.bailOrder.remove(0);
                           myPlan.fracutility.remove(0);
                           myPlan.positions.remove(0);
                           myPlan.utility.remove(0);
    }
    }

