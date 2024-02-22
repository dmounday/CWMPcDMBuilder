/**
 * 
 */
package com.falcontechnology.tr69.acsdatamodel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

import cwmpdatamodel.BaseObject;
import cwmpdatamodel.DataTypeDefinition;
import cwmpdatamodel.DataTypeReference;
import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;
import cwmpdatamodel.Profile;
import cwmpdatamodel.ProfileObject;
import cwmpdatamodel.ProfileParameter;
import cwmpdatamodel.ParameterAccess;

import cwmpdatamodel.Status;
import cwmpdatamodel.ModelSyntax;
import cwmpdatamodel.DocumentDocument.Document;

/**
 * @author dmounday
 *
 */
public class RootDataModel {

	private Model cpeModel;
	private HashMap<String, ImportDataTypeDef> dataTypeMap;
	
	private PrintWriter errLog;
	
	public RootDataModel( PrintWriter errLogger,
			              HashMap<String, ImportDataTypeDef> importDataTypeMap,
			              String modelName){
		cpeModel = Model.Factory.newInstance();
		cpeModel.setName(modelName);
		dataTypeMap = importDataTypeMap;
		errLog = errLogger;
	}
	
	public Model getCpeModel() {
		return cpeModel;
	}
	
	public boolean updateModel( Model m){
		if ( m.isSetIsService() )
			cpeModel.setIsService(m.getIsService());
		if ( m.isSetStatus())
			cpeModel.setStatus(m.getStatus());
		for ( ModelParameter mp: m.getParameterArray()){
			if ( mp.isSetName()){
				ModelParameter np = cpeModel.addNewParameter();
				np.set(mp.copy());
				resolveType( np );
			} else 
				updateParameter( mp, cpeModel.getParameterArray());
		}
		return true;
	}
	/**
	 * Find ModelObject in the cpeModel and update it if it exists.
	 * If not found then add the new ModelObject to the cpeModel.
	 * @param mo
	 * @return
	 */
	public boolean updateObject( String pathName, ModelObject mo ){
		if ( mo.isSetName() ){
			if ( findObject( pathName )!= null ){
				System.err.println("Object present: " + pathName + " base=" + mo.getBase() );
			} else {
				System.out.println("AddObject:       " + pathName + " base=" + mo.getBase() + " (" + cpeModel.sizeOfObjectArray() +")" );
				ModelObject nObj = cpeModel.addNewObject();
				nObj.set(mo.copy()); // This copies all parameters with it.
				nObj.setName(pathName); // prepend the path
				return false;
			}
		} else {
			boolean found = false;
			System.out.println("UpdateObject:    " + pathName );
			for (ModelObject cpeObj : cpeModel.getObjectArray()) {
				if (found = cpeObj.getName().equals(pathName)) {
					// found obj -- update it.
					if (mo.getAccess() != null)
						cpeObj.setAccess(mo.getAccess());
					if (mo.getEnableParameter() != null)
						cpeObj.setEnableParameter(mo.getEnableParameter());
					if (mo.getDescription() != null)
						cpeObj.setDescription(mo.getDescription());
					if (mo.getNumEntriesParameter() != null)
						cpeObj.setNumEntriesParameter(mo
								.getNumEntriesParameter());
					for (ModelParameter mp : mo.getParameterArray()) {
						if (mp.isSetBase()) {
							if ( mp.isSetStatus() && mp.getStatus()==Status.DELETED )
								deleteModelParameter(mp.getBase(), cpeObj );
							else
								updateParameter(mp, cpeObj.getParameterArray());
						} else {
							// just copy ModelParameter to array
							ModelParameter newP = cpeObj.addNewParameter();
							newP.set(mp.copy());
							resolveType(newP);
							System.out.println("AddParameter " + newP.getName());
						}
					}
					for ( BaseObject.UniqueKey moKey: mo.getUniqueKeyArray()){
						BaseObject.UniqueKey newK = cpeObj.addNewUniqueKey();
						newK.set(moKey.copy());
					}
					break;
				}
			}
			if (!found)
				System.err.println("Update obj Failed: " + pathName );
		}
		return true;
	}

	private void deleteModelParameter(String name, ModelObject cpeObj) {
		ModelParameter[] p2 = new ModelParameter[cpeObj.sizeOfParameterArray()-1];
		for ( int i=0, j=0; j< cpeObj.sizeOfParameterArray(); ++j ){
			if ( !cpeObj.getParameterArray(j).getName().equals(name)){
				p2[i] = ModelParameter.Factory.newInstance();
				p2[i].set(cpeObj.getParameterArray(j));
				++i;
			}
		}
		cpeObj.setParameterArray(p2);
		return;	
	}

