package viz;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class QueryDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1502031671326007776L;
	protected String query = "";
	protected JTextArea txtQuery;
	protected boolean loadingPoints = false;

	public QueryDialog(JFrame frame, boolean modal) {
		super(frame, modal);
		
		
		// create the panel
		JPanel pnl  = new JPanel();
		pnl.setLayout(new GridBagLayout());

		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(2, 3, 2, 3);
		
		pnl.add(new JLabel("Points or Trajectories?"),c);
		
		JRadioButton rdoPoints = new JRadioButton("Points");
		rdoPoints.setSelected(true);
		rdoPoints.setActionCommand("Points");
		
		JRadioButton rdoTrajectories = new JRadioButton("Trajectories");
		rdoTrajectories.setActionCommand("Trajectories");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdoPoints);
		bg.add(rdoTrajectories);
		ActionListener btnListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String act = e.getActionCommand();
				if(act.equals("Trajectories"))
					loadingPoints = false;
				else
					loadingPoints = true;
			}
		};
		rdoPoints.addActionListener(btnListener);
		rdoTrajectories.addActionListener(btnListener);
		
		c.gridwidth = 1;
		c.gridy = 1;
		pnl.add(rdoPoints,c);
		c.gridx = 1;
		pnl.add(rdoTrajectories,c);
		
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 2;
		pnl.add(new JLabel("Query:"),c);
		
		
		c.gridy = 3;
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		txtQuery = new JTextArea(query);
		pnl.add(new JScrollPane(txtQuery),c);
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth =1;
		c.gridy = 4;
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 0;
		
		
		
		JButton btnRun = new JButton("Run");
		pnl.add(btnRun,c);
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				query = txtQuery.getText();
				setVisible(false);
			}
		});
		c.gridx = 1;
		
		
		JButton btnCancel = new JButton("Cancel");
		pnl.add(btnCancel,c);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				query = "";
				setVisible(false);
			}
		});
		
		
		Container cnt = getContentPane();
		cnt.setLayout(new BorderLayout());
		cnt.add(pnl, BorderLayout.CENTER);
		setPreferredSize(new Dimension(400, 300));
		pack();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
		txtQuery.setText(query);
	}
		
}
