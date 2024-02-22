package com.gatespace.tr69.codegen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.darwinsys.lang.GetOpt;
import com.darwinsys.lang.GetOptDesc;
import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import com.falcontechnology.tr69.acsdatamodel.SchemaVersions;

import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.DocumentDocument.Document;
import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;

/**
 * This assumes that the Data Model instance document has been normalized and
 * contains the definition of a single model.
 * The only references are to dataTypes which are included in the instance document.
 * All includes have been merged. 
 * 
 * @author dmounday
 *
 */
public class CWMPcCodeGenerator {

	// TR-069 Object parameter tree
	CodeGenObject root;
	BuilderProfiles profiles;
	// XML Document 
	DocumentDocument docDoc;
	Model model;
	public String cwmpCDir;
	public String codeGenDir;
	public String codeGenModelFname;
	public String tablePreamble= null;
	public String functionsPreamble = null;
	public boolean genSCReference = false;
	public boolean seperateStubs = false;
	public boolean genObjTypeDefs = false;
	public boolean ignoreDeprecated = false;
	public boolean ignoreDeprecatedAlias = false;
	public boolean genAliasInstanceGetSet = false;
	public boolean genAddObjStaticInstanceStubs = false;

	/**
	 * Construct CWMPcCodeGenerator object.
	 * 
	 * @param modelFname: file path of filtered DM instance file.
	 * @param fname: file path of DM Instance file.
	 * @param selectedProfiles: profile to include in DM instance
	 * @param addedObjParam: Additional object and parameter to add to DM instance.
	 */
	public CWMPcCodeGenerator(String modelFname, String fname, String selectedProfiles,
			CustomObjParam custom ){
		codeGenModelFname = modelFname; 
		File f = new File(fname);
		try {
			docDoc = DocumentDocument.Factory.parse(f);
			Model normalizedModel = docDoc.getDocument().getModelArray(0); // only one model allowed.
			if ( selectedProfiles!=null ){
				File pf = new File(selectedProfiles);
				if (pf.exists()) {
					profiles = new BuilderProfiles(	selectedProfiles);
					if ( custom != null ){
						profiles.mergeObjParam( custom.getSelectedObjParam() );
						profiles.addCommitObjs(custom.getCommitObjs());
						profiles.addStaticAddObjs(custom.getSelectedStaticInstances());
						profiles.addInitailizeObjs(custom.getSelectedInitializeObjs());

					}
					model = filterModel( normalizedModel);
				} else
					model = normalizedModel;
			} else
				model = normalizedModel;
		} catch (XmlException e) {
			System.err.println("XML exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IO excpetion: " + e.getLocalizedMessage());
		} 
		saveCodeGenModel( codeGenModelFname );
	}
	/**
	 * Build up a Model to pass to the code generator that contains
	 * just the object and parameters from the selected Profiles.
	 * @param nModel
	 * @return
	 */
	private Model filterModel(Model nModel) {
		Model pModel = Model.Factory.newInstance();
		pModel.setName(nModel.getName());
		ModelParameter[] topLevelP = addParams("", nModel.getParameterArray());
		pModel.setParameterArray(topLevelP);
		ModelObject[] pObjs = addObjects( nModel.getObjectArray() );
		pModel.setObjectArray(pObjs);
		if (  nModel.isSetIsService() )
			pModel.setIsService(nModel.getIsService());
		if ( nModel.isSetBase())
			pModel.setBase(nModel.getBase());
		if ( nModel.isSetDescription())
			pModel.setDescription(nModel.getDescription());
		if (nModel.isSetId())
			pModel.setId(nModel.getId());
		if (nModel.isSetStatus())
			pModel.setStatus(nModel.getStatus());
		return pModel;
	}

