/*package Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static jcuda.driver.JCudaDriver.setLogLevel;
import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.ObjectMessage;
import turtlekit.kernel.Patch;
import turtlekit.kernel.TKEnvironment;
import turtlekit.kernel.Turtle;
/**
 *
 * @author Ximo
 */
/*public class centralisedEnvironment extends TKEnvironment {
    
                 Patch[] myPatchGrid;
                 boolean communicate_mode = true; 
                 int count = 0;
                 myMessage patchGrid_encrypted = new myMessage();
                 final int number_of_bails = 24;
                 HashMap<Integer,myBail> panorama = new HashMap();
                 Turtle utilityTurtle;
                 List<myBug> myTurtles;
                 Class<myBug> myBug;
                 Double[][][] utilityCubeMatrix;
                 ArrayList<Integer> positions;
                
	
	@Override
	protected void activate() {
                
		super.activate();
                setLogLevel(Level.FINEST);
                createGroupIfAbsent("myturtles","myturtles");
                requestRole("myturtles","myturtles","scenario");
                
                
		final Patch[] patchGrid = getPatchGrid();
                myPatchGrid = patchGrid;
                
		int gridLength = patchGrid.length;


                for (int k = 0; k < number_of_bails ; k++){
                               
                int j = (int) (Math.random() * gridLength);
                myBail droppedBail = new myBail();
                droppedBail.position = j;
                this.panorama.put(j, droppedBail);
                
                }
                
                this.positions = new ArrayList(this.panorama.keySet());
                
                
		for (int i = 0; i < positions.size(); i++) {
			patchGrid[positions.get(i)].dropMark("Bail", this.panorama.get(positions.get(i)));
                        }
             
            //add to the message
            this.patchGrid_encrypted.hash = panorama; 
            this.patchGrid_encrypted.patchGrid = this.myPatchGrid;
             
        }
        
        @Override
            protected void update() {
                
                purgeMailBox();
                // check the bails
                 myTurtles = utilityTurtle.getOtherTurtlesWithRole(getWidth(), true, "bug", myBug);
                 for (int i = 0; i < myTurtles.size(); i++){
                     for (int j = 0; j < myTurtles.size(); j++){
                         for (int k = 0; k < panorama.size(); k++){
                             utilityCubeMatrix[i][j][k] = getUtilityCubeMatrix(myTurtles.get(i),
                                     myTurtles.get(j),this.positions.get(k));
                         } 
                     }
                 }
                 
                 move(utilityCubeMatrix);
                 
                //communicate_mode
              if (communicate_mode){                              
                        myMessage m = new myMessage();           
                        m = (myMessage) purgeMailbox();
                        
                                try{
                                    this.panorama.remove(m.bailIate);
                                } catch (NullPointerException e){};
                                if (m != null){
                                    
                                    sendReply(m, patchGrid_encrypted);
                                    logger.info("Done!");
                                                                       
                                } 
                                                                                            
                                else {
                                    logger.info("Try Again!");
                                    }
              }
     
    }

    private Double getUtilityCubeMatrix(myBug bugA, myBug bugB, Integer pos_bailC) {
               
            int i = 0;
            double alpha = 0;
            myBail bailenQuestio;
            bailenQuestio = panorama.get(pos_bailC);
        
            if(bugA == bugB){

                     
            switch (bailenQuestio.bail_sensor.size()){
                case 1: 
                    if (bailenQuestio.bail_sensor.get(0) == "MW"){
                        alpha = 0.2;
                    }
                    if (bailenQuestio.bail_sensor.get(0) == "IR"){
                        alpha = 0.6;
                    }
                case 2:
                    
                    alpha = 1;
            }   
            
            Double distance;
            distance = (Double) alpha*calculateDistance(bugA, pos_bailC);
            Double utility = bailenQuestio.score - distance;
            
            return utility;
                    } else {
            
                if(bugA.actual_sensors.size() == 1 && bugB.actual_sensors.size() == 1 
                        && bugA.actual_sensors.get(0) != bugB.actual_sensors.get(0)){
            Double distance1 = calculateDistance(bugA,pos_bailC);
            Double distance2 = calculateDistance(bugB,pos_bailC);
            alpha = 1.2;
            Double utility = bailenQuestio.score - alpha*(distance1+distance2)/2;
                return utility;
                }
                return -10E5;
            }   
    }

    private Double calculateDistance(myBug bugA, Integer pos_bailC) {
     Double distance1 =  bugA.distanceNoWrap(myPatchGrid[pos_bailC].x,myPatchGrid[pos_bailC].y);
     return distance1;
        
    }
}
}*/