package viz;

import it.cnr.isti.kdd.diaries.ActivityLocation;
import it.cnr.isti.kdd.diaries.DiariesExtractor;
import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.lorenzo.dbmanager.ConnectionManager;
import it.cnr.isti.kdd.sax.mbmodel.Circle;
import it.cnr.isti.kdd.sax.mbmodel.PointOfTrajectory;
import it.cnr.isti.kdd.sax.mbmodel.SQLTrajectoryIterator;
import it.cnr.isti.kdd.sax.mbmodel.Trajectory;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import utils.ExtensionFileFilter;
import utils.GPXManipulator;
import utils.TrajectoryAnnotation;
import utils.TrajectoryFromPointsIterator;
import br.ufsc.core.TPoint;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class AnimateOctoDiary {
	private Logger log = Logger.getLogger(AnimateOctoDiary.class);
	protected JFrame frame = null;

	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected int circleId = 0;

	protected File[] gpxFiles;
//	public static List<TrajectoryAnnotation> trajAn = new ArrayList<TrajectoryAnnotation>();
	private GPXManipulator gpxMan = null;
	protected DiaryPanel gpgps;
	protected GeoPanel geoGPS;
	private double maxRadius = 0.0050; // 0.0045; //~500m
	private double minRadius = 0.0003;
	protected br.ufsc.core.Trajectory mainTrajectory = new br.ufsc.core.Trajectory(-1);

	
	String[] options = {"Points","Trajectories"};
	protected JList lstPointsOrTrajectories = new JList(options);
	protected JTextArea txtQuery = new JTextArea();
	protected MovementEditorPanel mep;
	
	
	public double getMaxRadius() {
		return maxRadius;
	}

	public void setMaxRadius(double maxRadius) {
		this.maxRadius = maxRadius;
	}

	public double getMinRadius() {
		return minRadius;
	}

	public void setMinRadius(double minRadius) {
		this.minRadius = minRadius;
	}

	public static void main(String[] args) throws SQLException {
		AnimateOctoDiary vd = new AnimateOctoDiary();
		vd.show();

		// JFileChooser jfcGpx = new JFileChooser();
		// jfcGpx.setDialogType(JFileChooser.OPEN_DIALOG);
		// jfcGpx.setDialogTitle("Open gpx");
		// FileFilter filter = new ExtensionFileFilter("gpx", "Gpx file");
		// jfcGpx.setFileFilter(filter);
		// jfcGpx.setMultiSelectionEnabled(true);
		// int value = jfcGpx.showDialog(null,null);
		// if(value==JFileChooser.APPROVE_OPTION){
		// File[] gpxFiles = jfcGpx.getSelectedFiles();
		// try {
		// vd.show2(gpxFiles);
		// } catch (SQLException e1) {
		// e1.printStackTrace();
		// }
		// }else{
		// vd.show();
		// }

	}
	
	/**
	 * @param e
	 * @param m
	 * @throws HeadlessException
	 */
	protected void manageUnsavedEdits(PropertyChangeEvent e,
			Movement m) throws HeadlessException {
		// Do you want to save current changes?
		int n = JOptionPane.showConfirmDialog((Component) e.getSource(),
				"There unsaved edits on the panel. Do you want to save them "+"?",
				"Propagate current activity?", JOptionPane.YES_NO_OPTION);

		if (n == JOptionPane.YES_OPTION) {
			log.debug("Saving current edits before changing selection to " + m);
			mep.saveAnnotation();
		}
	}

	protected void show() throws SQLException {
		frame = new JFrame("Diary Visualization");
		this.createMenu();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String pid = "HH211237GL226828";
		pid = "HH210741GL225651";

		// pid = "HH217658GL241790";
		// pid = "HH121412GL135443";
		// pid = "HH127728GL151354";
		// pid = "HH215602GL237035";
		// pid = "HH209226GL221912";
		// pid = "HH141837GL186579";
		// pid = "HH211154GL226627";
		// pid = "HH122151GL137256";
		// pid = "HH127453GL150569";
		// pid = "HH124344GL142788";
		// pid = "HH127976GL152008";
		// pid = "HH125111GL144704";
		pid = "HH211471GL227356";
		// pid = "HH121933GL136787";
		// pid = "HH213763GL232752";
		// pid = "HH17096GL40571";
		// pid = "HH216828GL239943";
		// pid = "HH208315GL219749";
		// pid = "HH8796GL20812";
		// pid = "HH218411GL243676";
		// pid = "HH125094GL144670";

		// pid = "";

		frame.setTitle("DayTag");
		gpgps = new DiaryPanel(new GeoDiary());
		// final DiaryPanel dp = new DiaryPanel(createDiaryFromGPS(pid));
		long minT = Math.min(gpgps.minT, gpgps.minT);
		long maxT = Math.max(gpgps.maxT, gpgps.maxT);

		gpgps.addPropertyChangeListener("Selected movement",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						Movement m = (Movement) e.getNewValue();
						// System.out.println("Selected new movement " + m);

						if(mep.hasUnsavedChanges()){
							manageUnsavedEdits(e, m);
						}
						
						mep.setMovement(m);
						mep.repaint();
						mep.revalidate();
//						createMoveAnotationPanel(m);
					}

				});
		
		gpgps.addPropertyChangeListener("Clear selection", new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(mep.hasUnsavedChanges()){
					manageUnsavedEdits(evt, mep.mov);
				}
				
				mep.clearSelection();
				mep.repaint();
				mep.revalidate();
				
			}
		});

		GeoDiary geoDiary = (GeoDiary) gpgps.p;
		// if(geoDiary.positions.size()>0){
		// com.vividsolutions.jts.geom.Point p1 =
		// geoDiary.positions.firstEntry().getValue();
		// System.out.println("Point: " + p1);
		// }
		JPanel geoPanel = new JPanel();
		geoPanel.setLayout(new BoxLayout(geoPanel, BoxLayout.Y_AXIS));
		geoGPS = new GeoPanel(geoDiary);
