package com.falcontechnology.tr69.treemodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

class CheckBoxNodeRenderer implements TreeCellRenderer {
	private JCheckBox nodeRenderer = new JCheckBox();
	private NodeCheckBox nodeCheckBox;
	DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
	
	Color selectionBorderColor, selectionForeground, selectionBackground,
      textForeground, textBackground;
	Font fontValue;
	
	public JCheckBox getNodeRenderer(){
		return nodeRenderer;
	}
	
	public NodeCheckBox getNodeCheckBox(){
		return nodeCheckBox;
	}
	
	public CheckBoxNodeRenderer() {
	    //Font fontValue;
	    //fontValue = UIManager.getFont("Tree.font");
	    //if (fontValue != null) {
	    //  nodeRenderer.setFont(fontValue);
	    //}
	    //Boolean booleanValue = (Boolean) UIManager
	    //    .get("Tree.drawsFocusBorderAroundIcon");
	    //nodeRenderer.setFocusPainted((booleanValue != null)
	    //    && (booleanValue.booleanValue()));

	    selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
	    selectionForeground = UIManager.getColor("Tree.selectionForeground");
	    selectionBackground = UIManager.getColor("Tree.selectionBackground");
	    textForeground = UIManager.getColor("Tree.textForeground");
	    textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component returnValue;

		String stringValue = tree.convertValueToText(value, selected, expanded,
				leaf, row, false);
		nodeRenderer.setText(stringValue);
		nodeRenderer.setSelected(false);

		nodeRenderer.setEnabled(tree.isEnabled());

		if (selected) {
			nodeRenderer.setForeground(selectionForeground);
			nodeRenderer.setBackground(selectionBackground);
		} else {
			nodeRenderer.setForeground(textForeground);
			nodeRenderer.setBackground(textBackground);
		}

		if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
			Object userObject = ((DefaultMutableTreeNode) value)
					.getUserObject();
			if (userObject instanceof NodeCheckBox) {
				nodeCheckBox = (NodeCheckBox) userObject;
				nodeRenderer.setText(nodeCheckBox.getText());
				nodeRenderer.setSelected(nodeCheckBox.isSelected());
			}
		}
		returnValue = nodeRenderer;
		return returnValue;
	}
}
