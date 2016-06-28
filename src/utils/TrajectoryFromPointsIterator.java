package utils;

import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import method.CBsmotNoDB;
import br.ufsc.core.Stop;
import br.ufsc.core.TPoint;

import com.vividsolutions.jts.io.ParseException;

import core.CBFactory;
import core.ClusterTrajectory;

public class TrajectoryFromPointsIterator implements Iterator<Trajectory> {
	
	private static Logger log = Logger.getLogger(TrajectoryFromPointsIterator.class);

	public static int mt = 300; // minimum time in seconds
	// private static final double avg = 0.000009; //0.9 m/s?
	// private static final double sl =0.000013; //1.3 m/s?
	public static double avg = 0.3; // avg speed of the points of the stop
	public static double sl = 0.6; // speed limit for a point to be included in
									// the stop
	public static int tol = 10; // number of tolerace points with speed greater
								// than the speed lomit
	private static double buffer = 3;

	Trajectory trajectory = null;
	ArrayList<Trajectory> moves = new ArrayList<Trajectory>();
	Iterator<Trajectory> mainIterator = moves.iterator();

	public TrajectoryFromPointsIterator(br.ufsc.core.Trajectory tr) {
		try {
			ClusterTrajectory ct = CBFactory.getFromTrajectory(tr);
			CBsmotNoDB cb = new CBsmotNoDB(avg, mt, sl, tol, buffer, 4326,
					false, false);
			cb.setRelativeSpeedOn(); // relative speed or absolute speed
			List<Stop> stops;

			stops = cb.runOnTrajectory(ct);

			log.debug(stops.size() + " numeros de stop");

			List<br.ufsc.core.Trajectory> trs = extractMoves(tr, stops);
			for (Iterator<br.ufsc.core.Trajectory> i = trs.iterator(); i
					.hasNext();) {
				br.ufsc.core.Trajectory tri = i.next();
				Trajectory tri_ = Utils.covertBrTrajectory(tri);
				moves.add(tri_);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainIterator = moves.iterator();
	}

	/**
	 * @param trajectory
	 * @param stops
	 * @return
	 */
	protected List<br.ufsc.core.Trajectory> extractMoves(
			br.ufsc.core.Trajectory tr, List<Stop> stops) {
		List<TPoint> movePoints = new ArrayList<TPoint>();
		ArrayList<br.ufsc.core.Trajectory> moves = new ArrayList<br.ufsc.core.Trajectory>();
		int stopIndice = 0;
		boolean hasStop = stops.size() > 0;
		Stop stop = null;
		if (hasStop) {
			stop = stops.get(stopIndice);

			for (int i = 0; i < stops.size(); i++) {
				log.debug("Stop " + i);
				log.debug(stops.get(i).getStartTime());
				log.debug(stops.get(i).getEndTime());
			}

			log.debug(tr.getPoint(0).getTimestamp());
			log.debug(tr.getLastPoint().getTimestamp());
		}
		boolean instop = false;
		for (int i = 0; i < tr.getLen(); i++) {
			TPoint p1 = tr.getPoint(i);

			if (!instop) {
				if (hasStop && p1.getTime() >= stop.getStartTime().getTime()
						&& p1.getTime() <= stop.getEndTime().getTime()) {
					if (movePoints.size() > 0) {
						br.ufsc.core.Trajectory move = new br.ufsc.core.Trajectory(
								0);
						move.setPoint(movePoints);
						moves.add(move);
					}
					movePoints = new ArrayList<TPoint>();
					instop = true;
				} else {
					movePoints.add(p1);
				}

				if (i == tr.getLen() - 1) {
					br.ufsc.core.Trajectory move = new br.ufsc.core.Trajectory(
							0);
					move.setPoint(movePoints);
					moves.add(move);
					movePoints = new ArrayList<TPoint>();
				}
			} else {
				if (hasStop && p1.getTime() > stop.getEndTime().getTime()) {
					instop = false;
					movePoints.add(p1);
					stopIndice += 1;
					if (stopIndice < stops.size()) {
						stop = stops.get(stopIndice);
					}

				}
			}

		}

		log.info("Found " + moves.size() + " moves");
		for (int i = 0; i < moves.size(); i++) {
			log.debug(moves.get(i).getPoint(0).getTimestamp());
			log.debug(moves.get(i).getLastPoint().getTimestamp());
		}
		return moves;
	}

	@Override
	public boolean hasNext() {
		return mainIterator.hasNext();
	}

	@Override
	public Trajectory next() {
		return mainIterator.next();
	}

	@Override
	public void remove() {
		mainIterator.remove();
	}
}