//		geoGPS.setMinimumSize(new Dimension(700, 500));

		gpgps.setTemporalBoundary(minT, maxT);

		// dp.maxT-= (dp.maxT-dp.minT)/2;
		// dp.minT -= 1000*60*60*12*2;
		// dp.resetTemporalBoundaryToDiary(2*60*60*1000);
		// dp.setTemporalBoundary(dp.minT+1000*60*60*24*5, dp.maxT);
		Container cont = frame.getContentPane();
		cont.setLayout(new BorderLayout());

		gpgps.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5),
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("GPS"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5))));
		// dp.setBorder(BorderFactory.createCompoundBorder(
		// BorderFactory.createEmptyBorder(5, 5, 5, 5),
		// BorderFactory.createCompoundBorder(
		// BorderFactory.createTitledBorder("Diary"),
		// BorderFactory.createEmptyBorder(5, 5, 5, 5))));
		//

		// timePanel.add(dp);

//		JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		geoGPS.getOverlayPanel().setVisible(false);
//		mainPanel.add(gpgps);
//		mainPanel.add(geoGPS);

//		mainPanel.setLayout(new GridBagLayout());
//		
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
//		gbc.fill = GridBagConstraints.BOTH;
//		gbc.weightx = 0.7;
//		gbc.weighty = 0.5;
//		gbc.insets = new Insets(3, 3, 3, 3);
//		
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		mainPanel.add(gpgps,gbc);
//		
//		gbc.gridy = 1;
//		gbc.weighty = 0.4;
//		mainPanel.add(geoGPS,gbc);
		
		
		JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gpgps, geoGPS);
		mainPanel.setDividerLocation(0.5);

		
		mep = new MovementEditorPanel(geoDiary);
