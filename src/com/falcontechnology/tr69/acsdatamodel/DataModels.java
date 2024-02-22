/**
 * 
 */
package com.falcontechnology.tr69.acsdatamodel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author dmounday
 *
 */
public class DataModels {

	public ArrayList<DataModelDope> dataModels = new ArrayList<DataModelDope>();
	String directory;
	/**
	 * Construct a list of DataModels and the file they are defined in.
	 * @param dirName Directory containing the Broadband Forum Data model files.
	 */
	public DataModels( String dirName ){
		directory = dirName;
		String[] dir = new java.io.File(dirName).list(new XmlOnly() );
		for ( String fName: dir ){
			getDMList( fName );
		}
	}
	
	class XmlOnly implements FilenameFilter{
		public boolean accept(File dir, String s){
			return s.endsWith(".xml");
		}
	}
	
	private void getDMList( String fName ){
		String pathName = directory+File.separatorChar+fName;
		Builder root = new Builder();
		List<String> mList = root.getModelList(pathName);
		for ( String m: mList){
			System.out.println(pathName + "  	Data Model: "+ m);
			dataModels.add(new DataModelDope(m, pathName));
		}
	}
	
	public ArrayList<DataModelDope> getRootDataModels(){
		ArrayList<DataModelDope> rootDM = new ArrayList<DataModelDope>();
		for ( DataModelDope dm: dataModels){
			if ( dm.dataModel.startsWith("InternetGateway")
				|| (dm.dataModel.startsWith("Device"))){
				rootDM.add(dm);
			}
		}
		Collections.sort(rootDM, new Comparator<DataModelDope>(){
			public int compare( DataModelDope i1, DataModelDope i2){
				return i1.dataModel.compareTo(i2.dataModel);
			}
		});
		return rootDM;
	}

	public ArrayList<DataModelDope> getServicesModels(){
		ArrayList<DataModelDope> serviceDM = new ArrayList<DataModelDope>();
		for ( DataModelDope dm: dataModels){
			if ( !dm.dataModel.startsWith("InternetGateway")
				&& !(dm.dataModel.startsWith("Device"))){
				serviceDM.add(dm);
			}
		}
		Collections.sort(serviceDM, new Comparator<DataModelDope>(){
			public int compare( DataModelDope i1, DataModelDope i2){
				return i1.dataModel.compareTo(i2.dataModel);
			}
		});
		return serviceDM;
	}
	
	public ArrayList<DataModelDope> getDataModels(){
		return dataModels;
	}
}
