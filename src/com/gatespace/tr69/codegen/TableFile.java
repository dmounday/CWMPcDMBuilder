/**
 * 
 */
package com.gatespace.tr69.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * @author dmounday
 *
 */
public class TableFile extends CodeFile {
	HashMap<String, TablesCWMPObj> objects = new HashMap<String, TablesCWMPObj>();
	HashMap<String, TablesCWMPParam> params = new HashMap<String, TablesCWMPParam>();
	
	/**
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public TableFile(String fileName) throws FileNotFoundException {
		super(fileName);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.gatespace.tr69.codegen.CodeFile#scanObjects()
	 */
	@Override
	public void scanObjects() throws IOException {
		String line;
		for(boolean start=true;;){
			line = reader.readLine();
			if ( line == null )
				return /* end of stream */;
			/**
			if ( line.startsWith("/**@obj")){
				TablesCWMPObj o = new TablesCWMPObj(line, reader);
				objects.add(o.getName(), o);
				start = false;
			} else if (line.startsWith("/**param")){
				params.
			} else if (start){
				body.append(line + eol);
			}
			***/
		}

	}

	/* (non-Javadoc)
	 * @see com.gatespace.tr69.codegen.CodeFile#getGrpList()
	 */
	@Override
	public List<ObjectGroup> getGrpList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gatespace.tr69.codegen.CodeFile#findObjectGroup(java.lang.String)
	 */
	@Override
	public Object findObjectGroup(String objName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gatespace.tr69.codegen.CodeFile#writeFile(java.io.BufferedWriter)
	 */
	@Override
	public void writeFile(BufferedWriter writer) throws IOException {
		// TODO Auto-generated method stub

	}

}
