package it.cnr.isti.kdd.sax.mbmodel;

import com.vividsolutions.jts.geom.Point;

public class PointOfTrajectory extends Point {
	
	int uid;
	int tid;
	boolean isFirst = false;
	
	public PointOfTrajectory(Point p, int _uid, int _tid, boolean _isFirst) {
		super(p.getCoordinateSequence(),p.getFactory());
		uid = _uid;
		tid = _tid;
		isFirst = _isFirst;
	}
	
	@Override
	public String toString() {
		String res = "("+uid+","+tid+"," + (isFirst?"start":"end")+")";
		return res;
	}

}