//		gbc.gridx = 1;
//		gbc.gridy = 0;
//		gbc.gridheight = 2;
//		gbc.weightx = 0.3;
//		gbc.anchor = GridBagConstraints.FIRST_LINE_END;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
//		mainPanel.add(mep, gbc);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainPanel, mep);
	
		JToolBar tb = new JToolBar(JToolBar.HORIZONTAL);
		JButton btnZoomIn = new JButton("+");
		JButton btnZoomOut = new JButton("-");
		btnZoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gpgps.zoomInOut(-3);
			}
		});
		btnZoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gpgps.zoomInOut(3);
			}
		});
		tb.add(btnZoomIn);
		tb.add(btnZoomOut);
		
		
		cont.add(sp, BorderLayout.CENTER);
		cont.add(tb, BorderLayout.NORTH);
		// cont.add(geoPanel, BorderLayout.EAST);

		// frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
		// timePanel.repaint();
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuEdit = new JMenu("Edit");
		JMenuItem menuLoadGpx = new JMenuItem("Load gpx");
		final AnimateOctoDiary vz = this;

		menuLoadGpx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfcGpx = new JFileChooser();
				jfcGpx.setDialogType(JFileChooser.OPEN_DIALOG);
				jfcGpx.setDialogTitle("Open gpx");
				FileFilter filter = new ExtensionFileFilter("gpx", "Gpx file");
				jfcGpx.setFileFilter(filter);
				jfcGpx.setMultiSelectionEnabled(true);
				int value = jfcGpx.showDialog(frame, null);
				if (value == JFileChooser.APPROVE_OPTION) {
					gpxFiles = jfcGpx.getSelectedFiles();
					try {

						GeoDiary gd =  createAndAddTrajectoriesToDiary(gpxFiles,
								(GeoDiary) gpgps.p);
						gpgps.resetTemporalBoundaryToDiary();
						gpgps.repaint();
						
						gpgps.setDiary(gd);
						gpgps.resetTemporalBoundaryToDiary();
						gpgps.repaint();

						geoGPS.setDiary(gd);
						geoGPS.repaint();
						
						mep.setDiary(gd);

					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		JMenuItem mnuLoadTrajectoriesOCTO = new JMenuItem(
				"Load trajectories from DB");
		mnuLoadTrajectoriesOCTO.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// load dialogbox
					// get user input
					// check if points or trajectories
					// get the query to execute
					JComponent[] components = new JComponent[]{	
							
						new JLabel("Points of Trajectories?"),
						lstPointsOrTrajectories,
						new JLabel("Query:"),
						txtQuery
					};
					int uid = 93;
					uid = 149588;
					uid=136891;
					uid = 1071523; // octoscana pulito!!
					String sql = "select t.uid, t.tid, st_startpoint(t.object) fromP, st_endpoint(t.object) toP, "
							+ "t.time_start start_movement, "
							+ "t.time_start + interval '1 second'*t.duration end_movement, "
							+ "t.activity_type, 0 activity_category "
							+ "FROM "
							+ "mp.manual_and_gps_trajs_45_30 t, "
							+ "circles.cirlces_manual_and_gps_trajs_45_30_p cf, "
							+ "circles.cirlces_manual_and_gps_trajs_45_30_p ct "
							+ "where t.uid=cf.uid and t.uid=ct.uid and "
							+ "t.tid=cf.tid and cf.isfirst and "
							+ "t.tid = ct.tid and not ct.isfirst and "
							+ "get_token(pid,1,'_')=? " + "order by time_start";
					sql = "select uid, tid, asbinary(t.object), "
							+ "t.time_start start_movement, "
							+ "t.time_start + interval '1 second'*t.duration end_movement, "
							+ "activity_type " + "from mp.manual_and_gps_trajs_45_30 t "
							+ "where uid = " + uid + "  "
							+ "order by tid, time_start";
					
					
					
					sql = "SELECT uid, tid, asbinary(st_transform(the_traj,4326)), " +
							"time_start start_movement, " +
							"time_start+interval'1 second' * duration as end_movement   " +
							"FROM mp.octoscana_1200_50   " +
							"where uid = 1071523   " +
							"order by time_start asc";
					
					circleId = 0;
					QueryDialog qd = new QueryDialog(frame, true);
					qd.setQuery(sql);
					qd.setVisible(true);
					
					GeoDiary gd = null;
					if(qd.loadingPoints){
						gd = createLocationsAndDiaryFromGPSPoints("149588",qd.getQuery());
					}else{
						gd = createLocationsAndDiaryFromGPS("" + uid,qd.getQuery());
					}
					// location precise: 826214

					// int uid = 741239;
					// int uid = 826214;
					gpgps.setDiary(gd);
					gpgps.resetTemporalBoundaryToDiary();
					gpgps.repaint();

					geoGPS.setDiary(gd);
					geoGPS.repaint();
					
					mep.setDiary(gd);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		
		JMenuItem mnuLoadUserTrajectories = new JMenuItem(
				"Load trajectories by uid");
		mnuLoadUserTrajectories.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					// Start loading connection parameters
					Properties props = new Properties();
					FileInputStream in = null;
					in = new FileInputStream("database.properties");
					props.load(in);
					in.close();
					
					String sql = props.getProperty("diaryannotator.sql");
							
					circleId = 0;
					GeoDiary gd = null;
					// get uid number
					String strUid = JOptionPane.showInputDialog(null, "Enter user id: ", 
							"Insert User ID to load", JOptionPane.OK_CANCEL_OPTION);
					if(strUid!=null){
						System.out.println(sql);
						String sql2 = String.format(sql, strUid);
						System.out.println(sql2);
						frame.setTitle("DayTag: " + strUid);
						gd = createLocationsAndDiaryFromGPS(strUid,sql2);
						
						
					}
					// location precise: 826214

					// int uid = 741239;
					// int uid = 826214;
					gpgps.setDiary(gd);
					gpgps.resetTemporalBoundaryToDiary();
					gpgps.repaint();

					geoGPS.setDiary(gd);
					geoGPS.repaint();
					
					mep.setDiary(gd);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

//		JMenuItem mnuLoadPointsGPS = new JMenuItem("Load points from DB");
//		mnuLoadPointsGPS.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				try {
//					GeoDiary gd = createLocationsAndDiaryFromGPSPoints("149588");
//					
//					gpgps.p = gd;
//					gpgps.resetTemporalBoundaryToDiary();
//					gpgps.repaint();
//
//					geoGPS.setDiary(gd);
//					geoGPS.repaint();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		});

		
		// deprecated::: to be changed with the other one
		JMenuItem menuSaveDB = new JMenuItem("Save DB");
		menuSaveDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<TrajectoryAnnotation> trajAn = mep.trajAn;
				try {
					if (trajAn.size() > 0) {
//						trajAn.storeTrajectory(gpxMan   //TODO: check if this i really needed
//								.getFinalTrajectory());
						for (int i = 0; i < trajAn.size(); i++) {
							TrajectoryAnnotation ta = trajAn.get(i);
							ta.storeAnnotation();

						}

						JOptionPane.showMessageDialog(null, "Success!",
								"Success!", 1);
						gpgps.p = new GeoDiary();
						gpgps.resetTemporalBoundaryToDiary();
						gpgps.repaint();
					} else {
						JOptionPane.showMessageDialog(null, "No trajecoty!",
								"Fail!", 1);
					}

				} catch (Exception e1) {
					log.error(e1);
					JOptionPane.showMessageDialog(null, "Error", "Fail!", 1);
				}

			}
		});
		
		JMenuItem menuSaveFile = new JMenuItem("Save File");
		menuSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
