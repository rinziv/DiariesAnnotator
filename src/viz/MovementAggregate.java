package viz;

import it.cnr.isti.kdd.diaries.GeoLocation;
import it.cnr.isti.kdd.diaries.Location;

import java.util.ArrayList;

import utils.Utils;

public class MovementAggregate {
	protected ArrayList<Movement> movements = new ArrayList<Movement>();
	protected Location from;
	protected Location to;
	
	protected double distance = -1.0d;
	
	public MovementAggregate(Location _from, Location _to) {
		from = _from;
		to = _to;
	}
	
	
	public void addMovement(Movement m){
		movements.add(m);
		distance = -1.0d;
	}


	public ArrayList<Movement> getMovements() {
		return movements;
	}


	public Location getFrom() {
		return from;
	}


	public Location getTo() {
		return to;
	}
	
	
	public double getDistance(){
		if(distance<0){
			double d = 0.0d;
			for (Movement m : movements) {
				if(m.getTrajectory()!=null){
					d += m.getTrajectory().getLength();
				}else{
//					GeoLocation gfrom  = (GeoLocation) from;
//					GeoLocation gto = (GeoLocation) to;
//					d += Utils.distance(gfrom.getCentroid(),gto.getCentroid());
					d += 100;
				}
			}
			distance = d / movements.size(); 
		}
		return distance;
	}
	
}
