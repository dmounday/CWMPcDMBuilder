/**
 * 
 */
package com.falcontechnology.tr69.acsdatamodel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.xmlbeans.XmlException;

import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.DocumentDocument.Document;
import cwmpdatamodel.Model;
import cwmpdatamodel.Profile;

/**
 * @author dmounday
 *
 */
public class BuilderProfiles implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -665090120266762633L;
	HashMap<String, CwmpProfile > profiles = new HashMap<String,CwmpProfile>();
	// 
	Set<String> allObjects = new HashSet<String>(200);
	Set<String> allParams = new HashSet<String>(2000);
	//
	
	Set<String>commitObjs = new HashSet<String>(20);		// generate commit calls for Objs.
	Set<String>staticAddObjs = new HashSet<String>(20);		// generate addObj calls to create initial instance.
	Set<String>initializeObjs = new HashSet<String>(20);	// generate initialize calls to initialize initial obj.
	/**
	 * File name is full path to DM instance file with Profile definitions.
	 * @param fname
	 */
	public BuilderProfiles( String fname ){
	
		File f = new File(fname);
		System.out.println("BuilderProfiles: "+ fname);
		try {
			DocumentDocument docDoc = DocumentDocument.Factory.parse(f);
			Document doc = docDoc.getDocument();
			Model model = doc.getModelArray(0);
			for ( Profile profile: model.getProfileArray()){
				CwmpProfile cp = new CwmpProfile( profile );
				profiles.put(cp.getName(), cp);
				allObjects.addAll(cp.getObjectNameList());
				allParams.addAll(cp.getParams());
			}
		} catch (XmlException e) {
			System.err.println("IO exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IO excpetion: " + e.getLocalizedMessage());
		} 
		
	}
	
	public BuilderProfiles(){};
	
	public void addProfile( Profile profile ){
		CwmpProfile cp = new CwmpProfile( profile );
		profiles.put(cp.getName(), cp);
		allObjects.addAll(cp.getObjectNameList());
		allParams.addAll(cp.getParams());
	}
	
	public void delProfile( Profile profile){
		CwmpProfile cp = new CwmpProfile( profile );
		profiles.put(cp.getName(), cp);
		allObjects.removeAll(cp.getObjectNameList());
		allParams.removeAll(cp.getParams());	
	}
	public ArrayList<String> getProfileNames(){
		ArrayList<String> names = new ArrayList<String>();
		Set<String> keys = profiles.keySet();
		for ( String s: keys ){
			names.add(s);
		}
		return names;
	}

	public boolean isProfileObject(String oName) {
		//System.out.println("isProfileObject: "+ oName + " "+ allObjects.contains(oName));
		return allObjects.contains(oName);
	}
	
	public boolean isProfileParameter(String pName ){
		//System.out.println("isProfileParmeter: "+ pName + " " + allParams.contains(pName));
		return allParams.contains(pName);
	}
	
	public Set<String> getAllObject(){
		return allObjects;
	}
	public Set<String> getAllParameters(){
		return allParams;
	}
	/**
	 * Merge the sets as follows:
	 * 1. SDiff = allXXX - addedSet  *Remove any elements not in added set.
	 * 2. allXXX -= SDiff;
	 * 3. allXXX += addedSet.        *Add all elements in added set.
	 * @param addedObjParam
	 */
	public void mergeObjParam(ArrayList<String> addedObjParam) {
		Set<String> filterObjects = new HashSet<String>(200);
		Set<String> filterParams = new HashSet<String>(2000);
		for ( String s: addedObjParam ){
			if ( s.endsWith("."))
				filterObjects.add(s);
			else
				filterParams.add(s);
		}
		// 
		Set<String> diffObj = new HashSet<String>(allObjects);
		diffObj.removeAll(filterObjects);
		allObjects.removeAll(diffObj);
		allObjects.addAll(filterObjects);
		
		Set<String>diffP = new HashSet<String>(allParams);
		diffP.removeAll(filterParams);
		allParams.removeAll(diffP);
		allParams.addAll(filterParams);
	}
	
	public void addCommitObjs(ArrayList<String> addedCommits) {
		for ( String s: addedCommits ){
			commitObjs.add(s);
		}

	}
	
	public boolean isCommitObj(String name){
		return commitObjs.contains(name);
	}
	public void addStaticAddObjs(ArrayList<String> added) {
		for ( String s: added ){
			staticAddObjs.add(s);
		}

	}
	public boolean isStaticAddObj(String name){
		return staticAddObjs.contains(name);
	}
	public void addInitailizeObjs(ArrayList<String> added) {
		for ( String s: added ){
			initializeObjs.add(s);
		}

	}
	public boolean isInitializeObj(String name){
		return initializeObjs.contains(name);
	}
}