	/**
	 * Try to resolve any dataType references.
	 * @param mp
	 */
	public void resolveType( ModelParameter mp){
		if ( mp.isSetSyntax()){
			ModelSyntax sx = mp.getSyntax();
			if ( sx.isSetDataType() ){
				DataTypeReference dr = sx.getDataType();
				ImportDataTypeDef idr = null;
				String typeName = null;
				if ( dr.isSetRef() ){
					typeName = dr.getRef();
					idr = dataTypeMap.get(typeName);
				} else if ( dr.isSetBase() ){
					typeName = dr.getBase();
					idr = dataTypeMap.get(typeName);
				} else {
					System.err.println("---------- Unable to resolve type reference: "+ typeName);
					errLog.println("---------- Unable to resolve type reference: "+ typeName);
				}
				
				if ( idr != null ){
					idr.setReferenced();
				}
				//	System.out.println("resolveType: " + typeName + " to " + idr.getTypeStr(null));
				//	DataTypeDefinition dtd = idr.getDataTypeRef();
				//	sx.set(dtd);
				//	System.out.println( dtd.toString());
				//}
			} // else already resolved.
		}
	}

	public void updateProfileDef( Profile profile, String path ){
		if ( profile.isSetName() ){
			// New profile - just add it.
			Profile np = cpeModel.addNewProfile();
			np.set(profile);
			System.out.println("----- add profile: " + np.getName() + " base: "+ np.getBase() +
					" pcnt:" + cpeModel.sizeOfProfileArray());

			if ( path!= null ){
				for ( ProfileObject po: np.getObjectArray()){
					if ( !po.getRef().startsWith(path) ){
						// make Object name full path name
						po.setRef( path + po.getRef());
					}
				}
			}

		} else if ( profile.isSetBase() ){
			for ( int i=0; i< cpeModel.sizeOfProfileArray(); ++i ) {
				Profile cpeProf = cpeModel.getProfileArray(i);
				if ( cpeProf.getName().equals(profile.getBase())){
					if ( profile.isSetStatus() && profile.getStatus()== Status.DELETED ){
						deleteProfile( cpeModel, i);
						break;
					}
					updateProfileParameter( cpeProf, profile);
					updateProfileObjects( cpeProf, profile, path );
					break;
				}
			}
		}

	}
	
	private void updateProfileObjects(Profile cpeProf, Profile profile, String path) {
		for ( ProfileObject o: profile.getObjectArray()){		
			boolean found = false;
			ProfileObject[] cpeObjects = cpeProf.getObjectArray();
			for ( int cpeIndx=0; cpeIndx<cpeProf.sizeOfObjectArray(); ++ cpeIndx){
				
				if ( found = cpeObjects[cpeIndx].getRef().equals(o.getRef())){
					if ( o.isSetStatus() && o.getStatus()==Status.DELETED ){
						cpeProf.setObjectArray( deleteProfileObject( cpeProf.getObjectArray(), cpeIndx));
						break;
					}
					updateProfileObjectParameter( cpeObjects[cpeIndx], o.getParameterArray());
					for ( ProfileParameter pp: o.getParameterArray()){
						// nested obj parameter
						System.out.println("\t\tadd profile param:" + cpeProf.getName() + ": " + pp.getRef()+ " -" + pp.getStatus().toString());
						ProfileParameter npp = o.addNewParameter();
						npp.set(pp);
					}
				}
			}
			if ( !found ){
				if ( o.isSetStatus() && o.getStatus()==Status.DELETED ){
					System.out.println("\t\tprofile object status is DELETED: "+ o.getRef());
				} else {
					ProfileObject po = cpeProf.addNewObject();
					po.set(o);
					System.out.println("\t\tadd profile obj:" + cpeProf.getName() + ": " + po.getRef()+ " -"+ o.getStatus().toString());
					
				}
			}

		}
		
	}

	private void updateProfileObjectParameter(ProfileObject cpeObjects, ProfileParameter[] pParams) {
		for ( int pi=0; pi < pParams.length; ++pi ){
			ProfileParameter pp = pParams[pi];
			ProfileParameter cpeParams[] = cpeObjects.getParameterArray();
			boolean found = false;
			for ( int cpeIndx=0; cpeIndx< cpeObjects.sizeOfParameterArray(); ++ cpeIndx){
				if ( found = cpeParams[cpeIndx].getRef().equals(pp.getRef()) ){
					// update
					if ( pp.isSetStatus() && pp.getStatus()==Status.DELETED ){
						cpeObjects.setParameterArray(deleteProfileParameter( cpeObjects.getParameterArray(), cpeIndx ));
						break;
					}
				}
			}
			if ( !found ) {
				if ( pp.isSetStatus() && pp.getStatus()==Status.DELETED ){
					System.out.println("\t\tprofile param status is DELETED: "+ pp.getRef());
				} else {
					// Add new top level parameter
					System.out.println("\t\tadd profile param:" + pp.getRef());
					ProfileParameter rpp = cpeObjects.addNewParameter();
					rpp.set(pp);
				}
			}
		}
		
	}

