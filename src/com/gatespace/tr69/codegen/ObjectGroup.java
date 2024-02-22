/**
 * 
 */
package com.gatespace.tr69.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dmounday
 *
 */
public class ObjectGroup {
	final String OBJTAG= "/**@obj";
	final String OBJENDTAG = "/**@endobj";
	private String eol = System.getProperty("line.separator");
	String objName;
	private StringBuilder body = new StringBuilder(300);
	private List<CodeStub> stubsList = new ArrayList<CodeStub>();
	
	public ObjectGroup(String objline, BufferedReader reader) throws IOException{
		if ((!objline.startsWith(OBJTAG)))
			throw new IOException();
		body.append(objline + eol);
		int lth = objline.length() - 3; // remove trailing **/
		objName = objline.substring(OBJTAG.length()+1, lth).trim();
		// now scan for 1st @param and save any lines.
		String line = null;
		for(boolean start=true;;) {
			line = reader.readLine();
			if ( line == null)
				throw new IOException(); // file format error.
			if (line.startsWith(OBJENDTAG)){
				return;
			}
			if ( line.startsWith("/**@param")){
				stubsList.add(new CodeStub(line, reader));
				start = false;
			} else if (start){
				body.append(line + eol); // keep lines up to first @param.
			} 
		}
	}

	public String getObjName() {
		return objName;
	}

	public StringBuilder getBody() {
		return body;
	}

	public List<CodeStub> getStubList() {
		return stubsList;
	}

	public CodeStub findParam(String key){
		for ( CodeStub cs: stubsList){
			if ( cs.getStubName().equals(key))
				return cs;
		}
		return null;
	}
	
	public void writeGroup( BufferedWriter writer) throws IOException{
		writer.write(body.toString());
		for ( CodeStub cs: stubsList){
			cs.writeStub( writer );
		}
		writer.write(OBJENDTAG + " " + objName + " **/"+eol+eol);
	}
	
}
