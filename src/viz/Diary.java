package viz;

import it.cnr.isti.kdd.diaries.GeoLocation;
import it.cnr.isti.kdd.diaries.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.SingleSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class Diary implements SingleSelectionModel {

	private static Logger log = Logger.getLogger(Diary.class);

	private String personId;
	/**
	 * The map to index the location visited by the user. Fast retrieval by
	 * location id
	 */
	protected TreeMap<Integer, Location> locations = new TreeMap<Integer, Location>();
	/**
	 * A list of movement from location to location for the user. The movements
	 * are assumed to be sorted by time_start
	 */
	private ArrayList<Movement> movements = new ArrayList<Movement>();

	/**
	 * 
	 */
	private TreeMap<Integer, Movement> idxMovements = new TreeMap<Integer, Movement>();

	/**
	 * A map from location id to time spent in current location
	 */
	protected TreeMap<Integer, double[]> locationFrequencies = new TreeMap<Integer, double[]>();

	/**
	 * @return the locationFrequencies
	 */
	public TreeMap<Integer, double[]> getLocationFrequencies() {
		return locationFrequencies;
	}

	/**
	 * @param tid
	 * @return
	 */
	public Movement getMovementByTid(int tid) {
		return this.idxMovements.get(tid);
	}

	/**
	 * @return a map with the amount of time spent in each locations
	 * 
	 *         edit by lorenzo 2013 06 20 riedit by sax
	 */
	public double[] getLocationFrequenciesStats() {

		double[] stats = new double[locations.size()];

		// TreeMap<Integer, Double> stats = new TreeMap<Integer, Double>();
		for (Integer lid : locationFrequencies.keySet()) {
			double[] ds = locationFrequencies.get(lid);
			double timeSpent = 0;
			for (int i = 0; i < ds.length; i++) {
				timeSpent = timeSpent + ds[i];
			}
			stats[lid] = timeSpent;
			// stats.put(lid, timeSpent);
		}
		return stats;
	}

	public ArrayList<Movement> getMovements(int from, int to) {

		ArrayList<Movement> result = new ArrayList<Movement>();
		for (Movement m : movements) {
			if ((m.getStartLocation().getLid() == from)
					&& (m.getStopLocation().getLid() == to))
				result.add(m);
		}
		return result;
	}

	protected int selectedIndex = -1;

	private ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();
	private Calendar max_ts;
	private Calendar min_ts;

	public void addMovement(Movement m) {
		addLocation(m.startLocation);
		addLocation(m.stopLocation);

		if (getMovements().size() > 0) {
			Movement prev = getMovements().get(getMovements().size() - 1);
			prev.updateStopDuration(m);

			// System.out.println(m.getPerson().getPersonId());
			Calendar lastArrival = prev.getEndMovement();
			Calendar thisDeparture = m.getStartMovement();
			DateTime start = new DateTime(lastArrival);
			DateTime end = new DateTime(thisDeparture);

			Duration dur = new Duration(start, end);
			long width = dur.getStandardMinutes();
			int first = start.getMinuteOfDay();

			// System.out.println("--------------->>  Starting at minute" +
			// first + " and lasting " + width + " minutes");

			double[] minutes = locationFrequencies.get(prev.getStopLocation()
					.getLid());
			if (minutes == null) {
				minutes = new double[1440];
				// for(int i = 0; i < 1440; i++)
				// minutes[i] = new Double(0);
				locationFrequencies.put(prev.getStopLocation().getLid(),
						minutes);
			}

			while (width > 1440) {
				width -= 1440;
				for (int i = 0; i < minutes.length; i++) {
					minutes[i]++;
				}

			}
			for (int i = 0; i < width; i++) {
				minutes[(i + first) % 1440] += 1;
			}
		}
		getMovements().add(m);
		idxMovements.put(m.getTrajectory().getTid(), m);
	}

	protected void addLocation(Location loc) {
		if (getLocations().containsKey(loc.getLid())) {
			Location l = getLocations().get(loc.getLid());
			l.count++;
			// locations.put(startLocation, count);
		} else {
			getLocations().put(loc.getLid(), loc);
		}
	}

	public Iterator<Movement> iterator() {
		return getMovements().iterator();
	}

	// public int getNumHours(){
	// if(numHours==-1){
	// Calendar cFirst = movements.get(0).startMovement;
	// Calendar cLast = movements.get(movements.size()-1).endMovement;
	//
	// long span = cLast.getTimeInMillis()-cFirst.getTimeInMillis();
	// int hours = (int) Math.floor(span/1000/60/60);
	// numHours = hours;
	// }
	// return numHours;
	// }

	// public double getHourOfPeriod(Calendar c){
	// Calendar cFirst = movements.get(0).startMovement;
	// long span = c.getTimeInMillis()-cFirst.getTimeInMillis();
	// return 1.0d*(span/1000/60/60);
	// }

	public Movement getMovementAt(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);

		for (Movement m : getMovements()) {
			if (m.startMovement.before(c) && m.endMovement.after(c)) {
				return m;
			}

		}
		return null;
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void clearSelection() {
		selectedIndex = -1;
		fireChange();
	}

	protected void fireChange() {
		// System.out.println("**** propagating event to " + listeners.size() +
		// " listeners");
		for (ChangeListener listener : listeners) {
			// System.out.println("Talking to " + listener);
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	@Override
	public int getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	public boolean isSelected() {
		return (selectedIndex != -1);
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void setSelectedIndex(int index) {
		selectedIndex = index;
		fireChange();
	}

	public void setSelectedObject(Movement m) {
		int index = getMovements().indexOf(m);
		setSelectedIndex(index);
	}

	public Movement getSelectedMovement() {
		if (isSelected())
			return getMovements().get(selectedIndex);
		else
			return null;
	}

	public void clear() {
		setLocations(new TreeMap<Integer, Location>());
		setMovements(new ArrayList<Movement>());
		selectedIndex = -1;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public TreeMap<Integer, Location> getLocations() {
		return locations;
	}

	public void setLocations(TreeMap<Integer, Location> locations) {
		this.locations = locations;
	}

	public ArrayList<Movement> getMovements() {
		return movements;
	}

	public void setMovements(ArrayList<Movement> movements) {
		this.movements = movements;
	}

	public void setMaxTimestamp(Calendar ts) {
		this.max_ts = ts;
	}

	public Calendar getMaxTimestamp() {
		return this.max_ts;
	}

	public void setMinTimestamp(Calendar ts) {
		this.min_ts = ts;
	}

	public Calendar getMinTimestamp() {
		return this.min_ts;
	}
	
	public DirectedGraph<Location, MovementAggregate> asDirectedgraph(){
		DirectedSparseGraph<Location, MovementAggregate> graph = new DirectedSparseGraph<Location, MovementAggregate>();
		Location prev = null;
		Calendar prevTS = null;
		for (Movement m : movements) {
			Location lfrom = m.startLocation;
			Location lto = m.stopLocation;
			if((prev!=null)&&(!lfrom.equals(prev))){
				// create a fake flow to guarantee that the graph is connected
				Movement fm = new Movement(this, lfrom, lto, prevTS, m.getStartMovement(), -1, -1, -1);
				MovementAggregate fma = getMovementAggregate(graph, prev, lfrom);
				fma.addMovement(fm);
			}
			
			MovementAggregate ma = getMovementAggregate(graph, lfrom, lto);
			ma.addMovement(m);
			prev = lto;
			prevTS = m.getEndMovement();
		}
		
		return graph;
	}

	/**
	 * @param graph
	 * @param lfrom
	 * @param lto
	 * @return
	 */
	protected MovementAggregate getMovementAggregate(
			DirectedSparseGraph<Location, MovementAggregate> graph,
			Location lfrom, Location lto) {
		Collection<MovementAggregate> outEdges = graph.getOutEdges(lfrom);
		if(outEdges==null)
			outEdges = new ArrayList<MovementAggregate>();
		MovementAggregate ma = null;
		for (MovementAggregate gma : outEdges) {
			if(gma.to.equals(lto)){
				ma = gma;
				break;
			}
		}
		if(ma==null){
			// this link is found for the first time
			ma = new MovementAggregate(lfrom, lto);
			graph.addEdge(ma, lfrom, lto);
		}
		return ma;
	}
	
}
