/**
 * 
 */
package com.falcontechnology.tr69.treetable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author dmounday
 *
 */
public class DMTreeTableSelectionForm extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4519918354858710103L;
	private javax.swing.JButton applyButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton clearAllButton;
	
	private boolean apply = false;
	private boolean cancel = false;
	
	
public DMTreeTableSelectionForm (JTreeTable treeTable){

	JScrollPane scrollPane = new JScrollPane( treeTable );
	this.add(scrollPane);
	this.setTitle("Data Model Object/Parameter Selection");
	applyButton = new javax.swing.JButton("Apply  ");
	clearAllButton = new javax.swing.JButton("Reset All");
	cancelButton = new javax.swing.JButton("Cancel");

	applyButton.setActionCommand("dm-apply");
	cancelButton.setActionCommand("dm-cancel");
	clearAllButton.setActionCommand("dm-resetall");
	applyButton.addActionListener(this);
	cancelButton.addActionListener(this);
	clearAllButton.addActionListener(this);
	
	GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	getContentPane().setLayout(layout);
	layout.setAutoCreateGaps(true);
    layout.setAutoCreateContainerGaps(true);
    
    layout.setHorizontalGroup(layout.createSequentialGroup()
    		.addComponent(scrollPane)
    		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				.addComponent(applyButton)
    				.addComponent(cancelButton)
    				.addComponent(clearAllButton))
    				);
    layout.setVerticalGroup(layout.createSequentialGroup()
    		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				.addGroup(layout.createSequentialGroup()
    						.addComponent(applyButton)		
    						.addComponent(cancelButton)
    						.addComponent(clearAllButton))
    				.addComponent(scrollPane))
    						
    				);

	pack();
	setVisible(true);
}
	public boolean isApply() {
		return apply;
	}

	public boolean isCancel() {
		return cancel;
	}
	private ArrayList<ChangeListener> changeListener;

	public void addChangeListener(ChangeListener cl) {
		if (changeListener == null)
			changeListener = new ArrayList<ChangeListener>();
		changeListener.add(cl);
	}

	private void fireChangeEvent() {
		if (changeListener != null) {
			for (ChangeListener cl : changeListener)
				cl.stateChanged(new ChangeEvent(this));
		} else
			System.err.println("No changeListener defined");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("dm-apply".equals(e.getActionCommand())) {
			System.out.println("DMSelect Apply button");
			apply = true; cancel = false;
			fireChangeEvent();
			setVisible(false);
		} else if ("dm-cancel".equals(e.getActionCommand())) {
			System.out.println("DMSelect Cancel Button");
			cancel = true; apply = false;
			fireChangeEvent();
			setVisible(false);
		} else if ("dm-resetall".equals(e.getActionCommand())){
			System.out.println("DMSelect ClearAll Button");
			apply = cancel = false;
			fireChangeEvent();
			setVisible(false);
		} else {
			System.err.println("DMSelect action unidentified");
		}


	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		final DModelTreeTable dmTreeTable = new DModelTreeTable(
			"/home/dmounday/workspace/cwmpc/tr181-simple/dm-instance.xml",
			"/home/dmounday/workspace/cwmpc/tr181-simple/dm-selected-profiles.xml",
			null);
		JTreeTable treeTable = dmTreeTable.init();
		final DMTreeTableSelectionForm dtForm = new DMTreeTableSelectionForm(treeTable);
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				// init() something???
				dtForm.setVisible(true);
			}
		});
	}
}
