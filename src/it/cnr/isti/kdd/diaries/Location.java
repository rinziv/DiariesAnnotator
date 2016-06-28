/**
author : Lorenzo 
KDDLAB ISTI CNR
 */

package it.cnr.isti.kdd.diaries;

public class Location implements Comparable<Location> {

	int lid;
	int uid;
	public int count=1;
	private int inDegree;
	private int outDegree;
	private double x;
	private double y;

	public Location(int lid, int uid) {
		super();
		this.lid = lid;
		this.uid = uid;
	}
	/**
	 * @return the lid
	 */
	public int getLid() {
		return lid;
	}
	/**
	 * @param lid the lid to set
	 */
	public void setLid(int lid) {
		this.lid = lid;
	}
	/**
	 * @return the uid
	 */
	public int getUid() {
		return uid;
	}
	/**
	 * @param uid the uid to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	public String toString()
	{
		return "user:"+uid + " - location:"+ lid;
	}

	public String locationName()
	{
		return "location:"+ lid;
	}
	/**
	 * @return the outDegree
	 */
	public int getOutDegree() {
		return outDegree;
	}
	/**
	 * @return the inDegree
	 */
	public int getInDegree() {
		return inDegree;
	}


	/**
	 * @param outDegree the outDegree to set
	 */
	public void setInDegree(int inDegree) {
		this.inDegree = inDegree;
	}

	public void setOutDegree(int outDegree) {
		this.outDegree = outDegree;
	}


	@Override
	public int compareTo(Location l) {
		return new Integer(lid).compareTo(l.getLid());
	}
	public void setX(double x) {
		this.x=x;

	}
	public void setY(double y) {
		this.y=y;		
	}
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}


}
