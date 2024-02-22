package com.gatespace.tr69.codegen;

import java.io.File;
import java.io.FilenameFilter;

public class CHCodeFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".c")
			||  name.endsWith(".h"));
		
	}

}
