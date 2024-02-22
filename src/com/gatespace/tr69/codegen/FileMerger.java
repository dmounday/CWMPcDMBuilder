package com.gatespace.tr69.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.darwinsys.io.FileIO;

public class FileMerger extends Merge {
	boolean noImpFile;
	
	public FileMerger(String impFileName, String rawFileName,
			String mergedFileName) throws FileNotFoundException, IOException {
		this.impFileName = impFileName;
		this.rawFileName = rawFileName;
		this.mergedFileName = mergedFileName;
		noImpFile = false;
		try {
			imp = new StubsFile( impFileName );
		} catch (FileNotFoundException e) {
			System.out.println("previous implementation file not found: "+ impFileName);
			noImpFile = true;
			return;
		}
		try {
			raw = new StubsFile( rawFileName );
		} catch (FileNotFoundException e) {
			System.err.println("RawFileName not found: "+ rawFileName);
			throw new FileNotFoundException();
		}
		try {
			writer = new BufferedWriter( new FileWriter(mergedFileName ));
		} catch (IOException e) {
			System.err.println("Unable to open merged file: "+ mergedFileName);
			throw new IOException();
		}
	}
	
	public void mergeGroups(){
		List<ObjectGroup> impG = imp.getGrpList(); // existing @obj <obj-name>
		List<ObjectGroup> rawG = raw.getGrpList(); // newly generatd @obj <obj-name>
		List<ObjectGroup> toRem = new ArrayList<ObjectGroup> ();
		for (ObjectGroup ig: impG){           // scan existing implementation obj
			if ( raw.findObjectGroup(ig.getObjName())==null){
				// if not in raw then remove Object from impG.
				toRem.add(ig);
			} else {
				// if in raw then merge stubs lines.
				mergeParam( ig, (ObjectGroup) raw.findObjectGroup(ig.getObjName()));
			}
		}
		impG.removeAll(toRem);
		List<ObjectGroup> toAdd = new ArrayList<ObjectGroup> ();
		for (ObjectGroup rg: rawG){                        // add new obj in ram to imp.
			if (imp.findObjectGroup(rg.getObjName())==null){
				toAdd.add(rg);
			}
		}
		impG.addAll(toAdd);
	}

	private void mergeParam(ObjectGroup ig, ObjectGroup rg) {
		List<CodeStub> impc = ig.getStubList();
		List<CodeStub> rawc = rg.getStubList();
		List<CodeStub> toRem = new ArrayList<CodeStub>();
		for ( CodeStub cs: impc){
			if ( rg.findParam(cs.getStubName())==null){
				toRem.add(cs);
			}
		}
		impc.removeAll(toRem);
		List<CodeStub> toAdd = new ArrayList<CodeStub>();
		for ( CodeStub cs: rawc){
			if ( ig.findParam(cs.getStubName())==null){
				toAdd.add(cs);
			}
		}
		impc.addAll(toAdd);
	}
	
	public void merge(){
		// No impFile to merge with new code so just copy it.
		if (noImpFile){
			try {
				FileIO.copyFile(rawFileName, mergedFileName);
			} catch (FileNotFoundException e) {
				System.err.println("Unable to copy "+ rawFileName + " to "+ mergedFileName);
			} catch (IOException e) {
				System.err.println("Unable to copy "+ rawFileName + " to "+ mergedFileName);
			}
			return;
		}
		try {
			imp.scanObjects();
			imp.closeCodeFile();
		} catch (IOException e) {
			System.err.println("IOException scanning objects in file: " + impFileName);
			return;
		}
		
		try {
			raw.scanObjects();
			raw.closeCodeFile();
		} catch (IOException e) {
			System.err.println("IOException scanning objects in file: " + rawFileName);
			return;
		}
		mergeGroups();
		try {
			imp.writeFile(writer);
			writer.close();
		} catch (IOException e) {
			System.err.println("Error closing file: "+ mergedFileName);
			return;
		}
		System.out.println("Merged "+ impFileName + " -> "+ rawFileName);
	}
	
}
