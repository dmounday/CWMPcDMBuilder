package com.falcontechnology.tr69.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.falcontechnology.tr69.acsdatamodel.DataModelDope;
import com.falcontechnology.tr69.acsdatamodel.DataModels;

public class DataModelsTest {

	public static String dirName = "/home/dmounday/bbfdm";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDataModels() {
		DataModels dm = new DataModels(dirName);
		ArrayList<DataModelDope> dmd = dm.getDataModels();
		assertTrue("DataModels array empty", dmd.size()>0);
	}

	@Test
	public void testGetRootDataModels() {
		DataModels dm = new DataModels(dirName);
		ArrayList<DataModelDope> dmd = dm.getRootDataModels();
		assertTrue("root DataModels array empty", dmd.size()>0);
	}

	@Test
	public void testGetServicesModels() {
		DataModels dm = new DataModels(dirName);
		ArrayList<DataModelDope> dmd = dm.getServicesModels();
		assertTrue("services DataModels array empty", dmd.size()>0);
	}

}