	private ModelObject[] addObjects( ModelObject[] nObjArray) {
		ModelObject[] moObject = null;
		ArrayList<ModelObject> moList = new ArrayList<ModelObject>();
		for (int k=0; k < nObjArray.length; ++k) {
			ModelObject mo = nObjArray[k];
			if ( profiles.isProfileObject( mo.getName()) || k==0 ){ // always add root obj.
				ModelObject addMO = ModelObject.Factory.newInstance();
				addMO.set(mo);
				moList.add(addMO);
				// now fix up params
				ModelParameter[] params = addParams(mo.getName(), mo.getParameterArray());
				addMO.setParameterArray(params);
			}
		}
		if ( moList.size()>0){
			moObject = new ModelObject[moList.size()];
			for ( int i=0; i< moList.size(); ++i )
				moObject[i] = moList.get(i);
		}
		return moObject;
	}

	private ModelParameter[] addParams(String oName, ModelParameter[] nParamArray) {
		ModelParameter[] mpArray = null;
		ArrayList<ModelParameter> pList = new ArrayList<ModelParameter>();
		for ( ModelParameter mp: nParamArray){
			if ( profiles.isProfileParameter(oName + mp.getName())){
				ModelParameter addMP = ModelParameter.Factory.newInstance();
				addMP.set(mp);
				pList.add(addMP);
			}
		}
		if ( pList.size()>0){
			mpArray = new ModelParameter[pList.size()];
			for (int i=0; i< pList.size(); ++i )
				mpArray[i] = pList.get(i);
		}
		return mpArray;
	}

	public boolean run(String tablesName, String stubsName ){
		CodeGenObject.dataTypeDefinition = docDoc.getDocument().getDataTypeArray();
		if ( docDoc.getDocument().sizeOfModelArray()>1 ){
			System.err.println("XML DM document must be normalized to one DM model");
			return false;
		}

		ModelObject[] modelObjects = model.getObjectArray();
		// assume that the first ModelObject is the root object or
		// a service object such as FAPService.{i}.
		String oName = modelObjects[0].getName();
		if (( oName.indexOf('.')== oName.length()-1)
			  || ( model.isSetIsService() && model.getIsService()) ) {
			// must contain a single '.' as the last character. i. e. "Device."
			System.out.println("Generating code for: "+ oName);
			root = new CodeGenObject();
			CodeGenObject.model = model;
			if ( tablePreamble!=null ) root.setTablesPreamble(tablePreamble);
			if ( functionsPreamble != null ) root.setFunctionPreamble(functionsPreamble);
			root.setSeperateStubs(seperateStubs);
			root.setGen_scLables(genSCReference);
			root.setGenTypeDefs(genObjTypeDefs);
			root.setGenAliasInstanceGetSet(genAliasInstanceGetSet);
			root.setGenStaticInstanceAddObj(genAddObjStaticInstanceStubs);
			root.setIgnoreDeprecated(ignoreDeprecated);
			root.setIgnoreDeprecatedAlias(ignoreDeprecatedAlias);
			root.processObjects(modelObjects);
			if ( profiles == null ){
				System.err.println("profile is null. Is at least one selected?");
				return false;
			}
			root.genTables(tablesName, profiles);
			root.genStubs(stubsName, profiles);
			if ( genObjTypeDefs ) 
				root.genTypeDefs( stubsName );
		} else {
			System.err.println("CWMPcCodeGenerator.run: First ModelObject name not an object:" + oName);
			System.err.println(" model isSetService: " + model.isSetIsService() + "  isService: "+ model.getIsService() );
			
		}
		return true;
	}

