/**
 * 
 */
package com.gatespace.ui;

import javax.swing.table.AbstractTableModel;

/**
 * @author dmounday
 *
 */
public class ObjectCodeGenOptionsTable extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3672033214134529050L;

	private String[] columnNames = {"Object Path", "Commit Callback"};
	
	private Object[][] data;
	
	public ObjectCodeGenOptionsTable(Object[][] data) {
		this.data = data;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		return data[arg0][arg1];
	}

	/*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }	
}
