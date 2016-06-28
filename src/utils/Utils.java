package utils;

import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import br.ufsc.core.TPoint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.util.GeometryEditor;
import com.vividsolutions.jump.coordsys.CoordinateSystem;

public class Utils {


	
	public static Trajectory covertBrTrajectory(br.ufsc.core.Trajectory tr){
		GeometryFactory gf = new GeometryFactory();
		
		Coordinate[] coords = new Coordinate[tr.getLen()];
		for(int i = 0; i < coords.length; i++){
			TPoint p = tr.getPoint(i);
			Coordinate c = new Coordinate(p.getX(), p.getY(), p.getTime());
			coords[i] = c;
		}
		
		LineString ls = gf.createLineString(coords);
		Trajectory t = new Trajectory(-1, tr.getTid(), ls);
		t.setStartTime(tr.getStartTime());
		t.setEndTime(tr.getEndTime());
		
		return t;
	}
	

	public static br.ufsc.core.Trajectory convertTrajectory(Trajectory tr) {
		br.ufsc.core.Trajectory t = new br.ufsc.core.Trajectory(tr.getTid());
		
		LineString ls = tr.getGeom();
		Coordinate[] coords = ls.getCoordinates();
		for(int i = 0; i < coords.length; i++){
			Coordinate c = coords[i];
			TPoint p = new TPoint(c.x, c.y, new Timestamp((long) c.z), c.x, c.y);
			p.setTimestamp(new Timestamp((long) c.z));
			t.addPoint(p);
		}
		
		return t;
	}

	/**
	 * Get a diff between two dates
	 * @param date1 the oldest date
	 * @param date2 the newest date
	 * @param timeUnit the unit in which you want the diff
	 * @return the diff value, in the provided unit
	 */
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	
	
	static GeometryFactory gf = new GeometryFactory();
	static CoordinateSystem cs = new CoordinateSystem("UTM 32N", 32632,
			new MyUTMProjection(32));	
	static GeometryProjector gp = new GeometryProjector(cs);
	static GeometryEditor ge = new GeometryEditor(gf);
	
	
	static public double distance(Geometry g1, Geometry g2){
		Geometry gp1 = ge.edit(g1, gp);
		Geometry gp2 = ge.edit(g2, gp);
		return  gp1.distance(gp2);
	}
	
}
