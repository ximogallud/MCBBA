/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import madkit.action.SchedulingAction;
import madkit.kernel.AgentAddress;
import madkit.kernel.Madkit;
import madkit.message.SchedulingMessage;
import turtlekit.kernel.Patch;
import turtlekit.kernel.TKEnvironment;
import turtlekit.kernel.Turtle;

/**
 *
 * @author Ximo
 */
public class myEnvironment extends TKEnvironment {
    
                 public Patch[] myPatchGrid;
                 boolean communicate_mode = true; 
                 int count = 0;
                 myMessage patchGrid_encrypted = new myMessage();
                 final int typeBailSc =  4;
                 final int typeBugSc = 11;
                 int bugsDied = 0;
                 int numberofBugs = 2;
                 int numberofBails = 20;
                 HashMap<Integer,myBail> panorama = new HashMap();
                 public ScenarioBail bail_scenario;
                 public ScenarioBug bug_scenario;   
                 public boolean create;
                 AgentAddress schedulerAddress = null;
                 public Double totalScenarioScore = 0.0;
                 public boolean randomBail = true;
                 
	
	@Override
	protected void activate() {
                
		super.activate();
                //this.bail_scenario = new ScenarioBail(typeBailSc);
                 
                setLogLevel(Level.INFO);
                createGroupIfAbsent("myturtles","myturtles1");
                requestRole("myturtles","myturtles1","scenario");
                createGroupIfAbsent("myturtles","myturtles");
                requestRole("myturtles","myturtles","scenario");
		final Patch[] patchGrid = getPatchGrid();
                myPatchGrid = patchGrid;
                int gridLength = patchGrid.length;
                if (bail_scenario != null){
                    for (int k = 0; k < bail_scenario.positions.size(); k++){

                        myBail droppedBail = new myBail();
                        droppedBail.score = bail_scenario.scores.get(k);
                        droppedBail.bail_sensor = bail_scenario.bail_sensor.get(k);
                        droppedBail.position = bail_scenario.positions.get(k);
                        droppedBail.x = patchGrid[droppedBail.position].x;
                        droppedBail.y = patchGrid[droppedBail.position].y;
                        this.panorama.put(droppedBail.position, droppedBail);
                        this.totalScenarioScore = droppedBail.score + this.totalScenarioScore;

                    }
                } else {
                    for (int j = 0; j < numberofBails; j++){
                        int v = (int) (Math.random()*gridLength);
                        ArrayList<String> sensors = new ArrayList<String>();
                        sensors.add("IR");
                        sensors.add("MW");
                        myBail droppedBail = new myBail();
                        droppedBail.bail_sensor = sensors;
                        droppedBail.score = setRandomScore(1000.0);
                        droppedBail.position = v;
                        droppedBail.x = patchGrid[droppedBail.position].x;
                        droppedBail.y = patchGrid[droppedBail.position].y;
                        this.panorama.put(v, droppedBail);
                        this.totalScenarioScore = droppedBail.score + this.totalScenarioScore;
                }
                }
                
                this.bug_scenario = new ScenarioBug(typeBugSc);
                if (bug_scenario != null) {
                    this.numberofBugs = bug_scenario.positions.size();
                    for (int r = 0; r <  this.numberofBugs; r++){
                        myBugCBBATESTDEF holi = new myBugCBBATESTDEF();
                        //myBug holi = new myBug();
                        holi.cost_movement = bug_scenario.cost_movement.get(r);
                        holi.cost_photo = bug_scenario.cost_photo.get(r);
                        holi.money = bug_scenario.money.get(r);
                        holi.initialMoney = bug_scenario.money.get(r);
                        holi.actual_sensors.addAll(bug_scenario.actual_sensors.get(r));
                        createTurtle(holi, this.myPatchGrid[bug_scenario.positions.get(r)].x,
                                myPatchGrid[bug_scenario.positions.get(r)].y);
                    
                    }
                }
                
                ArrayList<Integer> positions = new ArrayList(this.panorama.keySet());
                
		for (int i = 0; i < positions.size(); i++) {
			patchGrid[positions.get(i)].dropMark("Bail", this.panorama.get(positions.get(i)));
                        }
             
            //add to the message
            this.patchGrid_encrypted.hash = panorama; 
            this.patchGrid_encrypted.patchGrid = this.myPatchGrid;
            create = true;
             
        }
        
        @Override
            protected void update() {               
              if(schedulerAddress == null){
                  schedulerAddress = getAgentsWithRole("myturtles","myturtles","scheduler").get(0);
              }
              
              if (communicate_mode){                              
                        myMessage m = new myMessage();           
                        m = (myMessage) nextMessage();
                                try{
                                    this.panorama.remove(m.bailIate);
                                    
                                } catch (NullPointerException e){};
                                if (m != null){
                                    patchGrid_encrypted.totalScenarioScore = this.totalScenarioScore;
                                    patchGrid_encrypted.numberofBugs = this.numberofBugs;
                                    sendReply(m, patchGrid_encrypted);
                                    logger.info("Done!");
                                    if (m.things_to_say == "died"){
                                    this.bugsDied++;
                                    logger.info("A bug died");
                                    if(this.bugsDied == this.numberofBugs){
                        sendMessage(schedulerAddress, new SchedulingMessage(SchedulingAction.SHUTDOWN)); 
                                    }
                                   // logger.info("Try Again!");
                                   
                                }
                             
                    } 
                              
              }
 
    }
    public Double setRandomScore(Double mean){
        return Math.random()*mean;
    }
    
   
}
//}
        
        
	

    
