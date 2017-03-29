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
  
    public class myBugCoalitionsCCBBAGuai extends Turtle { 
        
        //ownVariablesTurtle
        private ArrayList possible_sensors;
        public ArrayList<String> actual_sensors = new ArrayList();
        public Double score = 0.0;
        public Double money;
        public Double initialMoney;
        public Double cost_movement;
        public Double cost_photo = 5.0;
        private myBail myTargetBail;
        private Double velocity = 1.0;
        private HashMap<activityMultisensor, Integer> myBundleActivity = new HashMap();
        private activityMultisensor myTargetActivity;
        private AgentAddress myAddress;
        private ArrayList<myBail> myBundle = new ArrayList();
        private HashMap<myBail,String> bailsAndStrategy = new HashMap();
        private ArrayList<myBail> def_position = new ArrayList();
        private ArrayList<Double> arrival_time = new ArrayList();
        private HashMap<myBail, Double> arrival_time_winners = new HashMap();
        private HashMap<AgentAddress, HashMap<myBail,Double>> arrival_time_winners_map = new HashMap();
        private ArrayList<Double[]> def_coordinates = new ArrayList();
        private HashMap<myBail,Boolean> hvector = new HashMap();
        private HashMap<myBail,Double> myBids = new HashMap();
        private HashMap<myBail,Double> mymatesBids = new HashMap();
        private HashMap<myBail,AgentAddress> winners = new HashMap();
        private HashMap<myBail, Integer> timer = new HashMap();
        private HashMap<AgentAddress, HashMap <myBail, Integer>> timer_map = new HashMap();
        private HashMap<AgentAddress, HashMap<myBail,AgentAddress>> winMap = new HashMap();
        private ArrayList<Double> bundlePlainScore = new ArrayList();
        private final int numPlans = 20;
        
        
        // Variables from the scenario
        private Patch[] patchGrid;
        private HashMap<Integer, myBail> myBailsmap;
        private HashMap<Integer, activityMultisensor> myActivitymap;
        private int dropoff_ut_value;
        private ArrayList<Integer> Position_activities; //in the Grid's counting method 
        private ArrayList<myBail> totalBailsavailable;
        private ArrayList<activityMultisensor> totalActivitiesavailable;
        private ArrayList<AgentAddress> scenario_address;
        private AgentAddress schedulerAddress;
        private HashMap<AgentAddress,HashMap<myBail,Double>> myTurtlemap = new HashMap(); 
        private ArrayList<myBail> otherTurtlesBails = new ArrayList();
        private HashMap<AgentAddress,Boolean> agreement_code = new HashMap();
        private int numberofBugs;
        private Double totalScenarioScore;
        
        
        //Scheduling variables
        private Plan myPlan;
        private Schedule currentSchedule = new Schedule();
        private Schedule auxSchedule = new Schedule();
        private HashMap<myBail,ArrayList<myBail>> order = new HashMap();
        private HashMap<myBail,ArrayList<Double>> timing = new HashMap();
        private HashMap<myBail,ArrayList<Double[]>> coordinates = new HashMap();
        private HashMap<myBail,Integer> wsolo = new HashMap();
        private HashMap<myBail,Integer> wany = new HashMap();
        private HashMap<myBail,Integer> countConstraintViolation = new HashMap();
        private HashMap<myBail,Integer> timoutConstraintViolation = new HashMap();
        private int count_convergence = 0;
        private final int maximum_counts_convergence = 350;
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
            myMessage hi = new myMessage();
            hi.things_to_say = "helloscenario";
            this.scenario_address = (ArrayList<AgentAddress>)
                    getAgentsWithRole("myturtles","myturtles","scenario"); //get other turtles what they are doing
            
            sendMessage (scenario_address.get(0), hi);
            boolean received = false;       
            myMessage m = (myMessage) purgeMailbox();
            //logger.info("I checked my mail");
            try{
                
                m.hashAct.isEmpty();
                this.patchGrid = m.patchGrid;
                this.myActivitymap = m.hashAct;
                this.Position_activities = new ArrayList(this.myActivitymap.keySet());
                this.totalBailsavailable = new ArrayList (getBailsFromActivity(this.myActivitymap));
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
                     this.wsolo.put(bb,1);
                     this.wany.put(bb,1);
                     this.countConstraintViolation.put(bb,0);
                     this.timoutConstraintViolation.put(bb,3);
                    
                }
            }
            if (mymatesBids.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    mymatesBids.put(bb,0.0);
                }
            }
            if (this.winners.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    this.winners.put(bb,null);
                }
            }
            if (this.arrival_time_winners.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    this.arrival_time_winners.put(bb,0.0);
                }
            }
           
             if (timer.isEmpty()){
                for (myBail bb : totalBailsavailable){
                    timer.put(bb, 0);
                }
            }          
        firstPhase();
        broadcast(myAddress);
           //logger.info("exited first phase");
        return("secondPhase");
        }        
        public String secondPhase(){  
       // phase 2
            this.myTurtlemap.clear();
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
                this.arrival_time_winners_map.put(m.SP.myAddress, m.SP.arrival_time_winners);
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
              int hello = 8;
                if (count_convergence > this.maximum_counts_convergence - 3){
                    hello = 0;
                }
          
            this.number_iterations ++;
            AgentAddress senderWinner;
            AgentAddress myWinner;
            for ( AgentAddress address : myTurtlemap.keySet()){
                for (myBail bail : myTurtlemap.get(address).keySet()){
                    
                senderWinner = winMap.get(address).get(bail);
                myWinner = this.winners.get(bail);
                if((senderWinner != myWinner) && hello == 0){
                    hello = 1;
                }
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
                        if(this.timer_map.get(address).get(bail) > timer.get(bail)){
                        update(bail,address);
                        broadcast(address);
                    }}
                    
                    } else {
                        if(myWinner == address){
                            update(bail,address);
                             broadcast(address);
                        } else if (myWinner != address && myWinner != myAddress && myWinner != null){
                            if(this.timer_map.get(address).get(bail) > this.timer.get(bail) ){
                                update(bail,address);
                                broadcast(address);
                            }
                        } else if(myWinner == myAddress){
                                broadcast(myAddress);
                            } else{}
                        }
                    }
                }
            
          for (myBail bailEnquestio : myBundle){
              Integer nsat = getNsat(bailEnquestio);
              boolean mutexSat = true;
              boolean depSat = true;
              boolean timeSat = true;
              DMatrix dmatrix = bailEnquestio.myActivity.dmatrix;
              TMatrix tmatrix = bailEnquestio.myActivity.tmatrix;
             for (myBail bail : bailEnquestio.myActivity.listOfBails){
                  if(bail != bailEnquestio){
                    if(this.mymatesBids.get(bail) > this.mymatesBids.get(bailEnquestio) 
                            && dmatrix.get(bail,bailEnquestio) == -1){
                      mutexSat = false;
                      break;
                  }
                          
              }
              
          }
             
             
              if ((this.bailsAndStrategy.get(bailEnquestio) == "pessimistic")){
                if (nsat != bailEnquestio.myActivity.dependencyHash.get(bailEnquestio)){
                    depSat = false;
                }
                for (myBail bail : bailEnquestio.myActivity.listOfBails){
                    
                    if((!(arrival_time_winners.get(bailEnquestio) <= arrival_time_winners.get(bail) +
                            tmatrix.get(bailEnquestio,bail)) || !(arrival_time_winners.get(bail) <= 
                            arrival_time_winners.get(bailEnquestio) + tmatrix.get(bail,bailEnquestio)))
                            && dmatrix.get(bail,bailEnquestio) > 0){
                      timeSat = false;
                      break;
                   }
              }
                if (!mutexSat || !timeSat || !depSat){
                    this.winners.put(bailEnquestio, null);
                    }
              } else {
                  if (nsat < bailEnquestio.myActivity.dependencyHash.get(bailEnquestio)){
                    this.countConstraintViolation.put(bailEnquestio,this.countConstraintViolation.get(bailEnquestio) + 1);
                } 
                  if(this.countConstraintViolation.get(bailEnquestio) >= this.timoutConstraintViolation.get(bailEnquestio)){
                      depSat = false;
                  }
                  for (myBail bail : bailEnquestio.myActivity.listOfBails){
                    
                    if((!(arrival_time_winners.get(bailEnquestio) <= arrival_time_winners.get(bail) +
                            tmatrix.get(bailEnquestio,bail)) || !(arrival_time_winners.get(bail) <= 
                            arrival_time_winners.get(bailEnquestio) + tmatrix.get(bail,bailEnquestio)))
                            && dmatrix.get(bail,bailEnquestio) > 0 && 
                            arrival_time_winners.get(bailEnquestio) < arrival_time_winners.get(bail)){
                      timeSat = false;
                      break;
                   }
              }
                   if (!mutexSat || !timeSat || !depSat){
                    this.winners.put(bailEnquestio, null);
                    this.wsolo.put(bailEnquestio,this.wsolo.get(bailEnquestio) - 1);
                    this.wany.put(bailEnquestio,this.wany.get(bailEnquestio) - 1);
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
                                        arrival_time.remove(pos);
                                        def_position.remove(myBundle.get(u));
                                        bundlePlainScore.remove(i+1);
                                    }
                                    break;                                  
                                }
          }
          for (int i = 0; i < myBundle.size(); i++){
              myBail bailTC2 = myBundle.get(i);
              if(winners.get(bailTC2) != myAddress){
                    for (int r = (i); r < myBundle.size(); r++){
                    if(winners.get(myBundle.get(r)) == null || winners.get(myBundle.get(r)) == myAddress ){
                    mymatesBids.put(myBundle.get(r), 0.0);
                    timer.put(myBundle.get(r), time);
                    winners.put(myBundle.get(r), null);
                    arrival_time_winners.put(myBundle.get(r), 0.0);
                    }
                    }
              }
          }
          
          myBundle.retainAll(def_position);
          this.myBundleActivity.clear();
          //refill Bundle:
          for (myBail bail : myBundle){
              this.myBundleActivity.put(bail.myActivity, bail.myActivity.levelorder.get(bail));
          }
          HashMap<myBail,Double> auxmymatesBids2 = new HashMap();
           auxmymatesBids2.putAll(mymatesBids);
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

            HashMap<myBail,String> avBailsKeySet = new HashMap();
            List<AgentAddress> list1 = getAgentsWithRole("myturtles","myturtles","agreementPhase",false);
            List<AgentAddress> list2 = getAgentsWithRole("myturtles","myturtles1","bug",false);
            avBailsKeySet = getAvailableBails();
            if(avBailsKeySet.size() == 0 || avBailsKeySet == null){
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
           if (list1.size() == list2.size() && avBailsKeySet.size() != 0 && agreement == false){
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
            HashMap<myBail, String> avBailsKeySet = new HashMap();
             avBailsKeySet = getAvailableBails();
            int x =myTargetBail.x -xcor();
            int y = myTargetBail.y -ycor();
            Patch patchBail = getPatchAt(x,y);
            patchBail.setColor(blue);
            Color myColor = patchBail.getColor();
                avBailsKeySet.clear();
             avBailsKeySet = getAvailableBails();
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
            Double xorigin, Double yorigin, Double score_bails){
    UtCost littleBundle = new UtCost();
    
    Double ev_point = sqrt((xbail - xev)*(xbail - xev) + (ybail - yev)*(ybail - yev));
    Double distance;
    
    distance = (Double) sqrt((xbail - xorigin)*(xbail - xorigin) + (ybail - yorigin)*(ybail - yorigin));
    Double utility = score_bails*deltaK(ev_point)//(1/(1 + exp((ev_point) - dropoff_ut_value))) 
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

   
    private HashMap<myBail, String> getAvailableBails() {
            HashMap<myBail,String> setBails = new HashMap<>();
            for (myBail bailEnquestio : this.totalBailsavailable){
             
             Patch  patchBEQ = new Patch();  
             patchBEQ = (Patch) (this.patchGrid[bailEnquestio.position]);
              
                if (!Collections.disjoint(actual_sensors,
                        bailEnquestio.bail_sensor)&& patchBEQ.getColor() == yellow)
                {
                    String bidStrat = getBiddingStrategy(bailEnquestio);
                    boolean canBid = canBid(bidStrat, bailEnquestio);
                    if (canBid == true){
                        setBails.put(bailEnquestio, bidStrat);
                    }
                    
                }
            }    
            return setBails;
    } 

    private Plan evaluatePermutations(HashMap<Integer, ArrayList<myBail>> total_permutations, myBail bailEnquestio) {
        Plan sample_plan = new Plan();
        //Phase 1 : calculate all the scores
        Double taumin = gettaumin(bailEnquestio);
        Double taumax = gettaumax(bailEnquestio);
        for (int i = 0; i < total_permutations.size(); i++){
        ArrayList<Double> provisional_timing = new ArrayList();
        provisional_timing.addAll(this.arrival_time);
        
        ArrayList<Double[]> optimum_position = new ArrayList<>();
        double utility = 0;
        double cost = 0;
        ArrayList<myBail> setB = total_permutations.get(i);
        Double xsub1 = getX();
        Double ysub1 = getY();
            for (int k = 0; k < setB.size(); k++){
                Double optUtility = 0.0;
                Double totalCost = 0.0;
                ArrayList common_sensors = new ArrayList();
                common_sensors.addAll(actual_sensors);
                common_sensors.retainAll(setB.get(k).bail_sensor);
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
                    Integer hey;
                    if(j == 25){
                        hey = 2;
                    }
                    utcostsingle = getUtility ((double) setB.get(k).x,(double) setB.get(k).y, 
                        xsub1, ysub1, xorigin,yorigin, setB.get(k).score); 
                    
                    utilitysingle = utcostsingle.utility;
                    costsingle = utcostsingle.cost;
                    if (utilitysingle > optUtility || xpointOpt == null){
                        totalCost = costsingle;
                        optUtility = utilitysingle;
                        optUtility = utilitysingle;
                        xpointOpt = xsub1;
                        ypointOpt = ysub1;
                    } 
                        xsub1 = xsub1 + xsum;
                        ysub1 = ysub1 + ysum;
                }
                Double[] point = {xpointOpt,ypointOpt};
                utility = utility + optUtility;
                cost = cost + totalCost;
                optimum_position.add(k, point);
                xsub1 = xpointOpt;
                ysub1 = ypointOpt;
            }
            
            
            double auxUtility;
            auxUtility = utility;
            Double tearliest = findtearliest(setB, bailEnquestio, taumin, optimum_position);
            Double tlastest = findtlastest(setB, bailEnquestio, taumax, optimum_position);
             if (cost + setB.size()*this.cost_photo > this.money || tlastest < tearliest){
                auxUtility = 0.0;
            } else {
            auxUtility = utility;
            sample_plan.utility.add(auxUtility);
            ArrayList<myBail> setProvisional = new ArrayList();
            setProvisional.addAll(setB);
            sample_plan.bailOrder.add(setProvisional);
            ArrayList<Double[]> optimum_position_provisional = new ArrayList();
            optimum_position_provisional.addAll(optimum_position);
            sample_plan.positions.add(optimum_position_provisional);
            ArrayList<Double> arrivalAux = new ArrayList();
            arrivalAux.addAll(this.arrival_time);
            Double newTime = tearliest;
            /*if(tlastest > 1000.0){
                newTime = tearliest + (double) getWorldWidth()/this.velocity;
            } else {
                newTime = (tearliest + tlastest)/2;
            }*/
            arrivalAux.add(setB.indexOf(bailEnquestio),newTime);
            sample_plan.arrival_times.add(arrivalAux);
            
        }
             
            
            
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
            //ArrayList<ArrayList<myBail>> anotherArray = new ArrayList();
            ArrayList<ArrayList<myBail>> myArray = new ArrayList();
            //myArray = permute(mySch.bailsToeat, mySch.bailsToeat.size(), anotherArray);
            //if want to return to permute, copypaste Permute from myBUGCBBATESTDEF;
            myArray = calculateValidPermutations(bailEnquestio);
            for (int y = 0; y < myArray.size(); y++){
               solution.put(y, myArray.get(y));
           }
           Plan  this_plan = new Plan();
           this_plan = evaluatePermutations(solution,bailEnquestio);
           Double increase;
           if (!this_plan.utility.isEmpty()){
           increase = this_plan.utility.get(0) - previous_bid;
           if (increase > 0){
               mySch.bailsToeat = this_plan.bailOrder.get(0);
               mySch.positionsToEat = this_plan.positions.get(0);
               mySch.arrival_times = this_plan.arrival_times.get(0);
               mySch.TotalScore = increase;
               return mySch;
           } else {
               mySch.TotalScore = 0.0;
               return mySch;
           }
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
      timer.put(bail, this.timer_map.get(address).get(bail));
      this.arrival_time_winners.put(bail, this.arrival_time_winners_map.get(address).get(bail));
   }
    
    private void firstPhase(){
         bailsAndStrategy = getAvailableBails();
         for (myBail bb : totalBailsavailable){
             if(bailsAndStrategy.keySet().contains(bb)){
             hvector.put(bb, true);
        } else {
                 hvector.put(bb,false);
             }
         }
         Integer differentPlans;
         boolean closeWhile = false;
      while (!closeWhile && hvector.containsValue(true))
            { //order.clear();
              //coordinates.clear();
                for ( myBail bailEnquestio : bailsAndStrategy.keySet() ){
                    closeWhile = checkmaxActivities(bailEnquestio);
                    if (closeWhile == false){
                    auxSchedule = calculateIncreaseUtility(myBundle, bailEnquestio, 
                            bundlePlainScore.get(myBundle.size()));
                    myBids.put(bailEnquestio, auxSchedule.TotalScore);
                    order.put(bailEnquestio,auxSchedule.bailsToeat);
                    coordinates.put(bailEnquestio, auxSchedule.positionsToEat);
                    timing.put(bailEnquestio, auxSchedule.arrival_times);
                    if(myBids.get(bailEnquestio) > mymatesBids.get(bailEnquestio)){
                        hvector.put(bailEnquestio, true);
                    } else {
                        hvector.put(bailEnquestio, false);
                        }
                    
                } else {
                        hvector.put(bailEnquestio, false);
                        }
                }
                for ( myBail bailEnquestio : bailsAndStrategy.keySet() ){
                for (myBail bail : bailEnquestio.myActivity.listOfBails){
                        if(bail != bailEnquestio){
                          if(this.mymatesBids.get(bail) > this.myBids.get(bailEnquestio) 
                                  && bailEnquestio.myActivity.dmatrix.get(bail,bailEnquestio) == -1){
                            this.hvector.put(bailEnquestio, false);
                            break;
                        }
                    }
                }    
                }
                //decision
                myBail optimum = new myBail();
                optimum = null;
                Double winningBid = 0.0;
                for (myBail aa : bailsAndStrategy.keySet()){
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
                    winners.put(auxiliaryBail, myAddress);
                    timer.put(auxiliaryBail, time);
                    mymatesBids.put(auxiliaryBail, winningBid);
                    arrival_time = timing.get(auxiliaryBail);
                    arrival_time_winners.put(auxiliaryBail,arrival_time.get(def_position.indexOf(auxiliaryBail)));
                    this.myBundleActivity.put(auxiliaryBail.myActivity,auxiliaryBail.myActivity.levelorder.get(auxiliaryBail));
                    hvector.put(optimum,false);
            } else {
                    closeWhile = true;
                }
                
                //differentPlans = getDifferentPlans(myBundle);
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
            sendMyNumbers.SP.arrival_time_winners = this.arrival_time_winners;
            sendMyNumbers.SP.myTimer.putAll(timer);
            }else{
            sendMyNumbers.SP.winners.putAll(winMap.get(bugEnquestio));
            sendMyNumbers.SP.maximumBids.putAll(myTurtlemap.get(bugEnquestio));
            sendMyNumbers.SP.myAddress = bugEnquestio;
            sendMyNumbers.SP.arrival_time_winners.putAll(this.arrival_time_winners_map.get(bugEnquestio));
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

    private ArrayList<myBail> getBailsFromActivity(HashMap<Integer, activityMultisensor> myActivitymap) {
        ArrayList<myBail> solutionArray = new ArrayList();
        for (activityMultisensor activity : myActivitymap.values()){
            solutionArray.addAll(activity.listOfBails);
        }
        return solutionArray;
    }

    private String getBiddingStrategy(myBail bailEnquestio) {
        activityMultisensor actIBelong = bailEnquestio.myActivity;
        DMatrix actDMatrix = actIBelong.dmatrix;
        for(myBail matesBails : actIBelong.listOfBails){
            if (actDMatrix.get(matesBails, bailEnquestio) >= 1 && actDMatrix.get(bailEnquestio, matesBails) == 1){
                return "optimistic";  
            }                
        }
        return "pessimistic";
    }

    private boolean canBid(String bidStrat, myBail bailEnquestio) {
        activityMultisensor actIBelong = bailEnquestio.myActivity;
        Integer nsat = getNsat(bailEnquestio);
        if (bidStrat == "pessimistic"){
            if (nsat == actIBelong.dependencyHash.get(bailEnquestio)){
                return true;
            } else {
                return false;
            }
        } else if (bidStrat == "optimistic"){
            if ((this.wany.get(bailEnquestio) > 0 && nsat > 0)||(this.wsolo.get(bailEnquestio) > 0)
                    ||(nsat == actIBelong.dependencyHash.get(bailEnquestio))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private Double gettaumin(myBail bailEnquestio) {
        Double maxtminconst = 0.0;
        activityMultisensor myActivity = bailEnquestio.myActivity;
        TMatrix tmatrix = myActivity.tmatrix;
        DMatrix dmatrix = myActivity.dmatrix;
        for (myBail bail : myActivity.listOfBails){
            Double tminconst;
            tminconst = this.arrival_time_winners.get(bail) - tmatrix.get(bail, bailEnquestio);
            if (tminconst > maxtminconst && this.winners.get(bail) != null && dmatrix.get(bail, bailEnquestio) > 0){
                maxtminconst = tminconst;
            }
        }
        return maxtminconst;
    }

    private Double gettaumax(myBail bailEnquestio) {
        Double mintmaxconst = 10000000.0;
        activityMultisensor myActivity = bailEnquestio.myActivity;
        TMatrix tmatrix = myActivity.tmatrix;
        DMatrix dmatrix = myActivity.dmatrix;
        for (myBail bail : myActivity.listOfBails){
            Double tmaxconst = this.arrival_time_winners.get(bail) + tmatrix.get(bailEnquestio, bail);
            if (tmaxconst < mintmaxconst && this.winners.get(bail) != null && dmatrix.get(bail, bailEnquestio) > 0){
                mintmaxconst = tmaxconst;
            }
        }
        return mintmaxconst;
    }

    private Double findtearliest(ArrayList<myBail> setB, myBail bailEnquestio, Double taumin, ArrayList<Double[]> coordinates) {
        Double comparativeTime;
        Integer indexOfBail = setB.indexOf(bailEnquestio);
        if(indexOfBail == 0){
            Double displacementTime;
            displacementTime = distance((double)getX(), (double)getY(),
                    coordinates.get(0)[0], coordinates.get(0)[1])*this.velocity;
            comparativeTime = displacementTime;
        } else {
            Double displacementTime;
            displacementTime = distance(coordinates.get(indexOfBail - 1)[0], coordinates.get(indexOfBail - 1)[1],
                    coordinates.get(indexOfBail)[0], coordinates.get(indexOfBail)[1])*this.velocity;
            comparativeTime = displacementTime + this.arrival_time.get(indexOfBail - 1);
        }
       if(taumin >= comparativeTime){
           return taumin;
       } else {
           return comparativeTime;
       }
        }
 
    private Double findtlastest(ArrayList<myBail> setB, myBail bailEnquestio, Double taumax, ArrayList<Double[]> coordinates) {
        Double tlastest;
        Double comparativeTime;
        Integer indexOfBail = setB.indexOf(bailEnquestio);
      //  provisional_timing.add(indexOfBail, 0.0);
        if(indexOfBail == setB.size()-1){
            tlastest = taumax;
            return tlastest;
        } else {
            Double displacementTime;
            displacementTime = distance(coordinates.get(indexOfBail)[0], coordinates.get(indexOfBail)[1],
                    coordinates.get(indexOfBail + 1)[0], coordinates.get(indexOfBail + 1)[1])*this.velocity;
            comparativeTime = this.arrival_time.get(indexOfBail) - displacementTime ;
            if(comparativeTime >= taumax){
                return taumax;
            } else {
                return comparativeTime;
            }   
        } 
    }
    
    private Double distance (Double xbail, Double ybail, Double xorigin, Double yorigin){       
    return (Double) sqrt((xbail - xorigin)*(xbail - xorigin) + (ybail - yorigin)*(ybail - yorigin));
    }

    private ArrayList<ArrayList<myBail>> calculateValidPermutations(myBail bailEnquestio) {
        ArrayList<ArrayList<myBail>> solution = new ArrayList();
        for (int i = 0; i < this.def_position.size() + 1 ; i++){
            ArrayList<myBail> solution_i = new ArrayList();
            solution_i.addAll(this.def_position);
            solution_i.add(i, bailEnquestio);
            solution.add(solution_i);
        }
        return solution;
    }
    
    private Integer getNsat (myBail bailEnquestio){
        activityMultisensor actIBelong = bailEnquestio.myActivity;
        DMatrix actDMatrix = actIBelong.dmatrix;
        int maximumD = 1;
            int nsat = 0;
            for(myBail matesBails : actIBelong.listOfBails){
                 
                 if(this.winners.get(matesBails) != null && actDMatrix.get(matesBails,bailEnquestio) == 1){
                     nsat++;
            }   else if (this.winners.get(matesBails) != null && actDMatrix.get(matesBails,bailEnquestio) > maximumD){
                maximumD = actDMatrix.get(matesBails,bailEnquestio);
            }   else {}         
        }
            nsat = nsat + maximumD - 1;
            return nsat;
    }

    private boolean checkmaxActivities(myBail auxiliaryBail) {
        Integer previous;
        activityMultisensor activityBail = auxiliaryBail.myActivity;
        Integer level = activityBail.levelorder.get(auxiliaryBail);
        previous = this.myBundleActivity.get(activityBail);
        if((previous == level || previous == null) && this.myBundleActivity.size() < numPlans){
            return false;
        } else if (previous == level && this.myBundleActivity.size() == numPlans) {
            return false;
        } else {
            this.hvector.put(auxiliaryBail,false);
            return true;
        }
    }
    }
