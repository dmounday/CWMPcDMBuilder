/**
 * 
 */
package com.gatespace.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author dmounday
 *
 */
public class ProfileFilterForm extends JFrame  implements ActionListener{
    
	private JButton applyButton; // Profile filter form buttons
	private JButton cancelButton;
	private JButton resetButton;
	private boolean apply;
	private boolean cancel;
	
	protected ArrayList<JCheckBox> cbList = new ArrayList<JCheckBox>();
	protected JList pList;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4467766152896906258L;
	private JScrollPane 	sPane;

	/**
	 * Pass the ArrayList<SelectListItem> containing the profiles
	 * to select from.
	 * @param profiles
	 */
	public ProfileFilterForm(JList profileList ){

		sPane = new JScrollPane(profileList);
		this.add(sPane);
		this.setTitle("Profile Filter Form");
		
		//profileFrame.setContentPane(pfForm);
		applyButton = new JButton("Apply ");
		cancelButton = new JButton("Cancel");
		resetButton = new JButton("Unset All");
		applyButton.setActionCommand("profile-apply");
		cancelButton.setActionCommand("profile-cancel");
		resetButton.setActionCommand("profile-unsetall");
		applyButton.addActionListener(this);
		cancelButton.addActionListener(this);
		resetButton.addActionListener(this);
		GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addComponent(sPane)
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addComponent(applyButton)
        				.addComponent(cancelButton)
        				.addComponent(resetButton))
        				);
        layout.setVerticalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        						.addComponent(applyButton)		
        						.addComponent(cancelButton)
        						.addComponent(resetButton))
        				.addComponent(sPane))
        						
        				);
		pack();
		setVisible(true);
	}

	private ArrayList<ChangeListener> changeListener;
	public void addChangeListener( ChangeListener cl){
		if (changeListener == null)
			changeListener = new ArrayList<ChangeListener>();
		changeListener.add(cl);
	}
	private void fireChangeEvent(){
		for ( ChangeListener cl: changeListener )
			cl.stateChanged(new ChangeEvent(this));
	}
	
	public void actionPerformed(ActionEvent e) {
		if ("profile-apply".equals(e.getActionCommand())) {
			apply = true;
			System.out.println("Apply button");
			fireChangeEvent();
		} else if ("profile-cancel".equals(e.getActionCommand())){
			cancel = true;
			System.out.println("Profile Filtering cancelled");
			fireChangeEvent();
		} else {
			;
		}
		setVisible(false);
	}

	/**
	 * @return the applyButton
	 */
	public boolean isApply() {
		return apply;
	}

	/**
	 * @return the isCancel
	 */
	public boolean isCancel() {
		return cancel;
	}

	
}
