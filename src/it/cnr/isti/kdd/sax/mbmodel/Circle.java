package it.cnr.isti.kdd.sax.mbmodel;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Circle {
	private int circleID = -1;
	private Point centroid;
	private double radius;
	private ArrayList<PointOfTrajectory> points = new ArrayList<PointOfTrajectory>(1);

	/**
	 * The fitness of two circles is the resulting diameter of the union of the two circles, i.e.
	 * the circle containing all the points of the two circles.
	 * The implementation computed the diameter as the maximum distance among the pairwise distances
	 * of the points in the two sets.
	 * 
	 * @param neighborCircle
	 * @return
	 */
	public double fitness(Circle neighborCircle) {
		double diameter = Double.MIN_VALUE;
		for (Point p1 : getPoints()) {
			for (Point p2 : neighborCircle.getPoints()) {
				double d = p1.distance(p2);
				if (d > diameter){
					diameter = d;
				}
			}
		}
		return diameter;
	}
	
	public double computeRadius() {
		double maxRadius = Double.MIN_VALUE;
		for (Point p : getPoints()) {
			double dist = p.distance(getCentroid());
			if(dist> maxRadius)
				maxRadius = dist;
		}
		return maxRadius;
	}
	
	/**
	 * Determine the new centroid of the group of points. The centroid is the mean point of the set
	 * and may not be a member of the set. The maximum radius of the circle is updated as well
	 */
	public void updateCentroid() {
		if(getPoints().size()>0){
		Geometry[] geos = GeometryFactory.toGeometryArray(getPoints());
        GeometryFactory gf = geos[0].getFactory();
        Geometry gc = gf.createGeometryCollection(geos);
        setCentroid(gc.getCentroid());
        setRadius(computeRadius());
		}
	}
	
	public Envelope getEnvelope(){
		Envelope e = getCentroid().getEnvelopeInternal();
		e.expandBy(getRadius());
		
		return e;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}
	
	public void addPoint(PointOfTrajectory p){
		getPoints().add(p);
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public ArrayList<PointOfTrajectory> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<PointOfTrajectory> points) {
		this.points = points;
	}

	public int getCircleID() {
		return circleID;
	}

	public void setCircleID(int circleID) {
		this.circleID = circleID;
	}
	
}
