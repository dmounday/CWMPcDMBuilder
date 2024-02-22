package com.gatespace.tr69.codegen;

import java.io.File;
import java.io.FilenameFilter;

public class HCodeFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(".h"));
	}


}