//					jfcGpx.setDialogType(JFileChooser.OPEN_DIALOG);
//					jfcGpx.setDialogTitle("Open gpx");
					
					JFileChooser jfcCvs = new JFileChooser();
					FileFilter filter = new ExtensionFileFilter("csv", "CSV file");
					jfcCvs.setFileFilter(filter);
					
					int returnVal = jfcCvs.showSaveDialog(frame);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File fileCvs = jfcCvs.getSelectedFile();
						
						if(!fileCvs.getPath().toLowerCase().endsWith(".csv"))
						{
							fileCvs = new File(fileCvs.getPath() + ".csv");
						}
						List<TrajectoryAnnotation> trajAn = mep.trajAn;

						if (trajAn.size() > 0) {
	//						TrajectoryAnnotation.storeTrajectory(gpxMan
	//								.getFinalTrajectory());
							FileWriter fw = new FileWriter(fileCvs);
					        PrintWriter pw = new PrintWriter(fw);
							String file = "TID;TIMESTART;TIMEEND;TEMPERATURE;WEATHER;GOAL;TRANSPORTATION;PLACE FROM;PLACE TO;EVENT;USER;IS STOP";
							
							pw.println(file);
//							for (int i = 0; i < trajAn.size(); i++) {
//								TrajectoryAnnotation ta = trajAn.get(i);
//								ta.storeAnnotationFile(pw);
//							}
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
							DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							for (Movement m : gpgps.p.getMovements()) {
								TrajectoryAnnotation ta = m.getTa();
//								String line = m.getTrajectory().getTid()+";";
//								line += sdf.format(m.getStartMovement().getTime()) + ";";
//								line += sdf.format(m.getEndMovement().getTime()) + ";";
								if(ta!=null)
									ta.storeAnnotationFile(pw);
								else{
									String line= m.getTrajectory().getTid()+";"+
											dfTime.format(m.getStartMovement().getTime())+";"+
											dfTime.format(m.getEndMovement().getTime())+";"+
											"null;"+//temperature
											"null;"+// weather
											"null;"+// goal/activity
											"null;"+// transportation mode
											m.getStartLocation().getLid() + ";" +
											m.getStopLocation().getLid()+";"+
											"null;"+// event
											m.getTrajectory().getUid()+";"+// current user
											"0";
									
									pw.println(line);
								}
							}
							
							
							
							
							pw.flush();
					        pw.close();
					        fw.close(); 
	
							JOptionPane.showMessageDialog(null, "Success!",
									"Success!", 1);
//							gpgps.p = new GeoDiary();
//							gpgps.resetTemporalBoundaryToDiary();
//							gpgps.repaint();
						} else {
							JOptionPane.showMessageDialog(null, "No trajecoty!",
									"Fail!", 1);
						}
					}

				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error", "Fail!", 1);
				}

			}
		});

		JMenuItem menuStopValues = new JMenuItem("Stop Values");

		menuStopValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame jan = new JFrame();
				jan.setSize(350, 600);
