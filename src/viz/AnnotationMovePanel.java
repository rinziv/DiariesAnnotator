package viz;

import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.postgresql.translation.messages_cs;

import utils.TrajectoryAnnotation;

public class AnnotationMovePanel extends JPanel {
	
	protected Font font = new Font("Arial", Font.BOLD, 10);
	
	private JFrame mainWindow;
	
	private Movement m;
	
	
	private JLabel lblStartTime = new JLabel("Start time");
	private JTextField txtStartTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	
	private JLabel lblEntTime = new JLabel("End time");
	private JTextField txtEndTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	
	private JLabel lblGoal = new JLabel("Goal");
	private JComboBox cbGoal = new  JComboBox();
	
//	private JLabel lblSubGoal = new JLabel("Sub Goal");
//	private JTextField txtSubGoal = new JTextField("", 15);
	
	private JLabel lblTransportation = new JLabel("Transportation");
	private JComboBox cbTransportation = new JComboBox();
	
	private JLabel lblOrigin = new JLabel("Origin");
	private JComboBox cbOrigin = new JComboBox();
	
	private JLabel lblDestination = new JLabel("Destination");
	private JComboBox cbDestination = new JComboBox();
	
	private JLabel lblWeather = new JLabel("Weather");
	private JComboBox cbWeather = new JComboBox();
	
	private JLabel lblTemperature = new JLabel("Temperature");
	private JTextField txtTemperature = new JTextField("", 15);
	
	private JButton btOk = new JButton("Ok");
	private JButton btCancel = new JButton("Cancel");
	
	static int lastWeather;
	static String lastTemperature;
	
