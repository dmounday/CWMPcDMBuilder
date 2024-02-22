/**
 * 
 */
package com.gatespace.tr69.codegen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dmounday
 *
 */
public class TablesCWMPObj {
	List<String> macros = new ArrayList<String>();
	List<String> objects = new ArrayList<String>();
	String	name;
	
	public TablesCWMPObj(String line, BufferedReader reader) throws IOException {
		super();
		int e = line.lastIndexOf('.');
		name = line.substring(8, e+1);
		while ( true ) {
			String buf = reader.readLine();
			if (buf == null)
				return;
			if ( buf.startsWith("/**@endobj"))
				return;
			if ( buf.startsWith("CPE")){
				// macro
				macros.add(buf); 
			} else if ( buf.startsWith("CWMPObject ")){
				continue;
			} else {
				int s = buf.indexOf("{\"");
				if ( s>0 ){
					objects.add(buf.substring(s+3));
				}
			}
		}
	}

	public String getName() {
		return name;
	}
	
	
}
