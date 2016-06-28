package viz;

import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import utils.TrajectoryAnnotation;
import utils.Utils;


public class Movement implements Comparable<Movement>{
	Diary person;
	Location startLocation;
	Location stopLocation;
	Calendar startMovement;
	Calendar endMovement;
	long stopDuration = -1;
	int activityType;
	int activityCategory;
	int weather=-1;
	int tid=-1;
	Double temperature=null;
	Trajectory traj = null;
	TrajectoryAnnotation ta = null;
	
	public Movement(Diary _person, Location _startLocation, Location _stopLocation, Calendar _startMovement, Calendar _endMovement, int _activityType, int _activityCategory, int _tid) {
		person = _person;
		startLocation = _startLocation;
		stopLocation = _stopLocation;
		startMovement = _startMovement;
		endMovement = _endMovement;
		activityType = _activityType;
		activityCategory =  _activityCategory;
		tid=_tid;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return startLocation+";"+stopLocation+";"+sdf.format(startMovement.getTime())+";"+sdf.format(endMovement.getTime())+";"+activityType;
	}

	@Override
	public int compareTo(Movement o) {
		return startMovement.compareTo(o.startMovement);
	}
	
	public Diary getPerson() {
		return person;
	}

	public void setPerson(Diary person) {
		this.person = person;
	}

	public Location getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}

	public Location getStopLocation() {
		return stopLocation;
	}

	public void setStopLocation(Location stopLocation) {
		this.stopLocation = stopLocation;
	}

	public Calendar getStartMovement() {
		return startMovement;
	}

	public void setStartMovement(Calendar startMovement) {
		this.startMovement = startMovement;
	}

	public Calendar getEndMovement() {
		return endMovement;
	}

	public void setEndMovement(Calendar endMovement) {
		this.endMovement = endMovement;
	}

	public int getActivityType() {
		return activityType;
	}

	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}

	public int getActivityCategory() {
		return activityCategory;
	}

	public void setActivityCategory(int activityCategory) {
		this.activityCategory = activityCategory;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public void setTrajectory(Trajectory t) {
		traj = t;
	}
	
	public Trajectory getTrajectory(){
		return traj;
	}

//	public long getDuration() { edit by lorenzo 2013 06 07 - metodo spostato in trajectory
////		return Utils.getDateDiff(startMovement.getTime(), endMovement.getTime(), TimeUnit.MILLISECONDS);
//		return endMovement.getTimeInMillis() - startMovement.getTimeInMillis() - 1000*60*60; // TODO: check if it is needed this adjstment
//	}
//	
	public void updateStopDuration(Movement next){
		Calendar nextStart = next.getStartMovement();
		stopDuration = nextStart.getTimeInMillis() - endMovement.getTimeInMillis() - 1000*60*60;
	}

	public long getStopDuration(){
		return stopDuration;
	}

	public TrajectoryAnnotation getTa() {
		return ta;
	}

	public void setTa(TrajectoryAnnotation ta) {
		this.ta = ta;
	}

	/**
	 * @return the tid
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * @param tid the tid to set
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}
}
