/**
 * 
 */
package com.gatespace.tr69.codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * @author dmounday
 *
 */
public abstract class Merge {
	public CodeFile imp;		// implementation file with changes.
	public CodeFile raw;		// raw generated stubs file. No changes?
	FileWriter out;
	BufferedWriter writer;
	
	String	impFileName;
	String	rawFileName;
	String	mergedFileName;
	
	public abstract void merge();
}
