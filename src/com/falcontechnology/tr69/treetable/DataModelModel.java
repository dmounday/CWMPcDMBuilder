/**
 * 
 */
package com.falcontechnology.tr69.treetable;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import com.gatespace.tr69.codegen.CustomObjParam;
import com.falcontechnology.tr69.treemodel.NodeCheckBox;

import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;


/**
 * @author dmounday
 *
 */
public class DataModelModel extends AbstractTreeTableModel implements TreeTableModel {

	static protected String[] cNames = {"", "Name", "Commit Call", "Init Obj/Param"};
	@SuppressWarnings("rawtypes")
	static protected Class[] cTypes = {Boolean.class, TreeTableModel.class, Boolean.class, Boolean.class};
	static protected int[] cWidths = {2, 150, 6, 6};
	
	private BuilderProfiles profiles;
	
	public DataModelModel(Model m){
		super( new ObjParamNode("Model:", true, m ));
	}
	
	public void initTreeTable(Model dmModel, BuilderProfiles selectedProfiles){
		profiles = selectedProfiles;
		scanObjects( dmModel );	
	}
	private void scanObjects(Model m) {
		ModelObject[] nObjArray = m.getObjectArray();
		ObjParamNode root = (ObjParamNode)getRoot();
		for (int k=0; k < m.sizeOfObjectArray(); ++k) {
			ObjParamNode node = new ObjParamNode(nObjArray[k].getName(), false, nObjArray[k] );
			node.setFullPath(nObjArray[k].getName());
			root.param[k] = node;
			if (profiles!=null ) { 
				if ( profiles.isProfileObject(nObjArray[k].getName())){
					//System.out.println("preselect: "+ nObjArray[k].getName());
					node.setSelected(true);
					node.setProfileItem(true);
				}
				if ( profiles.isCommitObj(nObjArray[k].getName())){
					node.generateCommit = true;
				}
				if ( profiles.isStaticAddObj(nObjArray[k].getName())){
					node.staticAddObj = true;
				}
				if ( profiles.isInitializeObj(nObjArray[k].getName())){
					node.initializeObj = true;
				}
			}
			
			node.scanParams( profiles, nObjArray[k]);
		}
	}
	/**
	 * Scan objects and childern for selected objects and parameters.
	 * @param selected  Array in which to return path of selected items.
	 */
	public void scanSelectedObjs(ArrayList<String> selected ){
		for (ObjParamNode n: ((ObjParamNode)getRoot()).param){
			if ( n.isSelected() ){
				selected.add(n.getFullPath());
				System.out.println("Sel: " + n.getFullPath());
				n.scanSelected(selected);
			}
		}
	}
	/**
	 * Return nodes with GenerateCommit set.
	 * @param selected
	 */
	public void scanCommits( ArrayList<String> commits, ArrayList<String> statics, ArrayList<String> initObjs ){
		for (ObjParamNode n: ((ObjParamNode)getRoot()).param){
			if ( n.isGenerateCommit()){
				commits.add(n.getFullPath());
			}
			if ( n.isStaticAddObj())
				statics.add(n.getFullPath());
			if (n.isInitializeObj()){
				initObjs.add(n.getFullPath());
				System.out.println("init: " + n.getFullPath());
			}
			initObjs.addAll(n.getParamInits());
		}
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	protected Object[] getChildern(Object node){
		ObjParamNode opnode = (ObjParamNode)node;
		return opnode.getChildern(); 
	}
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	@Override
	public Object getChild(Object node, int i) {
		return getChildern(node)[i];
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(Object node) {
		Object[] childern = getChildern(node);
		return (childern == null)? 0: childern.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		ObjParamNode node = (ObjParamNode)arg0;
		
		for(int i=0; i<node.param.length; ++i)
			if ( node.param[i].equals(arg1))
				return i;
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	@Override
	public Object getRoot() {
		return root;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	@Override
	public boolean isLeaf(Object arg) {
		ObjParamNode op = (ObjParamNode)arg;
		return ( op.param!=null && op.param.length>0 )? false: true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return cNames.length;
	}

	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return cNames[column];
	}


	@Override
	public int getPreferredColumnWidth(int column) {
		return cWidths[column];
	}
	
	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int column) {
		return cTypes[column];
	}

	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#getValueAt(java.lang.Object, int)
	 */
	@Override
	public Object getValueAt(Object node, int column) {
		ObjParamNode op = (ObjParamNode)node;
		switch (column){
		case 1:
			return op.getFullPath();
		case 0:
			return op.isSelected(); 
		case 2:
			return op.generateCommit;
		case 3:
			return op.isInitializeObj();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#isCellEditable(java.lang.Object, int)
	 */
	@Override
	public boolean isCellEditable(Object node, int column) {
		System.out.println("isCellEditable(): col:" + column);
		ObjParamNode op = (ObjParamNode)node;
		if ( column == 0)
			return true;
		if ( column == 1)
			return true;
		if ( column==2 && op.getFullPath()!=null && !op.getFullPath().endsWith("."))
			return false;  // return false for parameter names.
		if ( column==3 && op.getFullPath()!=null) // allow parameters to set init.
			return true;
		return column>0? true: false;
	}

	/* (non-Javadoc)
	 * @see com.falcontechnology.tr69.treetable.TreeTableModel#setValueAt(java.lang.Object, java.lang.Object, int)
	 */
	@Override
	public void setValueAt(Object aValue, Object node, int column) {
		ObjParamNode op = (ObjParamNode)node;
		System.out.print("node: " + op!=null? op.getFullPath(): "null");
		System.out.println("  setValueAt(): col:"+ column);
		switch (column){
		case 1:
			return;
		case 0:
			op.setSelected(!op.isSelected());
			return; 
		case 2:
			op.generateCommit = !op.generateCommit;
			return;
		case 3:
			op.initializeObj = !op.initializeObj;
			return;
		}
		return;
	}

	public CustomObjParam getSelectedCustom(){
		ArrayList<String> selected = new ArrayList<String>();
		ArrayList<String> commits = new ArrayList<String>();
		ArrayList<String> staticsInstance = new ArrayList<String>();
		ArrayList<String> initializeObjs = new ArrayList<String>();
		scanSelectedObjs(selected);
		scanCommits( commits, staticsInstance, initializeObjs);
		return new CustomObjParam( selected, commits, staticsInstance, initializeObjs );
	}

}

class ObjParamNode extends NodeCheckBox {

	public ObjParamNode[] param;
	public boolean generateCommit;
	public boolean staticAddObj;
	public boolean initializeObj;
	
	public ObjParamNode(String text, boolean selected, Model m) {
		super(text, selected);
		generateCommit = false;
		if ( m!=null &&  m.sizeOfObjectArray()>0){
			param = new ObjParamNode[m.sizeOfObjectArray()];
			//System.err.println("ObjParamNode Model: " + text + " size:" + m.sizeOfObjectArray());
		}
	}

	public ObjParamNode(String text, boolean selected, ModelObject mo) {
		super(text, selected);
		generateCommit = false;
		if ( mo!=null &&  mo.sizeOfParameterArray()>0) {
			param = new ObjParamNode[mo.sizeOfParameterArray()];
			//System.err.println("ObjParamNode ModelObject: " + text + " size:" + mo.sizeOfParameterArray());
		}
	}
	public ObjParamNode(String text ){
		super(text, false);
	}
	public void scanParams(BuilderProfiles profiles, ModelObject mo) {
		ModelParameter[] mp = mo.getParameterArray();
		for ( int i = 0; i<mo.sizeOfParameterArray(); ++i){
			param[i] = new ObjParamNode("param");
			String oParamName = mo.getName() + mp[i].getName();
			param[i].setFullPath(oParamName);
			param[i].generateCommit = false;
			
			if (profiles !=null ) {
				if ( profiles.isProfileParameter(oParamName)){
					//System.out.println("preselect: "+ nParamArray[i].getName());
					param[i].setSelected(true);
					param[i].setProfileItem(true);
				} else {
					param[i].setSelected(false);
					param[i].setProfileItem(false);
				}
				if ( profiles.isInitializeObj(oParamName)){
					System.out.println("init true " + oParamName);
					param[i].initializeObj = true;
				}
			}
		}
	}
	
	protected Object[] getChildern(){
		return param;
	}
	
	public String toString(){
		return getFullPath();
	}
	
	public boolean isGenerateCommit(){
		return generateCommit;
	}
	
	public boolean isStaticAddObj(){
		return staticAddObj;
	}
	public boolean isInitializeObj(){
		return initializeObj;
	}
	public void scanSelected(ArrayList<String> selected ){
		if ( param != null ){
			for ( ObjParamNode n: param){
				if ( n.isSelected() ){
					selected.add(n.getFullPath());
					System.out.println("      " + n.getFullPath());
				}
			}
		}
	}
	public ArrayList<String> getParamInits() {
		ArrayList<String> inits = new ArrayList<String>();
		if ( param!=null) {
			for (ObjParamNode n: param	){
				if (n.isInitializeObj()) {
					inits.add(n.getFullPath());
					System.out.println("  " + n.getFullPath());
				}
			}
		}
		return inits;
	}
}
