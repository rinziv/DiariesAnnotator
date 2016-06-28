package viz;

import it.cnr.isti.kdd.diaries.ActivityLocation;
import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.lorenzo.dbmanager.ConnectionManager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class VisualizeDiary {
	
	private static Logger log = Logger.getLogger(VisualizeDiary.class);

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws SQLException {
		VisualizeDiary vd = new VisualizeDiary();
		vd.show();
	}

	protected void show() throws SQLException {
		JFrame frame = new JFrame("Diary Visualization");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String pid = "HH211237GL226828";
		pid = "HH210741GL225651";
		
//				pid = "HH217658GL241790";
//				pid = "HH121412GL135443";
//				pid = "HH127728GL151354";
//				pid = "HH215602GL237035";
				pid = "HH209226GL221912";
//				pid = "HH141837GL186579";
//				pid = "HH211154GL226627";
//				pid = "HH122151GL137256";
//				pid = "HH127453GL150569";
//				pid = "HH124344GL142788";
//				pid = "HH127976GL152008";
//				pid = "HH125111GL144704";
//				pid = "HH211471GL227356";
//				pid = "HH121933GL136787";
//				pid = "HH213763GL232752";
//				pid = "HH17096GL40571";
//				pid = "HH216828GL239943";
//				pid = "HH208315GL219749";
//				pid = "HH8796GL20812";
//				pid = "HH218411GL243676";
//				pid = "HH125094GL144670";

	
		//pid = "";
		
		frame.setTitle("Diary Visualization - "+pid);
		final DiaryPanel gpgps = new DiaryPanel(createDiaryFromGPS(pid));
		final DiaryPanel dp = new DiaryPanel(createDiary(pid));
		final long minT = Math.min(dp.minT,gpgps.minT);
		final long maxT = Math.max(dp.maxT,gpgps.maxT);
		
		
		
		loadTimeRanges(dp, pid);

		dp.setTemporalBoundary(minT, maxT);
		gpgps.setTemporalBoundary(minT, maxT);
		
		dp.addMouseListener(new MouseAdapter() {
			
			long t1 = -1;
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				t1 = dp.getTimeForPoint(p);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				Point p = e.getPoint();
				long t2 = dp.getTimeForPoint(p);
				log.debug("Selected interval "+ t1 + "-" + t2);
				if(t1<=t2){
					dp.setTemporalBoundary(t1, t2);
					gpgps.setTemporalBoundary(t1, t2);
				}else{
					dp.setTemporalBoundary(minT, maxT);
					gpgps.setTemporalBoundary(minT, maxT);
				}
					
					//dp.resetTemporalBoundaryToDiary(1000*60*60);
				
				dp.repaint();
				gpgps.repaint();
				t1=-1;
				
			}
		});

		// dp.maxT-= (dp.maxT-dp.minT)/2;
		// dp.minT -= 1000*60*60*12*2;
		// dp.resetTemporalBoundaryToDiary(2*60*60*1000);
		// dp.setTemporalBoundary(dp.minT+1000*60*60*24*5, dp.maxT);
		Container cont = frame.getContentPane();
		cont.setLayout(new BorderLayout());

		JPanel timePanel =  new JPanel();
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
		gpgps.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5), 
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("GPS"), 
						BorderFactory.createEmptyBorder(5, 5, 5, 5))));
		dp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5), 
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Diary"), 
						BorderFactory.createEmptyBorder(5, 5, 5, 5))));
		
		
		timePanel.add(gpgps);
		timePanel.add(dp);
		
		
		cont.add(timePanel, BorderLayout.CENTER);

		// frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		timePanel.repaint();
	}

	protected Diary createDiary() {
		Diary d = new Diary();

		d.setPersonId("HH217658GL241790");
//		d.addMovement(new Movement(d, 44606, 44608,
//				getCalendar("2008-02-05 10:25:00+01"),
//				getCalendar("2008-02-05 10:50:00+01"), 20, 1));
//		d.addMovement(new Movement(d, 44608, 44609,
//				getCalendar("2008-02-05 10:55:00+01"),
//				getCalendar("2008-02-05 11:00:00+01"), 17, 1));
//		d.addMovement(new Movement(d, 44609, 44606,
//				getCalendar("2008-02-05 11:30:00+01"),
//				getCalendar("2008-02-05 11:40:00+01"), 0, 1));
//		d.addMovement(new Movement(d, 44620, 44611,
//				getCalendar("2008-02-07 09:40:00+01"),
//				getCalendar("2008-02-07 09:50:00+01"), 20, 2));
//		d.addMovement(new Movement(d, 44611, 44612,
//				getCalendar("2008-02-07 10:00:00+01"),
//				getCalendar("2008-02-07 10:05:00+01"), 20, 2));
//		d.addMovement(new Movement(d, 44612, 44613,
//				getCalendar("2008-02-07 10:35:00+01"),
//				getCalendar("2008-02-07 10:50:00+01"), 20, 3));
//		d.addMovement(new Movement(d, 44613, 44606,
//				getCalendar("2008-02-07 11:25:00+01"),
//				getCalendar("2008-02-07 11:40:00+01"), 0, 3));
//		d.addMovement(new Movement(d, 44620, 44614,
//				getCalendar("2008-02-08 09:55:00+01"),
//				getCalendar("2008-02-08 10:15:00+01"), 19, 1));
//		d.addMovement(new Movement(d, 44614, 44606,
//				getCalendar("2008-02-08 10:50:00+01"),
//				getCalendar("2008-02-08 11:00:00+01"), 0, 1));
//		d.addMovement(new Movement(d, 44620, 44615,
//				getCalendar("2008-02-08 14:00:00+01"),
//				getCalendar("2008-02-08 14:05:00+01"), 20, 1));
//		d.addMovement(new Movement(d, 44615, 44606,
//				getCalendar("2008-02-08 16:00:00+01"),
//				getCalendar("2008-02-08 16:05:00+01"), 0, 1));
//		d.addMovement(new Movement(d, 44617, 44617,
//				getCalendar("2008-02-10 09:05:00+01"),
//				getCalendar("2008-02-10 09:10:00+01"), 19, 1));
//		d.addMovement(new Movement(d, 44617, 44606,
//				getCalendar("2008-02-10 09:25:00+01"),
//				getCalendar("2008-02-10 09:30:00+01"), 0, 1));
//		d.addMovement(new Movement(d, 44618, 44606,
//				getCalendar("2008-02-11 13:25:00+01"),
//				getCalendar("2008-02-11 13:35:00+01"), 0, 1));
//		d.addMovement(new Movement(d, 44619, 44619,
//				getCalendar("2008-02-11 18:05:00+01"),
//				getCalendar("2008-02-11 18:20:00+01"), 21, 3));
//		d.addMovement(new Movement(d, 44619, 44606,
//				getCalendar("2008-02-11 21:30:00+01"),
//				getCalendar("2008-02-11 21:45:00+01"), 0, 3));

		return d;
	}

	protected Diary createDiary(String pid) throws SQLException {
		Diary d = new Diary();
		d.setPersonId(pid);

		Connection conn = ConnectionManager.getConnection();
		String sql = "SELECT start_loc_id, end_loc_id, "
				+ "start_movement - interval '1 second'*extract(timezone from start_movement) start_movement, "
				+ "end_movement - interval '1 second'*extract(timezone from end_movement) end_movement, "
				+ "activity_type, activity_category "
				+ "FROM rawdata.diaries where person_id=? "
				+ "order by person_id, start_movement";
		
		
		// sql schema to load:
		// start_loc_id: integer
		// 
		
		sql="SELECT start_loc_id, lt1.label start_label_id, lt2.label end_label_id,end_loc_id, " +
				"start_movement - interval '1 second'*extract(timezone from start_movement) start_movement, " +
				"end_movement - interval '1 second'*extract(timezone from end_movement) end_movement," +
				"d.activity_type, at.label activity_label, activity_category " +
				"FROM rawdata.diaries d, rawdata.activity_type_info at, rawdata.locations l1,rawdata.locations l2," +
				" rawdata.location_type_info lt1, rawdata.location_type_info lt2 " +
				"where d.activity_type = at.activity_type and l1.location_id = start_loc_id and l2.location_id=end_loc_id and " +
				"lt1.location_category=l1.location_type_id and lt2.location_category=l2.location_type_id and d.person_id=? " +
				"order by d.person_id, start_movement";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, pid);

		ResultSet rs = pstmt.executeQuery();
		TreeMap<Integer, ActivityLocation> locations = new TreeMap<Integer, ActivityLocation>();

		while (rs.next()) {
			int startLoc = rs.getInt("start_loc_id");
			int endLoc = rs.getInt("end_loc_id");
			Calendar startCal = getCalendar(rs.getTimestamp("start_movement"));
			Calendar endCal = getCalendar(rs.getTimestamp("end_movement"));
			int activityType = rs.getInt("activity_type");
			int activityCategory = rs.getInt("activity_category");
			String startLocationLabel = rs.getString("start_label_id");
			String endLocationLabel = rs.getString("end_label_id");
			
			ActivityLocation start = getActivityLocation(startLoc, startLocationLabel,locations);
			ActivityLocation end = getActivityLocation(endLoc, endLocationLabel, locations);
			
			
			Movement m = new Movement(d, start, end, startCal, endCal,
					activityType, activityCategory,-1);
			log.debug("   :" + m);
			d.addMovement(m);
		}

		return d;
	}
	
	
	protected ActivityLocation getActivityLocation(int locId, String locationLabel, 
			TreeMap<Integer, ActivityLocation> locations) {
		ActivityLocation l = locations.get(locId);
		if(l==null){
			l = new ActivityLocation(locId, -1);
			l.activityType=locationLabel;
			locations.put(locId, l);
		}
		if(!l.activityType.equals(locationLabel)){
			System.err.println("Label conflict: "+l.getLid()+" "+ l.activityType+ "<->"+locationLabel);
		}
		
		return l;
	}

	protected Diary createDiaryFromGPS(String pid) throws SQLException{
		Diary d = new Diary();
		d.setPersonId(pid);

		Connection conn = ConnectionManager.getConnection();
		
		String sql = "select t.uid, t.tid, cf.cid start_loc_id, ct.cid end_loc_id, " +
				"t.time_start start_movement, " +
				"t.time_start + interval '1 second'*t.duration end_movement, " +
				"t.activity_type, 0 activity_category " +
				"FROM " +
				"mp.manual_and_gps_trajs_45_30 t, " +
				"circles.cirlces_manual_and_gps_trajs_45_30_p cf, " +
				"circles.cirlces_manual_and_gps_trajs_45_30_p ct " +
				"where t.uid=cf.uid and t.uid=ct.uid and " +
				"t.tid=cf.tid and cf.isfirst and " +
				"t.tid = ct.tid and not ct.isfirst and " +
				"get_token(pid,1,'_')=? " +
				"order by time_start";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, pid);

		log.info(sql);
		ResultSet rs = pstmt.executeQuery();
		TreeMap<Integer, Location> locations =  new TreeMap<Integer, Location>();

		while (rs.next()) {
			int startLoc = rs.getInt("start_loc_id");
			int endLoc = rs.getInt("end_loc_id");
			Calendar startCal = getCalendar(rs.getTimestamp("start_movement"));
			Calendar endCal = getCalendar(rs.getTimestamp("end_movement"));
			int activityType = rs.getInt("activity_type");
			int activityCategory = rs.getInt("activity_category");
			int tid = rs.getInt("tid");
			
			Location start = getLocation(startLoc, locations);
			Location end = getLocation(endLoc, locations);
			
			
			Movement m = new Movement(d, start, end, startCal, endCal,
					activityType, activityCategory,tid);
			log.debug("GPS: "+m);
			d.addMovement(m);
		}

		return d;
	}
	
	protected Location getLocation(int locId,TreeMap<Integer, Location> locations) {
		Location l = locations.get(locId);
		if(l==null){
			l = new Location(locId, -1);
			locations.put(locId, l);
		}
		
		return l;
	}
	
	

	protected void loadTimeRanges(DiaryPanel dp, String string)
			throws SQLException {
		Connection conn = ConnectionManager.getConnection();

		String sql = " select time_start, time_start+interval'1 second' * duration as time_end, activity_type from mp.manual_and_gps_trajs_45_30 where get_token(pid,1,'_') = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, string);

		ResultSet rs = pstmt.executeQuery();

		Location dummyLocation = new Location(0, 0);
		while (rs.next()) {
			Calendar startCal = getCalendar(rs.getTimestamp("time_start"));
			Calendar endCal = getCalendar(rs.getTimestamp("time_end"));
			int activityType = rs.getInt("activity_type");
			Movement m = new Movement(dp.p, dummyLocation, dummyLocation, startCal, endCal, activityType, 0,-1);
//			System.err.println(m);
			dp.timeRanges.add(m);
		}

	}

	private Calendar getCalendar(Timestamp timestamp) {
		Calendar c = Calendar.getInstance();
		c.setTime(timestamp);
		return c;
	}

	protected Calendar getCalendar(String cal) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(cal));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c;
	}

	// Regular expressio conversion:
	// Find: ([0-9]+);([0-9]+);("[^"]+");("[^"]+");([0-9]+);([0-9]+)
	// Replace: d.addMovement(new Movement(d, $1, $2,
	// getCalendar($3),getCalendar($4), $5,$6));
	// 44606;44608;"2008-02-05 10:25:00+01";"2008-02-05 10:50:00+01";20;1
	// 44608;44609;"2008-02-05 10:55:00+01";"2008-02-05 11:00:00+01";17;1
	// 44609;44606;"2008-02-05 11:30:00+01";"2008-02-05 11:40:00+01";0;1
	// 44620;44611;"2008-02-07 09:40:00+01";"2008-02-07 09:50:00+01";20;2
	// 44611;44612;"2008-02-07 10:00:00+01";"2008-02-07 10:05:00+01";20;2
	// 44612;44613;"2008-02-07 10:35:00+01";"2008-02-07 10:50:00+01";20;3
	// 44613;44606;"2008-02-07 11:25:00+01";"2008-02-07 11:40:00+01";0;3
	// 44620;44614;"2008-02-08 09:55:00+01";"2008-02-08 10:15:00+01";19;1
	// 44614;44606;"2008-02-08 10:50:00+01";"2008-02-08 11:00:00+01";0;1
	// 44620;44615;"2008-02-08 14:00:00+01";"2008-02-08 14:05:00+01";20;1
	// 44615;44606;"2008-02-08 16:00:00+01";"2008-02-08 16:05:00+01";0;1
	// 44617;44617;"2008-02-10 09:05:00+01";"2008-02-10 09:10:00+01";19;1
	// 44617;44606;"2008-02-10 09:25:00+01";"2008-02-10 09:30:00+01";0;1
	// 44618;44606;"2008-02-11 13:25:00+01";"2008-02-11 13:35:00+01";0;1
	// 44619;44619;"2008-02-11 18:05:00+01";"2008-02-11 18:20:00+01";21;3
	// 44619;44606;"2008-02-11 21:30:00+01";"2008-02-11 21:45:00+01";0;3

	// "HH217658GL241790";44606;44608;"05-02-08";"2008-02-05 10:25:00+01";"2008-02-05 10:50:00+01";"6 days 11:20:00";0.2;20;1;17567
	// "HH217658GL241790";44608;44609;"05-02-08";"2008-02-05 10:55:00+01";"2008-02-05 11:00:00+01";"6 days 11:20:00";0.1;17;1;17567
	// "HH217658GL241790";44609;44606;"05-02-08";"2008-02-05 11:30:00+01";"2008-02-05 11:40:00+01";"6 days 11:20:00";0.2;0;1;17567
	// "HH217658GL241790";44620;44611;"07-02-08";"2008-02-07 09:40:00+01";"2008-02-07 09:50:00+01";"6 days 11:20:00";0.3;20;2;17569
	// "HH217658GL241790";44611;44612;"07-02-08";"2008-02-07 10:00:00+01";"2008-02-07 10:05:00+01";"6 days 11:20:00";0.3;20;2;17569
	// "HH217658GL241790";44612;44613;"07-02-08";"2008-02-07 10:35:00+01";"2008-02-07 10:50:00+01";"6 days 11:20:00";0.7;20;3;17569
	// "HH217658GL241790";44613;44606;"07-02-08";"2008-02-07 11:25:00+01";"2008-02-07 11:40:00+01";"6 days 11:20:00";0.7;0;3;17569
	// "HH217658GL241790";44620;44614;"08-02-08";"2008-02-08 09:55:00+01";"2008-02-08 10:15:00+01";"6 days 11:20:00";0.2;19;1;17570
	// "HH217658GL241790";44614;44606;"08-02-08";"2008-02-08 10:50:00+01";"2008-02-08 11:00:00+01";"6 days 11:20:00";0.2;0;1;17570
	// "HH217658GL241790";44620;44615;"08-02-08";"2008-02-08 14:00:00+01";"2008-02-08 14:05:00+01";"6 days 11:20:00";0.1;20;1;17570
	// "HH217658GL241790";44615;44606;"08-02-08";"2008-02-08 16:00:00+01";"2008-02-08 16:05:00+01";"6 days 11:20:00";0.1;0;1;17570
	// "HH217658GL241790";44617;44617;"10-02-08";"2008-02-10 09:05:00+01";"2008-02-10 09:10:00+01";"6 days 11:20:00";0.2;19;1;17572
	// "HH217658GL241790";44617;44606;"10-02-08";"2008-02-10 09:25:00+01";"2008-02-10 09:30:00+01";"6 days 11:20:00";0.2;0;1;17572
	// "HH217658GL241790";44618;44606;"11-02-08";"2008-02-11 13:25:00+01";"2008-02-11 13:35:00+01";"6 days 11:20:00";0.2;0;1;17573
	// "HH217658GL241790";44619;44619;"11-02-08";"2008-02-11 18:05:00+01";"2008-02-11 18:20:00+01";"6 days 11:20:00";1;21;3;17573
	// "HH217658GL241790";44619;44606;"11-02-08";"2008-02-11 21:30:00+01";"2008-02-11 21:45:00+01";"6 days 11:20:00";1;0;3;17573

}
