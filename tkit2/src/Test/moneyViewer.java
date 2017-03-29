/*
 * Decompiled with CFR 0_114.
 */
package Test;

import java.awt.Container;
import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import madkit.kernel.AbstractAgent;
import madkit.kernel.Probe;
import madkit.simulation.probe.PropertyProbe;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import turtlekit.gui.util.ChartsUtil;
import turtlekit.kernel.Turtle;
import turtlekit.viewer.AbstractObserver;
import turtlekit.viewer.GenericViewer;

@GenericViewer
public class moneyViewer
extends AbstractObserver {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    private int index = 0;
    private PropertyProbe<Turtle,Double> probe;
    private Map<Turtle , XYSeries> series;
    //private Set<String> handledRoles = new HashSet<String>();
    private int timeFrame = 0;
    private boolean monitorTurtle;
    private Set<Turtle> handledTurtles = new HashSet<>();

    public moneyViewer() {
        this.series = new HashMap<Turtle, XYSeries>();
        this.createGUIOnStartUp();
    }

    @Override
    protected void activate() {
        this.setLogLevel(Level.ALL);
        this.probe = new PropertyProbe("myturtles","myturtles","bug","money");
        this.addProbe(probe);
        super.activate();
        this.observe();
    }

    @Override
    public void setupFrame(JFrame jFrame) {
        ChartPanel chartPanel = ChartsUtil.createChartPanel(this.dataset, "Money", null, null);
        chartPanel.setPreferredSize(new Dimension(550, 250));
        jFrame.setContentPane(chartPanel);
        jFrame.setLocation(50, 0);
        
    }

    private void addSerie(Turtle turtle) {

        XYSeries xYSeries = new XYSeries((Comparable)((Object)turtle));
        this.series.put(turtle, xYSeries);
        this.dataset.addSeries(xYSeries);
        this.handledTurtles.add(turtle);
    }

    @Override
    protected void observe() {
        this.updateSeries();
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                series.entrySet().stream().forEach((entry) -> {
                    entry.getValue().add(index, probe.getPropertyValue(entry.getKey()));
                });
                moneyViewer.this.index++;
                if (moneyViewer.this.timeFrame > 0 && moneyViewer.this.index % moneyViewer.this.timeFrame == 0) {
                    for (XYSeries xYSeries : moneyViewer.this.series.values()) {
                        xYSeries.clear();
                    }
                }
            }
        });
    }

    public void setTimeFrame(int n) {
        this.timeFrame = n;
    }

    public void setMonitorTurtleRole(boolean bl) {
        this.monitorTurtle = bl;
        if (this.isAlive()) {
            this.updateSeries();
        }
    }

    protected void updateSeries() {
        List<Turtle> treeSet = probe.getCurrentAgentsList();
        if (treeSet != null && treeSet.size() != this.handledTurtles.size()) {
            for (Turtle turtle : treeSet) {
                if (!this.handledTurtles.add(turtle)) continue;
                this.addSerie(turtle);
            }
        }
    }
}

