package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import com.gatespace.tr69.codegen.CustomObjParam;

public class BuilderProfile_Test {

	static BuilderProfiles profiles;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		profiles = new BuilderProfiles("/home/dmounday/workspace/cwmpc/tr181-simple/dm-selected-profiles.xml");
	}

	@Test
	public void testGetAllObjects() {
		Set<String> pObjs = profiles.getAllObject();
		assertTrue( pObjs.size()>0 );
		// something we can sort
		ArrayList<String> list = new ArrayList<String>(pObjs);
		Collections.sort(list);
		for( String s: list){
			System.out.println(s);
		}
	}

	@Test
	public void testGetAllParams() {
		Set<String> p = profiles.getAllParameters();
		assertTrue( p.size()>0 );
		// something we can sort
		ArrayList<String> list = new ArrayList<String>(p);
		Collections.sort(list);
		for( String s: list){
			System.out.println(s);
		}
	}


	@Test
	public void testIsProfileXXX() {
		BuilderProfiles p = new BuilderProfiles("/home/dmounday/workspace/cwmpc/tr181-simple/dm-selected-profiles.xml");
		ArrayList<String> op = new ArrayList<String>();
		//ArrayList<String> commits = new ArrayList<String>();
		op.add("Device.DNS.Client.Server.{i}.Enable");
		op.add("Device.DNS.Client.");
		p.mergeObjParam(op);
		assertTrue( p.isProfileParameter("Device.DNS.Client.Server.{i}.Enable"));
		assertFalse( p.isProfileParameter("Device.DNS.Client.Server.{i}.Alias"));
		assertTrue( p.isProfileObject("Device.DNS.Client."));
		assertFalse( p.isProfileObject("Device.DeviceInfo."));
	}

}
