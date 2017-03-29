package Test;

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
import java.util.Set;
  
    public class myBugCBBACoalitions2 extends Turtle { 
        
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
        private HashMap<myBail,Double> myBids = new HashMap();
        private HashMap<myBail,Double> maximumsumBids = new HashMap();
        private HashMap<myBail,HashMap<AgentAddress,Double>> mymatesBids = new HashMap();
        private HashMap<myBail,ArrayList<AgentAddress>> winners = new HashMap();
        private HashMap<myBail, HashMap<AgentAddress, Integer>> timer = new HashMap();
        private HashMap<AgentAddress, HashMap <myBail, HashMap<AgentAddress,Integer>>> timer_map = new HashMap();
        private HashMap<AgentAddress, HashMap<myBail,ArrayList<AgentAddress>>> winMap = new HashMap();
        private ArrayList<Double> bundlePlainScore = new ArrayList();
        private final int numPlans = 2;
        //private HashMap<myBail, ArrayList<String>> sensorsIBid = new HashMap();
        //private HashMap<myBail, HashMap<AgentAddress,ArrayList<String>>>  mapSensorBids = new HashMap();
        
        
        // Variables from the scenario
        private Patch[] patchGrid;
        private HashMap<Integer, myBail> myBailsmap;
        private int dropoff_ut_value;
        private ArrayList<Integer> Position_bails; //in the Grid's counting method 
        private ArrayList<myBail> totalBailsavailable;
        private ArrayList<AgentAddress> scenario_address;
        private AgentAddress schedulerAddress;
        private HashMap<AgentAddress,HashMap<myBail,HashMap<AgentAddress,Double>>> myTurtlemap = new HashMap(); 
        private HashMap<AgentAddress,ArrayList<String>> myTurtlemapsensor = new HashMap();
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
                setLogLevel(Level.FINEST);
                //Starting array of types of sensors
               /* possible_sensors = new ArrayList<>();
                possible_sensors.add("MW"); //sensor type 1
                possible_sensors.add("IR"); //sensor type 2*/
                
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
            this.myTurtlemapsensor.put(myAddress, actual_sensors);
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
            if (myBids.isEmpty()){
                bundlePlainScore.add(0.0);
                for (myBail bb : totalBailsavailable){
                    myBids.put(bb, 0.0);
                    
                }
            }
            if (mymatesBids.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    mymatesBids.put(bb,new HashMap());
                }
            }
           
             /*if (timer.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    
                    timer.put(bb, 0);
                }
            }     */     
     firstPhase();
     logger.info("exited first phase");
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
                this.myTurtlemap.put(m.SP.myAddress, m.SP.maximumBidscoal);
                this.winMap.put(m.SP.myAddress, m.SP.winnerscoal);
                this.timer_map.put(m.SP.myAddress, m.SP.myTimercoal);
                this.myTurtlemapsensor.put(m.SP.myAddress, m.SP.mySensors);
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
            
            }} else if (this.myTurtlemap.size() != getAgentsWithRole("myturtles","myturtles1","bug").size()){
                return ("secondPhase");
            }
                
            time++;
          
            this.number_iterations ++;
            ArrayList<AgentAddress> senderWinnerArray;
            ArrayList<AgentAddress> myWinnerArray;
            
            for ( AgentAddress address : myTurtlemap.keySet()){
                
                for (myBail bail : myTurtlemap.get(address).keySet()){ 
                senderWinnerArray = winMap.get(address).get(bail);
                myWinnerArray = this.winners.get(bail);
                provisional ProvisionalData = new provisional();                
                comparator listWinners = getWinnersList(senderWinnerArray,myWinnerArray,bail);
                for ( String sensor : bail.bail_sensor){
                    AgentAddress senderWinner = listWinners.winnersSender.get(sensor);
                    AgentAddress myWinner = listWinners.winnersReceiver.get(sensor);
                if(senderWinner == address){
                    if(myWinner == myAddress){
                        winnerSet decision = new winnerSet();
                        decision = winnerDetermination(senderWinner,myWinner,address,bail,listWinners);
                        updateProvisional(ProvisionalData, sensor, decision.winner, bail,decision.winner);
                        }                    
                    else if(myWinner != myAddress && myWinner != address && myWinner != null){
                        if(this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                       updateProvisional(ProvisionalData, sensor, senderWinner, bail, senderWinner);
                        
                        }  else {
                            winnerSet decision = new winnerSet();
                            decision = winnerDetermination(senderWinner,myWinner,address,bail,listWinners);
                            updateProvisional(ProvisionalData, sensor, senderWinner, bail, senderWinner);
                        }                        
                    } else if (myWinner == address){
                        if (this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                            updateProvisional(ProvisionalData, sensor, senderWinner, bail, senderWinner);        
                        } else {
                            updateProvisional(ProvisionalData, sensor, myWinner, bail, myWinner);
                        }
                        
                    } else {
                        updateProvisional(ProvisionalData, sensor, senderWinner, bail, senderWinner);
                        
                    }
                } else if(senderWinner == myAddress){
                    if(myWinner == address ){
                        resetProvisional(ProvisionalData, sensor, bail);
                    } else {
                        updateProvisional(ProvisionalData, sensor, myWinner, bail, myWinner);
                    }                     
                } else if (senderWinner != myAddress && senderWinner != address && senderWinner != null){
                    if(myWinner == myAddress){
                       winnerSet decision = new winnerSet();
                        decision = winnerDetermination(senderWinner,myWinner,address,bail,listWinners);
                        AgentAddress time_update;
                        if(decision.winner == myAddress){
                            time_update = myAddress;
                                    } else {
                            time_update = address;
                                    }
                        updateProvisional(ProvisionalData, sensor, decision.winner, bail, time_update);
                        
                  }  else if (myWinner == address){
                       if (this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                            updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);
                        } else {
                            resetProvisional(ProvisionalData, sensor, bail);;
                        }
                    } else if( myWinner != myAddress && myWinner != address && myWinner != senderWinner
                            && myWinner != null){
                        if(this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                       updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);
                        
                        }  else {
                            winnerSet decision = new winnerSet();
                            decision = winnerDetermination(senderWinner,myWinner,address,bail,listWinners);
                            updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);
                        }  
                    } else if(myWinner == senderWinner){
                        if (this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                            updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);        
                        } else {
                            updateProvisional(ProvisionalData, sensor, myWinner, bail, myAddress);
                        }
                    } else  {
                        updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);
                        
                    }
                    
                    } else {
                        if(myWinner == address){
                            updateProvisional(ProvisionalData, sensor, myWinner, bail, myAddress);
                        } else if (myWinner != address && myWinner != myAddress && myWinner != null){
                            if(this.timer_map.get(address).get(bail).get(address) >= timer.get(bail).get(myWinner)){
                                updateProvisional(ProvisionalData, sensor, senderWinner, bail, address);
                            } else {
                                updateProvisional(ProvisionalData, sensor, myWinner, bail, myAddress);
                            }
                        } else if(myWinner == myAddress){
                            updateProvisional(ProvisionalData, sensor, myWinner, bail, myAddress);
                        } else{
                            //updateProvisional(ProvisionalData,sensor,myWinner,bail, myAddress);
                        }
                        }
                    }
                checkProvisional(ProvisionalData, bail); 
                }
                
            }
            
             
           //check releases:
          for (int i = 0; i < myBundle.size() ; i++){
              myBail bailTC = myBundle.get(i);
              if(!winners.get(bailTC).contains(myAddress)){
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
              if(!winners.get(bailTC2).contains(myAddress)){
                    for (int r = (i); r < myBundle.size(); r++){
                    provisional provisionalBundle = new provisional();
                    for (String sensor : myBundle.get(r).bail_sensor){
                        for(AgentAddress address : mymatesBids.get(myBundle.get(r)).keySet()){
                            if(address != myAddress){
                            if(this.myTurtlemapsensor.get(address).contains(sensor)){
                                provisionalBundle.provisionalScore.put(sensor,mymatesBids.get(myBundle.get(r)).get(address));
                                provisionalBundle.provisionalWinners.put(sensor,address);
                                provisionalBundle.provisionalTime.put(sensor,this.timer.get(myBundle.get(r)).get(address));
                            }
                        }
                    }
                    
                   /*Double update(provisional ProvisionalData, myBail bailEnquestio)
                    HashMap<AgentAddress, Double> newBugs = new HashMap();
                    ArrayList<AgentAddress> newAddresses = new ArrayList();
                    newBugs = mymatesBids.get(myBundle.get(r));
                    newBugs.remove(myAddress);
                    mymatesBids.put(myBundle.get(r), newBugs);
                    newAddresses = winners.get(myBundle.get(r));
                    newAddresses.remove(myAddress);
                    winners.put(myBundle.get(r), newAddresses);*/
                    }
                    Double maxbid;
                    maxbid = update(provisionalBundle, myBundle.get(r));
                    this.maximumsumBids.put(myBundle.get(r),maxbid);
              }
                    break;
          }
          }
          
          myBundle.retainAll(def_position);
          HashMap<myBail,Double> auxmymatesBids2 = new HashMap();
           auxmymatesBids2.putAll(this.maximumsumBids);
            history.add(auxmymatesBids2);
            broadcast(myAddress);
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
            return("sitAndThink");*/
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
            } */
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
            Double xorigin, Double yorigin, Double score_bails,ArrayList sensor_tasks, myBail bailEnquestio,
            ArrayList matesSensors){
    UtCost littleBundle = new UtCost();
    int i = 0;
    double alpha = 0;
    int j = bailEnquestio.bail_sensor.size();
    int k = matesSensors.size();
    double result = (double) k/j;
    alpha = alphafunction(result);
    Double ev_point = sqrt((xbail - xev)*(xbail - xev) + (ybail - yev)*(ybail - yev));
    Double distance;
   // alpha = 1;
    distance = (Double) sqrt((xbail - xorigin)*(xbail - xorigin) + (ybail - yorigin)*(ybail - yorigin));
    /*Double utility = score_bails*alpha*(1/(1 + exp((ev_point) - dropoff_ut_value)))*(this.actual_sensors.size()/j) 
            - this.cost_movement*(distance - ev_point);*/
    Double utility = score_bails*alpha*deltaK(ev_point)*(this.actual_sensors.size()/k) //(1/(1 + exp((ev_point) - dropoff_ut_value))) 
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

   /* private void assignSensorsRandomly() {
                int[] number_of_sensors = {1,2};
                double[] probabilities = {0.7,0.3};
                this.actual_sensors = (ArrayList<String>) myFunctions.assignRandomSensors(number_of_sensors, 
                probabilities, possible_sensors);

    }*/

   
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

    private Plan evaluatePermutations(HashMap<Integer, ArrayList<myBail>> total_permutations, 
            Double previous_bid, ArrayList<String> matesSensors) {
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
                        xsub1, ysub1, xorigin,yorigin, setB.get(k).score,actual_sensors , setB.get(k),
                        matesSensors); 
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
            if (cost + setB.size()*this.cost_photo > this.money ){
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
            
            return sample_plan;
    }

    private Schedule calculateIncreaseUtility(ArrayList<myBail> bundle, 
            myBail bailEnquestio, Double previous_bid, ArrayList<String> matesSensors) {
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
           this_plan = evaluatePermutations(solution,previous_bid,matesSensors);
           Double increase;
           
           increase = this_plan.utility.get(0) - previous_bid;
           if (increase > 0){
               mySch.bailsToeat = this_plan.bailOrder.get(0);
               mySch.positionsToEat = this_plan.positions.get(0);
               mySch.TotalScore = increase;     
               mySch.arrival_times = this_plan.arrival_times.get(0);
           } else {
               mySch.TotalScore = 0.0;
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
   private void checkProvisional (provisional ProvisionalData, myBail bail){
       HashMap<AgentAddress, ArrayList<String>> checkmap = new HashMap();
       AgentAddress winner;
   for (String sensor : bail.bail_sensor ){
       ArrayList<String> amount_sensors = new ArrayList();
       winner = ProvisionalData.provisionalWinners.get(sensor);
       if(checkmap.containsKey(winner)){
       amount_sensors.addAll(checkmap.get(winner));
       }
       amount_sensors.add(sensor);
       checkmap.put(winner, amount_sensors);
   }
   //check
   for (AgentAddress winners : checkmap.keySet()){
       if(winners != null){
       if(!checkmap.get(winners).containsAll(this.myTurtlemapsensor.get(winners)) 
               || checkmap.get(winners).size() != this.myTurtlemapsensor.get(winners).size()){
           for (String sensor : checkmap.get(winners)){
           resetProvisional(ProvisionalData,sensor,bail);
           }
        } 
       }
       Double maxBid;
       maxBid = update(ProvisionalData, bail);
       this.maximumsumBids.put(bail, maxBid);
   }
   }
   private void updateProvisional(provisional ProvisionalData, String sensor, AgentAddress winner, myBail bail,
           AgentAddress senderAddress){
        ProvisionalData.provisionalWinners.put(sensor,winner);
        if(senderAddress != myAddress){
        ProvisionalData.provisionalScore.put(sensor,myTurtlemap.get(senderAddress).get(bail).get(winner));
        ProvisionalData.provisionalTime.put(sensor,this.timer_map.get(senderAddress).get(bail).get(winner));
        } else {
        ProvisionalData.provisionalScore.put(sensor,this.mymatesBids.get(bail).get(winner));
        ProvisionalData.provisionalTime.put(sensor,this.timer.get(bail).get(winner));   
        }
}
   /*private void update(myBail bail, AgentAddress address){
       mymatesBids.put(bail,myTurtlemap.get(address).get(bail));
      this.winners.put(bail, this.winMap.get(address).get(bail));
      timer.put(bail, this.timer_map.get(address).get(bail));
   }*/
    
    private void firstPhase(){
         HashMap<Integer,myBail> myBailsavailable = new HashMap();
         myBailsavailable = getAvailableBails();
         HashMap<myBail,HashMap<AgentAddress,Double>> provisional = new HashMap();
         //HashMap<myBail, ArrayList<AgentAddress>> bidBeaten = new HashMap();
         for (myBail bb : this.totalBailsavailable){
             if(myBailsavailable.containsValue(bb)){
            hvector.put(bb, true);
        } else{
                 hvector.put(bb,false);
             }
         }
      while (myBundle.size()< numPlans && hvector.containsValue(true))
            { //order.clear();
              //coordinates.clear()
                boolean close = false;
                for ( myBail bailEnquestio : myBailsavailable.values() ){
                ArrayList<AgentAddress> bugsJoined = new ArrayList();
                bugsJoined = this.winners.get(bailEnquestio);
                ArrayList common_sensors = new ArrayList();
                common_sensors.addAll(actual_sensors);
                if(bugsJoined != null){
                for (AgentAddress bugJoined : bugsJoined){
                    ArrayList<String> usedSensors = new ArrayList();
                    try{
                        usedSensors.addAll(this.myTurtlemapsensor.get(bugJoined));
                    } catch (NullPointerException e){}
                    if(Collections.disjoint(actual_sensors, usedSensors)) {
                      common_sensors.add(usedSensors);
                      HashMap<AgentAddress,Double> provisional2 = new HashMap();
                      provisional2.putAll(provisional.get(bailEnquestio));
                      provisional2.put(bugJoined, this.mymatesBids.get(bailEnquestio).get(bugJoined));
                      provisional.put(bailEnquestio, provisional2);
                    }
                }
                }
                /*if(bugsJoined != null){
                for (AgentAddress bugJoined : bugsJoined){
                    ArrayList<String> usedSensors = new ArrayList();
                    try{
                        usedSensors.addAll(this.myTurtlemapsensor.get(bugJoined));
                    } catch (NullPointerException e){}
                    if(!Collections.disjoint(actual_sensors, usedSensors)) {
                      common_sensors.add(usedSensors);
                    }
                }*/
                
                
                    auxSchedule = calculateIncreaseUtility(myBundle, bailEnquestio, 
                            bundlePlainScore.get(myBundle.size()),common_sensors);
                    myBids.put(bailEnquestio, auxSchedule.TotalScore);
                    order.put(bailEnquestio,auxSchedule.bailsToeat);
                    coordinates.put(bailEnquestio, auxSchedule.positionsToEat);
                    updateHashMap(provisional,bailEnquestio,myAddress,myBids.get(bailEnquestio));
                    //bidBeaten.put(bailEnquestio, checkhvector(bailEnquestio, myBailsavailable, provisional));
                    checkhvector(bailEnquestio, myBailsavailable, provisional);
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
                    Double mu = bundlePlainScore.get(bundlePlainScore.size()-1);
                    bundlePlainScore.add(winningBid + mu);
                    def_position = order.get(auxiliaryBail);
                    def_coordinates = coordinates.get(auxiliaryBail);
                    /*ArrayList<AgentAddress> bugsBeaten = new ArrayList();
                    bugsBeaten = bidBeaten.get(auxiliaryBail);
                    ArrayList<AgentAddress> winnersBail = new ArrayList();
                    try{
                    winnersBail.addAll(winners.get(auxiliaryBail));
                    } catch (NullPointerException e){}
                    if(!bugsBeaten.isEmpty()){
                        winnersBail.removeAll(bugsBeaten);
                    } 
                    winnersBail.add(myAddress);
                    winners.put(auxiliaryBail,winnersBail);
                    HashMap mybid = new HashMap();
                    mybid.put(myAddress, winningBid);
                    this.mymatesBids.put(auxiliaryBail, mybid);
                    HashMap<AgentAddress,Integer> timeupdate = new HashMap();
                    try{
                    timeupdate.putAll(timer.get(auxiliaryBail));
                    }catch (NullPointerException e){}
                    timeupdate.put(myAddress, time);
                    timer.put(auxiliaryBail, timeupdate);*/
                    ArrayList<AgentAddress> updateWinners = new ArrayList();
                    updateWinners.addAll(provisional.get(auxiliaryBail).keySet());
                    winners.put(auxiliaryBail,updateWinners);
                    mymatesBids.get(auxiliaryBail).clear();
                    mymatesBids.put(auxiliaryBail, provisional.get(auxiliaryBail));
                    updateHashMap(timer, auxiliaryBail, myAddress, time);
                    hvector.put(optimum,false);
            }
                    
            }
                
         
            
    }
         
    private void resetProvisional(provisional ProvisionalData, String sensor, myBail bail){
        ProvisionalData.provisionalWinners.put(sensor,null);
        ProvisionalData.provisionalScore.put(sensor,0.0);
           
    }

   /* private void reset(myBail bail) {
      mymatesBids.put(bail,0.0);
      
      this.winners.put(bail, null);
      
    }*/

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
            try{
            sendMyNumbers.SP.myAddress = this.myAddress;
            sendMyNumbers.SP.winnerscoal.putAll(this.winners);
            sendMyNumbers.SP.mySensors.addAll(actual_sensors);
            sendMyNumbers.SP.maximumBidscoal = this.mymatesBids;
            sendMyNumbers.SP.myTimercoal.putAll(timer);
            
            } catch (NullPointerException e){}
            }else{
            try{
            sendMyNumbers.SP.myAddress = bugEnquestio;
            sendMyNumbers.SP.winnerscoal.putAll(winMap.get(bugEnquestio));
            sendMyNumbers.SP.mySensors.addAll(this.myTurtlemapsensor.get(bugEnquestio));
            sendMyNumbers.SP.maximumBidscoal.putAll(myTurtlemap.get(bugEnquestio));
            sendMyNumbers.SP.myTimercoal.putAll(this.timer_map.get(bugEnquestio)); 
            
            } catch (NullPointerException e){}
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
             this.count_convergence = 0;    
    }
    
        private void assignSensorsRandomly() {
                int[] number_of_sensors = {1,2};
                double[] probabilities = {0.7,0.3};
                this.actual_sensors = (ArrayList<String>) myFunctions.assignRandomSensors(number_of_sensors, 
                probabilities, possible_sensors);

    }
    

    private ArrayList<AgentAddress> checkhvector(myBail bailEnquestio, HashMap<Integer, myBail> myBailsavailable) {
        //HashMap<AgentAddress, ArrayList<String>> sensors = new HashMap();
        ArrayList<AgentAddress> winners = this.winners.get(bailEnquestio);
        HashMap<AgentAddress, Double> matesBids = this.mymatesBids.get(bailEnquestio);
        if (winners != null && winners.size() == 0 && myBailsavailable.containsValue(bailEnquestio)){
            hvector.put(bailEnquestio, true);
            return null;
        } else if (!myBailsavailable.containsValue(bailEnquestio)){
            hvector.put(bailEnquestio, false);
            return null;
        } else {
           ArrayList<AgentAddress> competitorsList = new ArrayList(); 
           ArrayList<String> sensorswinner = new ArrayList();
           ArrayList<AgentAddress> othersList = new ArrayList();
           ArrayList<String> otherssensors = new ArrayList();
           Double scorewinner = 0.0;
           Double scoreothers = 0.0;
         if (winners != null && this.myTurtlemapsensor.size() != 1){  
        for (AgentAddress address : winners ){
            if (Collections.disjoint(this.myTurtlemapsensor.get(address),actual_sensors)){
                competitorsList.add(address);
                sensorswinner.addAll(this.myTurtlemapsensor.get(address));
                scorewinner = scorewinner + this.mymatesBids.get(bailEnquestio).get(address);
            } else {
                othersList.add(address);
                otherssensors.addAll(this.myTurtlemapsensor.get(address));
                scoreothers = scoreothers + this.mymatesBids.get(bailEnquestio).get(address);
            }
        }
         }
            if(competitorsList.contains(myAddress)){
                if(this.myBids.get(bailEnquestio) > this.mymatesBids.get(bailEnquestio).get(myAddress)){
                    this.hvector.put(bailEnquestio, true);
                    ArrayList<AgentAddress> thisreturn = new ArrayList();
                    thisreturn.add(myAddress);
                    return thisreturn;
                } else {
                this.hvector.put(bailEnquestio, false);
                return null;
                }
                
            } else {
                if(sensorswinner.containsAll(this.actual_sensors) &&
                        sensorswinner.size() == this.actual_sensors.size()){
                    if(this.myBids.get(bailEnquestio) > scorewinner){
                    ArrayList<AgentAddress> thisreturn = new ArrayList();
                    thisreturn.addAll(competitorsList);
                    return thisreturn;
                    } else {
                    this.hvector.put(bailEnquestio, false);
                    return null;
                    }
                } else{
                    ArrayList<String> sensorsExcept = new ArrayList();
                    int m = this.actual_sensors.size();
                    int n = sensorswinner.size();
                    if(n > m){
                        if(this.myBids.get(bailEnquestio) < scorewinner){
                        this.hvector.put(bailEnquestio, false);
                        return null;
                        } else {
                        sensorsExcept.addAll(sensorswinner);
                        sensorsExcept.removeAll(this.actual_sensors);
                        int r1 = sensorswinner.size() + otherssensors.size();
                        int r2 = r1 - sensorsExcept.size();
                        Double deltaAlpha = (alphafunction((double)r1/bailEnquestio.bail_sensor.size())
                                - alphafunction((double)r2/bailEnquestio.bail_sensor.size()))*bailEnquestio.score;
                        if(this.myBids.get(bailEnquestio) - deltaAlpha > scorewinner){
                            this.hvector.put(bailEnquestio, true);
                            return competitorsList;
                        } else {
                            this.hvector.put(bailEnquestio, false);
                            return null;  
                        }
                      }
                    } else {
                        if(this.myBids.get(bailEnquestio) > scorewinner){
                        this.hvector.put(bailEnquestio, true);
                        return competitorsList;
                        } else {
                        sensorsExcept.addAll(this.actual_sensors);
                        sensorsExcept.removeAll(sensorswinner);
                        int r1 = sensorswinner.size() + otherssensors.size();
                        int r2 = r1 + sensorsExcept.size();
                        Double deltaAlpha = ( - alphafunction((double)r1/bailEnquestio.bail_sensor.size())
                                + alphafunction((double)r2/bailEnquestio.bail_sensor.size()))*bailEnquestio.score;
                        if(this.myBids.get(bailEnquestio) + deltaAlpha > scorewinner){
                            this.hvector.put(bailEnquestio, true);
                            return competitorsList;
                        } else {
                            this.hvector.put(bailEnquestio, false);
                            return null;  
                        }
                        }
                    }
                }
            }
        }
    }
 
    private double alphafunction(double result) {
        
        return 2*result*result/3 + result/3;//-2.58333*result + 12.0833*result*result - result*result*result*8.5;    
    }
    
    private comparator getWinnersList(ArrayList<AgentAddress> senderWinner, 
            ArrayList<AgentAddress> myWinner, myBail bailEnquestio) {
        comparator thisComparator = new comparator();
        
            for (String sensorkind : bailEnquestio.bail_sensor){
                if(senderWinner == null){
                    thisComparator.winnersSender.put(sensorkind,null);
                } else {
                for (AgentAddress senderAddress : senderWinner){
                    if (myTurtlemapsensor.get(senderAddress).contains(sensorkind)){
                        thisComparator.winnersSender.put(sensorkind,senderAddress);
                    }
                }
                if (!thisComparator.winnersSender.containsKey(sensorkind)){
                    thisComparator.winnersSender.put(sensorkind, null);
                }
                }
                if(myWinner == null){
                    thisComparator.winnersReceiver.put(sensorkind,null);
                } else {
                for (AgentAddress senderAddress : myWinner){
                    if (myTurtlemapsensor.get(senderAddress).contains(sensorkind)){
                        thisComparator.winnersReceiver.put(sensorkind,senderAddress);
                    }
                }
                if (!thisComparator.winnersReceiver.containsKey(sensorkind)){
                    thisComparator.winnersReceiver.put(sensorkind, null);
                }
                }
                
        
    }
        
 
    return thisComparator;
    }
    
    private winnerSet winnerDetermination (AgentAddress senderwinner, AgentAddress myWinner, AgentAddress sender,
            myBail bailEnquestio, comparator comparatorEnquestio){
                    //Sensors per winner
                    ArrayList<String> sensorssenderwin = new ArrayList(this.myTurtlemapsensor.get(senderwinner));
                    ArrayList<String> sensorsmywin = new ArrayList(this.myTurtlemapsensor.get(myWinner));
                    //Amount of sensors per winner
                    int m = sensorsmywin.size();
                    int n = sensorssenderwin.size();
                    //Other sensors in the task not won bythe sender/receiver
                    ArrayList<String> othersensorssender = new ArrayList();
                    ArrayList<String> othersensorsmy = new ArrayList();
                    Double scorewinner;
                    Double scoremy;
                    //Bid per winner
                    scorewinner = this.myTurtlemap.get(sender).get(bailEnquestio).get(senderwinner) ;
                    scoremy = this.mymatesBids.get(bailEnquestio).get(myWinner);
                    
                    
                    for (AgentAddress thisaddress : this.winMap.get(senderwinner).get(bailEnquestio)){
                        othersensorssender.addAll(this.myTurtlemapsensor.get(thisaddress));
                    }
                    othersensorssender.removeAll(sensorssenderwin);
                    for (AgentAddress thisaddress : this.winners.get(bailEnquestio)){
                        othersensorsmy.addAll(this.myTurtlemapsensor.get(thisaddress));
                    }
                    othersensorsmy.removeAll(sensorsmywin);
                       
        
                    winnerSet solution = new winnerSet();
                    ArrayList<String> sensorsExcept = new ArrayList();
                    ArrayList<String> othersensorsExcept = new ArrayList();
                    //If numberSensors sender > numberSensors receiver
                    if(n > m){
                        ArrayList<AgentAddress> previous = new ArrayList();
                         othersensorsmy.removeAll(sensorssenderwin);
                        sensorsExcept.addAll(sensorssenderwin);
                        sensorsExcept.removeAll(sensorsmywin);
                        for(String sensor : sensorsExcept){
                            AgentAddress auxAd = comparatorEnquestio.winnersReceiver.get(sensor);
                            if (!previous.contains(auxAd)){
                                scoremy = scoremy + this.mymatesBids.get(bailEnquestio).get(auxAd);
                        }
                        }
                     
                        int p = othersensorsmy.size();
                        int q = othersensorssender.size();
                        int r1 = n + p;
                        int r2 = n + q;
                        
                        int w = bailEnquestio.bail_sensor.size();
                        Double deltaAlpha = (alphafunction((double)r1/w) - alphafunction((double)r2/w))*bailEnquestio.score*n/w;
                        if(scoremy > this.myTurtlemap.get(sender).get(bailEnquestio).get(senderwinner) + deltaAlpha){
                            solution.winner = myWinner;
                        } else {
                            solution.winner = senderwinner;
                        }
                            ArrayList<String> sensorsToavoid = new ArrayList();
                            solution.sensorsToAvoid = sensorsExcept;
                            solution.deltaAlpha = deltaAlpha;
                            return solution;
                        
                        } else if( n < m){
                           ArrayList<AgentAddress> previous = new ArrayList();
                        for(String sensor : sensorsExcept){
                            AgentAddress auxAd = comparatorEnquestio.winnersSender.get(sensor);
                            if (!previous.contains(auxAd)){
                                scorewinner = scorewinner + this.myTurtlemap.get(sender).get(bailEnquestio).get(auxAd);
                        }
                        }
                        othersensorssender.removeAll(sensorsmywin);
                        sensorsExcept.addAll(sensorsmywin);
                        sensorsExcept.removeAll(sensorssenderwin);
                        int p = othersensorsmy.size();
                        int q = othersensorssender.size();
                        int r1 = n + p;
                        int r2 = n + q;
                        int w = bailEnquestio.bail_sensor.size();
                        Double deltaAlpha = (alphafunction((double)r1/w) - alphafunction((double)r2/w))*bailEnquestio.score*n/w;
                        if(scorewinner - deltaAlpha < this.mymatesBids.get(bailEnquestio).get(myWinner)){
                            solution.winner = myWinner;
                            
                        } else {
                            solution.winner = senderwinner;
                           
                        } 
                        ArrayList<String> sensorsToavoid = new ArrayList();
                            solution.sensorsToAvoid = sensorsExcept;
                            solution.deltaAlpha = deltaAlpha;
                            return solution;
                        } else {
                        int p = othersensorsmy.size();
                        int q = othersensorssender.size();
                        int r1 = n + p;
                        int r2 = n + q;
                        int w = bailEnquestio.bail_sensor.size();
                        Double deltaAlpha = (alphafunction((double)r1/w) - alphafunction((double)r2/w))*bailEnquestio.score*n/w;
                            if(scorewinner + deltaAlpha < this.mymatesBids.get(bailEnquestio).get(myWinner)){
                            solution.winner = myWinner;                        
                        } else {
                            solution.winner = senderwinner;
                        } 
                         ArrayList<String> sensorsToavoid = new ArrayList();
                            solution.sensorsToAvoid = null;
                            solution.deltaAlpha = deltaAlpha;
                            return solution;   
                    }
                }

    private Double update(provisional ProvisionalData, myBail bailEnquestio) {
        Integer NumbsensBef;
        NumbsensBef = computeNumberSensors(this.mymatesBids.get(bailEnquestio));
        HashMap<AgentAddress, Double> newSolution = new HashMap();
        HashMap<AgentAddress, Integer> newSolutiontime = new HashMap();
        try{
        this.mymatesBids.get(bailEnquestio).clear();
        this.winners.get(bailEnquestio).clear();
        this.timer.get(bailEnquestio).clear();
        }catch(NullPointerException e){}
        for (String sensor : ProvisionalData.provisionalWinners.keySet()){
            newSolution.put(ProvisionalData.provisionalWinners.get(sensor),
                    ProvisionalData.provisionalScore.get(sensor));
            newSolutiontime.put(ProvisionalData.provisionalWinners.get(sensor),
                    ProvisionalData.provisionalTime.get(sensor));
        }
        Integer NumbsensAft;
        
        NumbsensAft = computeNumberSensors(newSolution);
        int w = bailEnquestio.bail_sensor.size();
        Double deltaAlpha = (alphafunction((double)NumbsensAft/w) - alphafunction((double)NumbsensBef/w))*bailEnquestio.score;
        Double total_bid = 0.0;
        ArrayList<AgentAddress> newWinners = new ArrayList();
        for(AgentAddress address : newSolution.keySet()){
            newWinners.add(address);
            Double actual_bid = newSolution.get(address) +  deltaAlpha*this.myTurtlemapsensor.get(address).size()/NumbsensAft;
            newSolution.put(address, actual_bid) ;
            updateHashMap(this.mymatesBids, bailEnquestio, address, actual_bid);
            total_bid = total_bid + actual_bid;
                    }
        this.winners.put(bailEnquestio,newWinners);
        this.timer.put(bailEnquestio,newSolutiontime);
        
        return total_bid;
    }
            

    private Integer computeNumberSensors(HashMap<AgentAddress, Double> other) {
        int i = 0;
        for(AgentAddress address : other.keySet()){
            i = i + this.myTurtlemapsensor.get(address).size();
        }
        return i;
    }

    private <T> void updateHashMap(HashMap<myBail, HashMap<AgentAddress, T>> provisional, myBail bailEnquestio, AgentAddress myAddress, T get) {
                    HashMap<AgentAddress,T> provisional2 = new HashMap();
                      try{
                      provisional2.putAll(provisional.get(bailEnquestio));
                      } catch (NullPointerException e){}
                      provisional2.put(myAddress, get);
                      provisional.put(bailEnquestio, provisional2);    
        
    }

    private void checkhvector(myBail bailEnquestio, HashMap<Integer, myBail> myBailsavailable, 
            HashMap<myBail, HashMap<AgentAddress, Double>> provisional) {
             ArrayList<AgentAddress> bugsJoined = new ArrayList();
             sensc solPrevious = new sensc();
                ArrayList common_sensors_prev = new ArrayList();
                Double scoreWinners = 0.0;
                solPrevious = extractSensorsAndScore(this.mymatesBids.get(bailEnquestio));
                common_sensors_prev = solPrevious.sensors;
                scoreWinners = solPrevious.totalScore;
                
                sensc solProvisional = new sensc();
                ArrayList common_sensors_bid = new ArrayList();
                Double scoreBid = 0.0;
                solProvisional = extractSensorsAndScore(provisional.get(bailEnquestio));
                common_sensors_bid = solProvisional.sensors;
                scoreBid = solProvisional.totalScore;
                int p = common_sensors_prev.size();
                int q = common_sensors_bid.size();
                int w = bailEnquestio.bail_sensor.size();
                Double deltaAlpha;
                deltaAlpha = (alphafunction((double)q/w) - alphafunction((double)p/w))*bailEnquestio.score;
                for (AgentAddress address : provisional.get(bailEnquestio).keySet()){
                        if (address != myAddress){
                            updateHashMap( provisional, bailEnquestio, address, 
                                    provisional.get(bailEnquestio).get(address) 
                                            + deltaAlpha/(provisional.get(bailEnquestio).size()-1));
                        }
                    }
                solProvisional = extractSensorsAndScore(provisional.get(bailEnquestio));
                scoreBid = solProvisional.totalScore;
                if(scoreBid > scoreWinners){
                    this.hvector.put(bailEnquestio, true);   
                } else {
                    this.hvector.put(bailEnquestio, false);
                }
                
    }
   
    private sensc extractSensorsAndScore(HashMap<AgentAddress,Double> get) {
            sensc solution = new sensc();
            ArrayList<String> common_sensors = new ArrayList();
            Double scoreWinners = 0.0;
                if(get != null){
                for (AgentAddress bugJoined : get.keySet()){
                    try{
                        scoreWinners = scoreWinners + get.get(bugJoined);
                        common_sensors.addAll(this.myTurtlemapsensor.get(bugJoined));
                    } catch (NullPointerException e){}
                }
    }           
                solution.totalScore = scoreWinners;
                solution.sensors = common_sensors;
                return solution;
    }
    }

    