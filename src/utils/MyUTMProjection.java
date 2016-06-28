package utils;

import com.vividsolutions.jump.coordsys.Geographic;
import com.vividsolutions.jump.coordsys.Planar;
import com.vividsolutions.jump.coordsys.Projection;
import com.vividsolutions.jump.coordsys.Radius;
import com.vividsolutions.jump.coordsys.Spheroid;
import com.vividsolutions.jump.coordsys.impl.TransverseMercator;



public class MyUTMProjection extends Projection {
	
	private static final double SCALE_FACTOR = 0.9996;
	private static final double FALSE_EASTING = 500000.0;
	private static final double FALSE_NORTHING = 0.0;
	
	private TransverseMercator mercator = new TransverseMercator();
	
	public MyUTMProjection(int zone) {
		mercator.setSpheroid(new Spheroid(new Radius(Radius.GRS80)));
		switch (zone) {
		case 32:
			 mercator.setParameters(9);
			break;

		default:
			break;
		}
	}
	
	public void setSpheroid(Spheroid s){
		mercator.setSpheroid(s);
	}

	@Override
	public Geographic asGeographic(Planar p, Geographic g) {
		p.x = (p.x - FALSE_EASTING) /SCALE_FACTOR;
		p.y = (p.y - FALSE_NORTHING) /SCALE_FACTOR;
		mercator.asGeographic(p, g);
		
		return g;
	}

	@Override
	public Planar asPlanar(Geographic g, Planar p) {
		
		mercator.asPlanar(g, p);
		p.x = SCALE_FACTOR * p.x + FALSE_EASTING;
		p.y = SCALE_FACTOR * p.y + FALSE_NORTHING;
		
		return p;
	}

}
