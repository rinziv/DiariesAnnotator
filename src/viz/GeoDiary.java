package viz;

import it.cnr.isti.kdd.diaries.Flow;
import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.sax.mbmodel.Circle;
import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.util.ArrayList;
import java.util.TreeMap;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class GeoDiary extends Diary {

	/**
	 * Map each location to a Point on the geography
	 */
	protected TreeMap<Location,Circle> positions = new TreeMap<Location, Circle>();

	protected ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();


	public Circle getPosition(Location loc){
		return positions.get(loc);
	}

	public void addPosition(Location loc, Circle p){
		positions.put(loc, p);
	}

	public Graph<Location, Flow> getMobilityNetwork() {
		//Creo la struttura dati che conterra' nodi e archi
		Graph<Location, Flow> g = new DirectedSparseGraph<Location, Flow>();
		for (Movement m:this.getMovements())
		{
			String uid = m.person.getPersonId();
			Location startLocation = m.getStartLocation();
			Location stopLocation = m.getStopLocation();
			boolean isAnnotated = true;
			int activityType = m.getActivityType();
			Flow f = new Flow(Integer.parseInt(uid), 1, startLocation.getLid(), stopLocation.getLid(), isAnnotated, 
					activityType);
			//Per ogni movement aggiungo un arco nel grafo
			g.addEdge(f, startLocation, stopLocation);
		}
		return g;

	}
}