	public void saveCodeGenModel(String fname) {
		if (fname != null) {
			DocumentDocument dmDoc = DocumentDocument.Factory.newInstance();
			// First add the referenced data types
			Document doc = dmDoc.addNewDocument();
			doc.setDataTypeArray(docDoc.getDocument().getDataTypeArray());
			Model m = doc.addNewModel();
			m.set(model);
			XmlOptions xo = new XmlOptions();
			HashMap<String, String> prefix = new HashMap<String, String>();
			prefix.put(SchemaVersions.CURRENT_SCHEMA_VERSION, "dm");
			prefix.put("urn:broadband-forum-org:cwmp:datamodel-report-0-1",
					"dmr");
			prefix.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
			xo.setSaveSuggestedPrefixes(prefix);

			xo.setSaveAggressiveNamespaces();
			xo.setSaveNamespacesFirst();
			xo.setSavePrettyPrint();
			// Force add the namespaces so they aren't repeated in every object.
			XmlCursor c = doc.newCursor();
			c.toNextToken();
			c.insertNamespace("dm",	SchemaVersions.CURRENT_SCHEMA_VERSION);
			c.insertNamespace("dmr", "urn:broadband-forum-org:cwmp:datamodel-report-0-1");
			c.insertNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

			File f = new File(fname);
			try {
				dmDoc.save(f, xo);
			} catch (IOException e) {
				System.out.println("saveCodeGenModel: "
						+ e.getLocalizedMessage());
			}
		}
		return;
	}

	
	public static void displayHelp(){
		System.out.println(
				"CWMPc code generator for the C object/parameter tree structures\n"+
				"\n"+
				"Options:\n"+
				"\t-d <data-model-filename>  : Path of DM definition file (.xml)\n"+
				"\t-p <selected-profiles-file>: Path to DM definition of selected profiles (.xml)\n"+
				"\t-t <tables-filename>      : Path of generated tables file (.c)\n"+
				"\t-s <function-stubs_file>  : filename or prefix of function stubs file(s)\n"+
				"\t-b                        : Flag to break function stubs into multiple files\n"+
				"\t-i <tables-preamble-file> : Include this file as preamble to tables file\n"+
				"\t-f <functions-preamble-file> : Include this file as preamble to function stubs files\n"+
				"\t-S                        : Generate sc_name references for string constants\n"+
				"\t-h                        : This help msg\n"
				);
	}
	public static void main(String[] args) {
		String dmName=null;			/* name DM instance file */
		String tablesName=null;
		String stubsName=null;
		String tablePreamble= null;
		String functionsPreamble = null;
		String profilesFile = null;
		String codeGenDMFile = null;
		boolean genSCReference = false;
		boolean seperateStubs = false;

		
		GetOptDesc options[] = { 
				new GetOptDesc('c', "code-gen-dm-instance", true),
				new GetOptDesc('d', "data-model-filename", true),
				new GetOptDesc('p', "selected-profiles-file", true),
				new GetOptDesc('t', "generated-tables-filename", true),
				new GetOptDesc('s', "function-stubs-filename", true),
				new GetOptDesc('i', "tables-preamble-file", true),
				new GetOptDesc('f', "functions-preamble-file", true),
				new GetOptDesc('S', "gen-sc-reference", false),
				new GetOptDesc('b', "seperate-stubs", false),
				new GetOptDesc('h', "help", false)
		};
		
		GetOpt optParser = new GetOpt(options);
		Map<String, String> optionsFound = optParser.parseArguments(args);
		for ( String key: optionsFound.keySet()){
			char c = key.charAt(0);
			switch (c) {
			case 'c':
				codeGenDMFile = optionsFound.get(key);
				break;
			case 'd':
				dmName = optionsFound.get(key);
				break;
			case 'p':
				profilesFile = optionsFound.get(key);
				break;
			case 't':
				tablesName = optionsFound.get(key);
				break;
			case 's':
				stubsName = optionsFound.get(key);
				break;
			case 'S':
				genSCReference = true;
				break;
			case 'i':
				tablePreamble = optionsFound.get(key);
				break;
			case 'f':
				functionsPreamble = optionsFound.get(key);
				break;
			case 'b':
				seperateStubs = true;
				break;
			case 'h':
				displayHelp();
				System.exit(0);
				break;
				
			// other options here
			default:
				break;
			}

		}
		CWMPcCodeGenerator cGen = new CWMPcCodeGenerator(codeGenDMFile, dmName, profilesFile, null);
		cGen.tablePreamble= tablePreamble;
		cGen.functionsPreamble = functionsPreamble;
		cGen.genSCReference = genSCReference;
		cGen.seperateStubs = seperateStubs;
		if ( !cGen.run(tablesName, stubsName))
			System.err.println("CWMPcCodeGenerator run failed");

	}
	
}
