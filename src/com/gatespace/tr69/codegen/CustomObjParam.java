package com.gatespace.tr69.codegen;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Wrapper for custom object and parameter selected.
 * Selections are saved in program state files.
 * @author dmounday
 *
 */
public class CustomObjParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4114551147541055377L;
	
	private ArrayList<String> selectedObjParam;
	private ArrayList<String> selectedCommitObj;
	private ArrayList<String> selectedStaticInstances;
	private ArrayList<String> selectedInitializeObjs;
	
	public CustomObjParam() {
	}

	public CustomObjParam( ArrayList<String> selectedOP, ArrayList<String> selectedCommit,
			ArrayList<String> staticsInstance, ArrayList<String> initializeObjs){
		selectedObjParam = selectedOP;
		selectedCommitObj = selectedCommit;
		selectedStaticInstances = staticsInstance;
		selectedInitializeObjs = initializeObjs;
	}
	public ArrayList<String> getSelectedObjParam() {
		return selectedObjParam;
	}

	public void setObjParam(String s) {
		selectedObjParam.add(s);
	}

	public ArrayList<String> getCommitObjs() {
		return selectedCommitObj;
	}

	public void setCommitObj(String s) {
		selectedCommitObj.add(s);
	}

	public ArrayList<String> getSelectedStaticInstances() {
		return selectedStaticInstances;
	}

	public void setSelectedStaticInstance(String selectedInstance) {
		selectedStaticInstances.add(selectedInstance );
	}

	public ArrayList<String> getSelectedInitializeObjs() {
		return selectedInitializeObjs;
	}

	public void setSelectedInitializeObj(String selectedInitializeObj) {
		selectedInitializeObjs.add(selectedInitializeObj);
	}

	
	
}