	private ProfileObject[] deleteProfileObject(ProfileObject[] objects, int delIndx) {
		ProfileObject[] o2 = new ProfileObject[objects.length-1];
		for ( int i=0, j=0; j< objects.length; ++j){
			if ( j!=delIndx){
				o2[i] = ProfileObject.Factory.newInstance();
				o2[i].set(objects[j]);
				++i;
			} else {
				System.err.println("\t\t\tdeleteProfileObject " + objects[j].getRef());
			}
		}
		return o2;
	}

	private void updateProfileParameter(Profile cpeProf, Profile profile) {
		for ( int pi=0; pi < profile.sizeOfParameterArray(); ++pi ){
			ProfileParameter pp = profile.getParameterArray(pi);
			ProfileParameter cpeParams[] = cpeProf.getParameterArray();
			boolean found = false;
			for ( int cpeIndx=0; cpeIndx< cpeProf.sizeOfParameterArray(); ++ cpeIndx){
				if ( found = cpeParams[cpeIndx].getRef().equals(pp.getRef()) ){
					// update
					if ( pp.isSetStatus() && pp.getStatus()==Status.DELETED ){
						cpeProf.setParameterArray(deleteProfileParameter( cpeProf.getParameterArray(), cpeIndx ));
						break;
					}
				}
			}
			if ( !found ) {
				// Add new top level parameter
				if ( pp.isSetStatus() && pp.getStatus()==Status.DELETED ){
					System.out.println("\t\tprofile param status is DELETED: "+ pp.getRef());
				} else {
					System.out.println("\t\tadd profile param:" + cpeProf.getName() + ": " + pp.getRef());
					ProfileParameter rpp = cpeProf.addNewParameter();
					rpp.set(pp);
				}
			}
		}
		
	}

	private ProfileParameter[] deleteProfileParameter(ProfileParameter[] pParams,
			int delIndx) {
		ProfileParameter[] p2 = new ProfileParameter[pParams.length-1];
		for ( int i=0, j=0; j< pParams.length; ++j ){
			if ( j!=delIndx){
				p2[i] = ProfileParameter.Factory.newInstance();
				p2[i].set(pParams[j]);
				++i;
			} else {
				System.err.println("\t\t\tdeleteProfileParameter " + pParams[j].getRef());
			}
		}
		return p2;
	}

	private void deleteProfile(Model model, int delIndex) {
		Profile[] profiles = model.getProfileArray();
		Profile[] p2 = new Profile[model.sizeOfProfileArray()-1];
		for (int i=0, j=0; j<model.sizeOfProfileArray(); ++j){
			if ( j != delIndex ){
				p2[i] = Profile.Factory.newInstance();
				p2[i].set(profiles[j]);
			} 
		}
		model.setProfileArray(p2);
	}
	/**
	 * return the Profile by name or null;
	 * @param name
	 * @return
	 */
	public Profile findProfile(String name){
		for ( Profile p: cpeModel.getProfileArray()){
			if ( p.getName().equals(name))
				return p;
		}
		return null;
	}

	public void updateParameter(ModelParameter mp, ModelParameter[] cpeParams) {
		for (ModelParameter cpeMp : cpeParams) {
			if (mp.getBase().equals(cpeMp.getName())) {
				// found parameter update it.
				if (mp.isSetDescription())
					cpeMp.setDescription(mp.getDescription());
				if (mp.isSetActiveNotify())
					cpeMp.setActiveNotify(mp.getActiveNotify());
				if (mp.isSetForcedInform())
					cpeMp.setForcedInform(mp.getForcedInform());
				if (mp.isSetStatus())
					cpeMp.setStatus(mp.getStatus());
				if (mp.isSetId())
					cpeMp.setId(mp.getId());
				if (mp.isSetSyntax())
					cpeMp.setSyntax(mp.getSyntax()); // TODO: may need to be
														// more selective
														// regarding Syntax
				resolveType(cpeMp);
			}

		}
	}
	
