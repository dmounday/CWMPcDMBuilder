package com.gatespace.tr69.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class CodeStub {

	private String eol = System.getProperty("line.separator");
	private String stubName;
	private StringBuilder body = new StringBuilder(300); 

	/**
	 * If the paramLine does not begin with /**@param then throw an IOException.
	 * Set the stubName from the @param line and add remaining lines up thru the
	 * @endparam to the body.
	 * @param paramLine: String containing the first line of the @param group.
	 * @param reader: BufferedReader source stream.
	 * @throws IOException
	 */
	public CodeStub(String paramLine, BufferedReader reader) throws IOException {
		if ((!paramLine.startsWith("/**@param")))
			throw new IOException(); 
		body.append(paramLine + eol);
		int lth = paramLine.length() - 3; // remove trailing **/
		stubName = paramLine.substring(10, lth).trim();
		String line = null;
		do {
			line = reader.readLine();
			if ( line==null)
				throw new IOException();
			body.append(line + eol);
		} while ( !line.startsWith("/**@endparam"));
	}
	/**
	 * Add the next code body to the StringBuilder body property and set the 
	 * stubName.
	 * @param reader BufferedReader.
	 * @throws IOException EOF.
	 */
	public CodeStub(BufferedReader reader) throws IOException {
		String line;
		do {
			line = reader.readLine();
			if ( line == null )
				throw new IOException();
		} while ( (!line.startsWith("/**@param")));
		body.append(line + eol);
		int lth = line.length() - 3; // remove trailing **/
		stubName = line.substring(10, lth).trim();
		do {
			line = reader.readLine();
			if ( line == null)
				throw new IOException();
			body.append(line +eol);
		} while ( !line.startsWith("/**@endparam"));
	}

	public String getStubName() {
		return stubName;
	}

	public StringBuilder getBody() {
		return body;
	}
	
	public void writeStub(BufferedWriter writer) throws IOException {
		//writer.write(eol);
		writer.write(body.toString());
	}

	
}
