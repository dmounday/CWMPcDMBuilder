package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gatespace.tr69.codegen.ObjectGroup;
import com.gatespace.tr69.codegen.StubsFile;


public class StubsFileTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testScanObjects() {
		try {
			StubsFile sf = new StubsFile( "rawManServer.c");
			sf.scanObjects();
			List<ObjectGroup> og = sf.getGrpList();
			for ( ObjectGroup o: og){
				System.out.println(o.getObjName());
			}
			assertEquals(10, sf.getGrpList().size());
			StringBuilder b = sf.getBody();
			assertEquals(0, b.indexOf("/*-----") );
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			fail("File not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail("IOException on scanObjects call");
		}
	}

}
