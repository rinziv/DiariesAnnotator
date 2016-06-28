package viz;

import it.cnr.isti.kdd.diaries.Location;
import it.cnr.isti.kdd.sax.mbmodel.PointOfTrajectory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import utils.TrajectoryAnnotation;

public class MovementEditorPanel extends JPanel implements ItemListener {

	Movement mov = null;
	GeoDiary diary = null;
	protected List<TrajectoryAnnotation> trajAn = new ArrayList<TrajectoryAnnotation>();
	protected boolean hasChanges = false;

	private JTextField txtStartTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	private JTextField txtEndTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	private JComboBox cbGoal = new JComboBox();
	private JComboBox cbTransportation = new JComboBox();
	private JComboBox cbOrigin = new JComboBox();
	private JComboBox cbDestination = new JComboBox();
	private JComboBox cbWeather = new JComboBox();
	private JTextField txtTemperature = new JTextField("", 15);

	

	public MovementEditorPanel(GeoDiary _diary) {
		setDiary(_diary);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createEmptyBorder(5, 5, 5, 5), BorderFactory
				.createCompoundBorder(
						BorderFactory.createTitledBorder("Movements"),
						BorderFactory.createEmptyBorder(5, 5, 5, 5))));

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		JLabel lbl = new JLabel("No movement selected");
		content.add(lbl, BorderLayout.CENTER);

		add(content);
	}

	public void setDiary(GeoDiary _diary) {
		diary = _diary;
		
		for (Location l : diary.getLocations().values()) {
//			System.out.println(l);
			cbOrigin.addItem(l);
			cbDestination.addItem(l);
		}
		
	}

	public void setMovement(Movement m) {
		mov = m;
		removeAll();
		JPanel pnlMovement = createMovementPanel(m);
		JPanel pnlAttributes = createAttributesPanel(m);
		JPanel pnlButtons = createButtonsPanel(m);
		JPanel pnlStatistics = createStatistics(m);
		hasChanges = false;
		
		add(pnlMovement);
		add(pnlAttributes);
		add(pnlStatistics);
		add(pnlButtons);
		revalidate();
		repaint();

	}

	protected JPanel createStatistics(Movement m) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		double[] minutesFrom = diary.locationFrequencies.get(m
				.getStartLocation().getLid());
		if(minutesFrom!=null){
		
		ChartPanel chartFrom = createLocationPreenceChart(m.getStartLocation()
				.getLid(), minutesFrom);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		pnl.add(chartFrom, c);
		
		double[] minutesTo = diary.locationFrequencies.get(m
				.getStopLocation().getLid());
		ChartPanel chartTo = createLocationPreenceChart(m.getStopLocation()
				.getLid(), minutesTo);
		c.gridy = 1;
		pnl.add(chartTo, c);
		}
		
		return pnl;
	}

	/**
	 * @param m
	 * @param minutesFrom
	 * @return
	 */
	protected ChartPanel createLocationPreenceChart(int lid,
			double[] minutesFrom) {
		TimeSeries tseries = new TimeSeries("L" + lid, Minute.class);
		RegularTimePeriod rtpMinute = new Minute(0, 0, 1, 1, 1900);

		for (int i = 0; i < 1440; i++) {
			tseries.add(rtpMinute, minutesFrom[i]);
			rtpMinute = rtpMinute.next();
		}
		TimeSeriesCollection xysc = new TimeSeriesCollection();
		xysc.addSeries(tseries);

		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("Presence L"
				+ lid, null, null, xysc, false, false, false);
		XYPlot xyplot = (XYPlot) jfreechart.getPlot();
		xyplot.setBackgroundPaint(Color.lightGray.brighter());

		XYBarRenderer xybr = new XYBarRenderer(0.2d);
		xybr.setDrawBarOutline(false);
		xybr.setShadowVisible(false);
		xybr.setBarPainter(new StandardXYBarPainter());
		xybr.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		xyplot.setForegroundAlpha(0.65F);

		xyplot.setRenderer(xybr);

		DateAxis pa = (DateAxis) xyplot.getDomainAxis();
		pa.setRange(new Minute(0, 0, 1, 1, 1900).getStart(), rtpMinute.getEnd());
		
		xyplot.getRangeAxis().setRange(0, 31);

		ChartPanel chartPanel = new ChartPanel(jfreechart);
//		chartPanel.setMaximumSize(new Dimension(500, 200));
		chartPanel.setPreferredSize(new Dimension(300, 130));
		chartPanel.setMouseZoomable(true);
		return chartPanel;
	}

	private JPanel createButtonsPanel(Movement m) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JButton btnSave = new JButton("Save");
		JButton btnClose = new JButton("Close");
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 3, 3, 3);
		pnl.add(btnSave, c);
		btnSave.setActionCommand("Save");
		c.gridx = 1;
		pnl.add(btnClose, c);
		btnClose.setActionCommand("Cancel");

		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAnnotation();
			}
		});

		btnClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				diary.clearSelection();

			}
		});

		return pnl;
	}

	private JPanel createAttributesPanel(Movement m) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
		populateCombos();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 2, 5, 2);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.2;
		c.gridwidth = 1;
		pnl.add(new JLabel("Start Time:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		txtStartTime = new JTextField(
				sdf.format(m.getStartMovement().getTime()));
		pnl.add(txtStartTime, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("End Time:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		txtEndTime = new JTextField(sdf.format(m.getEndMovement().getTime()));
		pnl.add(txtEndTime, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("Transportation:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		pnl.add(cbTransportation, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("Goal:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		cbGoal.setPrototypeDisplayValue("Education and training");
		if(m.activityType>=0){
			cbGoal.setSelectedIndex(m.activityType);
		}else{
			cbGoal.setSelectedIndex(cbGoal.getModel().getSize()-1);
		}
		pnl.add(cbGoal, c);

		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("Origin:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		pnl.add(cbOrigin, c);
		cbOrigin.setSelectedItem(m.startLocation);

		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("Destination:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		pnl.add(cbDestination, c);
		cbDestination.setSelectedItem(m.stopLocation);

		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0.2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		pnl.add(new JLabel("Weather:"), c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.6;
		c.gridwidth = 2;
		c.gridx = 1;
		pnl.add(cbWeather, c);

		
		cbWeather.addItemListener(this);
		cbGoal.addItemListener(this);
		cbDestination.addItemListener(this);
		cbOrigin.addItemListener(this);
		cbTransportation.addItemListener(this);
		
		return pnl;
	}

	protected JPanel createMovementPanel(Movement m) {
		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.4;
		c.gridx = 1;

		c.gridy = 0;
		content.add(new JLabel("Start:"), c);

		c.gridy = 1;
		content.add(new JLabel(sdf.format(m.getStartMovement().getTime())), c);

		c.gridy = 2;
		content.add(new JLabel("Move Duration:"), c);

		c.gridy = 3;
		content.add(new JLabel(sdf.format(new Date(m.getTrajectory().getDuration()))), c);

		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 2;

		c.gridy = 0;
		content.add(new JLabel("End:", SwingConstants.RIGHT), c);

		c.gridy = 1;
		content.add(new JLabel(sdf.format(m.getEndMovement().getTime()),
				SwingConstants.RIGHT), c);

		c.gridy = 2;
		content.add(new JLabel("Stop Duration:", SwingConstants.RIGHT), c);

		c.gridy = 3;

		content.add(new JLabel(sdf.format(new Date(m.getStopDuration())),
				SwingConstants.RIGHT), c);

		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.1;
		c.gridheight = 4;
		c.gridx = 0;
		c.gridy = 0;

		JLabel lblFrom = new JLabel("" + m.getStartLocation().getLid());
		lblFrom.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrom.setVerticalAlignment(SwingConstants.CENTER);
		lblFrom.setFont(lblFrom.getFont().deriveFont(22f));
		content.add(lblFrom, c);

		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 3;
		c.gridy = 0;
		JLabel lblTo = new JLabel("" + m.getStopLocation().getLid());
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setVerticalAlignment(SwingConstants.CENTER);
		lblTo.setFont(lblTo.getFont().deriveFont(22f));
		content.add(lblTo, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0.3;
		c.fill = GridBagConstraints.NONE;
		JButton btnGMOrigin = new JButton("Open in Google Maps");
		btnGMOrigin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PointOfTrajectory pOrigin = mov.getTrajectory().getStartPoint();
				String url = "https://maps.google.it/maps?q=" + pOrigin.getY()
						+ "+" + pOrigin.getX() + "&hl=it&t=h&z=19";
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		content.add(btnGMOrigin, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 0.3;
		JButton btnGMDestination = new JButton("Open in Google Maps");
		btnGMDestination.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PointOfTrajectory pDestination = mov.getTrajectory()
						.getEndPoint();
				String url = "https://maps.google.it/maps?q="
						+ pDestination.getY() + "+" + pDestination.getX()
						+ "&hl=it&t=h&z=19";
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		content.add(btnGMDestination, c);

		return content;
	}

	public void clearSelection() {
		mov = null;
		removeAll();

		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		JLabel lbl = new JLabel("No movement selected");
		content.add(lbl, BorderLayout.CENTER);

		add(content);
		revalidate();
		repaint();
	}

	protected void populateCombos() {
		String[] tranportation = { "By car", "On foot", "By bus", "By train",
				"By bike", "Other" };
		// String[] places = {"Place0", "Place1", "Place2", "Place3"};
		String[] weather = { "Sun", "Rain", "Fog", "Snow", "Other" };
		// String[] activty = {"Home","Work","Shopping","Social"};
		String[] activty = { 
				"Going Home",
				"Working",
				"Shopping (clothing, furniture, shopping, ...)",
				"Daily shopping (baker, butcher, supermarket, ...)",
				"Services (hairdresser, doctor, bank, ...)",
				"Food (restaurant, snack bar, snack bar, ...)",
				"Education and training (courses or classes, internships, ...)",
				"Social activities (visiting, bar, party, ...)",
				"Leisure (sports, fishing, excursions, culture, ...)",
				"Something or someone pick up, drop off",
				"Touring (ride made no specific purpose)", "Refuelling",
				"Other",
				""};

		// cbDestination=new JComboBox(places);
		// cbOrigin=new JComboBox(places);
		cbTransportation = new JComboBox(tranportation);
		cbWeather = new JComboBox(weather);
		cbGoal = new JComboBox(activty);
	}

	protected void saveAnnotation() {
		mov.startLocation = (Location) cbOrigin.getSelectedItem();
		mov.stopLocation = (Location) cbDestination.getSelectedItem();
		AnnotationMovePanel.lastTemperature = txtTemperature.getText();
		AnnotationMovePanel.lastWeather = cbWeather.getSelectedIndex();


		TrajectoryAnnotation ta = createAnnotation(mov);
		trajAn.add(ta);
		mov.setTa(ta);

		// Do you want to propagate the same activity type to other trips
		// arriving to location destination?
		int n = JOptionPane.showConfirmDialog(this,
				"Do you want to propagate the same activity ("
						+ cbGoal.getSelectedItem().toString() + ")\n"
						+ "to all trips arriving to location "
						+ cbDestination.getSelectedItem().toString() + "?",
				"Propagate current activity?", JOptionPane.YES_NO_OPTION);

		if (n == JOptionPane.YES_OPTION) {
//			System.out.println("propagate");
			Location stop = mov.getStopLocation();
			for (Movement movement : diary.getMovements()) {
				if (movement.getStopLocation().equals(stop)) {
					ta = createAnnotation(movement);
					trajAn.add(ta);
					movement.setTa(ta);
				}
			}
		}

	}

	/**
	 * @param m
	 * @return
	 */
	protected TrajectoryAnnotation createAnnotation(Movement m) {
		m.activityType = cbGoal.getSelectedIndex();
		m.weather = cbWeather.getSelectedIndex();

		if (!txtTemperature.getText().isEmpty())
			m.temperature = new Double(txtTemperature.getText());

		TrajectoryAnnotation ta = new TrajectoryAnnotation(m.getTrajectory()
				.getTid(), m, m.getTemperature(), cbGoal.getSelectedIndex(),
				cbOrigin.getSelectedIndex(), cbDestination.getSelectedIndex(),
				cbTransportation.getSelectedIndex(), m.weather, 0, 0, 0);
		ta.setGoal_name(cbGoal.getSelectedItem().toString());
		ta.setPlace_from_name(cbOrigin.getSelectedItem().toString());
		ta.setPlace_to_name(cbDestination.getSelectedItem().toString());
		ta.setTransportation_name(cbTransportation.getSelectedItem().toString());
		ta.setWeather_name(cbWeather.getSelectedItem().toString());
		return ta;
	}

	public boolean hasUnsavedChanges() {
		return hasChanges;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		hasChanges = true;
	}
}