	ModelObject findObject( String path ){
		for ( ModelObject mo: cpeModel.getObjectArray()){
			if ( mo.getName().equals(path))
				return mo;
		}
		return null;
	}
	/**
	 * Called to group descendant objects with their parent.
	 */
	public void sortObjects(){
		ModelObject[] modelObject = cpeModel.getObjectArray();
		Arrays.sort(modelObject, new Comparator<ModelObject>() {
			public int compare(ModelObject o1, ModelObject o2){
				return o1.getName().compareTo(o2.getName());
			}
		});
		cpeModel.setObjectArray(modelObject);
	}
	
	public void addService( RootDataModel service ){
		DocumentDocument dd = service.getDocumentDocument();
		Document doc = dd.getDocument();
		Model m = doc.getModelArray(0);
		if ( m.isSetIsService() && m.getIsService() ){
			ModelObject servicesObj = findObject("Device.Services.");
			if (servicesObj != null){
				ModelParameter mp = servicesObj.addNewParameter();
				mp.set(m.getParameterArray(0)); // set numberOf...
				String serviceName = m.getName();
				String fullServiceName = "Device.Services." + serviceName;
				m.setName(fullServiceName);
				ModelObject nObj = cpeModel.addNewObject();
				nObj.set(servicesObj);
			}
		}
	}

	public DocumentDocument getDocumentDocument(){
		DocumentDocument dmDoc = DocumentDocument.Factory.newInstance();
		// First add the referenced data types
		Document doc = dmDoc.addNewDocument();
		for ( String typeName: dataTypeMap.keySet()){
			ImportDataTypeDef idt = dataTypeMap.get(typeName);
			if ( idt.isReferenced()){
				DataTypeDefinition dtd = doc.addNewDataType();
				dtd.set(idt.getDataTypeRef());
			}
		}
		Model m = doc.addNewModel();
		m.set(cpeModel);
		return dmDoc;		
	}
	
	public void saveXml(String fname){
		DocumentDocument dmDoc = DocumentDocument.Factory.newInstance();
		// First add the referenced data types
		Document doc = dmDoc.addNewDocument();
		for ( String typeName: dataTypeMap.keySet()){
			ImportDataTypeDef idt = dataTypeMap.get(typeName);
			System.out.println("dataType: " + idt.getName() + "referenced: "+ idt.isReferenced());
			//if ( idt.isReferenced()){  ignore referenced. flag is not set properly.
				DataTypeDefinition dtd = doc.addNewDataType();
				dtd.set(idt.getDataTypeRef());
			//}
		}
		Model m = doc.addNewModel();
		m.set(cpeModel);
		
		XmlOptions xo = new XmlOptions();
		HashMap<String, String> prefix = new HashMap<String, String>();
		prefix.put(SchemaVersions.CURRENT_SCHEMA_VERSION, "dm");
		prefix.put("urn:broadband-forum-org:cwmp:datamodel-report-0-1", "dmr");
		prefix.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");		
		xo.setSaveSuggestedPrefixes(prefix);

		xo.setSaveAggressiveNamespaces();
		xo.setSaveNamespacesFirst();
		xo.setSavePrettyPrint();
		// Force add the namespaces so they aren't repeated in every object.
		XmlCursor c = doc.newCursor();
		c.toNextToken();
		c.insertNamespace("dm", SchemaVersions.CURRENT_SCHEMA_VERSION);
		c.insertNamespace("dmr", "urn:broadband-forum-org:cwmp:datamodel-report-0-1");
		c.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		File f = new File(fname);
		try {
			dmDoc.save(f, xo);
		} catch (IOException e) {
			System.out.println( "saveXml: " + e.getLocalizedMessage());
		}
		return;
	}
	
