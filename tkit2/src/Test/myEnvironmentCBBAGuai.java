/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import Test.ScenarioBail.degree_of_modularity;
import Test.ScenarioBail.density_tasks;
import Test.ScenarioBail.score_dispersion;
import Test.ScenarioBail.sensors_dispersion;
import Test.ScenarioBail.spatial_concentration;
import Test.ScenarioBug.cost_of_moving;
import Test.ScenarioBug.cost_of_moving_dispersion;
import Test.ScenarioBug.degree_of_modularity_bug;
import Test.ScenarioBug.density_bugs;
import Test.ScenarioBug.photo_cost;
import Test.ScenarioBug.photo_cost_dispersion;
import Test.ScenarioBug.resources_quantity;
import Test.ScenarioBug.resources_quantity_dispersion;
import Test.ScenarioBug.sensors_dispersion_bug;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
public class myEnvironmentCBBAGuai extends TKEnvironment {
    
                 public Patch[] myPatchGrid;
                 boolean communicate_mode = true; 
                 int count = 0;
                 myMessage patchGrid_encrypted = new myMessage();
                 final Integer typeBailSc = 7;
                 final int typeBugSc = 8;
                 int bugsDied = 0;
                 int numberofBugs;
                 int numberofBails = 20;
                 HashMap<Integer,activityMultisensor> panorama = new HashMap();
                 public ScenarioBail bail_scenario;
                 public ScenarioBug bug_scenario;   
                 public boolean create;
                 AgentAddress schedulerAddress = null;
                 public Double totalScenarioScore = 0.0;
                 public boolean randomBail = true;
                 public int simulationID = 40002;
                 public int randomIdentifier = (int) (Math.random() * 100000000);

    public density_tasks DensTask = density_tasks.high;
    public spatial_concentration SpatConc = spatial_concentration.low;
    public degree_of_modularity Mod = degree_of_modularity.three;
    public score_dispersion DispScore = score_dispersion.low;
    public sensors_dispersion SensDisp = sensors_dispersion.high;
    public density_bugs DensBug = density_bugs.high;
    public sensors_dispersion_bug SensDispBug = sensors_dispersion_bug.low;
    public degree_of_modularity_bug ModBug = degree_of_modularity_bug.three;
    public cost_of_moving CostMov = cost_of_moving.superlow;
    public cost_of_moving_dispersion CostDisp = cost_of_moving_dispersion.low;
    public photo_cost PhotoCost = photo_cost.superlow;
    public photo_cost_dispersion PhotoCostDisp = photo_cost_dispersion.low;
    public resources_quantity ResQuan = resources_quantity.superlow;
    public resources_quantity_dispersion ResQuanDisp = resources_quantity_dispersion.low;
	
	@Override
	protected void activate() {
                
		super.activate();
                //this.bail_scenario = new ScenarioBail(typeBailSc);
                this.bail_scenario = new ScenarioBail(DensTask, SpatConc, Mod, DispScore, SensDisp, true,50);
                
                 //this.simulationID = (int) (Math.random()*10000);
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

                        activityMultisensor myActivity = new activityMultisensor(bail_scenario.bail_sensor.get(k));
                        myActivity.maximum_score = bail_scenario.scores.get(k);
                        myActivity.position = bail_scenario.positions.get(k);
                        myActivity.x = patchGrid[myActivity.position].x;
                        myActivity.y = patchGrid[myActivity.position].y;
                        myActivity.assignFeatures();
                        this.panorama.put(myActivity.position, myActivity);
                        this.totalScenarioScore = myActivity.maximum_score + this.totalScenarioScore;

                    }
                } else {
                    for (int j = 0; j < numberofBails; j++){
                        int v = (int) (Math.random()*gridLength);
                        //myBail droppedBail = new myBail();
                        ArrayList<String> sensorArray = new ArrayList();
                        sensorArray.add("IR");
                        sensorArray.add("MW");
                        activityMultisensor myActivity = new activityMultisensor(sensorArray);
                        myActivity.maximum_score = setRandomScore(1000.0);
                        myActivity.position = v;
                        myActivity.x = patchGrid[v].x;
                        myActivity.y = patchGrid[v].y;
                        myActivity.assignFeatures();
                        this.panorama.put(v, myActivity);
                        this.totalScenarioScore = myActivity.maximum_score + this.totalScenarioScore;
                }
                }
                
               this.bug_scenario = new ScenarioBug(DensBug, SensDispBug,ModBug,CostMov, CostDisp, PhotoCost,
             PhotoCostDisp, ResQuan, ResQuanDisp);
                if (bug_scenario != null) {
                    this.numberofBugs = bug_scenario.positions.size();
                        for (int r = 0; r <  this.numberofBugs; r++){
                        myBugCoalitionsCCBBAGuaiDEF holi = new myBugCoalitionsCCBBAGuaiDEF();
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
            this.patchGrid_encrypted.hashAct = panorama; 
            this.patchGrid_encrypted.patchGrid = this.myPatchGrid;
            this.patchGrid_encrypted.simulationID = this.simulationID;
            this.patchGrid_encrypted.randomIdentifier = this.randomIdentifier;
                    
            create = true;
            File idea = new File("simulationInputs"+this.simulationID+".txt");
            if(!idea.exists()){
            try(FileWriter cw = new FileWriter(idea,true);
            BufferedWriter aw = new BufferedWriter(cw);
            PrintWriter out = new PrintWriter(aw))
        {
            out.println("Density Bails: "+this.DensTask);
            out.println("Concentration of bails: " + this.SpatConc);
            out.println("Modularity: "+this.Mod);
            out.println("Score Dispersion: " + this.DispScore);
            out.println("Task Sensor Dispersion: "+this.SensDisp);
            out.println("Bug Sensor Dispersion: "+this.SensDispBug);
            out.println("Density Bugs: " + this.DensBug);
            out.println("Cost of Moving: "+this.CostMov);
            out.println("Cost of Moving Dispersion: " + this.CostDisp);
            out.println("Cost of Photo: "+this.PhotoCost);
            out.println("Cost of Photo Dispersion: " + this.PhotoCostDisp);
            out.println("Resources: "+ this.ResQuan);
            out.println("Resources Dispersion: " + this.ResQuanDisp);
            //more code
            //more code
        } catch (IOException e) {
    //exception handling left as an exercise for the reader
} } 
             
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
                                    //logger.info("Done!");
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
            @Override
            protected void end() {
    
}
    public Double setRandomScore(Double mean){
        return Math.random()*mean;
    }
    
   
}
//}

