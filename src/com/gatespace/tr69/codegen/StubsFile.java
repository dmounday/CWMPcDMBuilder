/**
 * 
 */
package com.gatespace.tr69.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dmounday
 *
 */
public class StubsFile extends CodeFile {
	private StringBuilder body = new StringBuilder(1024);
	private StringBuilder tail;
	private List<ObjectGroup> grpList = new ArrayList<ObjectGroup>();
	/**
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public StubsFile(String fileName) throws FileNotFoundException {
		super(fileName);
	}

	
	public void scanObjects() throws IOException{
		String line;
		boolean end = false;
		for(boolean start=true;;){
			line = reader.readLine();
			if ( line == null )
				return /* end of stream */;
			if ( line.startsWith("/**@obj")){
				grpList.add(new ObjectGroup(line, reader));
				start = false;
				end = true;
				tail = new StringBuilder(1024);
			} else if (start){
				body.append(line + eol);
			} else if (end ){
				tail.append(line + eol);
			}
		}
	}

	public StringBuilder getBody() {
		return body;
	}

	public List<ObjectGroup> getGrpList() {
		return grpList;
	}
	
	public ObjectGroup findObjectGroup(String name){
		for ( ObjectGroup g: grpList){
			if ( g.getObjName().equals(name))
				return g;
		}
		return null;
	}

	public void writeFile(BufferedWriter writer) throws IOException {
		writer.write(body.toString());
		for ( ObjectGroup g: grpList){
			g.writeGroup(writer);
		}
		if ( tail!=null && tail.length()>0){
			writer.write(tail.toString());
		}
	}
	
}
