/**
 * 
 */
package com.falcontechnology.tr69.treetable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.xmlbeans.XmlException;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import com.gatespace.tr69.codegen.CustomObjParam;


import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.Model;

/**
 * @author dmounday
 *
 */
public class DModelTreeTable {
	// XML Document 
	DocumentDocument docDoc;
	Model model;
	private DataModelModel dmm;
	BuilderProfiles selectedProfiles;
	CustomObjParam custom;
	
	public DModelTreeTable(String fName, String profileFname, CustomObjParam custom ){
		File f = new File(fName);
		try {
			docDoc = DocumentDocument.Factory.parse(f);
			model = docDoc.getDocument().getModelArray(0); // only one model allowed.
		} catch (XmlException e) {
			System.err.println("XML exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IO excpetion: " + e.getLocalizedMessage());
		} 
		File pf = new File(profileFname);
		if (pf.exists()) {
			selectedProfiles = new BuilderProfiles(profileFname);
		} else {
			selectedProfiles = null;
		}
		if ( custom!=null && custom.getSelectedObjParam().size()>0 ){
			selectedProfiles.mergeObjParam(custom.getSelectedObjParam());
			selectedProfiles.addCommitObjs(custom.getCommitObjs());
			selectedProfiles.addStaticAddObjs(custom.getSelectedStaticInstances());
			selectedProfiles.addInitailizeObjs(custom.getSelectedInitializeObjs());
		}
	}
	
	public DataModelModel getDataModelModel() {
		return dmm;
	}

	public JTreeTable init(){
		dmm = new DataModelModel(model);
		dmm.initTreeTable(model, selectedProfiles);
		JTreeTable treeTable = new JTreeTable( dmm );
		return treeTable;
	}
	
	public void run() {
		JFrame frame = new JFrame("DM TreeTable");
		dmm = new DataModelModel(model);
		dmm.initTreeTable(model, selectedProfiles);
		JTreeTable treeTable = new JTreeTable( dmm );
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
		});
		frame.getContentPane().add(new JScrollPane( treeTable));
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String args[]){
		
		DModelTreeTable dmTree = new DModelTreeTable(
				"/home/dmounday/workspace/cwmpc/tr-098-testdmgen/dm-instance.xml",
				"/home/dmounday/workspace/cwmpc/tr-098-testdmgen/dm-selected-profiles.xml",
				null);
		dmTree.run();
	}

}
