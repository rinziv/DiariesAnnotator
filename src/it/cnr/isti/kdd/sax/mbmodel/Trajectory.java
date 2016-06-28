package it.cnr.isti.kdd.sax.mbmodel;


import java.util.Calendar;

import utils.GeometryProjector;
import utils.MyUTMProjection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jump.coordsys.CoordinateSystem;

public class Trajectory {
	private int uid = -1;
	protected int tid = -1;
	protected LineString geom = null;
	private Calendar startTime = null;
	private Calendar endTime = null;
	protected LineString projected = null;
	private static GeometryProjector gp = new GeometryProjector(new CoordinateSystem("utm32", 32632, new MyUTMProjection(32)));
	
	public Trajectory(int _uid, int _tid, Geometry _geom) {
		setUid(_uid);
		tid = _tid;
		geom = (LineString) _geom;
		setStartTime(Calendar.getInstance());
		Point start = geom.getStartPoint();
		getStartTime().setTimeInMillis((long) start.getCoordinate().z);
		setEndTime(Calendar.getInstance());
		Point end = geom.getEndPoint();
		getEndTime().setTimeInMillis((long) end.getCoordinate().z);

		startPoint = new PointOfTrajectory(start, _uid, _tid, true);
		endPoint = new PointOfTrajectory(end, _uid, _tid, false);
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	PointOfTrajectory startPoint = null;

	public PointOfTrajectory getStartPoint() {
		return startPoint;
	}

	public PointOfTrajectory getEndPoint() {
		return endPoint;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public void setStartTime(long time) {
		startTime.setTimeInMillis(time);
	}

	public void setEndTime(long time) {
		endTime.setTimeInMillis(time);
	}

	PointOfTrajectory endPoint = null;

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public LineString getGeom() {
		return geom;
	}

	public double getLength()  {
		// conversione
		if (projected== null)
		{
			projected= (LineString) gp.edit(geom, geom.getFactory());
			
		}
		return projected.getLength();
	}

	public long getDuration() {
//		return Utils.getDateDiff(startMovement.getTime(), endMovement.getTime(), TimeUnit.MILLISECONDS);
		return (this.endTime.getTimeInMillis() - this.startTime.getTimeInMillis())/1000; // TODO: check if it is needed this adjstment
	}

	public double getAvgSpeed()
	{
		if(this.getDuration()>0)
			return (this.getLength()/this.getDuration())*3.6;
		return -1;
	}

	public int getStartHour() {
		return this.startTime.get(Calendar.HOUR_OF_DAY);
	}

	public String getSlotTime() {
		int startHour = this.getStartHour();
		String slotTime = "18-06";
		if (startHour >= 6 && startHour < 12)
			slotTime = "06-12";
		else if (startHour >= 12 && startHour < 18)
			slotTime = "12-18";
		return slotTime;
	}

	/**
	 * @return DAY OF WEEK 1 IS MONDAY, 7 IS SUNDAY
	 */
	public int getDayOfWeek() {
		return (this.startTime.get(Calendar.DAY_OF_WEEK) + 6) % 7;
	}

	public boolean isWeekday() {
		if (this.getDayOfWeek() > 5)
			return false;
		return true;
	}

	public int getWeek() {
		return this.startTime.get(Calendar.WEEK_OF_YEAR);
	}
}
