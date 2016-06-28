package viz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.vividsolutions.jts.index.quadtree.Quadtree;

import utils.GPXManipulator;
import utils.TrajectoryFromPointsIterator;
import br.ufsc.core.Trajectory;

public class ParametersPanel extends JPanel {
	
	protected Font font = new Font("Arial", Font.BOLD, 10);
	
	private JFrame mainWindow;
	
	
	private JLabel lblAvgSpeed = new JLabel("Average Speed in %");
	private JTextField txtAvgSpeed = new JTextField();
	
	private JLabel lblSpeedLimit = new JLabel("Speed Limit in %");
	private JTextField txtSpeedLimit = new JTextField();
	
	private JLabel lblMinTime = new JLabel("Min Time of Stop");
	private JTextField txtMinTime = new  JTextField();
	
	private JLabel lblTolerance = new JLabel("Number of Tolerance Points");
	private JTextField txtTolerance = new JTextField();
	
	private JLabel lblMaxRadios = new JLabel("Max Radio");
	private JTextField txtMaxRadios = new JTextField();
	
	private JLabel lblMinRadios = new JLabel("Min Radios");
	private JTextField txtMinRadios = new JTextField();
//	
//	private JLabel lblWeather = new JLabel("Weather");
//	private JComboBox cbWeather = new JComboBox();
//	
//	private JLabel lblTemperature = new JLabel("Temperature");
//	private JTextField txtTemperature = new JTextField("", 15);
	
	private JButton btOk = new JButton("Ok");
	private JButton btCancel = new JButton("Cancel");
	
	static int lastWeather;
	static String lastTemperature;
	
	VisualizeOctoDiary vz;
	
	public ParametersPanel(JFrame _mainWindow, VisualizeOctoDiary _vz){
		super();
		
		this.mainWindow = _mainWindow;
		
		this.vz = _vz;
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
		
		lblAvgSpeed.setPreferredSize(dmLbl);
	//	lblStartTime.setFont(font);
		lblSpeedLimit.setPreferredSize(dmLbl);
		lblMinTime.setPreferredSize(dmLbl);
//		lblSubGoal.setPreferredSize(dmLbl);
//		lblDestination.setPreferredSize(dmLbl);
//		lblOrigin.setPreferredSize(dmLbl);
//		lblTemperature.setPreferredSize(dmLbl);
		lblTolerance.setPreferredSize(dmLbl);
		lblMaxRadios.setPreferredSize(dmLbl);
		lblMinRadios.setPreferredSize(dmLbl);
//		lblWeather.setPreferredSize(dmLbl);
		
//		this.populateCombos();
		
		txtSpeedLimit.setPreferredSize(dmTxt);
		txtMinTime.setPreferredSize(dmTxt);
		txtAvgSpeed.setPreferredSize(dmTxt);
//		txtSubGoal.setPreferredSize(dmTxt);
//		txtTemperature.setPreferredSize(dmTxt);
//		cbDestination.setPreferredSize(dmTxt);
//		cbOrigin.setPreferredSize(dmTxt);
		txtTolerance.setPreferredSize(dmTxt);
		txtMaxRadios.setPreferredSize(dmTxt);
		txtMinRadios.setPreferredSize(dmTxt);
//		cbWeather.setPreferredSize(dmTxt);
		
		btCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				mainWindow.dispose();
			}
			}
			);
		
		btOk.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				updateValues();
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
		panel1.add(lblAvgSpeed,gbc);
		gbc.gridx=x2;
		panel1.add(txtAvgSpeed,gbc);;
		this.add(panel1);
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel2.add(lblMandatory2,gbc);
		gbc.gridy=0;
		panel2.add(lblSpeedLimit,gbc);
		gbc.gridx=x2;
		panel2.add(txtSpeedLimit,gbc);
		this.add(panel2);
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel3.add(lblMandatory3,gbc);
		gbc.gridy=0;
		panel3.add(lblMinTime,gbc);
		
		gbc.gridx=x2;
		txtMinTime.setPreferredSize(dmTxt);
		panel3.add(txtMinTime,gbc);
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
		panel5.add(lblTolerance,gbc);
		
		gbc.gridx=x2;
		panel5.add(txtTolerance,gbc);
		this.add(panel5);
		
		JPanel panel6 = new JPanel();
		panel6.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel6.add(lblMaxRadios,gbc);
		gbc.gridx=x2;
		panel6.add(txtMaxRadios,gbc);
		this.add(panel6);
		
		JPanel panel7 = new JPanel();
		panel7.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel7.add(lblMinRadios,gbc);
		gbc.gridx=x2;
		panel7.add(txtMinRadios,gbc);
		this.add(panel7);
//		
//		JPanel panel8 = new JPanel();
//		panel8.setLayout(layoutPanel);
//		gbc.gridx=x1;
//		panel8.add(lblWeather,gbc);
//		gbc.gridx=x2;
//		panel8.add(cbWeather,gbc);
//		this.add(panel8);
//		
//		JPanel panel9 = new JPanel();
//		panel9.setLayout(layoutPanel);
//		gbc.gridx=x1;
//		panel9.add(lblTemperature,gbc);
//		gbc.gridx=x2;
//		panel9.add(txtTemperature,gbc);
//		this.add(panel9);
		
		JPanel panel10 = new JPanel();
		panel10.setLayout(layoutPanel);
		gbc.gridx=x1;
		panel10.add(btOk,gbc);
		gbc.gridx=x2;
		panel10.add(btCancel,gbc);
		this.add(panel10);
		
		
	}
	
	protected  void updateValues() {
		TrajectoryFromPointsIterator.avg=new Double(txtAvgSpeed.getText());
		TrajectoryFromPointsIterator.mt=new Integer(txtMinTime.getText());
		TrajectoryFromPointsIterator.sl=new Double(txtSpeedLimit.getText());
		TrajectoryFromPointsIterator.tol=new Integer(txtTolerance.getText());
//		vz.setMaxRadius(new Double(txtMaxRadios.getText()));
//		vz.setMinRadius(new Double(txtMinRadios.getText()));
			vz.gpgps.p.clear();
			vz.circleId = 0;
			Iterator<it.cnr.isti.kdd.sax.mbmodel.Trajectory> iTrajectories = new TrajectoryFromPointsIterator(
					vz.mainTrajectory);
			Quadtree qTree = new Quadtree();
			vz.extractLocationsAndDiary((GeoDiary) vz.gpgps.p, qTree, iTrajectories);
			vz.gpgps.resetTemporalBoundaryToDiary();
			vz.gpgps.repaint();
			vz.geoGPS.repaint();
	
	}



	
	public void fillFields(){
		
		txtAvgSpeed.setText(new Double(TrajectoryFromPointsIterator.avg).toString());
		txtMinTime.setText(new Integer(TrajectoryFromPointsIterator.mt).toString());
		txtSpeedLimit.setText(new Double(TrajectoryFromPointsIterator.sl).toString());
		txtTolerance.setText(new Integer(TrajectoryFromPointsIterator.tol).toString());
		txtMaxRadios.setText(new Double(vz.getMaxRadius()).toString());
		txtMinRadios.setText(new Double(vz.getMinRadius()).toString());
		
		
	}
	
	
	

}