//				ParametersPanel pp = new ParametersPanel(jan, vz);
//				
//				
//				jan.add(pp);
//				pp.fillFields();
				jan.show();
			}
		});
		
		
		JMenuItem mnuLoadAnnotations = new JMenuItem("Load Annotations");
		mnuLoadAnnotations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				try {
////					jfcGpx.setDialogType(JFileChooser.OPEN_DIALOG);
////					jfcGpx.setDialogTitle("Open gpx");
//					
//					JFileChooser jfcCvs = new JFileChooser();
//					FileFilter filter = new ExtensionFileFilter("csv", "CSV file");
//					jfcCvs.setFileFilter(filter);
//					jfcCvs.setDialogType(JFileChooser.OPEN_DIALOG);
//					
//					int returnVal = jfcCvs.showSaveDialog(frame);
//					if(returnVal == JFileChooser.APPROVE_OPTION) {
//						File fileCvs = jfcCvs.getSelectedFile();
//						
//						if(!fileCvs.getPath().toLowerCase().endsWith(".csv"))
//						{
//							fileCvs = new File(fileCvs.getPath() + ".csv");
//						}
//						
//						BufferedReader br = new BufferedReader(new FileReader(fileCvs));
//						String line = br.readLine();  // skip first line for header
//						while((line=br.readLine())!=null){
//							String[] fields = line.split(";");
//							int tid = Integer.parseInt(fields[0]);
//							Movement m = gpgps.p.getMovementByTid(tid);
//							
//							
//						}
//						
//						List<TrajectoryAnnotation> trajAn = mep.trajAn;
//
//						if (trajAn.size() > 0) {
//	//						TrajectoryAnnotation.storeTrajectory(gpxMan
//	//								.getFinalTrajectory());
//							FileWriter fw = new FileWriter(fileCvs);
//					        PrintWriter pw = new PrintWriter(fw);
//							String file = "TID;TIMESTART;TIMEEND;TEMPERATURE;WEATHER;GOAL;TRANSPORTATION;PLACE FROM;PLACE TO;EVENT;USER;IS STOP";
//							
//							pw.println(file);
////							for (int i = 0; i < trajAn.size(); i++) {
////								TrajectoryAnnotation ta = trajAn.get(i);
////								ta.storeAnnotationFile(pw);
////							}
////							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
//							DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							for (Movement m : gpgps.p.getMovements()) {
//								TrajectoryAnnotation ta = m.getTa();
////								String line = m.getTrajectory().getTid()+";";
////								line += sdf.format(m.getStartMovement().getTime()) + ";";
////								line += sdf.format(m.getEndMovement().getTime()) + ";";
//								if(ta!=null)
//									ta.storeAnnotationFile(pw);
//								else{
//									String line= m.getTrajectory().getTid()+";"+
//											dfTime.format(m.getStartMovement().getTime())+";"+
//											dfTime.format(m.getEndMovement().getTime())+";"+
//											"null;"+//temperature
//											"null;"+// weather
//											"null;"+// goal/activity
//											"null;"+// transportation mode
//											m.getStartLocation().getLid() + ";" +
//											m.getStopLocation().getLid()+";"+
//											"null;"+// event
//											m.getTrajectory().getUid()+";"+// current user
//											"0";
//									
//									pw.println(line);
//								}
//							}
//							
//							
//							
//							
//							pw.flush();
//					        pw.close();
//					        fw.close(); 
//	
//							JOptionPane.showMessageDialog(null, "Success!",
//									"Success!", 1);
////							gpgps.p = new GeoDiary();
////							gpgps.resetTemporalBoundaryToDiary();
////							gpgps.repaint();
//						} else {
//							JOptionPane.showMessageDialog(null, "No trajecoty!",
//									"Fail!", 1);
//						}
//					}
//				} catch (Exception e) {
//					log.error(e);
//				}
				
			}
		});
		
		

		menuFile.add(menuLoadGpx);
		menuFile.add(mnuLoadTrajectoriesOCTO);
		menuFile.add(mnuLoadUserTrajectories);
