package com.falcontechnology.tr69.acsdatamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cwmpdatamodel.ProfileObject;
import cwmpdatamodel.ProfileParameter;

public class CwmpProfileObject {

	private TreeMap<String, CwmpProfileParameter> params = new TreeMap<String, CwmpProfileParameter>();
	public ProfileObject profileObject;
	
	public CwmpProfileObject(ProfileObject o) {
		//profileObject = ProfileObject.Factory.newInstance();
		//profileObject.set(o);
		profileObject = o;
		for ( ProfileParameter pp: o.getParameterArray()){
			CwmpProfileParameter p = new CwmpProfileParameter( pp );
			params.put(p.getName(), p);
		}
		
	}

	public List<String> getParameterNameList(){
		ArrayList<String> ret = new ArrayList<String>(params.keySet());
		return ret;
	}

	public String getName(){
		return profileObject.getRef();
	}
}
