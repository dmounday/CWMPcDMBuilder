package com.falcontechnology.tr69.acsdatamodel;

import cwmpdatamodel.ProfileParameter;
import cwmpdatamodel.ParameterAccess;

public class CwmpProfileParameter {

	public ProfileParameter profileParameter;

	public CwmpProfileParameter(ProfileParameter pp) {
		//profileParameter = ProfileParameter.Factory.newInstance();
		//profileParameter.set( pp );
		profileParameter = pp;
	}


	/**
	 * @return the requirement
	 */
	public ParameterAccess.Enum getRequirement() {
		return profileParameter.getRequirement();
	}

	public ProfileParameter getProfileParameter(){
		return profileParameter;
	}


	public String getName() {
		return profileParameter.getRef();
	}

}
