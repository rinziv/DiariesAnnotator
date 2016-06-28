package viz;

import it.cnr.isti.kdd.diaries.ActivityLocation;
import it.cnr.isti.kdd.diaries.Location;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DiaryPanel extends JPanel implements MouseListener,
		MouseMotionListener, MouseWheelListener {

	protected Diary p;

	protected Font font = new Font("Arial", Font.BOLD, 10);

	protected TreeMap<Integer, Integer> mapLocationPosition = new TreeMap<Integer, Integer>();

	protected int maxLabelWidth = 60;

	protected ArrayList<Movement> timeRanges = new ArrayList<Movement>();

	long t1 = -1;

	// the temporal boundary of the visualization
	protected long minT = 0;
	protected long maxT = 0;
	long allPeriod = 0;

	public DiaryPanel(Diary _p) {
		setDiary(_p);
		// setPreferredSize(new Dimension(800, 600));
		// resetTemporalBoundaryToDiary(1000*60*60);
		resetTemporalBoundaryToDiary();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
	}

	
	public void setDiary(Diary _p){
		p = _p;
		p.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				Diary d = (Diary) ce.getSource();
				if(d.isSelected()){
					Movement m = d.getSelectedMovement();
//					System.out.println("new selection");
					firePropertyChange("Selected movement", null, m);
					repaint();
				}else{
//					System.out.println("selection cleared");
					firePropertyChange("Clear selection", null, null);
					repaint();
				}
			}
		});
	}
	
	public void resetTemporalBoundaryToDiary() {
		resetTemporalBoundaryToDiary(0);

	}

	public void resetTemporalBoundaryToDiary(long offset) {
		if (p.getMovements().size() > 0) {
			Calendar cFirst = p.getMovements().get(0).startMovement;
			Calendar cLast = p.getMovements().get(p.getMovements().size() - 1).endMovement;
			setTemporalBoundary(cFirst.getTimeInMillis() - offset,
					cLast.getTimeInMillis() + offset);
		} else {
			Calendar cFirst = Calendar.getInstance();
			setTemporalBoundary(cFirst.getTimeInMillis() - 36000000,
					cFirst.getTimeInMillis() + 36000000);
		}
	}

	public void setTemporalBoundary(long min, long max) {
		minT = min;
		maxT = max;
		allPeriod = (maxT - minT);
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Dimension d = getSize();
		Insets insets = getInsets();

		double radius = (Math.min(d.getWidth() - (insets.left + insets.right),
				d.getHeight() - (insets.top + insets.bottom))) / 2;

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setBackground(Color.white);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setFont(font);
		// Move to the center
		// AffineTransform at = g2d.getTransform();
		// g2d.translate(d.getWidth() / 2, d.getHeight() / 2);

		int numLocations = p.getLocations().size();

		if (numLocations > 0) {
//			int interLocationSpace = 50;
			int interLocationSpace = (d.height - insets.top - insets.bottom) / numLocations;
			drawHorizontalLines(numLocations, interLocationSpace, g2d);
			drawDiary(g2d);
		}

	}

	protected void drawDiary(Graphics2D g2d) {

		Stroke oldStroke = g2d.getStroke();
		Color oldColor = g2d.getColor();
		Shape oldClip = g2d.getClip();
		Rectangle clip = new Rectangle(
				getWidth()-getInsets().left-getInsets().right-maxLabelWidth, 
				getHeight()-getInsets().top-getInsets().bottom);
		clip.x = maxLabelWidth+getInsets().left;
		clip.y = getInsets().top;
		g2d.setClip(clip);
		
		Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0);
		g2d.setStroke(drawingStroke);

		AffineTransform afOld = g2d.getTransform();
		AffineTransform af = AffineTransform.getTranslateInstance(
				maxLabelWidth, 0);
		g2d.setTransform(af);

		// long allPeriod = (maxT-minT)+60*60*1000;
		long vWidth = getWidth() - getInsets().left - getInsets().right
				- maxLabelWidth;

		int lastLocation = -1;
		Movement lastMovement = null;

		for (Movement mov : p.getMovements()) {
			drawMovement(g2d, vWidth, mov);
			if (lastMovement != null) {
				drawStop(g2d, vWidth, lastMovement, mov);
			}

			lastLocation = mov.stopLocation.getLid();
			lastMovement = mov;
		}

		for (Movement mov : timeRanges) {
			drawTimeRange(g2d, vWidth, mov);
		}

		g2d.setStroke(oldStroke);
		g2d.setTransform(afOld);
		g2d.setColor(oldColor);
		g2d.setClip(oldClip);

	}

	protected void drawStop(Graphics2D g2d, long vWidth, Movement last,
			Movement mov) {
		long start = last.endMovement.getTimeInMillis();
		long end = mov.startMovement.getTimeInMillis();

		int x1 = (int) (1.0d * (start - minT) * vWidth / allPeriod) + 2;
		int x2 = (int) (1.0d * (end - minT) * vWidth / allPeriod) - 2;

		int y1 = mapLocationPosition.get(last.stopLocation.getLid());
		int y2 = mapLocationPosition.get(mov.startLocation.getLid());

		int at = mov.activityType;
		Color c = Color.darkGray;
		if(y1!=y2)
			c = Color.red.brighter();
		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(4));

		g2d.setColor(c);

		g2d.drawLine(x1, y1, x2, y2);
		g2d.fillOval(x1 - 2, y1 - 2, 5, 5);
		g2d.fillOval(x2 - 2, y2 - 2, 5, 5);
		// g2d.drawString(mov.activityType+"", x2+3, y2-3);
		g2d.setStroke(oldStroke);

	}

	/**
	 * @param g2d
	 * @param vWidth
	 * @param mov
	 */
	public void drawMovement(Graphics2D g2d, long vWidth, Movement mov) {
		long start = mov.startMovement.getTimeInMillis();
		long end = mov.endMovement.getTimeInMillis();

		int x1 = (int) (1.0d * (start - minT) * vWidth / allPeriod);
		int x2 = (int) (1.0d * (end - minT) * vWidth / allPeriod);

		int y1 = mapLocationPosition.get(mov.startLocation.getLid());
		int y2 = mapLocationPosition.get(mov.stopLocation.getLid());

		int at = mov.activityType;
		Color c = ActivityColors.getColor(at);

		Stroke oldStroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke(4));
		if(p.isSelected()&&p.getSelectedMovement().equals(mov)){
			g2d.setColor(Color.yellow);
		}else{
			g2d.setColor(c);
		}
		
		g2d.drawLine(x1, y1, x2, y2);
		g2d.fillOval(x1 - 2, y1 - 2, 5, 5);
		g2d.fillOval(x2 - 2, y2 - 2, 5, 5);
		g2d.drawString(mov.activityType + "", x2 + 3, y2 - 3);
		g2d.setStroke(oldStroke);
	}

	public void drawTimeRange(Graphics2D g2d, long vWidth, Movement mov) {
		long start = mov.startMovement.getTimeInMillis();
		long end = mov.endMovement.getTimeInMillis();

		int x1 = (int) (1.0d * (start - minT) * vWidth / allPeriod);
		int x2 = (int) (1.0d * (end - minT) * vWidth / allPeriod);

		if (mov.activityType < 0)
			g2d.setColor(new Color(1.0f, 0.0f, 0.0f, 0.5f));
		else
			g2d.setColor(new Color(0.0f, 1.0f, 0.0f, 0.5f));

		g2d.fillRect(x1, getInsets().top, x2 - x1, getHeight()
				- getInsets().bottom - getInsets().top);

		// g2d.drawLine(x1, y1, x2, y2);
	}

	// protected double getHourOfPeriod(Calendar c) {
	// int h = c.get(Calendar.HOUR_OF_DAY);
	// int m = c.get(Calendar.MINUTE);
	//
	// return h + 1.0d*m/60.0d;
	// }

	protected void drawHorizontalLines(int numLocations,
			int interLocationSpace, Graphics2D g2d) {
		Insets insets = getInsets();
		FontMetrics fm = g2d.getFontMetrics();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd (EEE)");

		int y = interLocationSpace / 2;
		if (y < 25)
			y = 25;
		y += insets.top;

		int fh = fm.getHeight();
		for (Integer l : p.getLocations().keySet()) {
			g2d.drawLine(insets.left, y, getSize().width - insets.right, y);

			Location loc = p.getLocations().get(l);
			if (loc instanceof ActivityLocation) {
				String label = ((ActivityLocation) loc).activityType;
				g2d.drawString("" + l, insets.left, y - 2 - 2 - fh);
				g2d.drawString(label, insets.left, y - 2);
			} else if (p instanceof GeoDiary) {
				GeoDiary gd = (GeoDiary) p;
				g2d.drawString("Location: " + l, insets.left, y - 2);
			} else {
				g2d.drawString("" + l, insets.left, y - 2);
			}

			int lw = fm.stringWidth("Location: " + l);
			if (lw > maxLabelWidth)
				maxLabelWidth = lw;

			mapLocationPosition.put(l, y);

			y += interLocationSpace;
		}

		AffineTransform oldAf = g2d.getTransform();
		AffineTransform af = AffineTransform.getTranslateInstance(
				maxLabelWidth, 0);
		// g2d.setTransform(af);

		// trunc minimum start time to hour
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(minT);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.MILLISECOND, 0);

		// trunc maximum end time to floor hour
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(maxT);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.MILLISECOND, 0);
		end.add(Calendar.HOUR, 1);

		Color oldC = g2d.getColor();

		long vWidth = getWidth() - getInsets().left - getInsets().right
				- maxLabelWidth;

		Stroke oldStroke = g2d.getStroke();
		boolean drawnFirstLine = false;
		int hw = fm.stringWidth("00");
		int dw = fm.stringWidth("yyyy-MMM-dd (EEE)");
		int hp = (int) (3600000.0*vWidth/allPeriod);
		int dp = (int) (1000.0*60*60*24*vWidth/allPeriod);
		while (start.compareTo(end) <= 0) { // while start has not reached end
			int x = (int) (1.0d * (start.getTimeInMillis() - minT) * vWidth / allPeriod)
					+ maxLabelWidth;
			int h = start.get(Calendar.HOUR_OF_DAY);
			if (h % 6 == 0)
				g2d.setColor(Color.lightGray);
			else
				g2d.setColor(Color.lightGray);

			if (h == 0)
				g2d.setStroke(new BasicStroke(4));
			else if (h % 12 == 0)
				g2d.setStroke(new BasicStroke(2));
			else
				g2d.setStroke(oldStroke);

			if((hw<=hp)||(h==0)||((dw <= dp)&&(h==12))){
				g2d.drawLine(x + insets.left, getInsets().top, x + insets.left,
						getHeight() - getInsets().bottom);
				g2d.drawString("" + h, x + insets.left + 2, insets.top + 5);
			}
			
			
			if ((!drawnFirstLine) || ((h == 0)&&(dw <= dp))) {
				// draw the day
				g2d.setColor(Color.BLACK);
				// int fh = fm.getHeight();
				if(dw<=dp){
				g2d.drawString(sdf.format(start.getTime()),
						x + insets.left + 5, fh + 3 + insets.top);
				}else{
					g2d.drawString(start.get(Calendar.DAY_OF_MONTH)+"/"+Calendar.MONTH,
							x + insets.left + 5, fh + 3 + insets.top);
				}
				drawnFirstLine = true;
			}
			if((h==0) && (dw>dp)){
				g2d.setColor(Color.BLACK);
				g2d.drawString(start.get(Calendar.DAY_OF_MONTH)+"/"+Calendar.MONTH,
						x + insets.left + 5, fh + 3 + insets.top);
			}

			start.add(Calendar.HOUR_OF_DAY, 1);
		}

		g2d.setColor(oldC);
		g2d.setTransform(oldAf);
		g2d.setStroke(oldStroke);

	}

	public long getTimeForPoint(Point p2) {
		int x = p2.x - maxLabelWidth;
		long vWidth = getWidth() - getInsets().left - getInsets().right
				- maxLabelWidth;
		// int x1 = (int) (1.0d*(start-minT)*vWidth/allPeriod);
		long t1 = allPeriod * x / vWidth + minT;

		return t1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			// if double click zoom all
			resetTemporalBoundaryToDiary();
		}

		if (e.getClickCount() == 1) {
			// select the movement corresponding to the temporal position
			long t = getTimeForPoint(e.getPoint());
			Movement m = p.getMovementAt(t);
			
			if (m != null){
//				p.clearSelection();
				p.setSelectedObject(m);
				//firePropertyChange("Selected movement", null, m);
			}else{
				p.clearSelection();
				//firePropertyChange("Clear selection", null, null);
			}
		}
		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Cursor c1 = new Cursor(Cursor.HAND_CURSOR);
		setCursor(c1);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		Cursor c2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(c2);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		t1 = -1;

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point p = e.getPoint();
		long t = getTimeForPoint(p);
		if (t1 == -1) {
			t1 = t;
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else {
			long offset = t - t1;
			setTemporalBoundary(minT - offset, maxT - offset);
			repaint(200);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();

		zoomInOut(notches);
	}


	/**
	 * @param notches
	 */
	protected void zoomInOut(int notches) {
		long span = maxT-minT;
		long newSpan = (long) (-span*(notches*0.05));
		
		//long pos = getTimeForPoint(e.getPoint());
		long newMinT = minT + newSpan;
		long newMaxT = maxT - newSpan;
		if (newMinT < newMaxT)
			setTemporalBoundary(newMinT, newMaxT);
		repaint(200);
	}
}
