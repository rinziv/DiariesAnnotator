package viz;

import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.sax.mbmodel.Circle;
import it.cnr.isti.kdd.sax.mbmodel.PointOfTrajectory;
import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.roots.map.MapPanel;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeoPanel extends MapPanel{
	private static final Color TRAJECTORY_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.2f);
	protected static final int RADIUS = 3;
	private static final Color SELECTED_TRAJECTORY_COLOR = new Color(1.0f, 1.0f, 0.5f, 0.9f);
	protected GeoDiary diary = null;

	public GeoPanel(GeoDiary _diary) {
		diary = _diary;
		diary.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();	
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gOrig = (Graphics2D) g;
		Color oldColor = gOrig.getColor();
		Stroke oldStroke = gOrig.getStroke();

		gOrig.setColor(TRAJECTORY_COLOR);
		for (Movement m : diary.getMovements()) {
			//gOrig.setColor(ActivityColors.getColor(m.getActivityType()));
			drawMovement(gOrig, m);
		}

		if(diary.isSelected()){
			
			Stroke s = new BasicStroke(3.0f);
			gOrig.setStroke(s);
			Movement m = diary.getSelectedMovement();
			gOrig.setColor(TRAJECTORY_COLOR);
			drawMovement(gOrig, m);
			gOrig.setStroke(oldStroke);
		}

		for (Location loc : diary.positions.keySet()) {
			Circle c = diary.getPosition(loc);
			Point p = c.getCentroid();
			
			int pcx = lon2position(p.getX(), getZoom()) - getMapPosition().x;
			int pcy = lat2position(p.getY(), getZoom()) - getMapPosition().y;
			int numPoints = c.getPoints().size();

			
			gOrig.setColor(new Color(0.0f,1.0f,0.0f,0.5f));
			
			for (int i = 0; i < numPoints; i++){
				Point pi = c.getPoints().get(i);
				int px = lon2position(pi.getX(), getZoom()) - getMapPosition().x;
				int py = lat2position(pi.getY(), getZoom()) - getMapPosition().y;
				
				gOrig.drawLine(pcx, pcy, px, py);
				gOrig.fillOval(px - RADIUS, py - RADIUS, 2 * RADIUS, 2 * RADIUS);
			}
			
			// draw the containing circle
			Polygon circle = (Polygon) p.buffer(c.getRadius());
			LineString lsCircle = circle.getExteriorRing();
			GeneralPath gpc = new GeneralPath();
			gpc.moveTo(pcx, pcy);
			for (int i = 0; i < lsCircle.getNumPoints(); i++){
				Point pi = lsCircle.getPointN(i);
				int px = lon2position(pi.getX(), getZoom()) - getMapPosition().x;
				int py = lat2position(pi.getY(), getZoom()) - getMapPosition().y;
				
				gpc.lineTo(px, py);
			}
			gpc.closePath();
			
			gOrig.setStroke(new BasicStroke(3.0f));
			gOrig.setColor(new Color(0.0f, 0.0f, 1.0f, 0.4f));
			gOrig.fill(gpc);
			gOrig.setStroke(oldStroke);
			
			gOrig.setColor(oldColor);
			gOrig.fillOval(pcx - RADIUS, pcy - RADIUS, 2 * RADIUS, 2 * RADIUS);
			gOrig.drawString("" + loc.getLid()+"("+numPoints+")", pcx + 2, pcy - 2);
		}

		gOrig.setColor(oldColor);
		// System.out.println("Map pos: " + px+","+py);
	}

	/**
	 * @param gOrig
	 * @param m
	 */
	protected void drawMovement(Graphics2D gOrig, Movement m) {
		Trajectory t = m.getTrajectory();
		
//		Color c = ActivityColors.getColor(m.getActivityType());
//		Color oldColor = gOrig.getColor();
//		gOrig.setColor(c);
//		
		GeneralPath gp = new GeneralPath();
		PointOfTrajectory p0 = t.getStartPoint();
		gp.moveTo(lon2position(p0.getX(), getZoom()) - getMapPosition().x,
				lat2position(p0.getY(), getZoom()) - getMapPosition().y);
		int numPoints = t.getGeom().getNumPoints();
		for (int i = 0; i < numPoints; i++) {
			Point p = t.getGeom().getPointN(i);
			int px = lon2position(p.getX(), getZoom()) - getMapPosition().x;
			int py = lat2position(p.getY(), getZoom()) - getMapPosition().y;
			gp.lineTo(px, py);
			// gOrig.fillOval(px - 2, py - 2, 2 * 2, 2 * 2);
			gOrig.fillOval(px - RADIUS, py - RADIUS, 2 * RADIUS, 2 * RADIUS);
		}
		gOrig.draw(gp);
//		gOrig.setColor(oldColor);
	}
	
	public void setDiary(GeoDiary gd){
		diary = gd;
		diary.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();
				
			}
		});
	}

}