	public void writeObjectList(String fname){
		FileWriter f;
		try {
			f = new FileWriter(fname);
			BufferedWriter ww = new BufferedWriter(f);
			
			for( ModelObject o: cpeModel.getObjectArray() ) {
				ww.write(o.getName());
				ww.write('\n');
			}
			ww.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void writeObjParamList( String fname ){
		FileWriter f;
		try {
			f = new FileWriter(fname);
			BufferedWriter ww = new BufferedWriter(f);
			
			for( ModelObject o: cpeModel.getObjectArray() ) {
				ww.write(o.getName());
				ww.write('\n');
				for ( ModelParameter p: o.getParameterArray()){
					ww.write('\t');
					ww.write(p.getName());
					ww.write('\n');
				}
			}
			ww.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public ArrayList<String> getProfiles() {
		System.out.println("getProfiles list");
		ArrayList<String> pList = new ArrayList<String>(100);
		for ( Profile p: cpeModel.getProfileArray() ){
			pList.add(p.getName());
			System.out.println("Profile: "+ p.getName());
		}
		return pList;
	}
	
	public void writerProfileObjParamList(String fname) {
		FileWriter f;
		int profs = 0;
		int objs = 0;
		int params = 0;
		try {
			f = new FileWriter(fname);
			BufferedWriter ww = new BufferedWriter(f);
			for (Profile p : cpeModel.getProfileArray()) {
				++profs;
				ww.write(p.getName());
				ww.write('\n');
				for ( ProfileObject o: p.getObjectArray() ) {
					++objs;
					if ( o.isSetStatus() && o.getStatus()==Status.DELETED )
						ww.write('\t' + o.getRef() + "\t DELETED\n");
					else
						ww.write('\t' + o.getRef() + '\n');
					for ( ProfileParameter pp: o.getParameterArray()){
						++params;
						ww.write("\t\t");
						ww.write(pp.getRef());
						ParameterAccess.Enum x = pp.getRequirement();
						ww.write("     " + x.toString());
						if ( pp.isSetStatus() && pp.getStatus()==Status.DELETED )
							ww.write("\tDELETED");
						ww.write('\n');
					}
				}	
				
			}
			ww.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("writerProfileObjectParamList profiles: "+ profs + " objs:"+ objs + " params: "+ params);
	}

	/**
	 * Save the selected profiles as an XML document.
	 * @param file name of xml file.
	 * @param selectedNames  An ArrayList<String> of selected profile names. If null
	 * all profiles are saved in document.
	 */
	public void saveProfileXml(String fName, ArrayList<String> selectedNames) {
		ArrayList<String> resolved = resolveProfiles( selectedNames );
		System.out.println("File: " + fName + " Profiles selected: " + selectedNames.size() + " resolved to:"+ resolved.size());
		Profile[] selectedP = new Profile[resolved.size()];
		int i = 0;
		for ( String name: resolved ){
			Profile p;
			if ( (p = findProfile( name ))!=null ){
				selectedP[i] = Profile.Factory.newInstance();
				selectedP[i].set(p);
				++i;
			} else {
				System.err.println("Selected profile not found in data model:" + name);
			}
		}
		DocumentDocument dmDoc = DocumentDocument.Factory.newInstance();
		// First add the referenced data types
		Document doc = dmDoc.addNewDocument();
		Model m = doc.addNewModel();
		m.setName(cpeModel.getName());
		m.setProfileArray(selectedP);
		
		XmlOptions xo = new XmlOptions();
		HashMap<String, String> prefix = new HashMap<String, String>();
		prefix.put(SchemaVersions.CURRENT_SCHEMA_VERSION, "dm");
		prefix.put("urn:broadband-forum-org:cwmp:datamodel-report-0-1", "dmr");
		prefix.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");		
		xo.setSaveSuggestedPrefixes(prefix);

		xo.setSaveAggressiveNamespaces();
		xo.setSaveNamespacesFirst();
		xo.setSavePrettyPrint();
		// Force add the namespaces so they aren't repeated in every object.
		XmlCursor c = doc.newCursor();
		c.toNextToken();
		c.insertNamespace("dm", SchemaVersions.CURRENT_SCHEMA_VERSION);
		c.insertNamespace("dmr", "urn:broadband-forum-org:cwmp:datamodel-report-0-1");
		c.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		File f = new File(fName);
		try {
			dmDoc.save(f, xo);
		
		} catch (IOException e) {
			System.out.println( "save profiles Xml: " + e.getLocalizedMessage());
		}
		return;

		
	}

	/**
	 * examine each profile and add the profiles referenced by base and extends 
	 * attributes.
	 * @param selectedNames
	 * @return resolved list of names.
	 */
	private ArrayList<String> resolveProfiles(ArrayList<String> selectedNames) {
		Set <String> r = new HashSet<String>(selectedNames);
		for ( String name: selectedNames ){
			resolveProfile( r, name);
		}
		ArrayList<String> resolved = new ArrayList<String>(r);
		System.out.println("Profile references resolved");
		return resolved;
	}
	
	private void resolveProfile( Set<String> r, String name){
		Profile p;
		if ( (p=findProfile( name ))!=null){
			if ( p.isSetBase() ){
				System.out.println("Profile "+ name + " base: "+ p.getBase());
				r.add(p.getBase());
				resolveProfile(r, p.getBase());
			}
			if ( p.isSetExtends()){
				List<String> x = p.getExtends();
				System.out.println("Profile "+ name + " extends "+ x.toString());
				for ( String xs: x){
					r.add(xs);
					resolveProfile(r, xs);
				}
			}
			
		}else {
			System.err.println("Selected profile not found in data model:" + name);
		}
	}
}
