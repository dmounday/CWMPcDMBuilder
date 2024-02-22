package com.gatespace.tr69.codegen.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gatespace.tr69.codegen.CodeStub;

public class CodeStubTest {
	String eol = System.getProperty("line.separator");
	
	String t =
		"CPE_STATUS getHostsHostIPv4Address_IPAddress(Instance *ip, char **value)"+eol+
		"{"+eol+
		"	HostsHostIPv4Address *p = (HostsHostIPv4Address *)ip->cpeData;"+eol+
		"	if ( p->iPAddress )"+eol+
		"		*value = GS_STRDUP(p->iPAddress);"+eol+
		"	return CPE_OK;"+eol+
		"}"+eol+
		"/**@endparam                                               **/"+eol+
		""+eol+
		"/**@param HostsHostIPv6Address_IPAddress                     **/"+eol+
		"CPE_STATUS getHostsHostIPv6Address_IPAddress(Instance *ip, char **value)"+eol+
		"{"+eol+
		"	HostsHostIPv6Address *p = (HostsHostIPv6Address *)ip->cpeData;"+eol+
		"	if ( p->iPAddress )"+eol+
		"		*value = GS_STRDUP(p->iPAddress);"+eol+
		"	return CPE_OK;"+eol+
		"}"+eol+
		"/**@endparam                                               **/"+eol+
		""+eol+
		"/**@param HostsHost_Alias                     **/"+eol+
		"CPE_STATUS setHostsHost_Alias(Instance *ip, char *value)"+eol+
		"{"+eol+
		"	GS_HostsHost *p = (GS_HostsHost *)ip->cpeData;"+eol+
		"	COPYSTR(p->alias, value);"+eol+
		"	return CPE_OK;"+eol+
		"}"+eol+
		"CPE_STATUS getHostsHost_Alias(Instance *ip, char **value)"+eol+
		"{"+eol+
		"	GS_HostsHost *p = (GS_HostsHost *)ip->cpeData;"+eol+
		"	if ( p->alias )"+eol+
		"		*value = GS_STRDUP(p->alias);"+eol+
		"	return CPE_OK;"+eol+
		"}"+eol+
		"/**@endparam                                               **/"+eol+
		"";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testCodeStubBufferedReader() {
		StringReader instr = new StringReader(t);
		BufferedReader r = new BufferedReader( instr );
		try {
			String line = "/**@param HostsHostIPv4Address_IPAddress    **/"; 
			CodeStub cs = new CodeStub( line, r );
			String name = cs.getStubName();
			//System.out.println("name>"+name+"<");
			assertEquals( "HostsHostIPv4Address_IPAddress", cs.getStubName());
			String body = cs.getBody().toString();
			assertTrue( body.startsWith("/**@"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
