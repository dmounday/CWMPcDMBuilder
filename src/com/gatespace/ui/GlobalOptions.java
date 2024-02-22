/**
 * 
 */
package com.gatespace.ui;

import java.io.File;
import java.io.Serializable;

/**
 * @author dmounday
 *
 */
public class GlobalOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3043589516530940266L;
	public   String includeDir;
	public   String cWMPcDataModelDir;
	public   String cWMPcServiceDir;
	public   String codeGenDirPath;
	
	static final String CODE_GEN_DM_INSTANCE = "code-gen-dm-instance.xml";
	static final String DM_SELECTED_PROFILES = "dm-selected-profiles.xml";
	static final String DM_INSTANCE_FILE = "dm-instance.xml";
	public static final String GEN_SUBDIR = "generated";
	static final String TABLES_FILE = "tables.c";
	static final String STUBS_PREFIX = "";
	public static final String TEMPLATE_DIR = "code-gen-templates";
	static final String TABLES_PREAMBLE = TEMPLATE_DIR + File.separatorChar
			+ "tablegenPre";
	static final String FUNCTIONS_PREAMBLE = TEMPLATE_DIR + File.separatorChar
			+ "stubgenPre";
	public static final String OLD_DIR_NAME = "old";
	
	public	String tablePreamble= null;
	public  String functionsPreamble = null;
	public  boolean genSCReference = false;
	public  boolean seperateStubs = true;
	public  boolean genObjTypeDefs = true;
	public	boolean ignoreDeprecatedAlias = true;
	public  boolean ignoreDeprecated = false;
	public  boolean addPreambleFiles = true;
	public  boolean genAliasGetSet = true;
	public 	boolean mergeImplementationCode = true;
	public 	boolean mergeTemplateCode = true;
	public	boolean genAddObjStaticInstanceStubs = true;
}
