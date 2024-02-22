package com.gatespace.tr69.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import com.darwinsys.io.FileIO;

import com.gatespace.ui.GlobalOptions;

public class MergeCode {

	String dataModelDirPath;
	String cwmpDir;
	String genCodeDirPath;
	private String oldCodeDirPath;
	/**
	 * Construct merger object. 
	 * If code is present in the codeGenDirPath sub-directory it is moved to the 
	 * "old" sub-directory. If not the code from the templatePath directory is moved to
	 *  the "old" sub-directory (first time code is generated for a data model).
	 * This constructor is called before the code is generated into the "generated"
	 * sub-directory.
	 * @param cwmpDirPath  : Data model directory such as: "~/workspace/cwmpc/"
	 * @param codeGenDirPath: Subdir containing meta-files and destination for merged code.
	 * @param templatePath: Template code used to initialize the merge functions.
	 */
	public MergeCode( String cwmpDirPath, String codeGenDirPath, String templatePath) {
		super();
		
		this.dataModelDirPath = codeGenDirPath;
		this.cwmpDir = cwmpDirPath;
		genCodeDirPath = dataModelDirPath + GlobalOptions.GEN_SUBDIR;
		oldCodeDirPath = dataModelDirPath + GlobalOptions.OLD_DIR_NAME;
		if ( isOldCode()){
			copyDir(dataModelDirPath, oldCodeDirPath, new CHCodeFilter());
		} else {
			// check code gen directory exists.
			
			File genDir = new File(oldCodeDirPath);
			if ( !genDir.exists())
				genDir.mkdir();
			// initialize contents of "old" from template *.c and *.h files that are do not begin with "cpe".
			String [] chFNs = new java.io.File(templatePath).list( 
					new FilenameFilter(){
						@Override
						public boolean accept(File dir, String name) {
							return (name.endsWith(".c")||name.endsWith(".h"))&& (!name.startsWith("cpe"));
						}
						
					});
			if ( chFNs == null) {
				return;
			}
			for (String fn: chFNs){
				try {
					System.out.println("copy " +templatePath+File.separatorChar+fn +" to " +oldCodeDirPath+File.separatorChar+fn);
					FileIO.copyFile(templatePath+File.separatorChar+fn, oldCodeDirPath+File.separatorChar+fn);
				} catch (FileNotFoundException e) {
					System.err.println("Unable to copy " +templatePath+File.separatorChar+fn +" to " +oldCodeDirPath+File.separatorChar+fn);
				} catch (IOException e) {
					System.err.println("Unable to copy " +templatePath+File.separatorChar+fn +" to " +oldCodeDirPath+File.separatorChar+fn);
				}
			}
			// copy the common support and definition files to the datamodel dir. Which include any file starting with "cpe".
			try {
				FileIO.copyFile(templatePath+File.separatorChar+"targetsys-h", dataModelDirPath+File.separatorChar+"targetsys.h");
			} catch (Exception e) {
				System.err.println("Unable to copy targetsys-h from code-gen-template directory");
			} 
			try {
				FileIO.copyFile(templatePath+File.separatorChar+"Makefile", dataModelDirPath+File.separatorChar+"Makefile");
			} catch (Exception e) {
				System.err.println("Unable to copy Makefile from code-gen-template directory");

			}
			String [] cpeFNs = new java.io.File(templatePath).list( 
					new FilenameFilter(){
						@Override
						public boolean accept(File dir, String name) {
							return name.startsWith("cpe") && (name.endsWith(".c")||name.endsWith(".h"));
						}
						
					});
			for (String fn: cpeFNs){
				String fp = templatePath+File.separatorChar+fn;
				try {
					System.out.println("Copy "+ fp +" to "+ dataModelDirPath+File.separatorChar+fn);
					FileIO.copyFile(fp, dataModelDirPath+File.separatorChar+fn);
				} catch (Exception e) {
					System.err.println("Unable to copy " + fp +" to "+ dataModelDirPath+File.separatorChar+fn);
				} 
			}
		}
	}
	/**
	 * Return true if the Code Gen directory contains *.c or *.h files.
	 * @return
	 */
	public boolean isOldCode(){
		String [] codeFiles = new java.io.File(dataModelDirPath).list(new CHCodeFilter());
		return codeFiles.length>0;
	}
	/**
	 * Copy the filtered files in dataModelDirPath directory to the destDir sub-directory.
	 * Skip files that start with the strings "cpe" or "targetsys". 
	 */
	public void copyDir(String srcDir, String destDir, FilenameFilter fnFilter ){ 
		File dir = new File(destDir);
		if ( !dir.exists() ){
			if ( !dir.mkdir()){
				System.err.println("Unable to create directory: "+ destDir);
				return;
			}
		}
		String [] codeFiles = new java.io.File(srcDir).list(fnFilter);
		for ( String codeFile: codeFiles){
			File fCode = new File(srcDir+File.separatorChar+codeFile);
			String name = fCode.getName();
			if ( name.startsWith("targetsys") || name.startsWith("cpe") )
				continue;
			File impCodePath = new File(destDir+File.separatorChar+name);
			System.out.println("Move: "+ fCode.getPath() + " to: "+ impCodePath.getPath() );
			if ( !fCode.renameTo(impCodePath))
				System.err.println("Move " + fCode.getPath() + " failed");
		}
	}

	private void mergeFiles( String srcDirPath ){
		String [] cFiles = new java.io.File(srcDirPath).list(new CHCodeFilter());
		for ( String cFile: cFiles){
			File fCode = new File(cFile);
			String name = fCode.getName();
			if ( name.startsWith("cpe") ) {
				try {
					FileIO.copyFile(srcDirPath+File.separatorChar+cFile, dataModelDirPath+File.separatorChar+name);
				} catch (Exception e) {
					System.err.println("Unable to copy: " + srcDirPath+File.separatorChar+cFile 
						+ " to " + dataModelDirPath+File.separatorChar+name);
				}
				continue;  // skip merge of any files beginning with "cpe" ".
			}

			File newCode = new File(srcDirPath +File.separatorChar+name);
			if ( newCode.exists() ){
				File mrgdCode = new File(dataModelDirPath+File.separatorChar+name);
				Merge fm;
				try {
					if (name.startsWith("tables")){
						FileIO.copyFile(newCode, mrgdCode);
						System.out.println("Copy " + newCode.getAbsolutePath() + " to: "+ mrgdCode.getAbsolutePath());
					} else {
						fm = new FileMerger(oldCodeDirPath+File.separatorChar+cFile, 
							                       newCode.getPath(), mrgdCode.getPath());
						fm.merge();
					}
				} catch (FileNotFoundException e) {
					System.err.println("Merge file or files not found: "+ newCode.getPath());
				} catch (IOException e) {
					System.err.println("Merge file format error: "+ newCode.getPath());
				}
				
			}
		}		
	}
	

	public void mergeImplementation() {
		mergeFiles( genCodeDirPath );	
	}
}
