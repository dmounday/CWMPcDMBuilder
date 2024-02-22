package com.falcontechnology.tr69.test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.falcontechnology.tr69.acsdatamodel.ImportDataTypeDef;
import com.falcontechnology.tr69.acsdatamodel.RootDataModel;

import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;

public class RootDataModel_Test {

	static RootDataModel rootDM;
	static HashMap<String, ImportDataTypeDef> dataRefMap = new HashMap<String, ImportDataTypeDef>();

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
		rootDM = new RootDataModel(null, dataRefMap, "Device:1.9" );
	}

	@Test
	public void testGetCpeModel() {
		Model dm = rootDM.getCpeModel();
		assertEquals( "Device:1.9", dm.getName());
	}

	@Test
	public void testUpdateModel() {
		Model dm = Model.Factory.newInstance();
		ModelParameter mp = ModelParameter.Factory.newInstance();
		mp.setName("ParameterOne");
		ModelParameter n = dm.addNewParameter();
		n.set(mp.copy());
		rootDM.updateModel(dm);
		assertEquals( 1, rootDM.getCpeModel().sizeOfParameterArray());
	}


	@Test
	public void testUpdateObject() {
		Model dm = Model.Factory.newInstance();		
		ModelObject mo = ModelObject.Factory.newInstance();
		mo.setName("ObjectOne");
		ModelObject nobj = dm.addNewObject();
		nobj.set(mo.copy());
		rootDM.updateObject("ObjectOne", mo);
		assertEquals( 1, rootDM.getCpeModel().sizeOfObjectArray());
	}
	
	
	@Test
	public void testSave() {
		rootDM.saveXml("testSave.xml");
		assertTrue(true);
	}

}
