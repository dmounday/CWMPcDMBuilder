package com.gatespace.tr69.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public abstract class CodeFile {
	public String eol = System.getProperty("line.separator");
	String fileName;
	BufferedReader reader;

	

	public CodeFile(String fileName) throws FileNotFoundException {
		super();
		this.fileName = fileName;
		reader = new BufferedReader(new FileReader(fileName));
		
	}
	
	public void closeCodeFile() throws IOException{
		reader.close();
	}

	public abstract void scanObjects() throws IOException;
	public abstract List<ObjectGroup> getGrpList();

	public abstract Object findObjectGroup(String objName);

	public abstract void writeFile(BufferedWriter writer) throws IOException;

}
