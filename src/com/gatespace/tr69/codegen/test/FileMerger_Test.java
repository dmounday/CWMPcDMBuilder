package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gatespace.tr69.codegen.FileMerger;

public class FileMerger_Test {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}



	@Test
	public void testMerge() {
		try {
			FileMerger fm = new FileMerger("impManServer.c",
										   "rawManServer.c",
										   "mrgrdManServer.c");
			fm.merge();
			assertEquals(10, fm.imp.getGrpList().size());
		} catch (FileNotFoundException e) {
			fail("FileNotFoundException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("IOException");
		}
		
		
	}

}
