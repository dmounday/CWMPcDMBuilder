package com.falcontechnology.tr69.treemodel;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

	  /**
	 * 
	 */
	private static final long serialVersionUID = 3958572934410492447L;

	CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

	  ChangeEvent changeEvent = null;

	  JTree tree;

	  public CheckBoxNodeEditor(JTree tree) {
	    this.tree = tree;
	  }

	  public Object getCellEditorValue() {
	    JCheckBox checkBox = (JCheckBox) renderer.getNodeRenderer();
	    NodeCheckBox checkBoxNode = new NodeCheckBox(checkBox.getText(),
	        checkBox.isSelected());
	    checkBoxNode.setFullPath(renderer.getNodeCheckBox().getFullPath());
	    checkBoxNode.setProfileItem(renderer.getNodeCheckBox().isProfileItem());
	    return checkBoxNode;
	  }

	  public boolean isCellEditable(EventObject event) {
	    boolean returnValue = false;
	    if (event instanceof MouseEvent) {
	      MouseEvent mouseEvent = (MouseEvent) event;
	      TreePath path = tree.getPathForLocation(mouseEvent.getX(),
	          mouseEvent.getY());
	      if (path != null) {
	        Object node = path.getLastPathComponent();
	        if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
	          DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
	          Object userObject = treeNode.getUserObject();
	          //returnValue = ((treeNode.isLeaf()) && (userObject instanceof NodeCheckBox));
	          if ( userObject instanceof NodeCheckBox ){
	        	  NodeCheckBox cb = (NodeCheckBox)userObject;
	        	  if ( cb.isProfileItem() )
	        		  returnValue = false;
	        	  else 
	        		  returnValue = !cb.getText().equals("model:");
	          }
	        }
	      }
	    }
	    return returnValue;
	  }

	  public Component getTreeCellEditorComponent(JTree tree, Object value,
	      boolean selected, boolean expanded, boolean leaf, int row) {

	    Component editor = renderer.getTreeCellRendererComponent(tree, value,
	        true, expanded, leaf, row, true);

	    // editor always selected / focused
	    ItemListener itemListener = new ItemListener() {
	      public void itemStateChanged(ItemEvent itemEvent) {
	        if (stopCellEditing()) {
	          fireEditingStopped();
	        }
	      }
	    };
	    if (editor instanceof JCheckBox) {
	      ((JCheckBox) editor).addItemListener(itemListener);
	    }
	    return editor;
	  }
	}

