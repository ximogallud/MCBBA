

package Test;

import static turtlekit.kernel.TurtleKit.Option.cuda;
import static turtlekit.kernel.TurtleKit.Option.envDimension;
import static turtlekit.kernel.TurtleKit.Option.environment;
import static turtlekit.kernel.TurtleKit.Option.scheduler;
import static turtlekit.kernel.TurtleKit.Option.startSimu;
import static turtlekit.kernel.TurtleKit.Option.turtles;
import static turtlekit.kernel.TurtleKit.Option.viewers;

import javax.swing.JOptionPane;
import jdk.nashorn.internal.runtime.regexp.joni.Option;

import turtlekit.kernel.TKLauncher;
import turtlekit.kernel.TurtleKit;
import static turtlekit.kernel.TurtleKit.Option.noWrap;
import turtlekit.viewer.PheromoneViewer;

public class myLauncher extends TKLauncher{

    static void main() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	@Override
        
	protected void activate() {
                createGroupIfAbsent("launcherG","launcherG");
                requestRole("launcherG","launcherG","role");
		super.activate();
	}

	
	@Override
        
	protected void createSimulationInstance() {
		
		setMadkitProperty(envDimension, "50,50");
		initProperties();
		setMadkitProperty(startSimu, "true");
		setMadkitProperty(viewers,  BailViewer.class.getName());//+";"+ScoreViewer.class.getName()+";"+moneyViewer.class.getName());
		setMadkitProperty(environment,  myEnvironmentCBBAGuai.class.getName());
                setMadkitProperty(noWrap, "true");
                setMadkitProperty(scheduler, myScheduler.class.getName());
		super.createSimulationInstance();
	}
        
    public static void main(String[] args) {
        executeThisLauncher(); 
        executeThisLauncher(); 
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();
        /*executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();*/
        /*
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();
        executeThisLauncher();  
        executeThisLauncher();*/
}
	
}

    