//		menuFile.add(menuSaveDB);
//		menuFile.add(mnuLoadPointsGPS);
		menuFile.add(menuSaveFile);
		menuFile.add(mnuLoadAnnotations);
		menuEdit.add(menuStopValues);
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		frame.setJMenuBar(menuBar);

	}

	protected void createMoveAnotationPanel(Movement m) {
		JFrame jan = new JFrame();
		jan.setSize(350, 600);
//		AnnotationMovePanel an = new AnnotationMovePanel(jan, m, trajAn);
//		jan.add(an);
//		an.fillFields();
		jan.show();

	}

	protected Diary createDiary() {
		Diary d = new Diary();

		d.setPersonId("HH217658GL241790");
		// d.addMovement(new Movement(d, 44606, 44608,
		// getCalendar("2008-02-05 10:25:00+01"),
		// getCalendar("2008-02-05 10:50:00+01"), 20, 1));
		// d.addMovement(new Movement(d, 44608, 44609,
		// getCalendar("2008-02-05 10:55:00+01"),
		// getCalendar("2008-02-05 11:00:00+01"), 17, 1));
		// d.addMovement(new Movement(d, 44609, 44606,
		// getCalendar("2008-02-05 11:30:00+01"),
		// getCalendar("2008-02-05 11:40:00+01"), 0, 1));
		// d.addMovement(new Movement(d, 44620, 44611,
		// getCalendar("2008-02-07 09:40:00+01"),
		// getCalendar("2008-02-07 09:50:00+01"), 20, 2));
		// d.addMovement(new Movement(d, 44611, 44612,
		// getCalendar("2008-02-07 10:00:00+01"),
		// getCalendar("2008-02-07 10:05:00+01"), 20, 2));
		// d.addMovement(new Movement(d, 44612, 44613,
		// getCalendar("2008-02-07 10:35:00+01"),
		// getCalendar("2008-02-07 10:50:00+01"), 20, 3));
		// d.addMovement(new Movement(d, 44613, 44606,
		// getCalendar("2008-02-07 11:25:00+01"),
		// getCalendar("2008-02-07 11:40:00+01"), 0, 3));
		// d.addMovement(new Movement(d, 44620, 44614,
		// getCalendar("2008-02-08 09:55:00+01"),
		// getCalendar("2008-02-08 10:15:00+01"), 19, 1));
		// d.addMovement(new Movement(d, 44614, 44606,
		// getCalendar("2008-02-08 10:50:00+01"),
		// getCalendar("2008-02-08 11:00:00+01"), 0, 1));
		// d.addMovement(new Movement(d, 44620, 44615,
		// getCalendar("2008-02-08 14:00:00+01"),
		// getCalendar("2008-02-08 14:05:00+01"), 20, 1));
		// d.addMovement(new Movement(d, 44615, 44606,
		// getCalendar("2008-02-08 16:00:00+01"),
		// getCalendar("2008-02-08 16:05:00+01"), 0, 1));
		// d.addMovement(new Movement(d, 44617, 44617,
		// getCalendar("2008-02-10 09:05:00+01"),
		// getCalendar("2008-02-10 09:10:00+01"), 19, 1));
		// d.addMovement(new Movement(d, 44617, 44606,
		// getCalendar("2008-02-10 09:25:00+01"),
		// getCalendar("2008-02-10 09:30:00+01"), 0, 1));
		// d.addMovement(new Movement(d, 44618, 44606,
		// getCalendar("2008-02-11 13:25:00+01"),
		// getCalendar("2008-02-11 13:35:00+01"), 0, 1));
		// d.addMovement(new Movement(d, 44619, 44619,
		// getCalendar("2008-02-11 18:05:00+01"),
		// getCalendar("2008-02-11 18:20:00+01"), 21, 3));
		// d.addMovement(new Movement(d, 44619, 44606,
		// getCalendar("2008-02-11 21:30:00+01"),
		// getCalendar("2008-02-11 21:45:00+01"), 0, 3));

		return d;
	}

	protected ActivityLocation getActivityLocation(int locId,
			String locationLabel, TreeMap<Integer, ActivityLocation> locations) {
		ActivityLocation l = locations.get(locId);
		if (l == null) {
			l = new ActivityLocation(locId, -1);
			l.activityType = locationLabel;
			locations.put(locId, l);
		}
		if (!l.activityType.equals(locationLabel)) {
			System.err.println("Label conflict: " + l.getLid() + " "
					+ l.activityType + "<->" + locationLabel);
		}

		return l;
	}

