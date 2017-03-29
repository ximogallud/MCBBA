/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.util.ArrayList;
import java.util.HashMap;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import turtlekit.kernel.Patch;

/**
 *
 * @author Ximo
 */
public class myMessage extends Message{
    Patch[] patchGrid;

    HashMap<Integer,myBail> hash;
    HashMap<Integer,activityMultisensor> hashAct;
    AgentAddress myAddress;
    myBail bailIEat;
    ArrayList<Integer> Position_bails = new ArrayList();
    Schedule mySchedule;
    String things_to_say;
    SPMessage SP = new SPMessage();
    Integer simulationID;
    myBail bailIate;
    Plan myPlan;
    ArrayList<myBail> myBundle = new ArrayList();
    boolean agreement;
    int randomIdentifier;
    AgentAddress myaddress;
    int numberofBugs;
    Double totalScenarioScore;
    HashMap<AgentAddress, ArrayList<String>> sensorMap = new HashMap();
    
    ScenarioBail.density_tasks DensTask; 
    ScenarioBail.spatial_concentration SpatConc; 
    ScenarioBail.degree_of_modularity Mod;
    ScenarioBail.score_dispersion DispScore;
    ScenarioBail.sensors_dispersion SensDisp;
    ScenarioBug.density_bugs DensBug;
    ScenarioBug.sensors_dispersion_bug SensDispBug;
    ScenarioBug.degree_of_modularity_bug ModBug;
    ScenarioBug.cost_of_moving CostMov;
    ScenarioBug.cost_of_moving_dispersion CostDisp;
    ScenarioBug.photo_cost PhotoCost;
    ScenarioBug.photo_cost_dispersion PhotoCostDisp;
    ScenarioBug.resources_quantity ResQuan;
    ScenarioBug.resources_quantity_dispersion ResQuanDisp;
    
    
    
    public myMessage(){
        
    }
    public myMessage(String things){
        this.things_to_say = things;
    }


    
    
}
