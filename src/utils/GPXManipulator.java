package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;

import method.CBsmot;
import method.CBsmotNoDB;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.vividsolutions.jts.io.ParseException;

import br.ufsc.core.Stop;
import br.ufsc.core.TPoint;
import br.ufsc.core.Trajectory;
import core.CBFactory;
import core.ClusterTrajectory;

public class GPXManipulator {

	private List<FileReader> gpxFile;
	// private XMLReader xmlReader;
	private GPXHandler gpxHandler;

	public static int mt = 300; // minimum time in seconds
	// private static final double avg = 0.000009; //0.9 m/s?
	// private static final double sl =0.000013; //1.3 m/s?
	public static double avg = 0.3; // avg speed of the points of the stop
	public static double sl = 0.6; // speed limit for a point to be included in
									// the stop
	public static int tol = 10; // number of tolerace points with speed greater
								// than the speed lomit
	private static double buffer = 10;
	private static List<Trajectory> moves = new ArrayList<Trajectory>();

	public static void setMoves(List<Trajectory> moves) {
		GPXManipulator.moves = moves;
	}

	private static Trajectory finalTrajectory = new Trajectory(0);

	public GPXManipulator(List<String> gpxFile) throws FileNotFoundException {
		this.gpxFile = new ArrayList<FileReader>();
		for (int i = 0; i < gpxFile.size(); i++) {
			FileReader fr = new FileReader(gpxFile.get(i));
			this.gpxFile.add(fr);
		}

		gpxHandler = new GPXHandler();
	}

	public GPXManipulator(File[] gpxFile) throws FileNotFoundException {
		this.gpxFile = new ArrayList<FileReader>();
		for (int i = 0; i < gpxFile.length; i++) {
			FileReader fr = new FileReader(gpxFile[i].getAbsolutePath());
			this.gpxFile.add(fr);
		}

		gpxHandler = new GPXHandler();
	}

	// public List<Trajectory> (){
	//
	// }

//	public List<Trajectory> gpxToMove__() {
//
//		// moves = new ArrayList<Trajectory>();
//		try {
//			// xmlReader = XMLReaderFactory.createXMLReader();
//			//
//			//
//			// xmlReader.setContentHandler(gpxHandler);
//			// xmlReader.setErrorHandler(gpxHandler);
//			Trajectory trajectory = new Trajectory(0);
//			String sql = "";
//
//			// Collect all points for the user
//			for (int i = 0; i < gpxFile.size(); i++) {
//				GPXHandler gpxHandler = new GPXHandler();
//				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
//				xmlReader.setContentHandler(gpxHandler);
//				xmlReader.setErrorHandler(gpxHandler);
//				xmlReader.parse(new InputSource(gpxFile.get(i)));
//				for (TPoint point : gpxHandler.getTrack()) {
//					trajectory.addPoint(point);
//				}
//			}
//
//			return splitUserTrajectory(trajectory);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return moves;
//	}

	public Trajectory loadPointsAsTrajectory() {
		Trajectory trajectory = new Trajectory(0);
		// moves = new ArrayList<Trajectory>();
		try {
			
			// Collect all points for the user
			for (int i = 0; i < gpxFile.size(); i++) {
				GPXHandler gpxHandler = new GPXHandler();
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setContentHandler(gpxHandler);
				xmlReader.setErrorHandler(gpxHandler);
				xmlReader.parse(new InputSource(gpxFile.get(i)));
				for (TPoint point : gpxHandler.getTrack()) {
					trajectory.addPoint(point);
				}
			}
			return trajectory;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trajectory;
	}

	/**
	 * @param trajectory
	 * @return
	 * @throws ParseException
	 */
	protected List<Trajectory> splitUserTrajectory(Trajectory trajectory)
			throws ParseException {
		ClusterTrajectory ct = CBFactory.getFromTrajectory(trajectory);
		CBsmotNoDB cb = new CBsmotNoDB(avg, mt, sl, tol, buffer, 4326, false,
				false);
		cb.setRelativeSpeedOn(); // relative speed or absolute speed
		List<Stop> stops = cb.runOnTrajectory(ct);

		finalTrajectory.getPoints().addAll(trajectory.getPoints());

		System.out.println(stops.size() + " numeros de sopt");

		return extractMoves(trajectory, stops);
	}

	/**
	 * @param trajectory
	 * @param stops
	 * @return
	 */
	protected List<Trajectory> extractMoves(Trajectory trajectory,
			List<Stop> stops) {
		List<TPoint> movePoints = new ArrayList<TPoint>();
		int stopIndice = 0;
		boolean hasStop = stops.size() > 0;
		Stop stop = null;
		if (hasStop) {
			stop = stops.get(stopIndice);

			for (int i = 0; i < stops.size(); i++) {
				System.out.println("Stop " + i);
				System.out.println(stops.get(i).getStartTime());
				System.out.println(stops.get(i).getEndTime());
			}

			System.out.println(trajectory.getPoint(0).getTimestamp());
			System.out.println(trajectory.getLastPoint().getTimestamp());
		}
		boolean instop = false;
		for (int i = 0; i < trajectory.getLen(); i++) {
			TPoint p1 = trajectory.getPoint(i);

			if (!instop) {
				if (hasStop && p1.getTime() > stop.getStartTime().getTime()
						&& p1.getTime() < stop.getEndTime().getTime()) {
					if (movePoints.size() > 0) {
						Trajectory move = new Trajectory(0);
						move.setPoint(movePoints);
						moves.add(move);
					}
					movePoints = new ArrayList<TPoint>();
					instop = true;
				} else {
					movePoints.add(p1);
				}

				if (i == trajectory.getLen() - 1) {
					Trajectory move = new Trajectory(0);
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

		System.out.println("Found " + moves.size() + " moves");
		for (int i = 0; i < moves.size(); i++) {
			System.out.println(moves.get(i).getPoint(0).getTimestamp());
			System.out.println(moves.get(i).getLastPoint().getTimestamp());
		}
		return moves;
	}

	public static Trajectory getFinalTrajectory() {
		return finalTrajectory;
	}

	public static void setFinalTrajectory(Trajectory finalTrajectory) {
		GPXManipulator.finalTrajectory = finalTrajectory;
	}

	public List<Trajectory> getMoves() {
		return moves;
	}

}
