package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.*;
import static mockit.Deencapsulation.*;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gatespace.tr69.codegen.CodeGenObject;

import cwmpdatamodel.ModelObject;

public class CodeGenObject_Test {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test 
	public void testVName(){
		CodeGenObject cgo = new CodeGenObject();
		String s = invoke( cgo, "getVName", "ParameterName");
		assertEquals("parameterName", s);
	}
	@Test
	public void testProcessObjects() {

		ModelObject[] mo = new ModelObject[1];
		mo[0] = ModelObject.Factory.newInstance();
		mo[0].setName("A.");
		CodeGenObject cgo = new CodeGenObject();
		cgo.processObjects(mo, 0);
		assertEquals( "A.", cgo.getObjectName() );
		assertEquals( "A.", cgo.getObjectPath());
		ModelObject[] m2 = new ModelObject[2];
		m2[0] = ModelObject.Factory.newInstance();
		m2[1] = ModelObject.Factory.newInstance();
		m2[0].setName("A.");
		m2[1].setName("A.B.");
		CodeGenObject c2 = new CodeGenObject();
		assertTrue( c2.processObjects(m2, 0)>= m2.length);
		assertEquals( "A.", c2.getObjectName());
		
		List<CodeGenObject> oList = c2.getObjectList();
		assertEquals( "A.B.", oList.get(0).getObjectPath());
		assertEquals( "B.", oList.get(0).getObjectName());
	}
	@Test
	public void testProcessObjects_3_deep() {

		ModelObject[] m2 = new ModelObject[5];
		for (int i = 0; i< m2.length; ++i)
			m2[i] = ModelObject.Factory.newInstance();
		m2[0].setName("A.");
		m2[1].setName("A.B.");
		m2[2].setName("A.B.C.");
		m2[3].setName("A.B.D.");
		m2[4].setName("A.X.");
		CodeGenObject c2 = new CodeGenObject();
		assertTrue( c2.processObjects(m2, 0)>= m2.length);
		assertEquals( "A.", c2.getObjectName());
		
		List<CodeGenObject> oList = c2.getObjectList();
		assertEquals( "A.B.", oList.get(0).getObjectPath());
		assertEquals( "B.", oList.get(0).getObjectName());
		assertEquals( "X.", oList.get(1).getObjectName());
		
		List<CodeGenObject> oListAB = oList.get(0).getObjectList();
		assertEquals( "A.B.C.", oListAB.get(0).getObjectPath());
		assertEquals("D.", oListAB.get(1).getObjectName());
		
	}

}
