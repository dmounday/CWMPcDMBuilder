/**
 * 
 */
package com.falcontechnology.tr69.acsdatamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import cwmpdatamodel.Profile;
import cwmpdatamodel.ProfileObject;

/**
 * @author dmounday
 *
 */
public class CwmpProfile {

	Profile	profile;
	
	private TreeMap<String, CwmpProfileObject> objects = new TreeMap<String, CwmpProfileObject>();
	
	public CwmpProfile(Profile p) {
		profile = p;
		for ( ProfileObject po: profile.getObjectArray()){
			CwmpProfileObject o = new CwmpProfileObject( po );
			objects.put(o.getName(), o);
		}
		
	}

	public CwmpProfileObject getProfileObject(String name){
		return objects.get(name);
	}
	
	
	public List<String> getObjectNameList(){
		ArrayList<String> ret = new ArrayList<String>(objects.keySet());
		return ret;
	}

	public String getName() {
		return profile.getName();
	}

	/**
	 * return set of full parameter path names for all parameters in profile object.
	 * @return
	 */
	public Set< String> getParams() {
		Set<String> params = new HashSet<String>(100);
		for ( String name: objects.keySet()){
			CwmpProfileObject o = objects.get(name);
			for ( String pName: o.getParameterNameList()){
				String fullParamPath = name+pName;
				params.add(fullParamPath);
				System.out.println("Profile Params: "+ fullParamPath);
			}
		}
		return params;
	}

}
