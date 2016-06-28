package it.cnr.isti.kdd.diaries;

import it.cnr.isti.kdd.sax.mbmodel.Circle;
import it.cnr.isti.kdd.sax.mbmodel.PointOfTrajectory;
import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import viz.GeoDiary;
import viz.Movement;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class DiariesExtractor {
	private static Logger log  = Logger.getLogger(DiariesExtractor.class);
	
	int uid = -1;
	Iterator<Trajectory> iTrajectories = null;
	Quadtree qTree = new Quadtree();
	GeoDiary d = new GeoDiary();
	private double maxRadius = 0.0050; // 0.0045; //~500m
	private double minRadius = 0.0003;
	int circleId = 0;
	
	
	
	public DiariesExtractor(int _uid, Iterator<Trajectory> _iTrajectories){
		uid = _uid;
		iTrajectories = _iTrajectories;
		d.setPersonId(""+uid);
	}
	
	public DiariesExtractor(int _uid, ArrayList<Trajectory> _trajectories) {
		this(_uid, _trajectories.iterator());
	}
	
	/**
	 * @param d
	 * @param qTree
	 * @param iTrajectories
	 */
	protected void extractLocationsAndDiary() {
		Circle last = null;
		TreeMap<Integer, Location> locations = d.getLocations();
		Calendar lastTime = null;
		Calendar firstTime = null;
		
//		Iterator<Trajectory> iTrajectories = trajectories.iterator();
		
		while (iTrajectories.hasNext()) {
			// br.ufsc.core.Trajectory tr = iTrajectories.next();
			Trajectory t = iTrajectories.next();
			// int uid = t.getTid();
			// d.trajectories.add(t);
			Circle cfrom = getCircle(t.getStartPoint(), true, qTree, minRadius,
					maxRadius);
			Circle cTo = getCircle(t.getEndPoint(), false, qTree, minRadius,
					maxRadius);
			Location start = getLocation(cfrom.getCircleID(), locations);
			Location end = getLocation(cTo.getCircleID(), locations);

			if ((last != null) && (cfrom.getCircleID() != last.getCircleID())) {
				// the movement is starting from a different location
				log.error("Start mismatch");
				log.error("Was in " + last.getCircleID()
						+ " now starting from " + cfrom.getCircleID());
				log.error("distance from previous: "
						+ last.getCentroid().distance(t.getStartPoint()));
				// System.err.println("temporal distance from previous: "+
				// (t.getStartTime().getTimeInMillis()-lastTime.getTimeInMillis())/60000);
			}

			last = cTo;
			lastTime = Calendar.getInstance();
			lastTime.setTime(t.getEndTime().getTime());
			firstTime = Calendar.getInstance();
			firstTime.setTime(t.getStartTime().getTime());
			if (!start.equals(end)) {
				// the activity type is generate randomkly at the moment
				// int at = new Random().nextInt(4);
				Movement m = new Movement(d, start, end, firstTime, lastTime,
						-1, 0,0);
				m.setTrajectory(t);
				log.debug("GPS: " + m);
				d.addMovement(m);
				d.addPosition(start, cfrom);
				d.addPosition(end, cTo);
			}
		}
		Collections.sort(d.getMovements());
		log.info("The current diary contains " + locations.size()
				+ " distinct locations");
	}

	protected Circle getCircle(PointOfTrajectory point, boolean first,
			Quadtree qTree, double minRadius, double maxRadius) {
		Circle res = null;
		PointOfTrajectory pot = point;
		Envelope env = pot.getEnvelopeInternal();
		env.expandBy(maxRadius);
		// retrieve all the circles within maxRadius
		List<Circle> candidates = qTree.query(env);
		double minFitness = Double.POSITIVE_INFINITY;
		Circle bestMatch = null;
		for (Circle neighborCircle : candidates) {
			double dist = neighborCircle.getCentroid().distance(pot);
			if (dist < maxRadius) {
				// double fitness = c.fitness(neighborCircle); // compute a
				// fitness value for the given circle
				if (dist < minFitness) {
					minFitness = dist;
					bestMatch = neighborCircle;
				}
			}
		}
		log.debug("There are " + candidates.size()
				+ " neighbors for the current circle [" + minFitness + "]");
		if (bestMatch != null) {
			// exists a candidate to merge with the current point
			qTree.remove(bestMatch.getEnvelope(), bestMatch);
			bestMatch.addPoint(pot);
			bestMatch.updateCentroid();
			// substitute bestMatch with the new Circle
			qTree.insert(bestMatch.getEnvelope(), bestMatch);

			res = bestMatch;
			log.debug("Adding to circle: [" + bestMatch.getRadius() + ","
					+ bestMatch.getPoints().size() + "]");
		} else {
			Circle newCircle = new Circle();
			// newCircle.setCentroid(pot);
			newCircle.addPoint(pot);
			newCircle.updateCentroid();
			if (newCircle.getRadius() < minRadius)
				newCircle.setRadius(minRadius);
			qTree.insert(newCircle.getEnvelope(), newCircle);
			res = newCircle;
			newCircle.setCircleID(circleId++);
			log.debug("Inserting a new Circle");
		}

		return res;
	}

	protected Location getLocation(int locId,
			TreeMap<Integer, Location> locations) {
		Location l = locations.get(locId);
		if (l == null) {
			l = new Location(locId, -1);
			locations.put(locId, l);
		}

		return l;
	}

	public GeoDiary getGeoDiary() {
		this.extractLocationsAndDiary();
		return d;
	}
	
	
}
