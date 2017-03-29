/*package Test;

import java.awt.Color;
import static java.awt.Color.blue;
import static java.awt.Color.yellow;
import java.io.BufferedWriter;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.logging.Level;
import madkit.kernel.AgentAddress;
import turtlekit.kernel.Patch;
import turtlekit.kernel.Turtle;
import turtlekit.kernel.TurtleKit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import madkit.action.SchedulingAction;
import madkit.message.SchedulingMessage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
  
    public class myBugCoalitions extends Turtle { 
        
        //ownVariablesTurtle
        private ArrayList possible_sensors;
        public ArrayList<String> actual_sensors = new ArrayList();
        public Double score = 0.0;
        public Double money;
        public Double initialMoney;
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
        private HashMap<myBail,HashMap<ArrayList<String>,Double>> myBids = new HashMap();
        private HashMap<myBail,HashMap<ArrayList<String>,Double>> mymatesBids = new HashMap();
        private HashMap<myBail,ArrayList<AgentAddress>> winners = new HashMap();
        private HashMap<myBail, Integer> timer = new HashMap();
        private HashMap<AgentAddress, HashMap <myBail, Integer>> timer_map = new HashMap();
        private HashMap<AgentAddress, HashMap<myBail,ArrayList<AgentAddress>>> winMap = new HashMap();
        private ArrayList<HashMap<ArrayList<String>,Double>> bundlePlainScore = new ArrayList();
        private final int numPlans = 5;
        
        
        // Variables from the scenario
        private Patch[] patchGrid;
        private HashMap<Integer, myBail> myBailsmap;
        private int dropoff_ut_value;
        private ArrayList<Integer> Position_bails; //in the Grid's counting method 
        private ArrayList<myBail> totalBailsavailable;
        private ArrayList<AgentAddress> scenario_address;
        private AgentAddress schedulerAddress;
        private HashMap<AgentAddress,HashMap<myBail,Double>> myTurtlemap = new HashMap(); 
        private ArrayList<myBail> otherTurtlesBails = new ArrayList();
        private HashMap<AgentAddress,Boolean> agreement_code = new HashMap();
        private int numberofBugs;
        private Double totalScenarioScore;
        
        
        //Scheduling variables
        private int count_convergence = 0;
        private final int maximum_counts_convergence = 50;
        private int number_iterations = 0;
        private int time = 0;
        private ArrayList<Double> solucio_mestraUtil;
        private HashMap<HashMap<Integer,Double>,myBail> utilities;
        private ArrayList<Double> solucio_mestraScore;
        private ArrayList<HashMap<myBail,Double>> history = new ArrayList();
        private boolean agreement = false;
        private boolean bye = false;
        
        //Dummy objects
         private utile_functions myFunctions = new utile_functions();
        
	protected void activate() {
		super.activate();
                createGUIOnStartUp();
                setLogLevel(Level.INFO);
                //Starting array of types of sensors
               /* possible_sensors = new ArrayList<>();
                possible_sensors.add("MW"); //sensor type 1
                possible_sensors.add("IR"); //sensor type 2*/
                
                //assignSensorsRandomly(); //Starting quantity and type of sensors per each turtle(random)
		/*setNextAction("start"); //First action of the turtle (like a machine state)
                setColor(Color.red); //Turtle is color green in the viewer
                //money = getWorldWidth()*2.5;
                //dropoff_ut_value = getWorldWidth()/5;
                //cost_movement = (double) 150*0.5/getWorldWidth();
		randomLocation(); //Assign radom initial position to the turtles    
	}
        
        public String start(){
            //wiggle();
            createGroupIfAbsent("myturtles","myturtles");
            requestRole("myturtles","myturtles","bug");
            createGroupIfAbsent("myturtles","myturtles1");
            requestRole("myturtles","myturtles1","bug");
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
            if(schedulerAddress == null){
                  schedulerAddress = getAgentsWithRole("myturtles","myturtles","scheduler").get(0);
              }
            this.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
            myMessage hi = new myMessage();
            hi.things_to_say = "helloscenario";
            this.scenario_address = (ArrayList<AgentAddress>)
                    getAgentsWithRole("myturtles","myturtles","scenario"); //get other turtles what they are doing
            
            sendMessage (scenario_address.get(0), hi);
            boolean received = false;       
            myMessage m = (myMessage) purgeMailbox();
            //logger.info("I checked my mail");
            try{
                
                m.hash.isEmpty();
                this.patchGrid = m.patchGrid;
                this.myBailsmap = m.hash;
                this.Position_bails = new ArrayList(this.myBailsmap.keySet());
                this.totalBailsavailable = new ArrayList (this.myBailsmap.values());
                this.numberofBugs = m.numberofBugs - 1;
                this.totalScenarioScore = m.totalScenarioScore;
                //logger.info("I received the message");
                return("sitAndThink"); 
                
            } catch (NullPointerException e){}
                        
                return ("searchForPanorama");                                         
                        
        }
        
        //first phase of CBBA
        public String sitAndThink () {
            //Initialize
            ArrayList<Double> auxarr1 = new ArrayList<>();
            auxarr1.add(0.0);
           if (myBids.isEmpty()){
                bundlePlainScore.add(0.0);
                for (myBail bb : totalBailsavailable){
                    myBids.put(bb, auxarr1);
                    
                }
            }
            ArrayList<Double> auxarr2 = new ArrayList<>();
            auxarr2.add(0.0);
            if (mymatesBids.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    mymatesBids.put(bb,auxarr2);
                }
            }
           
             if (timer.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    timer.put(bb, 0);
                }
            }          
     firstPhase();
           //logger.info("exited first phase");
        return("secondPhase");
        }        
        public String secondPhase(){  
       // phase 2
            
            broadcast(myAddress);
            boolean closeMail = false;
            //myTurtlemap.clear();
            //avoid doing same thing
            
             while(!closeMail){            
            myMessage m = new myMessage();
                    m = (myMessage) nextMessage();
            
            if (m!= null) {
                if( m.SP.myAddress == null || m.SP.myAddress == myAddress){}else{
                this.myTurtlemap.put(m.SP.myAddress, m.SP.maximumBids);
                this.winMap.put(m.SP.myAddress, m.SP.winners);
                this.timer_map.put(m.SP.myAddress, m.SP.myTimer);
                }
                //logger.info("Received panorama message");
            } else {
                closeMail = true;
            }
            }
           
            if(this.myTurtlemap.size() == 0 ){
                try{ 
                if (getAgentsWithRole("myturtles","myturtles1","bug")==null || getAgentsWithRole("myturtles","myturtles1","bug").size() 
                            == getAgentsWithRole("myturtles","myturtles","agreementPhase").size()){
                    
                currentSchedule.bailsToeat.addAll(def_position);
                currentSchedule.positionsToEat.addAll(def_coordinates);

                leaveRole("myturtles","myturtles","bug");
                requestRole("myturtles","myturtles","going");
                
               // logger.info("I go to agreement phase");
                return("goForBail");
                } else{
                return ("secondPhase");
            } 
            } catch (NullPointerException e){ return("secondPhase");
            
            }}
                
            time++;
          
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
                        broadcast(address);
                        } else if(myTurtlemap.get(address).get(bail) == mymatesBids.get(bail)
                                && winMap.get(address).get(bail).hashCode() < winners.get(bail).hashCode()){
                        update(bail,address);
                        broadcast(address);
                        } else if(myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)){
                            timer.put(bail, time);
                            broadcast(myAddress);
                        } else {
                            broadcast(myAddress);
                        }
                    }
                    else if(myWinner != myAddress && myWinner != address && myWinner != null){
                        if( myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) >= timer.get(bail)){
                        update(bail,address);
                        broadcast(address);
                        }  else if (myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) <= timer.get(bail)){
                        broadcast(myAddress);
                    } else if (myTurtlemap.get(address).get(bail) == mymatesBids.get(bail)){
                        broadcast(myAddress);
                    } else if (myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) > timer.get(bail)){
                        reset(bail);
                        broadcast(address);
                    } else if (myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) < timer.get(bail)){
                        reset(bail);
                        broadcast(address);
                    } else {
                        broadcast(myAddress);
                    }
                    } else if (myWinner == address){
                        if (this.timer_map.get(address).get(bail) >= timer.get(bail)){
                            update(bail,address);
                        } else if (abs(this.timer_map.get(address).get(bail) - timer.get(bail))<1){
                            broadcast(myAddress);
                        } else if (this.timer_map.get(address).get(bail) < timer.get(bail)){
                            
                        } else {
                            broadcast(myAddress);
                        }
                        
                    } else {
                        update(bail,address);
                        broadcast(address);
                    }
                } else if(senderWinner == myAddress){
                    if(myWinner == address ){
                        reset(bail);
                        broadcast(address);
                    } else if (myWinner == myAddress){
                        if (abs(this.timer_map.get(address).get(bail) - timer.get(bail))<1){
                            broadcast(myAddress);
                        }
                    } else if (myWinner != myAddress && myWinner != address){
                     broadcast(myAddress);
                    }
                    
                } else if (senderWinner != myAddress && senderWinner != address && senderWinner != null){
                    if(myWinner == myAddress){
                        if(myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)){
                        update(bail,address);
                        broadcast(address);
                        } else if(myTurtlemap.get(address).get(bail) == mymatesBids.get(bail)
                                && winMap.get(address).get(bail).hashCode() < winners.get(bail).hashCode()){
                        update(bail,address);
                        broadcast(address);
                        } else if(myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)){
                            timer.put(bail, time);
                            broadcast(myAddress);
                        } else {
                            broadcast(myAddress);
                        }
                    
                    }  else if (myWinner == address){
                        
                       if (this.timer_map.get(address).get(bail) >= timer.get(bail)){
                            update(bail,address);
                            broadcast(address);
                        } else if (this.timer_map.get(address).get(bail) < timer.get(bail)){
                            reset(bail);
                            broadcast(address);
                        } else {
                            broadcast(myAddress);
                        }
                    } else if( myWinner != myAddress && myWinner != address && myWinner != senderWinner
                            && myWinner != null){
                         if( myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) >= timer.get(bail)){
                        update(bail,address);
                        broadcast(address);
                        }  else if (myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) <= timer.get(bail)){
                        broadcast(myAddress);
                    } else if (myTurtlemap.get(address).get(bail) == mymatesBids.get(bail)){
                        broadcast(myAddress);
                    } else if (myTurtlemap.get(address).get(bail) < mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) > timer.get(bail)){
                        reset(bail);
                        broadcast(address);
                    } else if (myTurtlemap.get(address).get(bail) > mymatesBids.get(bail)
                                && this.timer_map.get(address).get(bail) < timer.get(bail)){
                        reset(bail);
                        broadcast(address);
                    } else {
                        
                        broadcast(myAddress);
                    }
                    } else if(myWinner == senderWinner){
                        if (this.timer_map.get(address).get(bail) >= timer.get(bail)){
                            update(bail,address);
                        } else if (abs(this.timer_map.get(address).get(bail) - timer.get(bail))<1){
                            broadcast(myAddress);
                        } else if (this.timer_map.get(address).get(bail) < timer.get(bail)){
                            
                        } else {
                            broadcast(myAddress);
                        }
                    }
                    
                    else  {
                        update(bail,address);
                        broadcast(address);
                    }
                    
                    } else {
                        if(myWinner == address){
                            update(bail,address);
                             broadcast(address);
                        } else if (myWinner != address && myWinner != myAddress && myWinner != null){
                            if(this.timer_map.get(address).get(bail) >= this.timer.get(bail) ){
                                update(bail,address);
                                broadcast(address);
                            }
                        } else if(myWinner == myAddress){
                                broadcast(myAddress);
                            } else{}
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
            logger.info(time + ' ' + history.get(history.size()-1).values().toString());

            //check convergence:
            if (history.size() == 1){
                return ("sitAndThink");
            } else if(history.get(history.size()-1).equals(history.get(history.size()-2))){
                this.count_convergence++;
                
                if(count_convergence > this.maximum_counts_convergence ){
                currentSchedule.bailsToeat.addAll(def_position);
                currentSchedule.positionsToEat.addAll(def_coordinates);

                leaveRole("myturtles","myturtles","bug");
                requestRole("myturtles","myturtles","going");
               // logger.info("I go to agreement phase");
                return("goForBail");
                } else {
                    
                return("sitAndThink");
                } 
        } else {
                this.count_convergence = 0;
                return("sitAndThink");
            }
 }
            
                
        public String agreementPhase(){
            requestRole("myturtles","myturtles","agreementPhase");
            logger.info("entered agreement phase"); 

            HashMap<AgentAddress,Boolean> myAgreementmap = new HashMap<>();  
            myMessage myAgreement = new myMessage();

            HashMap<Integer, myBail> availableBails = new HashMap();
            List<AgentAddress> list1 = getAgentsWithRole("myturtles","myturtles","agreementPhase",false);
            List<AgentAddress> list2 = getAgentsWithRole("myturtles","myturtles1","bug",false);
            availableBails = getAvailableBails();
            if(availableBails.size() == 0 || availableBails == null){
                logger.info("no bails available");
                return("doNothing");
            }
            if (list2 == null){
                clearEverything();
                bye = true;
                return("sitAndThink");
            }
            
            
             if (agreement == true){
               logger.info("I trued");
               this.agreement = false;
               clearEverything();
            
               
               leaveRole("myturtles","myturtles","agreementPhase");
               requestRole("myturtles","myturtles","bug");
               return ("sitAndThink");
           } else {
           try{ 
           if (list1.size() == list2.size() && availableBails.size() != 0 && agreement == false){
               this.agreement = true;
               return("agreementPhase");
          
           } else {
                return("agreementPhase");
                }
           }catch (NullPointerException e){
               return("agreementPhase");
           }
                
            /*while (myAgreementmap.size() != this.numberofBugs){
            
            broadcastMessage("myturtles","myturtles","agreementPhase", myAgreement); 
            myMessage action = new myMessage();
            action =  (myMessage) nextMessage();
            
            if (action != null && action.myAddress != myAddress){
                myAgreementmap.put(myAddress, action.agreement);
                
            }
            if (myAgreementmap.size() == this.numberofBugs && agreement == true){
           
             myBids.clear();
               
             mymatesBids.clear();
             
             timer.clear();
               
               
                    this.myTurtlemap.clear();
                    
                    return("sitAndThink");
               
            } else {
                //agreement = false;
                return("agreementPhase");
            }
            
            }
            }
            return("sitAndThink");
        }
        }
    
             
        public String goForBail(){
            
            Double tentativeMoney;
            myTargetBail = currentSchedule.bailsToeat.get(0);
            /*myMessage m = (myMessage) purgeMailbox();
            if (m!= null){
                 myMessage data = new myMessage();
                 data.bailIEat = this.myTargetBail;
                 data.myAddress = getAgentAddressIn("myturtles","myturtles","bug");
                 sendReply(m, data);
            } 
            Double[] position;
            try{
                position = currentSchedule.positionsToEat.get(0);
            
            } catch (NullPointerException e){
                return("agreementPhase");
            }
            tentativeMoney = this.money - this.cost_movement;
            if(tentativeMoney <= this.cost_photo){
                logger.info("I do not have enough money, I stopped!");
                return ("doNothing");
            } else {
                this.money = tentativeMoney;
            }

            if (distance(position[0],position[1]) <= 1){
                
                moveTo(position[0],position[1]);
                logger.info ("Got Bail!");
                return ("eatBail");  
            } else {
           
            double direction = towards(position[0],position[1]);
            setHeading(direction);
            
            tentativeMoney = this.money - this.cost_movement;
            if(tentativeMoney <= 0){
                logger.info("I do not have enough money, I stopped!");
                return ("doNothing");
            } else {
                this.money = tentativeMoney;
            }
            fd(1);
            return ("goForBail");
            }            
        }
        
        public String eatBail() {
            HashMap<Integer, myBail> availableBails = new HashMap();
             availableBails = getAvailableBails();
            int x =myTargetBail.x -xcor();
            int y = myTargetBail.y -ycor();
            Patch patchBail = getPatchAt(x,y);
            patchBail.setColor(blue);
            Color myColor = patchBail.getColor();
                availableBails.clear();
             availableBails = getAvailableBails();
            this.money = this.money - this.cost_photo;
            myMessage m = new myMessage();
            m.bailIate = myTargetBail;
            this.score = this.score + myTargetBail.score;
            m.myAddress = getAgentAddressIn("myturtles","myturtles","going");
            sendMessage(this.scenario_address.get(0), m);
            
            if(currentSchedule.bailsToeat.size() > 1){
                currentSchedule.bailsToeat.remove(0);
                currentSchedule.positionsToEat.remove(0);
                
                return ("goForBail");
            } else {
                currentSchedule.bailsToeat.remove(0);
                currentSchedule.positionsToEat.remove(0);
            leaveRole("myturtles","myturtles","going");
            
                return ("agreementPhase");
                 
            }  
            
    }
        public String doNothing(){
           try(FileWriter fw = new FileWriter("score"+this.numPlans+".txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(myAddress.hashCode() +";"+this.score+";"+this.totalScenarioScore);
            //more code
            //more code
        } catch (IOException e) {
    //exception handling left as an exercise for the reader
}           Double cost = this.initialMoney - this.money;
           try(FileWriter cw = new FileWriter("cost"+this.numPlans+".txt", true);
            BufferedWriter dw = new BufferedWriter(cw);
            PrintWriter out = new PrintWriter(dw))
            {
            out.println(myAddress.hashCode() +";"+cost+";"+this.totalScenarioScore);
            //more code
            //more code
        } catch (IOException e) {
    //exception handling left as an exercise for the reader
}
            myMessage hi = new myMessage();
            hi.things_to_say = "died";
            sendMessage(this.scenario_address.get(0),hi);
            this.numberofBugs--;
            if(bye == true){
                 sendMessage(schedulerAddress, new SchedulingMessage(SchedulingAction.SHUTDOWN)); 
            }
            killAgent(this);
            
            return ("doNothing");
        }
        
public UtCost getUtility (Double xbail, Double ybail, Double xev, Double yev,
            Double xorigin, Double yorigin, Double score_bails,ArrayList sensor_tasks){
    UtCost littleBundle = new UtCost();
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
            alpha = 2;
            break;
        
    }
    
    Double ev_point = sqrt((xbail - xev)*(xbail - xev) + (ybail - yev)*(ybail - yev));
    Double distance;
   // alpha = 1;
    distance = (Double) sqrt((xbail - xorigin)*(xbail - xorigin) + (ybail - yorigin)*(ybail - yorigin));
    Double utility = score_bails*alpha*(1/(1 + exp((ev_point) - dropoff_ut_value))) 
            - this.cost_movement*(distance - ev_point);
    littleBundle.utility = utility;
    littleBundle.cost = this.cost_movement*(distance - ev_point);
    return littleBundle;
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
        double utility = 0;
        double cost = 0;
        List<myBail> setB = total_permutations.get(i);
        Double xsub1 = getX();
        Double ysub1 = getY();
            for (int k = 0; k < setB.size(); k++){
                ArrayList common_sensors = new ArrayList();
                common_sensors.addAll(actual_sensors);
                common_sensors.retainAll(setB.get(k).bail_sensor);
                
                Double optUtility = 0.0;
                Double totalCost = 0.0;
                
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
                    UtCost utcostsingle;
                    Double utilitysingle;
                    Double costsingle;
                    utcostsingle = getUtility ((double) setB.get(k).x,(double) setB.get(k).y, 
                        xsub1, ysub1, xorigin,yorigin, setB.get(k).score,common_sensors); 
                    utilitysingle = utcostsingle.utility;
                    costsingle = utcostsingle.cost;
                    if (utilitysingle > optUtility || xpointOpt == null){
                        totalCost = costsingle;
                        optUtility = utilitysingle;
                        xpointOpt = xsub1;
                        ypointOpt = ysub1;
                    } 
                        xsub1 = xsub1 + xsum;
                        ysub1 = ysub1 + ysum;
                }
                Double[] point = {xpointOpt,ypointOpt};
                cost = cost + totalCost;
                utility = utility + optUtility;
                optimum_position.add(k, point);
                xsub1 = xpointOpt;
                ysub1 = ypointOpt;
            }
            
            sample_plan.bailOrder.add((ArrayList<myBail>) setB);
            sample_plan.positions.add(optimum_position);
            double auxUtility;
            if (cost + setB.size()*this.cost_photo < this.money ){
                auxUtility = 0.0;
            } else {
            auxUtility = utility;            
        }
            sample_plan.utility.add(auxUtility);
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
        }
            sample_plan = calculateExtraBids(sample_plan);
            return sample_plan;
    }

    private Schedule calculateIncreaseUtility(ArrayList<myBail> bundle, 
            myBail bailEnquestio, HashMap<ArrayList<String>,Double> previous_bid) {
            Schedule mySch = new Schedule();
            mySch.bailsToeat.addAll(bundle);
        if (bundle.contains(bailEnquestio)){
            mySch.TotalScoreCoalitions.clear();
            mySch.TotalScoreCoalitions.put(actual_sensors, 0.0);
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
           for (ArrayList<String> bundleSensors : previous_bid.keySet()){
           increase = this_plan.utilityCoalitions.get(bundleSensors) - previous_bid.get(bundleSensors);
           if (increase > 0){
               mySch.bailsToeat = this_plan.bailOrder.get(0);
               mySch.positionsToEat = this_plan.positions.get(0);
               mySch.TotalScoreCoalitions.put(bundleSensors, this_plan.utilityCoalitions.get(bundleSensors));        
           } else {
               mySch.TotalScoreCoalitions.put(bundleSensors, 0.0);
           }
        }
        }
           return mySch;
     
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
      timer.put(bail, this.timer_map.get(address).get(bail));
   }
    
    private void firstPhase(){
         HashMap<Integer,myBail> myBailsavailable = new HashMap();
         myBailsavailable = getAvailableBails();
         for (myBail bb : myBailsavailable.values()){
            hvector.put(bb, true);
        }
      while (myBundle.size()< numPlans && hvector.containsValue(true))
            { //order.clear();
              //coordinates.clear();
                for ( myBail bailEnquestio : totalBailsavailable ){
                    
                    auxSchedule = calculateIncreaseUtility(myBundle, bailEnquestio, 
                            bundlePlainScore.get(myBundle.size()));
                    myBids.put(bailEnquestio, auxSchedule.TotalScoreCoalitions);
                    order.put(bailEnquestio,auxSchedule.bailsToeat);
                    coordinates.put(bailEnquestio, auxSchedule.positionsToEat);
                    checkhvector(bailEnquestio, myBailsavailable);
                }
                //decision
                myBail optimum = new myBail();
                optimum = null;
                ArrayList<String> sensorsOptimum = new ArrayList();
                Double winningBid = 0.0;
                for (myBail aa : myBailsavailable.values()){
                    if (hvector.get(aa) == true){
                        for (ArrayList<String> sensOptimum : myBids.get(aa).keySet()){
                            if (myBids.get(aa).get(sensOptimum) > winningBid || optimum == null){
                                winningBid = myBids.get(aa).get(sensOptimum);
                                optimum = aa;
                                sensorsOptimum = sensOptimum;
                            }
                        }
                    }
                }
                if (optimum != null){
                    myBail auxiliaryBail = new myBail();
                    auxiliaryBail = optimum;
                    myBundle.add(auxiliaryBail);
                    HashMap<ArrayList<String>,Double> mu = new HashMap();
                    mu = bundlePlainScore.get(bundlePlainScore.size()-1);
                    mu.put(sensorsOptimum, mu.get(sensorsOptimum) + winningBid);
                    def_position = order.get(auxiliaryBail);
                    def_coordinates = coordinates.get(auxiliaryBail);
                    ArrayList<AgentAddress> onemoreWinner = new ArrayList();
                    onemoreWinner.add(myAddress);
                    winners.put(auxiliaryBail, onemoreWinner);
                    timer.put(auxiliaryBail, time);
                    HashMap<ArrayList<String>,Double> winbidmap = new HashMap();
                    winbidmap.put(sensorsOptimum,winningBid);
                    mymatesBids.put(auxiliaryBail, winbidmap);
                    hvector.put(optimum,false);
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

    private void broadcast(AgentAddress bugEnquestio) {
            myMessage sendMyNumbers = new myMessage();
            if(bugEnquestio == myAddress){
            
            sendMyNumbers.SP.winners.putAll(this.winners);
            sendMyNumbers.SP.maximumBids.putAll(mymatesBids);
            sendMyNumbers.SP.myAddress = this.myAddress;
            
            sendMyNumbers.SP.myTimer.putAll(timer);
            }else{
            sendMyNumbers.SP.winners.putAll(winMap.get(bugEnquestio));
            sendMyNumbers.SP.maximumBids.putAll(myTurtlemap.get(bugEnquestio));
            sendMyNumbers.SP.myAddress = bugEnquestio;
            
            sendMyNumbers.SP.myTimer.putAll(this.timer_map.get(bugEnquestio)); 
            }
            broadcastMessage("myturtles","myturtles","bug", sendMyNumbers);  
    }

    private void clearEverything() {
            myBids.clear();
            myBundle.clear();
             mymatesBids.clear();
             timer.clear();
             history.clear();
             myTurtlemap.clear();
             winners.clear();
             winMap.clear();
             timer_map.clear();
             this.time = 0;
             this.count_convergence = 0;    }
    
        private void assignSensorsRandomly() {
                int[] number_of_sensors = {1,2};
                double[] probabilities = {0.7,0.3};
                this.actual_sensors = (ArrayList<String>) myFunctions.assignRandomSensors(number_of_sensors, 
                probabilities, possible_sensors);

    }

    private Plan calculateExtraBids(Plan sample_plan) {
        for (int i = 0; i < sample_plan.bailOrder.get(0).size(); i++){
            myBail analyzedBail = sample_plan.bailOrder.get(0).get(i);
            List<String> aux_sensors = new ArrayList<String>(this.actual_sensors);
            aux_sensors.removeAll(analyzedBail.bail_sensor);
            ArrayList<String> common_sensors = new ArrayList<String>();
            common_sensors.addAll(actual_sensors);
            common_sensors.retainAll(analyzedBail.bail_sensor);
            for (String sensor : aux_sensors){
                common_sensors.add(sensor);
                UtCost utcost = new UtCost();
                Double moreUtility;
                utcost = getUtility ((double)analyzedBail.x,(double) analyzedBail.y, 
                        sample_plan.positions.get(0).get(i)[0], sample_plan.positions.get(0).get(i)[1],
            getX(), getY(), analyzedBail.score, (ArrayList) common_sensors);
                moreUtility = utcost.utility;
                sample_plan.utilityCoalitions.put(common_sensors, moreUtility);
            }
        }
        sample_plan.utilityCoalitions.put(actual_sensors, sample_plan.utility.get(0));
        return sample_plan;
    }

    private void checkhvector(myBail bailEnquestio, HashMap<Integer, myBail> myBailsavailable) {
        for(ArrayList<String> sensors : myBids.get(bailEnquestio).keySet()){
        if (sensors.size() == 1){
        if(myBids.get(bailEnquestio).get(sensors) - mymatesBids.get(bailEnquestio).get(sensors) > 0.0
                       && myBailsavailable.values().contains(bailEnquestio)){
                        hvector.put(bailEnquestio, true);
                        //mymatesBids.put(bailEnquestio, myBids.get(bailEnquestio));
                    } else {
                        hvector.put(bailEnquestio, false);
                    }
        } else {
            
        }
    }
    }*/

