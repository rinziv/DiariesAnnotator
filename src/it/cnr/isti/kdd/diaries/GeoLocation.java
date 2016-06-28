package it.cnr.isti.kdd.diaries;

import it.cnr.isti.kdd.sax.mbmodel.PointOfTrajectory;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Point;


public class GeoLocation extends Location {
	Point centroid = null;
	ArrayList<PointOfTrajectory> points = new ArrayList<PointOfTrajectory>();
	
	public GeoLocation(int lid, int uid) {
		super(lid, uid);
	}

	public Point getCentroid() {
		return centroid;
	}
	
	

}
