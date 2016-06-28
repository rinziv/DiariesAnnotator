package utils;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.GeometryEditor.CoordinateOperation;
import com.vividsolutions.jump.coordsys.CoordinateSystem;
import com.vividsolutions.jump.coordsys.Geographic;
import com.vividsolutions.jump.coordsys.Planar;

public class GeometryProjector extends CoordinateOperation {

	protected CoordinateSystem cs;
	protected GeometryFactory gf = new GeometryFactory();
	
	public GeometryProjector(CoordinateSystem _cs) {
		cs =_cs;
	}
	
	
	@Override
	public Coordinate[] edit(Coordinate[] coords, Geometry g) {
		Coordinate[] newCoords = new Coordinate[coords.length];
		
		for (int i = 0; i < coords.length; i++) {
			Coordinate c = coords[i];
			Geographic geo = new Geographic(c.y, c.x);
			Planar p = new Planar();
			cs.getProjection().asPlanar(geo, p);
			newCoords[i] = new Coordinate(p.x, p.y);	
		}
		
		
		return newCoords;
	}

}