	public AnnotationMovePanel(JFrame _mainWindow, final Movement _m, final List<TrajectoryAnnotation> trajAn){
		super();
		
		this.mainWindow = _mainWindow;
		this.m=_m;
		
		//this.setPreferredSize(new Dimension(600,50));
		
		
		
		GridLayout layout = new GridLayout(9,1);
//		GridLayout layoutPanel = new GridLayout(1,2);
		GridBagLayout layoutPanel = new GridBagLayout();
//		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0,	1.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
//			    new Insets( 0, 0, 0, 0 ), 0, 0 );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor=GridBagConstraints.LINE_END;
		gbc.fill=GridBagConstraints.NONE;
		gbc.insets = new Insets(1,1,0,5); 
		gbc.weightx = 0;  
		gbc.weighty = 0;  
		
		this.setAlignmentY(LEFT_ALIGNMENT);
		
		Dimension dmTxt = new Dimension(180, 20);
		Dimension dmLbl = new Dimension(100,20);
		
		Font font2 = new Font("Arial", Font.BOLD, 10);
		
		JLabel lblMandatory = new JLabel("*");
		lblMandatory.setFont(font);
		lblMandatory.setForeground(Color.RED);
		
		JLabel lblMandatory2 = new JLabel("*");
		lblMandatory2.setFont(font);
		lblMandatory2.setForeground(Color.RED);
		
		JLabel lblMandatory3 = new JLabel("*");
		lblMandatory3.setFont(font);
		lblMandatory3.setForeground(Color.RED);
		
		JLabel lblMandatory4 = new JLabel("*");
		lblMandatory4.setFont(font);
		lblMandatory4.setForeground(Color.RED);
		
		lblStartTime.setPreferredSize(dmLbl);
	//	lblStartTime.setFont(font);
		lblEntTime.setPreferredSize(dmLbl);
		lblGoal.setPreferredSize(dmLbl);
//		lblSubGoal.setPreferredSize(dmLbl);
		lblDestination.setPreferredSize(dmLbl);
		lblOrigin.setPreferredSize(dmLbl);
		lblTemperature.setPreferredSize(dmLbl);
		lblTransportation.setPreferredSize(dmLbl);
		lblWeather.setPreferredSize(dmLbl);
		
		this.populateCombos();
		
		txtEndTime.setPreferredSize(dmTxt);
		cbGoal.setPreferredSize(dmTxt);
		txtStartTime.setPreferredSize(dmTxt);
//		txtSubGoal.setPreferredSize(dmTxt);
		txtTemperature.setPreferredSize(dmTxt);
		cbDestination.setPreferredSize(dmTxt);
		cbOrigin.setPreferredSize(dmTxt);
		cbTransportation.setPreferredSize(dmTxt);
		cbWeather.setPreferredSize(dmTxt);
		
		btCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				mainWindow.dispose();
			}
			}
			);
		
		btOk.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				addAnnotation(_m, trajAn);
				mainWindow.dispose();
			}
			}
			);

		
		this.setLayout(layout);
		
		int x1 =0;
		int x2= 1;		
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel1.add(lblMandatory,gbc);
		gbc.gridy=0;
		panel1.add(lblStartTime,gbc);
		gbc.gridx=x2;
		panel1.add(txtStartTime,gbc);;
		this.add(panel1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel2.add(lblMandatory2,gbc);
		gbc.gridy=0;
		panel2.add(lblEntTime,gbc);
		gbc.gridx=x2;
		panel2.add(txtEndTime,gbc);
		this.add(panel2);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel3.add(lblMandatory3,gbc);
		gbc.gridy=0;
		panel3.add(lblGoal,gbc);
		
		gbc.gridx=x2;
		cbGoal.setPreferredSize(dmTxt);
		panel3.add(cbGoal,gbc);
		this.add(panel3);
		
//		JPanel panel4 = new JPanel();
//		panel4.setLayout(layoutPanel);
//		gbc.gridx=x1;
//		panel4.add(lblSubGoal,gbc);
//		gbc.gridx=x2;
//		panel4.add(txtSubGoal,gbc);
//		this.add(panel4);
		
		JPanel panel5 = new JPanel();
		panel5.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel5.add(lblMandatory4,gbc);
		gbc.gridy=0;
		panel5.add(lblTransportation,gbc);
		
		gbc.gridx=x2;
		panel5.add(cbTransportation,gbc);
		this.add(panel5);
		
		JPanel panel6 = new JPanel();
		panel6.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel6.add(lblOrigin,gbc);
		gbc.gridx=x2;
		panel6.add(cbOrigin,gbc);
		this.add(panel6);
		
		JPanel panel7 = new JPanel();
		panel7.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel7.add(lblDestination,gbc);
		gbc.gridx=x2;
		panel7.add(cbDestination,gbc);
		this.add(panel7);
		
		JPanel panel8 = new JPanel();
		panel8.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel8.add(lblWeather,gbc);
		gbc.gridx=x2;
		panel8.add(cbWeather,gbc);
		this.add(panel8);
		
		JPanel panel9 = new JPanel();
		panel9.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel9.add(lblTemperature,gbc);
		gbc.gridx=x2;
		panel9.add(txtTemperature,gbc);
		this.add(panel9);
		
		JPanel panel10 = new JPanel();
		panel10.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel10.add(btOk,gbc);
		gbc.gridx=x2;
		panel10.add(btCancel,gbc);
		this.add(panel10);
		
		
	}
	
	protected  void addAnnotation(Movement m, List<TrajectoryAnnotation> trajAn) {
		// TODO Auto-generated method stub
		m.activityType=cbGoal.getSelectedIndex();
		m.weather=cbWeather.getSelectedIndex();
		if(!txtTemperature.getText().isEmpty())
			m.temperature=new Double(txtTemperature.getText());
		
		AnnotationMovePanel.lastTemperature=txtTemperature.getText();
		AnnotationMovePanel.lastWeather=cbWeather.getSelectedIndex();
		
		int tid = 0;
		
		TrajectoryAnnotation ta = new TrajectoryAnnotation(tid, m, m.getTemperature(), cbGoal.getSelectedIndex(), 
				cbOrigin.getSelectedIndex(), cbDestination.getSelectedIndex(), 
				cbTransportation.getSelectedIndex(), m.weather, 
				0, 0, 0);
		ta.setGoal_name(cbGoal.getSelectedItem().toString());
		ta.setPlace_from_name(cbOrigin.getSelectedItem().toString());
		ta.setPlace_to_name(cbDestination.getSelectedItem().toString());
		ta.setTransportation_name(cbTransportation.getSelectedItem().toString());
		ta.setWeather_name(cbWeather.getSelectedItem().toString());
		trajAn.add(ta);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
	}
	
	protected void populateCombos(){
		
		
		String[] tranportation = {"On foot", "By car", "By bus", "By train", "By bike", "Other"};
		//String[] places = {"Place0", "Place1", "Place2", "Place3"};
		String[] weather = {"Sun","Rain","Fog","Snow","Other"};
//		String[] activty = {"Home","Work","Shopping","Social"};
		String[] activty = {"Working",
				"Services (hairdresser, doctor, bank, ...)",
				"Food (restaurant, snack bar, snack bar, ...)",
				"Daily shopping (baker, butcher, supermarket, ...)",
				"Shopping (clothing, furniture, shopping, ...)",
				"Education and training (courses or classes, internships, ...)",
				"Social activities (visiting, bar, party, ...)",
				"Leisure (sports, fishing, excursions, culture, ...)",
				"Something or someone pick up, drop off",
				"Touring (ride made no specific purpose)",
				"Other",
				"Going Home",
				"Refuelling"};
		
		//cbDestination=new JComboBox(places);
		//cbOrigin=new JComboBox(places);
		cbTransportation=new JComboBox(tranportation);
		cbWeather=new JComboBox(weather);
		cbGoal= new JComboBox(activty);
	} 
	
	public void fillFields(){
		Calendar start = m.startMovement;
		
		DateFormat dfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		txtStartTime.setText(dfTime.format(start.getTime()));
		
		Calendar end = m.endMovement;
		txtEndTime.setText(dfTime.format(end.getTime()));
		
		//cbGoal.setText("Go to location "+m.stopLocation.toString());
		
		String placesOrig = m.startLocation.locationName();
		String placesDest= m.stopLocation.locationName();
		
		
		
		cbDestination.addItem(placesDest);
		
		//cbDestination=new JComboBox(placesDest);
		//cbOrigin=new JComboBox(placesOrig);
		
		cbOrigin.addItem(placesOrig);
		
		if(m.activityType!=-1){
			cbGoal.setSelectedIndex(m.activityType);
		}
		
		if(m.weather!=-1){
			cbWeather.setSelectedIndex(m.weather);
		}else{
			cbWeather.setSelectedIndex(AnnotationMovePanel.lastWeather);
		}
		
		if(m.temperature!=null){
			txtTemperature.setText(m.temperature.toString());
		}else{
			txtTemperature.setText(AnnotationMovePanel.lastTemperature);
		}
		
		
	}
	
	
	

}
