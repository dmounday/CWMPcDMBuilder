package com.gatespace.ui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;


public class ProfileFilterList implements ItemListener{


	protected ArrayList<JCheckBox> cbList = new ArrayList<JCheckBox>();
	protected JList pList;

	public ProfileFilterList(SelectList profiles ){

		for ( int i=0; i<profiles.getSize(); ++i ){
			SelectListItem s = (SelectListItem) profiles.getElementAt(i);
			JCheckBox b = new JCheckBox(s.id);
			cbList.add(b);
			b.addItemListener(this);
			b.setSelected(s.selected);
		}
		pList = new JList();
		pList.setModel(profiles);
		pList.setLayoutOrientation(JList.VERTICAL);
		pList.setVisibleRowCount(20);
		pList.setCellRenderer( new CKBoxRenderer() );
		pList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		CheckListener ckListener = new CheckListener();
		pList.addMouseListener(ckListener);
		pList.addKeyListener(ckListener);;
		
	}
	
	/**
	 * @return the pList
	 */
	public JList getpList() {
		return pList;
	}

	public void itemStateChanged( ItemEvent e ){
		JCheckBox cb = (JCheckBox) e.getItem();
		System.out.println( "event item: "+ cb.getText());
	}
	
	class CKBoxRenderer implements ListCellRenderer {
		
		public Component getListCellRendererComponent( JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus ){
			return cbList.get(index);
			
		}
	}
	
	class CheckListener implements MouseListener, KeyListener {

		protected void checkEvent(){
			int index = pList.getSelectedIndex();
			if ( index < 0 )
				return;
			System.out.println("Clicked item: " + index);
			SelectList m = (SelectList) pList.getModel();
			JCheckBox cb = cbList.get(index);
			m.invertElementAt(index);
			cb.setSelected(!cb.isSelected());
			pList.repaint();
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			checkEvent();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
