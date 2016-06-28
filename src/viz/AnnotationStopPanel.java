package viz;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AnnotationStopPanel extends JPanel {

protected Font font = new Font("Arial", Font.BOLD, 10);
	
	private JLabel lblStartTime = new JLabel("Start time");
	private JTextField txtStartTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	
	private JLabel lblEntTime = new JLabel("End time");
	private JTextField txtEndTime = new JTextField("yyyy-mm-dd 00:00:00", 15);
	
	private JLabel lblGoal = new JLabel("Goal");
	private JTextField txtGoal = new JTextField("", 15);
	
//	private JLabel lblSubGoal = new JLabel("Sub Goal");
//	private JTextField txtSubGoal = new JTextField("", 15);
	
	private JLabel lblTransportation = new JLabel("Transportation");
	private JComboBox cbTransportation = new JComboBox();
	
	private JLabel lblPlace = new JLabel("Place");
	private JComboBox cbPlace = new JComboBox();
	
	private JLabel lblEvent = new JLabel("Event");
	private JComboBox cbEvent = new JComboBox();
	
	private JLabel lblWeather = new JLabel("Weather");
	private JComboBox cbWeather = new JComboBox();
	
	private JLabel lblTemperature = new JLabel("Temperature");
	private JTextField txtTemperature = new JTextField("", 15);
	
	private JButton btOk = new JButton("Ok");
	private JButton btCancel = new JButton("Cancel");
	
	public AnnotationStopPanel(){
		super();
		
		this.setPreferredSize(new Dimension(600,50));
		
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
		
		lblStartTime.setPreferredSize(dmLbl);
		lblEntTime.setPreferredSize(dmLbl);
		lblGoal.setPreferredSize(dmLbl);
//		lblSubGoal.setPreferredSize(dmLbl);
		lblEvent.setPreferredSize(dmLbl);
		lblPlace.setPreferredSize(dmLbl);
		lblTemperature.setPreferredSize(dmLbl);
		lblTransportation.setPreferredSize(dmLbl);
		lblWeather.setPreferredSize(dmLbl);
		
		this.populateCombos();
		
		txtEndTime.setPreferredSize(dmTxt);
		txtGoal.setPreferredSize(dmTxt);
		txtStartTime.setPreferredSize(dmTxt);
//		txtSubGoal.setPreferredSize(dmTxt);
		txtTemperature.setPreferredSize(dmTxt);
		cbEvent.setPreferredSize(dmTxt);
		cbPlace.setPreferredSize(dmTxt);
		cbTransportation.setPreferredSize(dmTxt);
		cbWeather.setPreferredSize(dmTxt);
		
		
		
		this.setLayout(layout);
		
		int x1 =0;
		int x2= 1;		
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel1.add(lblStartTime,gbc);
		gbc.gridx=x2;
		panel1.add(txtStartTime,gbc);;
		this.add(panel1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel2.add(lblEntTime,gbc);
		gbc.gridx=x2;
		panel2.add(txtEndTime,gbc);
		this.add(panel2);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel3.add(lblGoal,gbc);
		gbc.gridx=x2;
		txtGoal.setPreferredSize(dmTxt);
		panel3.add(txtGoal,gbc);
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
		panel5.add(lblTransportation,gbc);
		gbc.gridx=x2;
		panel5.add(cbTransportation,gbc);
		this.add(panel5);
		
		JPanel panel6 = new JPanel();
		panel6.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel6.add(lblPlace,gbc);
		gbc.gridx=x2;
		panel6.add(cbPlace,gbc);
		this.add(panel6);
		
		JPanel panel7 = new JPanel();
		panel7.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel7.add(lblEvent,gbc);
		gbc.gridx=x2;
		panel7.add(cbEvent,gbc);
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
	}
	
	protected void populateCombos(){
		
		
		String[] tranportation = {"On foot", "By car", "By bus", "By train", "By bike", "Other"};
		String[] places = {"Place0", "Place1", "Place2", "Place3"};
		String[] events = {"Event0", "Event1", "Event2", "Event3"};
		String[] weather = {"Sun","Rain","Fog","Snow","Other"};
		
		cbEvent=new JComboBox(events);
		cbPlace=new JComboBox(places);
		cbTransportation=new JComboBox(tranportation);
		cbWeather=new JComboBox(weather);
	} 
	
}
