package com.falcontechnology.tr69.treemodel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DMSelectionForm extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5850489755513320006L;
	private JTree tree;
	private javax.swing.JButton applyButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton clearAllButton;
	
	private boolean apply = false;
	private boolean cancel = false;
	private ObjectTree	objectTree;
	
	
	public boolean isApply() {
		return apply;
	}

	public boolean isCancel() {
		return cancel;
	}

	/** Creates new form DMSelectionForm */
	public DMSelectionForm(ObjectTree oTree ) {	
		objectTree = oTree;
		tree = new javax.swing.JTree(oTree.treeModel);
		JScrollPane scrollPane = new JScrollPane( tree );
		this.add(scrollPane);
		this.setTitle("Data Model Object/Parameter Selection");
		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		tree.setCellRenderer(renderer);
		tree.setCellEditor(new CheckBoxNodeEditor(tree));
		tree.setEditable(true);
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		applyButton = new javax.swing.JButton("Apply");
		clearAllButton = new javax.swing.JButton("Clear All");
		cancelButton = new javax.swing.JButton("Cancel");

		applyButton.setActionCommand("dm-apply");
		cancelButton.setActionCommand("dm-cancel");
		clearAllButton.setActionCommand("dm-clearall");
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

	public void actionPerformed(ActionEvent e) {
		if ("dm-apply".equals(e.getActionCommand())) {
			System.out.println("DMSelect Apply button");
			apply = true;
			fireChangeEvent();
			setVisible(false);
		} else if ("dm-cancel".equals(e.getActionCommand())) {
			System.out.println("DMSelect Cancel Button");
			tree.clearSelection();
			cancel = true;
			fireChangeEvent();
			setVisible(false);
		} else if ("dm-clearall".equals(e.getActionCommand())){
			System.out.println("DMSelect ClearAll Button");
			tree.clearSelection();
			apply = cancel = false;
		} else {
			System.err.println("DMSelect action unidentified");
		}


	}
	
	public JTree getJTree(){
		return tree;
	}

	public ObjectTree getObjectTree(){
		return objectTree;
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		final DModelTree dmTree = new DModelTree(
				"/home/dmounday/workspace/cwmpc/tr181-2-3/dm-instance.xml");
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				ObjectTree ot = dmTree.init(null);
				new DMSelectionForm(ot).setVisible(true);
			}
		});
	}
	
}