//	protected Diary createDiaryFromGPS(String pid) throws SQLException {
//		Diary d = new Diary();
//		d.personId = pid;
//
//		Connection conn = ConnectionManager.getConnection();
//
//		String sql = "select t.uid, t.tid, cf.cid start_loc_id, ct.cid end_loc_id, "
//				+ "t.time_start start_movement, "
//				+ "t.time_start + interval '1 second'*t.duration end_movement, "
//				+ "t.activity_type, 0 activity_category "
//				+ "FROM "
//				+ "mp.manual_and_gps_trajs_45_30 t, "
//				+ "circles.cirlces_manual_and_gps_trajs_45_30_p cf, "
//				+ "circles.cirlces_manual_and_gps_trajs_45_30_p ct "
//				+ "where t.uid=cf.uid and t.uid=ct.uid and "
//				+ "t.tid=cf.tid and cf.isfirst and "
//				+ "t.tid = ct.tid and not ct.isfirst and "
//				+ "get_token(pid,1,'_')=? " + "order by time_start";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//
//		pstmt.setString(1, pid);
//
//		System.out.println(sql);
//		ResultSet rs = pstmt.executeQuery();
//		TreeMap<Integer, Location> locations = new TreeMap<Integer, Location>();
//
//		// Expected tuple
//		// start_loc_id::integer
//		// end_loc_id::integer
//		// start_time:: calendar
//		// end_time::calendar
//		// activity type
//		// activity category
//
//		while (rs.next()) {
//			int startLoc = rs.getInt("start_loc_id");
//			int endLoc = rs.getInt("end_loc_id");
//			Calendar startCal = getCalendar(rs.getTimestamp("start_movement"));
//			Calendar endCal = getCalendar(rs.getTimestamp("end_movement"));
//			int activityType = rs.getInt("activity_type");
//			int activityCategory = rs.getInt("activity_category");
//
//			Location start = getLocation(startLoc, locations);
//			Location end = getLocation(endLoc, locations);
//
//			Movement m = new Movement(d, start, end, startCal, endCal,
//					activityType, activityCategory);
//			System.out.println("GPS: " + m);
//			d.addMovement(m);
//		}
//		System.out.println("The current diary contains " + locations.size()
//				+ " distinct locations");
//
//		return d;
//	}

	protected GeoDiary createLocationsAndDiaryFromGPS(String pid, String sql)
			throws SQLException {
		GeoDiary d = new GeoDiary();
		d.setPersonId(pid);

		Quadtree qTree = new Quadtree();

		Connection conn = ConnectionManager.getConnection();

		

		log.info(sql);
		Iterator<Trajectory> iTrajectories = new SQLTrajectoryIterator(conn,
				sql);
		
		int uid = Integer.parseInt(pid);
		
		DiariesExtractor de = new DiariesExtractor(uid, iTrajectories);
		d = de.getGeoDiary();
		
		//extractLocationsAndDiary(d, qTree, iTrajectories);

		return d;
	}

	protected GeoDiary createLocationsAndDiaryFromGPSPoints(String pid, String sql)
			throws SQLException {
		Quadtree qTree = new Quadtree();

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);

		log.info(sql);
		ResultSet rs = pstmt.executeQuery();
		mainTrajectory = new br.ufsc.core.Trajectory(0);

		
		GeoDiary d = new GeoDiary();
		d.setPersonId(pid);
		while (rs.next()) {
			double lon = rs.getDouble(1);
			double lat = rs.getDouble(2);
			Timestamp ts = rs.getTimestamp(3);
			TPoint p = new TPoint(lon, lat);
			p.setTimestamp(ts);
			p.setTransformedGeom(null);
			mainTrajectory.addPoint(p);
		}

		Iterator<Trajectory> iTrajectories = new TrajectoryFromPointsIterator(
				mainTrajectory);
		

		int uid = Integer.parseInt(pid);
		DiariesExtractor de = new DiariesExtractor(uid, iTrajectories);
		d = de.getGeoDiary();
		
		//extractLocationsAndDiary(d, qTree, iTrajectories);
		return d;
	}

	protected GeoDiary createAndAddTrajectoriesToDiary(File[] gpxFiles,
			GeoDiary d) throws SQLException {
		d.setPersonId("0");
		Quadtree qTree = new Quadtree();

		// preload existing locations into qTree
		for (Location l : d.positions.keySet()) {
			Point p = d.positions.get(l).getCentroid();
			Envelope e = d.positions.get(l).getEnvelope();
			// p.getEnvelopeInternal();
			e.expandBy(maxRadius);
			Circle c = new Circle();
			c.setCentroid(p);
			c.setRadius(minRadius);
			qTree.insert(e, c);
		}

		try {
			gpxMan = new GPXManipulator(gpxFiles);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mainTrajectory = gpxMan.loadPointsAsTrajectory();
		// gpxToMove().iterator();
		Iterator<Trajectory> iTrajectories = new TrajectoryFromPointsIterator(
				mainTrajectory);
		
		int uid = 0;
		DiariesExtractor de = new DiariesExtractor(uid, iTrajectories);
		d = de.getGeoDiary();
		
//		extractLocationsAndDiary(d, qTree, iTrajectories);
		return d;
	}

	/**
	 * @param d
	 * @param qTree
	 * @param iTrajectories
	 */
//	protected void extractLocationsAndDiary(GeoDiary d, Quadtree qTree,
//			Iterator<Trajectory> iTrajectories) {
//		Circle last = null;
//		TreeMap<Integer, Location> locations = d.getLocations();
//		Calendar lastTime = null;
//		Calendar firstTime = null;
//
//		while (iTrajectories.hasNext()) {
//			// br.ufsc.core.Trajectory tr = iTrajectories.next();
//			Trajectory t = iTrajectories.next();
//			// int uid = t.getTid();
//			// d.trajectories.add(t);
//			Circle cfrom = getCircle(t.getStartPoint(), true, qTree, minRadius,
//					maxRadius);
//			Circle cTo = getCircle(t.getEndPoint(), false, qTree, minRadius,
//					maxRadius);
//			Location start = getLocation(cfrom.getCircleID(), locations);
//			Location end = getLocation(cTo.getCircleID(), locations);
//
//			if ((last != null) && (cfrom.getCircleID() != last.getCircleID())) {
//				// the movement is starting from a different location
//				log.error("Start mismatch");
//				log.error("Was in " + last.getCircleID()
//						+ " now starting from " + cfrom.getCircleID());
//				log.error("distance from previous: "
//						+ last.getCentroid().distance(t.getStartPoint()));
//				// System.err.println("temporal distance from previous: "+
//				// (t.getStartTime().getTimeInMillis()-lastTime.getTimeInMillis())/60000);
//			}
//
//			last = cTo;
//			lastTime = Calendar.getInstance();
//			lastTime.setTime(t.getEndTime().getTime());
//			firstTime = Calendar.getInstance();
//			firstTime.setTime(t.getStartTime().getTime());
//			if (!start.equals(end)) {
//				// the activity type is generate randomkly at the moment
//				// int at = new Random().nextInt(4);
//				Movement m = new Movement(d, start, end, firstTime, lastTime,
//						-1, 0,0);
//				m.setTrajectory(t);
//				log.debug("GPS: " + m);
//				d.addMovement(m);
//				d.addPosition(start, cfrom);
//				d.addPosition(end, cTo);
//			}
//		}
//		Collections.sort(d.getMovements());
//		log.info("The current diary contains " + locations.size()
//				+ " distinct locations");
//	}

//	protected Circle getCircle(PointOfTrajectory point, boolean first,
//			Quadtree qTree, double minRadius, double maxRadius) {
//		Circle res = null;
//		PointOfTrajectory pot = point;
//		Envelope env = pot.getEnvelopeInternal();
//		env.expandBy(maxRadius);
//		// retrieve all the circles within maxRadius
//		List<Circle> candidates = qTree.query(env);
//		double minFitness = Double.POSITIVE_INFINITY;
//		Circle bestMatch = null;
//		for (Circle neighborCircle : candidates) {
//			double dist = neighborCircle.getCentroid().distance(pot);
//			if (dist < maxRadius) {
//				// double fitness = c.fitness(neighborCircle); // compute a
//				// fitness value for the given circle
//				if (dist < minFitness) {
//					minFitness = dist;
//					bestMatch = neighborCircle;
//				}
//			}
//		}
//		log.debug("There are " + candidates.size()
//				+ " neighbors for the current circle [" + minFitness + "]");
//		if (bestMatch != null) {
//			// exists a candidate to merge with the current point
//			qTree.remove(bestMatch.getEnvelope(), bestMatch);
//			bestMatch.addPoint(pot);
//			bestMatch.updateCentroid();
//			// substitute bestMatch with the new Circle
//			qTree.insert(bestMatch.getEnvelope(), bestMatch);
//
//			res = bestMatch;
//			log.debug("Adding to circle: [" + bestMatch.getRadius() + ","
//					+ bestMatch.getPoints().size() + "]");
//		} else {
//			Circle newCircle = new Circle();
//			// newCircle.setCentroid(pot);
//			newCircle.addPoint(pot);
//			newCircle.updateCentroid();
//			if (newCircle.getRadius() < minRadius)
//				newCircle.setRadius(minRadius);
//			qTree.insert(newCircle.getEnvelope(), newCircle);
//			res = newCircle;
//			newCircle.setCircleID(circleId++);
//			log.debug("Inserting a new Circle");
//		}
//
//		return res;
//	}

//	protected Location getLocation(int locId,
//			TreeMap<Integer, Location> locations) {
//		Location l = locations.get(locId);
//		if (l == null) {
//			l = new Location(locId, -1);
//			locations.put(locId, l);
//		}
//
//		return l;
//	}

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
			Movement m = new Movement(dp.p, dummyLocation, dummyLocation,
					startCal, endCal, activityType, 0,0);
			// System.err.println(m);
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
