package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.gatespace.ui.ProfileFilterForm;
import com.gatespace.ui.ProfileFilterList;
import com.gatespace.ui.SelectList;

public class ProfileFilterForm_Test {
	static public ProfileFilterForm pfForm;
	static ArrayList<String> items = new ArrayList<String>();
	static SelectList sList = new SelectList( items );
	
	static ProfileFilterList pfList = new ProfileFilterList( sList );
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		pfForm = new ProfileFilterForm(pfList.getpList());
	}

	@Test
	public void testProfileFilterForm() {
		
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void testAddChangeListener() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void testActionPerformed() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void testIsApply() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void testIsCancel() {
		fail("Not yet implemented");
	}

}
