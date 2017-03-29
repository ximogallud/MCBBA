/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import jcuda.utils.Timer;
import madkit.action.KernelAction;
import madkit.kernel.Activator;
import madkit.kernel.Madkit;
import turtlekit.kernel.TKScheduler;
import turtlekit.kernel.TurtleKit;
import turtlekit.mle.Particule;

/**
 *
 * @author Ximo
 */
public class myScheduler extends TKScheduler {
    
    @Override
	protected void activate() {
                
		super.activate();
                createGroupIfAbsent("myturtles","myturtles");
                requestRole("myturtles","myturtles","scheduler");
                
        }    

    @Override
        protected void end() {

                                //Madkit m = new Madkit();                         
                                //m.doAction(KernelAction.STOP_NETWORK);

}
}