package com.gatespace.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractListModel;


public class SelectList extends AbstractListModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5944724018756119516L;
	ArrayList<SelectListItem> sList = new ArrayList<SelectListItem>();

	
	public SelectList(ArrayList<String> nList){
		for ( String s: nList){
			sList.add(new SelectListItem(s));
		}
		Collections.sort(sList, new Comparator<SelectListItem>(){
			public int compare( SelectListItem i1, SelectListItem i2){
				return i1.id.compareTo(i2.id);
			}
		});
		
	}
	
	public int getSize() {
		return sList.size();
	}

	public Object getElementAt(int i) {
		return sList.get(i);
	}	
	
	public String[] getNames(){
		String names[] = new String[sList.size()];
		int j = 0;
		for ( SelectListItem i: sList)
			names[j++] = i.id;
		return names;
	}
	/**
	 * Invert the selected property and return its new value
	 * of the element at index i.
	 * @param i
	 * @return inverted value.
	 */
	public boolean invertElementAt( int i){
		return sList.get(i).selected = !sList.get(i).selected;
	}
	
	public ArrayList<String> getSelectedNames(){
		ArrayList<String> ret = new ArrayList<String>();
		for (SelectListItem i: sList){
			if ( i.selected )
				ret.add(i.id);
		}
		return ret;
	}
	/**
	 * Set the named SelectListItem as selected and return it's
	 * index.
	 * @param name
	 * @return index of selected item or -1.
	 */
	public int setSelected( String name ){
		for (int i=0; i< sList.size(); ++i){
			SelectListItem item = sList.get(i);
			if ( item.id.equals(name)){
				item.selected = true;
				return i;
			}
		}
		return -1;
	}
}
