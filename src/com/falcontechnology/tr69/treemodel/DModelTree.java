package com.falcontechnology.tr69.treemodel;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;

import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.Model;

public class DModelTree {
	// XML Document 
	DocumentDocument docDoc;
	Model model;
	private ObjectTree ot;
	
	public DModelTree(String fName ){
		File f = new File(fName);
		try {
			docDoc = DocumentDocument.Factory.parse(f);
			model = docDoc.getDocument().getModelArray(0); // only one model allowed.
		} catch (XmlException e) {
			System.err.println("XML exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IO excpetion: " + e.getLocalizedMessage());
		} 
	}
	
	public ObjectTree init(BuilderProfiles selectedProfiles){
		ot = new ObjectTree();
		ot.initTree(model, selectedProfiles);
		return ot;
	}
	
	public void run() {
		ot = new ObjectTree();
		ot.initTree(model, null);
		ot.displayTree();
		ot.setVisible(true);
	}
	

	public static void main(String args[]){
		
		DModelTree dmTree = new DModelTree("/home/dmounday/workspace/cwmpc/tr181-simple/dm-instance.xml");
		dmTree.run();
	}

}
