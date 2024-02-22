package com.falcontechnology.tr69.acsdatamodel;
/**
 * The Builder class builds a data model file that is can be loaded by the
 * Gatespace ACS from the Broadband Forum data model definition files. The named
 * data model definition file is processed to combine the previously defined versions
 * of the parent definition files (older versions) into one normalized data model 
 * file.
 * 
 * The included files are assumed to be in the include directory defined by
 * the program parameters.
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.darwinsys.lang.GetOpt;
import com.darwinsys.lang.GetOptDesc;

import cwmpdatamodel.ComponentDefinition;
import cwmpdatamodel.ComponentReference;
import cwmpdatamodel.ComponentObject;

import cwmpdatamodel.DataTypeDefinition;
import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.DocumentDocument.Document;
import cwmpdatamodel.Import;
import cwmpdatamodel.Import.DataType;
import cwmpdatamodel.Model;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.ModelParameter;
import cwmpdatamodel.Profile;

public class Builder {
	private String includeDir=null;
	
	private HashMap<String, ImportDataTypeDef> dataRefMap = new HashMap<String, ImportDataTypeDef>();
	private HashMap<String, CWMPDoc> parsedDocs = new HashMap<String, CWMPDoc>();
	public RootDataModel rootDM;
	
	private StringWriter  log_sw = new StringWriter();
	public PrintWriter errLog = new PrintWriter(log_sw);
	
	
	public Builder(){};
	
	public Builder(String includeDir){
		if ( includeDir.endsWith("/"))
			this.includeDir = includeDir;
		else
			this.includeDir = includeDir + "/";
	}

	public String getLog() {
		errLog.flush();
		String ret = log_sw.toString();
		// reset log
		log_sw = new StringWriter();
		errLog = new PrintWriter(log_sw);
		return ret;
	}

	/**
	 * Return a List<String> object containing the model names contained 
	 * by the Data model file, fname.
	 * @param fname
	 * @return List<String> of model names.
	 */
	public List<String> getModelList( String fname){
		String path;
		List<String> ml = new ArrayList<String>();
		if ( !(new File(fname).isAbsolute()) && includeDir != null ){
			path = includeDir+File.separatorChar + fname;
		} else
			path = fname;
		File f = new File(path);
		//System.out.println("parse for model list fname= "+ fname);

		try {
			XmlOptions xo = setUpParseOptions();
			DocumentDocument docDoc = DocumentDocument.Factory.parse(f, xo);
			Document doc = docDoc.getDocument();
			Model[] models = doc.getModelArray();
			for ( Model m: models){
				ml.add(m.getName());
			}	
		} catch (XmlException e) {
			System.err.println("IO exception: " + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IO excpetion: " + e.getLocalizedMessage());
		} 		
		return ml;
	}
	
	public void addServices(Builder service){
		//cwmpDM.addService( service.cwmpDM );
	}

	public void scanParameters( ComponentDefinition cd, String path){
		for ( ModelParameter p: cd.getParameterArray()){
			System.out.println( "scanParameter: " + p.getName() + " Cd:" + cd.getName());
			ModelObject cpeObj = rootDM.findObject(path);
			if ( cpeObj !=null ){
				if ( p.isSetBase() )
					rootDM.updateParameter(p, cpeObj.getParameterArray());
				else {
					ModelParameter newP = cpeObj.addNewParameter();
					newP.set(p.copy());
					rootDM.resolveType( newP );	
					System.out.println("AddParameter " + newP.getName());
				}
			}
		}

	}
	
	public void scanModelObjs( ModelObject mObjs[], String prefix){
		for (ModelObject m : mObjs) {
			String oName = m.isSetBase()? m.getBase(): m.getName();
			if ( prefix != null )
				oName = prefix + oName;
			if ( oName!=null ){
				rootDM.updateObject(oName, m);
			} else {
				errLog.println("!!!! Null object name for model: "+ m.getName());
			}
		}		
	}

	public void scanComponentObjs( ComponentObject mObjs[], String prefix){
		for (ComponentObject m : mObjs) {
			String oName = m.isSetBase()? m.getBase(): m.getName();
			if ( prefix != null )
				oName = prefix + oName;
			// TODO: figure out what to do with the new definition of components and the rootDM.
			//if ( oName!=null ){
			//	rootDM.updateObject(oName, m);
			//} else {
			//	errLog.println("!!!! Null object name for component: "+ m.getName());
			//}
		}		
	}
	public void scanModelProfiles(Profile[] profileArray, String path ){
		for ( Profile p: profileArray  ){
			System.out.println("Profile: " + p.getName() + " base=" + p.getBase());
			rootDM.updateProfileDef(p, path);
		}
	}
	
	public void scanComponentDef(CWMPDoc doc, ComponentDefinition cDef, String path){
		processComponentRefs(doc, cDef.getComponentArray(), path);
		scanParameters( cDef, path );
		ComponentObject mObjs[] = cDef.getObjectArray();
		scanComponentObjs( mObjs, path);
		scanModelProfiles( cDef.getProfileArray(), path );	
	}

	/**
	 * Search for the component definition, refName. If not found then follow
	 * import chain. This method may make recursive calls.
	 * @param doc
	 * @param refName
	 * @return true if found component definition.
	 */
	public boolean processImportComponents( CWMPDoc doc, String refName, String path){
		Import im = doc.findImportForComponent(refName);
		if ( im != null ){
			System.out.println(">>try to import def: " + refName + " " + im.getFile());
			Import.Component imc = doc.findImportComponent(refName);
			String name = imc.isSetRef()? imc.getRef(): imc.getName();
			CWMPDoc imDoc = getXmlDoc(im.getFile());
			if ( imDoc != null ){
				ComponentDefinition cDef = imDoc.getComponentDef(name);
				if ( cDef != null ){
					System.out.println("<<Found def of " + name + " " + im.getFile());
					scanComponentDef( imDoc, cDef, path);
					return true;
				} else {
					if ( !processImportComponents( imDoc, name, path)){
						System.out.println("<<Undefined ref " + name + " " + im.getFile());			
					} else {
						System.out.println("processImportComponents rtn true "+ name + " " + path);
						return true;
					}
				}
			} else {
				System.err.println("Unable to import file: " + im.getFile());
			}
		} else {
			System.err.println("No import element for " + refName);
		}
		return false;
	}
	/**
	 * For each component reference find the component definition. If not
	 * in the current document then follow the import chain.
	 * @param doc
	 * @param componentReferences
	 */
	public void processComponentRefs (CWMPDoc doc, ComponentReference[] componentReferences, String path ){
		for (ComponentReference cRef: componentReferences ){
			ComponentDefinition cDef = doc.getComponentDef(cRef);
			if ( cDef != null ){ 
				System.out.println("fnd local def of " + cDef.getName());
				scanComponentDef( doc, cDef, path==null? cRef.getPath(): path);
			} else {
				// not defined in this doc. Try imports
				if ( !processImportComponents( doc, cRef.getRef(),  path==null? cRef.getPath(): path )){
					errLog.println("No ComponentDataTyperef for " + cRef.getRef() + " " + cRef.getPath());
					System.out.println("No Component def for " + cRef.getRef() + " " + cRef.getPath());
			
				}
			}
		}	
	}

	private void processDataType( DataTypeDefinition di){
		System.out.println("DataTypeDefinition: name: " + di.getName());
		ImportDataTypeDef typeRef = new ImportDataTypeDef(di.getName(), di);
		dataRefMap.put(di.getName(), typeRef);		
	}
	/**
	 * If the reqDataTypes is not null then only process the DateTypes that are in the
	 * reqDataTypes list; otherwise, process all DataTypeDefinitions.
	 * @param doc
	 * @param reqDataTypes
	 */
	private void processDataTypes(CWMPDoc doc, DataType[] reqDataTypes){
		DataTypeDefinition[] dt = doc.getDocument().getDataTypeArray();
		for (DataTypeDefinition di : dt) {
			if ( reqDataTypes != null){
				for (DataType r: reqDataTypes){
					if ( di.getName().equals(r.getName()))
						processDataType( di );
				}
			} else
				processDataType(di);

		}
		for ( Import im: doc.getDocument().getImportArray()){
			if ( im.sizeOfDataTypeArray() > 0 ){
				CWMPDoc imDoc = getXmlDoc( im.getFile() );
				if ( imDoc != null ){ 
					processDataTypes( imDoc, im.getDataTypeArray());
				}
			}
		}
	}
	/**
	 * Scan the Model m. First for the component references followed by scanning
	 * the model object array.
	 * 
	 * @param m
	 */
	public void processModel (CWMPDoc doc, Model m){
		errLog.println("scanModel: "+ m.getName());
		System.out.println("############### processModel " + m.getName());
		processComponentRefs(doc, m.getComponentArray(), null );
		rootDM.updateModel( m );		// top-level
		ModelObject[] mObjs = m.getObjectArray();   // top-level objs.
		scanModelObjs( mObjs, null );
		Profile mProfiles[] = m.getProfileArray();
		System.out.println("->scanModelProfile for model: "+ m.getName() + " profiles: "+ m.sizeOfProfileArray());
		scanModelProfiles( mProfiles, null);
	}

	
	/**
	 * If the xml file identified by fname has been parsed the DocumentDocument
	 * is in the parsedImports map. Just return it. otherwise parse the file
	 * and add the document to the map.
	 * @param fname
	 * @return DocumentDocument or null.
	 */
	public CWMPDoc getXmlDoc( String fname ){
		CWMPDoc doc =  parsedDocs.get(fname);
		if ( doc == null ){
			System.out.println("<<<import file: "+ fname);
			String path;
			if ( !(new File(fname).isAbsolute()) && includeDir != null ){
				path = includeDir +File.separatorChar+ fname;
			} else
				path = fname;
			File f = new File(path);
			XmlOptions xo = setUpParseOptions();
			try {
				doc = new CWMPDoc( DocumentDocument.Factory.parse(f, xo));
				parsedDocs.put(fname, doc);
			} catch (XmlException e) {
				errLog.println("IO exception: " + e.getLocalizedMessage());
			} catch (IOException e) {
				errLog.println("IO excpetion: " + e.getLocalizedMessage());
			} 
		} else {
			System.out.println("<<<use cached doc for file: "+ fname);
		}
		return doc;
		
	}

	/**
	 * Follow the model reference chain to the final model (no ref="m-name" 
	 * attribute). This method recursively calls its self to follow the reference chain.
	 * When the end of the references is found that model is processed first and
	 * then updated by the stacked models.
	 * 
	 * @param fname  XML document file
	 * @param targetModel Data model to search for.
	 * @return null if end of model reference chain.
	 */
	public CWMPDoc processModelChain( String fname, String targetModel ){
		System.out.println("<<<<<<<<<< processChainFile " + fname + " model: "+ targetModel);
		CWMPDoc doc = getXmlDoc(fname);
		if ( doc != null ){
			// Import any dataType that are referenced in the import array.
			processDataTypes( doc, null);
			
			Model model = doc.getModel(targetModel);
			if ( model != null ){
				if ( model.isSetBase() ){
					Import im = doc.findImportForModel(model.getBase());
					processModelChain( im.getFile(), model.getBase());  //TODO: handle ref=
				}
				processModel( doc, model );
			}
		}
		System.out.println(">>>>>>>>>>> processFile complete: " + fname + " model: " + targetModel);
		return doc;	
	}

	/**
	 * The XmlParsing options are set to map the older namespace references to the 
	 * latest namespace. The namespaces are "claimed" to be backwards compatible so
	 * this should be valid.
	 * @return
	 */
	
	private XmlOptions setUpParseOptions(){
		XmlOptions xo = new XmlOptions();
		HashMap<String, String> m = new HashMap<String, String>(5);
		for ( String oldVersion: SchemaVersions.BackSchemaVersions ){
			m.put(oldVersion, SchemaVersions.CURRENT_SCHEMA_VERSION);
		}

		xo.setLoadSubstituteNamespaces(m);
		return xo;
	}
	

	/**
	 * Start at the root level with a selected model name.
	 * @param fname  File path of root data model defintion.
	 * @param modelName Model name of root data model( i. e. Device:2.2).
	 * @return Normalized XmlObject representing the root data model.
	 */
	public RootDataModel parseTopLevel(String fname, String modelName){

		errLog.println("parseTopLevel " + fname + "  model: "+ modelName);
		System.out.println("parseTopLevel " + fname + "  model: "+ modelName);

		rootDM = new RootDataModel( errLog, dataRefMap, modelName );
		processModelChain( fname, modelName);
		rootDM.sortObjects();
		return rootDM;
	}
	/**
	 * 
	 * xml-datamodel-in >stdout
	 */
	public static void main(String[] args) {
		String serviceDMPath=null;  /* services data model to import for Device.Services. object */
		String dmName=null;			/* name of data model in top-level file */
		String includeDir=null;	
		boolean addService = false;
		
		GetOptDesc options[] = { 
				new GetOptDesc('i', "import-dir", true),
				new GetOptDesc('s', "ignore-import-spec", false),
				new GetOptDesc('d', "data-model-name", true),
				new GetOptDesc('S', "import-services-datamodel", true)
		};

		GetOpt optParser = new GetOpt(options);
		Map<String, String> optionsFound = optParser.parseArguments(args);
		for ( String key: optionsFound.keySet()){
			char c = key.charAt(0);
			switch (c) {
			case 'i':
				includeDir = optionsFound.get(key);
				break;
			//case 's':
			//	ignoreImportSpec = true;
			//	break;
			case 'd':
				dmName = optionsFound.get(key);
				break;
			case 'S':
				serviceDMPath = optionsFound.get(key);
				addService = true;
				break;
			// other options here
			default:
				break;
			}

		}
		List<String> files = optParser.getFilenameList();
		if (!files.isEmpty()) {
			String fname = files.get(0);
			Builder root = new Builder(includeDir);
			Builder service = new Builder(includeDir);
			root.parseTopLevel( fname, dmName );
			if ( addService  ){
				service.parseTopLevel(serviceDMPath, null);
			}
			if ( addService ){
				root.addServices( service );
			}
			/***
			StringWriter datamodel = new StringWriter();
			new XmlSerializer(datamodel).serialize(root.cwmpDM);
			if ( files.size()>1 ){
				Writer output;
				try {
					output = new BufferedWriter(new FileWriter(files.get(1)));
					output.write(datamodel.toString());
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			***/			
		} else {
			System.err.println("Use: builder [-s] [-i include-dir] [-d <data-model-name] <dm-file> <out-file>");
		}
	}

}
