package com.falcontechnology.tr69.treemodel;


// ObjectTree.java
// A simple test to see how we can build a tree and populate it.  This version
// builds the tree from hashtables.
//
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;

import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;

public class ObjectTree extends JFrame implements TreeSelectionListener, ItemListener, ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4169788344583408684L;
	public DefaultTreeModel treeModel;
	DefaultMutableTreeNode root;	
	public JTree tree;
	DefaultMutableTreeNode leadSelection;
	private BuilderProfiles profiles;
	
	public ObjectTree() {
		super("DM Selection Tree");
	}
	/**
	 * test function 
	 * normal display is done in DMSelectionForm.
	 */
	public void displayTree(){
		setSize(400, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		tree = new JTree(treeModel);
		CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
		tree.setCellRenderer(renderer);
		tree.setCellEditor(new CheckBoxNodeEditor(tree));
		tree.setEditable(true);
		
		JScrollPane scrollPane = new JScrollPane( tree );	
		getContentPane().add(scrollPane, BorderLayout.CENTER);	
		//
		tree.getSelectionModel().
		      setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		//tree.addTreeSelectionListener(this);
		//EEL eel = EEL.getInstance();
		//eel.addGui();
		//tree.addTreeSelectionListener(eel);
		//tree.getModel().addTreeModelListener(eel);
	}

	public DefaultTreeModel initTree(Model dmModel, BuilderProfiles selectedProfiles){
		profiles = selectedProfiles;
		root = new DefaultMutableTreeNode(new NodeCheckBox("model:", false));
		treeModel = new DefaultTreeModel(root);
		scanObjects( dmModel.getObjectArray());
		return treeModel;
	}

	private void scanObjects( ModelObject[] nObjArray) {
		for (int k=0; k < nObjArray.length; ++k) {
			NodeCheckBox cbox = new NodeCheckBox(nObjArray[k].getName(), false);
			cbox.setFullPath(nObjArray[k].getName());
			DefaultMutableTreeNode oNode = new DefaultMutableTreeNode(cbox);
			treeModel.insertNodeInto(oNode, root, k);
			if (profiles!=null && profiles.isProfileObject(nObjArray[k].getName())){
				//System.out.println("preselect: "+ nObjArray[k].getName());
				cbox.setSelected(true);
				cbox.setProfileItem(true);
			}
			scanParams( oNode, nObjArray[k].getName(), nObjArray[k].getParameterArray());
		}
	}

	private void scanParams(DefaultMutableTreeNode oNode,String oName, ModelParameter[] nParamArray) {
		for ( int i = 0; i<nParamArray.length; ++i){
			NodeCheckBox cbox = new NodeCheckBox(nParamArray[i].getName(), false);
			DefaultMutableTreeNode pNode = new DefaultMutableTreeNode(cbox);
			treeModel.insertNodeInto(pNode, oNode, i);
			String oParamName = oName + nParamArray[i].getName();
			cbox.setFullPath(oParamName);
			if (profiles !=null && profiles.isProfileParameter(oParamName)){
				//System.out.println("preselect: "+ nParamArray[i].getName());
				cbox.setSelected(true);
				cbox.setProfileItem(true);
			}
		}
	}

	@Override
	public void stateChanged( ChangeEvent e	){
		NodeCheckBox cbox = (NodeCheckBox)e.getSource();
		System.out.println( "NodeCheckBox change: " + cbox.getText() + " :"+ cbox.isSelected());
		
	}
	@Override
	public void itemStateChanged(ItemEvent e){
		NodeCheckBox cbox = (NodeCheckBox) e.getItemSelectable();
		System.out.println("NodeCheckBox "+ cbox.getText() + " " + cbox.isSelected());
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath leadPath = e.getNewLeadSelectionPath();
		System.out.print("valueChanged: ");
		if (leadPath!=null){
			leadSelection = (DefaultMutableTreeNode)leadPath.getLastPathComponent();
			System.out.println("selectEvent: " + leadSelection.getUserObject().toString());
		}
		/***
		int min = tree.getMinSelectionRow();
		int max = tree.getMaxSelectionRow();
		for ( int i=min; i<=max; ++i){
			TreePath sPath = tree.getPathForRow(i);
			if ( tree.isPathSelected(sPath)) {
				DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)sPath.getLastPathComponent();
				System.out.println("Selected Node: " + sNode.toString());
			}
		}
		***/
		int selected[] = tree.getSelectionRows();
		for ( int sel: selected ){
			TreePath sPath = tree.getPathForRow(sel);
			DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)sPath.getLastPathComponent();
			System.out.println("Selected Node: " + sNode.toString());	
		}
		repaint();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getSelected(){
		ArrayList<String> selected = new ArrayList<String>(); 
		for ( Enumeration<TreeNode> e = root.breadthFirstEnumeration(); e.hasMoreElements();){
			TreeNode n = e.nextElement();
			NodeCheckBox cb = (NodeCheckBox) ((DefaultMutableTreeNode) n).getUserObject();
			if ( cb.isSelected()){
				if ( cb.getFullPath()==null){
					System.err.println("null fullPath: " + cb.getText());
				} else {
					selected.add(cb.getFullPath());
					System.err.println("Selected obj: "+ cb.getFullPath());
				}
			}
		}
		
		return selected;
	}

}
