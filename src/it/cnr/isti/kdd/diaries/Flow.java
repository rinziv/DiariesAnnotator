/**
author : Lorenzo 
KDDLAB ISTI CNR
 */

package it.cnr.isti.kdd.diaries;

import java.sql.Timestamp;

public class Flow {
	private int uid;
	private int weight;
	private int from;
	private int to;
	private boolean isAnnotated;
	private int activityType;
	private String activityTypeLabel;
	private double totalDistance;
	private double length;
	private double duration;
	private Timestamp timeStart;
	
	public Flow(int uid, int weight, int from, int to) {
		super();
		this.uid = uid;
		this.weight = weight;
		this.from = from;
		this.to = to;
	}

	
	public Flow(int uid, int weight, int from, int to, boolean isAnnotated, int activityType) {
		super();
		this.uid = uid;
		this.weight = weight;
		this.from = from;
		this.to = to;
		this.isAnnotated = isAnnotated;
		this.activityType=activityType;
		this.activityTypeLabel=getActivityString(activityType);
	}

	public int getActivityType() {
		return activityType;
	}


	public double getTotalDistance() {
		return totalDistance;
	}


	public double getLength() {
		return length;
	}


	public double getDuration() {
		return duration;
	}


	public Flow(int uid, int weight, int from, int to, boolean isAnnotated,
			int activityType, double totalDistance,
			double length, double duration, Timestamp timeStart) {
		super();
		this.uid = uid;
		this.weight = weight;
		this.from = from;
		this.to = to;
		this.isAnnotated = isAnnotated;
		this.activityType = activityType;
		this.activityTypeLabel=getActivityString(activityType);
		this.totalDistance = totalDistance;
		this.length = length;
		this.duration = duration;
		this.timeStart = timeStart;
	}

	public Flow(int uid, int weight, int from, int to, boolean isAnnotated,
			int activityType, double length, double duration,
			Timestamp timeStart) {
		super();
		this.uid = uid;
		this.weight = weight;
		this.from = from;
		this.to = to;
		this.isAnnotated = isAnnotated;
		this.activityType = activityType;
		this.length = length;
		this.duration = duration;
		this.timeStart = timeStart;
		this.activityTypeLabel=getActivityString(activityType);
	}


	public Timestamp getTimeStart() {
		return timeStart;
	}


	/**
	 * @return the activityTypeLabel
	 */
	public String getActivityTypeLabel() {
		return activityTypeLabel;
	}


	/**
	 * @param activityTypeLabel the activityTypeLabel to set
	 */
	public void setActivityTypeLabel(String activityTypeLabel) {
		this.activityTypeLabel = activityTypeLabel;
	}

	/**
	 * 
	 * @param activityType
	 * @return
	 */
	private String getActivityString(int activityType) {
		switch (activityType) {
		case 1:
			return "annotated";
		case -1:
			return "non annotated";
		default: return "non annotated";
		}
	}

//	private String getActivityString(int activityType) {
//		switch (activityType) {
//		case 26:
//			return "other";
//		case 25:
//			return "touring";
//		case 24:
//			return "bring and get";
//		case 23:
//			return "leisure";
//		case 22:
//			return "social activity";
//		case 14:
//			return "activity at home";
//		case 15:
//			return "sleeping";
//		case 16:
//			return "working";
//		case 17:
//			return "services";
//		case 18:
//			return "eating";
//		case 19:
//			return "daily shopping";
//		case 20:
//			return "shopping";
//		case 21:
//			return "education";
//		default:
//			return "no label";
//		}
//
//	}


	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String toString() {
		return "" + weight;

	}

	public int getFrom() {

		return from;
	}

	public int getTo() {
		return to;
	}

	public boolean isAnnotated() {
		return isAnnotated;
	}
	
	
}
