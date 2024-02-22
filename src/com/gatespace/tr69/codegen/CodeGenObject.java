package com.gatespace.tr69.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.falcontechnology.tr69.acsdatamodel.BuilderProfiles;
import cwmpdatamodel.ActiveNotify;
import cwmpdatamodel.ActiveNotify.Enum;
import cwmpdatamodel.DataTypeDefinition;
import cwmpdatamodel.DataTypeReference;
import cwmpdatamodel.EnumerationFacet;
import cwmpdatamodel.ListFacet;
import cwmpdatamodel.Model;
import cwmpdatamodel.ModelSyntax;
import cwmpdatamodel.ModelObject;
import cwmpdatamodel.BaseObject;
import cwmpdatamodel.BaseObject.UniqueKey;
import cwmpdatamodel.BaseObject.UniqueKey.Parameter;
import cwmpdatamodel.ModelParameter;
import cwmpdatamodel.ModelParameter.*;
import cwmpdatamodel.ParameterAccess;
import cwmpdatamodel.ReadWriteAccess;
import cwmpdatamodel.SizeFacet;
import cwmpdatamodel.Status;
//import cwmpdatamodel.Syntax; <1.8

/**
 * * Represents the TR=069 objects in the data model object/param tree.
 * 
 * @author dmounday
 * 
 */
public class CodeGenObject {
	
	// Global reference to document's DataType definitions.
	static public DataTypeDefinition[] dataTypeDefinition;
	static public Model model;
	
	private boolean rootObj;
	private String oName;
	private ModelObject mo;

	// code generator options
	static private boolean seperateStubs; // generate a different file for each root level object.
	static private boolean gen_scLables;  // Generate the string constants references for strings.
	static private boolean genTypeDefs;		// Generate typedef and fill in stubs typedef structure pointer setup.
	static private String functionPreamble; // Preamble contents for function stubs file(s).
	static private String tablesPreamble;	// Preamble contents for tables file.
	static private boolean genCWMPAliasGetSet; 
	static private boolean genStaticInstanceAddObj; // Generate tables and stubs for AddObject calls for eStaticIntance objs.
	static private boolean ignoreDeprecated;
	static private boolean ignoreDeprecatedAlias;

	String eol = System.getProperty("line.separator");

	private List<CodeGenObject> oList;

	public CodeGenObject() {
		oList = new ArrayList<CodeGenObject>();
	};

	public int processObjects(ModelObject[] modelObjects, int index) {
		rootObj = index == 0 ? true : false;
		mo = modelObjects[index];
		oName = mo.getName();
		int i = index + 1;
		while (i < modelObjects.length) {
			ModelObject o = modelObjects[i];
			String name = o.getName();
			System.out.println("po>  " + name);
			if (name.startsWith(oName)) {
				// child object
				CodeGenObject cgo = new CodeGenObject();
				oList.add(cgo);
				i = cgo.processObjects(modelObjects, i);
			} else {
				break;
			}
		}
		return i;
	}

	public int processObjects(ModelObject[] modelObjects) {
		return processObjects(modelObjects, 0);
	}

	public boolean isGenStaticInstanceAddObj() {
		return genStaticInstanceAddObj;
	}

	public void setGenStaticInstanceAddObj(boolean genStaticInstanceAddObj) {
		CodeGenObject.genStaticInstanceAddObj = genStaticInstanceAddObj;
	}

	public boolean isGenAliasInstanceGetSet() {
		return genCWMPAliasGetSet;
	}

	public void setGenAliasInstanceGetSet(boolean genAliasInstanceGetSet) {
		CodeGenObject.genCWMPAliasGetSet = genAliasInstanceGetSet;
	}

	public boolean isIgnoreDeprecatedAlias() {
		return ignoreDeprecatedAlias;
	}

	public void setIgnoreDeprecatedAlias(boolean ignoreDeprecatedAlias) {
		CodeGenObject.ignoreDeprecatedAlias = ignoreDeprecatedAlias;
	}

	public boolean isIgnoreDeprecated() {
		return ignoreDeprecated;
	}

	public void setIgnoreDeprecated(boolean ignoreDeprecated) {
		CodeGenObject.ignoreDeprecated = ignoreDeprecated;
	}

	public boolean isGen_scLables() {
		return gen_scLables;
	}

	public void setGen_scLables(boolean gen_scLables) {
		CodeGenObject.gen_scLables = gen_scLables;
	}

	public boolean isSeperateStubs() {
		return seperateStubs;
	}

	public void setSeperateStubs(boolean seperateStubs) {
		CodeGenObject.seperateStubs = seperateStubs;
	}

	public void setGenTypeDefs(boolean genTypeDefs) {
		CodeGenObject.genTypeDefs = genTypeDefs;
	}

	public boolean isGenTypeDefs() {
		return genTypeDefs;
	}

	public String getFunctionPreamble() {
		return functionPreamble;
	}

	public void setFunctionPreamble(String functionPreamble) {
		CodeGenObject.functionPreamble = functionPreamble;
	}

	public String getTablesPreamble() {
		return tablesPreamble;
	}

	public void setTablesPreamble(String tablesPreamble) {
		CodeGenObject.tablesPreamble = tablesPreamble;
	}

	/**
	 * return the full object path name.
	 * 
	 * @return
	 */
	public String getObjectPath() {
		return oName;
	}
	/**
	 * Strip trailing '.' or trailing '.{i}.'
	 * @return
	 */
	public String getRootName() {
		int i;
		if ( (i = oName.indexOf(".{i}."))>0 ){ // Service object instance
			return oName.substring(0, i);
		}
		return oName.substring(0, oName.length()-1);
	}
	/**
	 * Return the object's name fragment such as for object path A.B.C return
	 * 'C.'
	 * 
	 * @return
	 */
	public String getObjectName() {
		int i = oName.substring(0, oName.length() - 1).lastIndexOf('.');
		if (i == -1)
			return oName;
		String name = oName.substring(i + 1);
		if (name.endsWith(".{i}."))
			return name.substring(0, name.lastIndexOf(".{i}.") + 1);
		return oName.substring(i + 1);
	}

	/**
	 * Return a String contains the child objects name. For example 
	 * A.X.B.{i}. returns "B".
	 * @return
	 */
	public String getStringName() {
		String s;
		if (oName.endsWith(".{i}.")) // strip trailing .{i}. if present as in
										// a.x.b.{i}.
			s = oName.substring(0, oName.lastIndexOf(".{i}.")); // now have
																// a.x.b
		else
			// or a.x.b.
			s = oName.substring(0, oName.length() - 1); // strip last .
		int i = s.substring(0, s.length() - 1).lastIndexOf('.'); // find last .
																	// before
																	// last
																	// name.
		return s.substring(i + 1); // return last name a 'b' from a.x.b
	}
	/**
	 * Return object path stripped of the root name as a C variable name. For example:
	 * Ax.By.Cz. is return as BxCz
	 * @return
	 */
	public String getCodeGenName() {
		String s;
		if ( !model.isSetIsService())
			s = oName.substring(oName.indexOf(".") + 1); // strip root name.
		else
			s = oName;
		s = s.replace(".", "");
		s = s.replace("{i}", "");
		return s;
	}
	
	public String getTypeDefName() {
		String s = getCodeGenName();
		if ( s.length()<10 )
			return "GS_" + s;
		return s;
	}
	
	
	static final String CPE_STATUS = "CPE_STATUS ";
	
	private StringBuilder getParamStubName(ModelParameter mp){
		return new StringBuilder(getCodeGenName() + "_" + mp.getName());		
	}
	
	public StringBuilder getParamGetterName(ModelParameter mp){ 
		return new StringBuilder("get" + getCodeGenName() + "_" + mp.getName());
	}
	
	public StringBuilder getParamSetterName(ModelParameter mp){
		return new StringBuilder("set" + getCodeGenName() + "_" + mp.getName());	
	}
	
	public StringBuilder getNumberOfEntriesObjName(){
		if ( gen_scLables )
			return new StringBuilder("sc_"+getStringName());
		else 
			return new StringBuilder("\""+getStringName()+"\"");
	}

	public List<CodeGenObject> getObjectList() {
		return oList;
	}

	public ModelObject getModelObject() {
		return mo;
	}
	
	private String buildHdrGuardStr( String fName){
		int fnd = 0;
		int i;
		for ( i=fName.length(); i>0 && fnd<1; --i){
			if ( fName.charAt(i-1)==File.separatorChar )
				fnd++;
		}
		String guard = fName.substring(i, fName.length());
		guard = guard.replace('.', '_');
		guard = guard.replace(File.separatorChar, '_');
		guard = "_GS_" + guard + "_";
		guard = guard.toUpperCase();
		return guard;
	}
	public int genTypeDefs(String fname) {
		if (!rootObj) {
			System.err
					.println("Logic error: genTypeDefs can only be called for root level");
			return -1;
		}
		if (rootObj) {
			for (CodeGenObject cgo : oList) {
				String objName = cgo.getCodeGenName();
				String fileName = fname + objName + ".h";
				FileWriter f;
				//System.out.println("Generating typedef for obj: " + objName
				//		+ " in file: " + fileName);
				try {
					f = new FileWriter(fileName);
					BufferedWriter writer = new BufferedWriter(f);
					String hdrProtect = buildHdrGuardStr(fileName);
					writer.write("#ifndef "+ hdrProtect+ eol);
					writer.write("#define " + hdrProtect + eol);
					cgo.generateObjTypeDef(writer);
					writer.write("#endif /* " + hdrProtect + " */"+eol);
					writer.close();
				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
			String objName = getRootName(); /* finish root level */
			String fileName = fname + objName + ".h";
			FileWriter f;
			System.out.println("Generating typedef root for obj: " + oName
					+ " in file: " + fileName);
			try {
				f = new FileWriter(fileName);
				BufferedWriter writer = new BufferedWriter(f);
				String hdrProtect = buildHdrGuardStr( fileName );
				writer.write("#ifndef "+ hdrProtect+ eol);
				writer.write("#define " + hdrProtect + eol);
				generateObjTypeDef(writer);
				writer.write("#endif /* " + hdrProtect + " */" +eol);
				writer.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}

		}
		return 0;
	}
	/**
	 * Added enum declaration to StringBuilder s if any defined for
	 * parameter p. 
	 * @param o
	 * @param p
	 * @param s
	 * @return enum name if any defined. Otherwise; null
	 */
	@SuppressWarnings("unused")
	private String genEnum( ModelObject o, ModelParameter p, StringBuilder s ){
		int i = 0;
		s.append("typedef enum {" + eol);
		cwmpdatamodel.ModelSyntax x = p.getSyntax();
		if ( x.isSetString()){
			cwmpdatamodel.ModelSyntax.String ss = x.getString();
			if ( ss.sizeOfEnumerationArray()>0 ){
				for ( EnumerationFacet ef: ss.getEnumerationArray()){
					s.append("\t" + p.getName().toUpperCase() + "_" + ef.getValue());
					if ( ++i < ss.sizeOfEnumerationArray() )
						s.append("," + eol);
					else
						s.append("" + eol);
				}
			}
		}
		s.append("} E_" + p.getName().toUpperCase() + ";" + eol + eol);
		return i>0? ("E_"+ p.getName().toUpperCase()): null;
	}
	/**
	 * 
	 * @param o
	 * @return true if any parameters other than NumberOfEntries.
	 */
	private boolean generateParameterTypeDef( ModelObject o ){
		if ( o.sizeOfParameterArray()>0 ){
			for ( ModelParameter mp: mo.getParameterArray()){
				if ( getNumEntriesParam(mp) == null ){
					return true;
				}
			}
		}
		return false;
	}
		
	private void generateObjTypeDef(BufferedWriter writer) throws IOException {
		// descend tree and generate from lowest level of each object.
		for (CodeGenObject cgo : oList) {
			cgo.generateObjTypeDef(writer);
		}
		// generate parameter get/set functions 
		generateTypeDef(writer);
		
	}

	private String getVName( String name ){
		char c = Character.toLowerCase(name.charAt(0));
		StringBuilder pname = new StringBuilder(256);
		pname.append(c);
		pname.append(name.substring(1));
		return pname.toString();  		
	}
	
	private void generateTypeDef(BufferedWriter w) throws IOException {
		if (generateParameterTypeDef(mo)) {
			StringBuilder s = new StringBuilder(1000);
			w.write(eol + "/**@obj " + getCodeGenName() + " **/" + eol);
			s.append("typedef struct " + getTypeDefName() + " {" + eol);
			for (ModelParameter mp : mo.getParameterArray()) {
				if (excludeParameter(mp) || (genCWMPAliasGetSet && isNonFunctionalAlias(mp))){
					; // don't include Alias parameters in typedef.
				} else if (getNumEntriesParam(mp) == null) { // don't include
														// numberOfEntries
														// parameters.
					String t = getDataType(mp);
					String type = "char *";
					String vName = getVName(mp.getName());

					if ("eBase64".equals(t))
						type = "char *";
					else if ("eBoolean".equals(t))
						type = "unsigned char";
					else if ("eDateTime".equals(t))
						type = "time_t";
					else if ("eUnsignedInt".equals(t))
						type = "unsigned";
					else if ("eLong".equals(t))
						type = "long";
					else if ("eUnsignedLong".equals(t))
						type = "long long";
					else if ("eString".equals(t))
						type = "char *";
					else if ("eInt".equals(t))
						type = "int";
					else if ("instanceRef".equals(t)){
						int refs = getInstanceRefCnt( mp );
						type = "Instance *";
						if ( refs > 1 ) {
							vName +="[" + refs + "+1]";
						}		
					}
					s.append("\t" + type + "\t" + vName	+ ";" + eol);

				}
			}
			s.append("} " + getTypeDefName() + ";" + eol);
			s.append("/**@endobj" + "  **/" + eol);
			w.write(s.toString());
		}
	}

	/**
	 * 
	 * @param profiles 
	 * @param if seperateStubs is true then prefix for stubs files; otherwise
	 * generate all in one file.
	 * @return
	 */
	public int genStubs ( String fname, BuilderProfiles profiles ){
		if ( !rootObj ){
			System.err.println("Logic error: genStubs can only be called for root level");
			return -1;
		}
		if ( !seperateStubs ){
			FileWriter f;
			try {
				f = new FileWriter(fname);
				BufferedWriter writer = new BufferedWriter(f);
				if ( functionPreamble!=null )
					includePreamble(functionPreamble, writer );
				generateStubs(writer, profiles);
				writer.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}	
		} else if (rootObj ){
			for (CodeGenObject cgo: oList ) {
				String objName = cgo.getCodeGenName();
				String fileName = fname + objName + ".c";
				FileWriter f;
				System.out.println("Generating code for obj: "+ objName + " in file: " +fileName);
				try {
					f = new FileWriter(fileName);
					BufferedWriter writer = new BufferedWriter(f);
					if ( functionPreamble!=null )
						includePreamble(functionPreamble, writer );
					if ( genTypeDefs ){
						String header = "#include \"" + objName + ".h\"" + eol;
						writer.write(header);
					}
					cgo.generateStubs(writer, profiles);
					writer.close();
				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
					e.printStackTrace();
				}	
			}
			String objName = getRootName(); /* finish root level */
			String fileName = fname + objName + ".c";
			FileWriter f;
			System.out.println("Generating code for root obj: "+ oName + " in file: " +fileName);
			try {
				f = new FileWriter(fileName);
				BufferedWriter writer = new BufferedWriter(f);
				if ( functionPreamble!=null )
					includePreamble(functionPreamble, writer );
				// generate parameter get/set functions 
				genParameterGetSetFunctions(writer, profiles);
				// generate this object Add/Delete functions
				genObjectAddDeleteFunctions(writer, profiles);
				writer.close();
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}	

		} 
		return 0;
	}
	
	/**
	 * 
	 * @param writer
	 * @param profiles 
	 * @throws IOException
	 */
	private void generateStubs(BufferedWriter writer, BuilderProfiles profiles) throws IOException {
		// descend tree and generate from lowest level of each object.
		for (CodeGenObject cgo : oList) {
			cgo.generateStubs(writer, profiles);
		}
		writer.write(eol + "/**@obj "+ getCodeGenName() +" **/" + eol);
		// generate this object Add/Delete functions
		genObjectAddDeleteFunctions(writer, profiles);
		// generate parameter get/set functions 
		genParameterGetSetFunctions(writer, profiles);
		writer.write("/**@endobj "+ getCodeGenName() +" **/" + eol);
		return;			
	}

	private void genObjectAddDeleteFunctions(BufferedWriter w, BuilderProfiles profiles) throws IOException {
		String oType = getOType(profiles, this);
		if ( mo.getAccess() == ReadWriteAccess.READ_WRITE 
			|| "eCPEInstance".equals(oType) || "eStaticInstance".equals(oType) ){
			StringBuilder s = new StringBuilder(1000);
			s.append(CPE_STATUS + " add" + getCodeGenName() +
					"(CWMPObject *o, Instance *ip)" + eol);
			s.append("{" + eol+
					 "\t/* add instance data */" + eol);
			if ( genTypeDefs )
				s.append( genAddInstance());
			else {
				s.append("\treturn CPE_OK;" + eol);
			}
			s.append("}" + eol);
			if ( mo.getAccess() == ReadWriteAccess.READ_WRITE
				|| "eCPEInstance".equals(oType)){
				s.append(CPE_STATUS + " del" + getCodeGenName() + "(CWMPObject *o, Instance *ip)" + eol);
				s.append("{" + eol+
						"\t/* delete instance data */" + eol);
				if ( genTypeDefs )
					s.append( genDelInstance() );
				s.append(
				//		"\tcwmpDeleteInstance(o,ip); /* framework clean up*/" + eol +
						"\treturn CPE_OK;" + eol +
				 		"}" + eol);
			}
			w.write(s.toString());
		}
		if ( "eObject".equals(oType) && profiles.isInitializeObj(getObjectPath())){
			StringBuilder s = new StringBuilder(1000);
			s.append(CPE_STATUS + " init" + getCodeGenName() +
					"(CWMPObject *o, Instance *ip)" + eol);
			s.append("{" + eol+
					 "\t/* initialize object */" + eol);
			if ( genTypeDefs )
				s.append( genAddInstance());
			else {
				s.append("\treturn CPE_OK;" + eol);
			}
			s.append("}" + eol);
			w.write(s.toString());
		}
		if ( profiles.isCommitObj(getObjectPath())){
			StringBuilder s = new StringBuilder(1000);
			s.append(CPE_STATUS + " commit" + getCodeGenName() +
					"(CWMPObject *o, Instance *ip, eCommitCBType cmt)" + eol);
			s.append("{" + eol+
					 "\t/* commit object instance */" + eol);
			s.append("\treturn CPE_OK;" + eol);
			s.append("}" + eol);
			w.write(s.toString());
		}
	}
/**
 * generate code that adds the object instance. If object contains parameters generate the code to allocate
 * the parameter structure.
*/
private StringBuffer genAddInstance() {
		StringBuffer s = new StringBuffer( 250 );
		//s.append("\tInstance *ip = cwmpCreateObjInstance(o, value);" +eol);
		//s.append("\tif ( ip ) {" + eol);
		if ( getNumNonNumEntryParam()>0 ){
			// only generated backing data allocation if parameters exist in object .
			s.append("\t" + getTypeDefName() + " *p = ("+getTypeDefName()+" *)GS_MALLOC( sizeof(struct " + getTypeDefName()+ "));" + eol);
			s.append("\tmemset(p, 0, sizeof(struct " + getTypeDefName() + "));" +eol);
			s.append("\tip->cpeData = (void *)p;" + eol );
		}
		s.append("\treturn CPE_OK;" + eol);
		//s.append("\t}" +eol);
		//s.append("\treturn CPE_ERR;" + eol );
		return s;
	}
/**
 * generate code to delete the object instance. If object contains parameter generate the code to free
 * the parameter data structure.
 * @return
 */
private StringBuffer genDelInstance() {
		StringBuffer s = new StringBuffer( 250 );
		if ( getNumNonNumEntryParam()> 0){
			s.append("\t" + getTypeDefName() + " *p = ("+getTypeDefName()+" *)ip->cpeData;" + eol);
			s.append("\tif( p ){" + eol);
			s.append("\t\t//TODO: free instance data" + eol);
			s.append("\t\tGS_FREE(p);" +eol);
			s.append("\t}" + eol);
		}
		return s;
	}

/**
 * scan child CodeGenObjects for any numEntriesParameter that match
 * the parameter p.	
 * @param p
 * @return CodeGenObject or null.
 */
	private CodeGenObject getNumEntriesParam( ModelParameter p ){
		for ( CodeGenObject cgo: oList  ){
			ModelObject o = cgo.mo;
			if ( o.isSetNumEntriesParameter() 
				&& o.getNumEntriesParameter().equals(p.getName()))
				return cgo;
		}
		return null;
	}
/**
 * 
 * @return Count of non-xxxNumberOfEntries in this object.
 */
	@SuppressWarnings("unused")
	private int getNumNonNumEntryParam(){
		int n =0;
		for ( ModelParameter mp: mo.getParameterArray()){
			if ( getNumEntriesParam(mp)==null)
				++n;
		}
		return n;
	}
/**
 * Generate the body of the parameter getter function.	
 * @param p
 * @return
 */
	private StringBuilder genGetParameterBody( ModelParameter p ){
		CodeGenObject co = getNumEntriesParam( p );
		StringBuilder s = new StringBuilder(1000);
		s.append("{" + eol);
		if (co!=null){
			s.append("\tcwmpGetObjInstanceCntStr("
						+co.getNumberOfEntriesObjName() +", value);" + eol 
					);
		//} else if (genCWMPAliasGetSet && isNonFunctionalAlias(p)){
		//	s.append("\t/* DO NOT CHANGE. Framework managed Alias data.*/" + eol);
		//	s.append("\t*value = GS_STRDUP(ip->alias);" + eol);
		} else {
			if ( genTypeDefs ){
				s.append("\t" +getTypeDefName()+ " *p = (" +getTypeDefName()+ " *)ip->cpeData;" + eol);
				s.append("\tif ( p ){" + eol);
				String t = getDataType( p );
				if ("eBase64".equals(t) || "eString".equals(t) 
					|| "eHexBinary".equals(t) || "eStringSetOnly".equals(t)) {
					s.append("\t\tif ( p->" + getVName(p.getName()) + " )" + eol +
							 "\t\t\t*value = GS_STRDUP(p->" + getVName(p.getName()) + ");" + eol);
				} else if ( "eBoolean".equals(t))
					s.append("\t\t*value = GS_STRDUP(p->" + getVName(p.getName()) + "? \"true\": \"false\");" + eol);
				else if ( "eDateTime".equals(t)) {
					s.append("\t\tchar buf[30];" + eol);
					s.append("\t\tstruct tm *bt=localtime(&p->" + getVName(p.getName()) + ");" + eol);
					s.append("\t\tstrftime(buf,sizeof(buf),\"%Y-%m-%dT%H:%M:%S\",bt );" + eol);
					s.append("\t\t*value = GS_STRDUP(buf);" + eol);
				}else if ( "eUnsignedInt".equals(t) )
					s.append("\t\tchar    buf[10];" + eol +
							 "\t\tsnprintf(buf,sizeof(buf),\"%u\", p->" + getVName(p.getName()) + ");" + eol +
							 "\t\t*value = GS_STRDUP(buf);" + eol);		
				else if ("eLong".equals(t))
					s.append("\t\tchar    buf[20];" + eol +
							 "\t\tsnprintf(buf,sizeof(buf),\"%ld\", p->" + getVName(p.getName()) + ");" + eol +
							 "\t\t*value = GS_STRDUP(buf);" + eol);	
				else if ("eUnsignedLong".equals(t))
					s.append("\t\tchar    buf[30];" + eol +
							 "\t\tsnprintf(buf,sizeof(buf),\"%lld\", p->" + getVName(p.getName()) + ");" + eol +
							 "\t\t*value = GS_STRDUP(buf);" + eol);	
				else if ("eInt".equals(t))
					s.append("\t\tchar    buf[10];" + eol +
							 "\t\tsnprintf(buf,sizeof(buf),\"%d\", p->" + getVName(p.getName()) + ");" + eol +
							 "\t\t*value = GS_STRDUP(buf);" + eol);
				else if ("instanceRef".equals(t)){
					String setCode = genGetInstanceRef( p );
					s.append( setCode );
				} else {
					s.append("\t\tif ( p->" + getVName(p.getName()) + " )" + eol +
							 "\t\t\t*value = GS_STRDUP(p->" + getVName(p.getName()) + ");" + eol);
				}
				s.append("\t}" + eol);
			} else 
				s.append("\t/* get parameter */" + eol);			
		}
		s.append("\treturn CPE_OK;" + eol +
		 "}" + eol);
		return s;
		
	}
/**
 * Return a string containing the code for getting the instance path reference or 
 * references for a parameter row reference.	
 * @param p ModelParameterReference.
 * @return String containing line of code.
 */
	private String genGetInstanceRef(ModelParameter p) {
		String code = "";
		int maxItems = getInstanceRefCnt(p);
		if ( maxItems > 1){
			code = "\t\t*value = cwmpGetPathRefRowsStr(p->" + getVName(p.getName()) +");" + eol;
		} else {
			code =  "\t\t*value = cwmpGetInstancePathStr(p->" + getVName(p.getName()) + ");" + eol;
		}
		return code;
	}
/**
 * Return the number of possible instance references for the ModelParameter.
 * ModelParameter must have <syntax> with <string/>.
 * 	
 * @param p
 * @return 0 
 */
	private int getInstanceRefCnt(ModelParameter p) {
		int maxItems = 0;
		cwmpdatamodel.ModelSyntax sx = p.getSyntax();
		// if <size maxLength="256" then maxItems is 1.
		cwmpdatamodel.ModelSyntax.String s = sx.getString();
		if (s.sizeOfSizeArray() > 0) {
			SizeFacet sf = s.getSizeArray(0);
			if (sf.isSetMaxLength() ){
				if ( sf.getMaxLength().equals(new BigInteger("256"))) {
					maxItems = 1;
				} else {
					maxItems = 10;
				}
			} else {
				maxItems = 1;
			}
		}
		// check if List Facet overrides the above assumptions.
		if (sx.isSetList()) {
			ListFacet lf = sx.getList();
			if (lf.isSetMaxItems()) {
				String x = lf.getMaxItems().toString();
				maxItems = new Integer(x);
			} else {
				maxItems = 10;
			}
		}
		return maxItems;
	}

	private StringBuilder genSetParameterBody(ModelParameter p) {
		//System.out.println("genSetParameterBody(): " + getObjectName() + p.getName());
		//System.out.println(" = " + p.toString());
		StringBuilder s = new StringBuilder(1000);
		s.append("{" + eol);
		//if ( genCWMPAliasGetSet && isNonFunctionalAlias( p )){
		//	s.append("\t/*DO NOT CHANGE. Framework managed Alias data. */" + eol);
		//	s.append("\tCOPYSTR(ip->alias, value);" + eol);
		//} else
			if (genTypeDefs) {
			s.append("\t" +getTypeDefName()+ " *p = (" +getTypeDefName()+ " *)ip->cpeData;" + eol);
			String t = getDataType(p);
			s.append("\tif ( p ){" +eol );
			if ("eBase64".equals(t) || "eString".equals(t) || "eHexBinary".equals(t) || "eStringSetOnly".equals(t))
				s.append("\t\tCOPYSTR(p->" + getVName(p.getName()) + ", value);" + eol);
			else if ("eBoolean".equals(t))
				s.append("\t\tp->" + getVName(p.getName()) + "=testBoolean(value);" + eol);
			else if ("eDateTime".equals(t)) {
			    s.append("\t\tstruct tm bt;" + eol +
			    		 "\t\tstrptime(value,\"%Y-%m-%dT%H:%M:%S\", &bt );" + eol +
			    		 "\t\tp->" + getVName(p.getName()) + "= mktime(&bt);" + eol);
			} else if ("eUnsignedInt".equals(t))
				s.append("\t\tp->" + getVName(p.getName()) + "=atoi(value);" + eol);
			else if ("eLong".equals(t))
				s.append("\t\tp->" + getVName(p.getName()) + "=strtol(value, NULL, 10);" + eol);
			else if ("eUnsignedLong".equals(t))
				s.append("\t\tp->" + getVName(p.getName()) + "=strtol(value, NULL, 10);" + eol);
			else if ("eInt".equals(t))
				s.append("\t\tp->" + getVName(p.getName()) + "=atoi(value);" + eol);
			else if ("instanceRef".equals(t)){
				s.append("\t\t//TODO: resolve Instance pointers." + eol);
				//s.append("\t\tif( cwmpFindObject(value)!=NULL )" + eol);
				//s.append("\t\t\tp->" + getVName(p.getName()) + "=cwmpGetCurrentInstance();" + eol);
				//s.append("\t\telse" + eol);
				//s.append("\t\t\treturn CPE_9007;" + eol);
			} else {
				System.err.println("type set: "+ t +" for " + getObjectName() + p.getName());
				s.append("\t\tCOPYSTR(p->" + getVName(p.getName()) + ", value);" + eol);
			}
			s.append("\t}" + eol);
		} else
			s.append("\t/* set parameter */" + eol);
		s.append("\treturn CPE_OK;" + eol + "}" + eol);
		return s;
	}
/**
 * Return true if the parameter references the object's uniqueKey that is
 * has a ref of "Alias" and functional is "false".
 * @param p
 * @return
 */
	private boolean isNonFunctionalAlias(ModelParameter p) {
		if ( p.getName().equals("Alias")){
			ModelSyntax sx = p.getSyntax();
			DataTypeReference dref = sx.getDataType();
			if (dref!=null && "Alias".equals(dref.getRef() ))
				for ( UniqueKey k: mo.getUniqueKeyList()) {
					if ( !k.getFunctional() ){
						for ( Parameter kp: k.getParameterArray() ){
							if ( kp.getRef().equals(p.getName() ) )
								return true;
						}
					}
				}
			System.out.println("Alias." + mo.getName());
		}
		return false;
	}

	private void genParameterGetSetFunctions(BufferedWriter w, BuilderProfiles profiles) throws IOException {

		//if ( genTypeDefs && mo.sizeOfParameterArray()>0 && isSingleInstanceObject()){
		//	if ( getTypeDefName().length()>0 && getNumNonNumEntryParam()>0 ){
		//		w.write("static " + getTypeDefName() + "\t" + getVName(getTypeDefName()) + ";" + eol);
		//	}
		//}
		for ( ModelParameter mp: mo.getParameterArray()){
			if ( profiles.isProfileParameter(getObjectPath()+mp.getName())){
				if ( excludeParameter(mp) || (isNonFunctionalAlias(mp) && genCWMPAliasGetSet) ){
					continue;
				} else {
					StringBuilder s = new StringBuilder(1000);
					s.append(eol + "/**@param " + getParamStubName(mp) + "                     **/" + eol);
					if ( mp.getAccess()==ParameterAccess.READ_WRITE
							|| profiles.isInitializeObj(getObjectPath()+mp.getName())) {
						s.append(CPE_STATUS + getParamSetterName(mp) 
								+ "(Instance *ip, char *value)" + eol);
						s.append( genSetParameterBody(mp));		
					}
					s.append(CPE_STATUS + getParamGetterName(mp) 
							+ "(Instance *ip, char **value)" + eol);
					s.append( genGetParameterBody( mp ));
					s.append("/**@endparam                                               **/" + eol);
					w.write(s.toString());
				}
			}
		}
	}

	public void genTables(String fname, BuilderProfiles profiles) {
		// First pass generates the object and parameter structure arrays.
		FileWriter f;
		try {
			f = new FileWriter(fname);
			BufferedWriter writer = new BufferedWriter(f);
			if ( tablesPreamble!=null )
				includePreamble(tablesPreamble, writer );
			generateProfileList(writer, profiles);
			generateTables(writer, profiles);
			generateRootObjs(writer, profiles);
			writer.close();
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	private void includePreamble(String fName, BufferedWriter writer) {
		FileReader f = null;
		try {
			f = new FileReader(fName);
		} catch (FileNotFoundException e) {
			System.err.println( "Preamble file "+ fName + " not found" + eol);
			e.printStackTrace();
			System.exit(1);
		}
		BufferedReader r = new BufferedReader(f);
		char[] buf = new char[1000];
		int lth;
		try {
			while ( (lth = r.read(buf)) != -1 ){
				writer.write(buf, 0, lth);
			}
			r.close();
		} catch (IOException e) {
			System.err.println( "Preamble file read failure" + eol);
			e.printStackTrace();
		}
	
	}

	private void generateProfileList(BufferedWriter w, BuilderProfiles profiles) throws IOException {
		if ( profiles!=null && profiles.getProfileNames().size()>0){
			StringBuilder s = new StringBuilder(1000);
			s.append("/**  ");
			int i = 0;
			System.out.println("generating tables for profiles:" + eol );
			for ( String name: profiles.getProfileNames()){
				System.out.println( name + eol);
				s.append(name);
				s.append(", ");
				if ( ++i%5 == 0 ) s.append(eol + "    ");
			}
			s.append(eol + "**/" + eol);
			w.write(s.toString());
		}
			
	}
	/*
	 * root object has to be generated.
	 */
	private void generateRootObjs(BufferedWriter w, BuilderProfiles profiles) throws IOException {
		if ( model.isSetIsService() && model.getIsService() ){
			w.write("/** CWMP "+ oName + " Object Table  */" + eol + eol);												
			w.write("CWMPObject "+getRootName() +"[]={" + eol);
		} else {
			w.write("/** CWMP ROOT Object Table  */" + eol + eol);												
			w.write("CWMPObject CWMP_RootObject[]={" + eol);
		}
		if ( gen_scLables)
			w.write("\t{sc_"+ getRootName() + ",");       // *name
		else
			w.write("\t{\"" + getRootName() + "\",");

		w.write(" NULL,");									// pDelObj
		w.write(" NULL,");									// pAddObj
		w.write(" NULL,"); // commit callback is manually added
		w.write(" NULL,"); // open obj callback is manually added.
		// this objects childern
		w.write(" " + getRootName() + "_Objs,");	// *objList

		w.write(" " + getRootName() + "_Params,");	// *paramList
		w.write(" NULL,");  									// *iList
		if ( isSingleInstanceObject()){			// 
			w.write(" eObject,");					
		} else {
			w.write(" eInstance,");
		}
		w.write(" 0}," + eol);   // oFlags field.
		w.write("\t{NULL}" + eol);
		w.write("};" + eol + eol);
		
	}
	
	private boolean generateTables(BufferedWriter writer, BuilderProfiles profiles) throws IOException {
		// descend tree and generate from lowest level of each object.
		for (CodeGenObject cgo : oList) {
			cgo.generateTables(writer, profiles);
		}
		// generate this objects parameter tables.
		genParameterTables(writer, profiles);
		if ( oList.size() > 0) {
			writer.write("/**@obj " + getObjectPath() + "  */" + eol);
			// generate object extern add/del macros
			genObjectDelAddMacros(writer, profiles);
			// now generate this objects child object list.
			genObjectTables(writer, profiles);
			writer.write("/**@endobj "+ getObjectPath() + "  */" + eol);
		}
		return true;
	}

	private void genObjectDelAddMacros(BufferedWriter w, BuilderProfiles profiles) throws IOException {
		for (CodeGenObject cgo : oList) {
			if ( !excludeObject( cgo.mo )){
				String oType = getOType(profiles, cgo);
				if ( ("eInstance".equals(oType) && cgo.mo.getAccess() == ReadWriteAccess.READ_WRITE )
						 || ("eCPEInstance".equals(oType) && profiles.isInitializeObj(cgo.getObjectPath()))) {
					w.write("CPEADDOBJ(add" + cgo.getCodeGenName() + ");" + eol);
					w.write("CPEDELOBJ(del" + cgo.getCodeGenName() + ");" + eol);
				} else if ("eStaticInstance".equals(oType) &&
					        profiles.isInitializeObj(cgo.getObjectPath())){
					w.write("CPEADDOBJ(add" + cgo.getCodeGenName() + ");" + eol);
				} else if ("eObject".equals(oType)
						    && profiles.isInitializeObj(cgo.getObjectPath())){
					w.write("CPEADDOBJ(init" + cgo.getCodeGenName() + ");" +eol);
				}
				if (profiles.isCommitObj(cgo.getObjectPath())){
					w.write("CPECOMMIT(commit" + cgo.getCodeGenName() + ");" +eol);
				}
			}
		}
	}
/******
	private boolean isGenStaticInstanceAdd(CodeGenObject cgo){
		return CodeGenObject.genStaticInstanceAddObj
		       && ((!cgo.isSingleInstanceObject())
			       && (cgo.mo.getAccess() == ReadWriteAccess.READ_ONLY ));
	}
***********/	
	private boolean excludeObject( ModelObject mo){
		return mo.getStatus()== Status.OBSOLETED
		    || (ignoreDeprecated && mo.getStatus()== Status.DEPRECATED);
	}
	
	private void genObjectTables(BufferedWriter w, BuilderProfiles profiles) throws IOException {
		if (oList.size() > 0) {
			if (rootObj) {
				w.write("CWMPObject " + getRootName() +"_Objs[]={" + eol);
			} else {
				w.write("CWMPObject " + getCodeGenName() + "_Objs[]={" + eol);
			}
			for (CodeGenObject cgo : oList) {
				if ( !excludeObject( cgo.mo )){
					if ( gen_scLables)
						w.write("\t{sc_" + cgo.getStringName() + ",");       // *name
					else
						w.write("\t{\"" + cgo.getStringName() + "\",");
					String oType = getOType(profiles, cgo );
					if ( ("eInstance".equals(oType) && cgo.mo.getAccess() == ReadWriteAccess.READ_WRITE )
						 || ("eCPEInstance".equals(oType) && profiles.isInitializeObj(cgo.getObjectPath()))) {
						w.write(" del" + cgo.getCodeGenName() + ",");		//pDelObj
						w.write(" add" + cgo.getCodeGenName() + ",");		//pAddObj
					} else if ("eStaticInstance".equals(oType) &&
							profiles.isInitializeObj(cgo.getObjectPath())){
						w.write(" NULL,");	
						w.write(" add" + cgo.getCodeGenName() + ",");
					} else if ("eObject".equals(oType) &&
							   profiles.isInitializeObj(cgo.getObjectPath())){
						w.write(" NULL,");
						w.write(" init" + cgo.getCodeGenName() + ",");
					} else {
						w.write(" NULL,");									// pDelObj
						w.write(" NULL,");									// pAddObj
					}
					if (profiles.isCommitObj(cgo.getObjectPath()) )
						w.write(" commit" + cgo.getCodeGenName() + ",");
					else
						w.write(" NULL,"); // commit callback is manually added
					w.write(" NULL,"); // open obj callback is manually added.
					// this objects childern
					if (cgo.oList.size() > 0) {
						w.write(" " + cgo.getCodeGenName() + "_Objs,");	// *objList
					} else {
						w.write(" NULL,");
					}
					if (cgo.getModelObject().sizeOfParameterArray() > 0) {
						w.write(" " + cgo.getCodeGenName() + "_Params,");	// *paramList
					} else {
						w.write(" NULL,");
					}
					w.write(" NULL,"); // *iList
					w.write(" " + oType + ", ");
					w.write(" 0}," + eol);  // oFlags:8 field
				}
			}
			w.write("\t{NULL}" + eol);
			w.write("};" + eol + eol);
		}
	}
	/**
	 * Return oType string for the object.
	 * 
	 * @param profiles
	 * @param cgo
	 * @return
	 */
	private String getOType(BuilderProfiles profiles, CodeGenObject cgo) {
		if (cgo.mo.getAccess() == ReadWriteAccess.READ_WRITE){
			return "eInstance";
		}
		if (profiles.isInitializeObj(cgo.getObjectPath())){
			if (cgo.isSingleInstanceObject() || cgo.isZeroOrOneObject() )
				return "eObject";
			for( ModelParameter mp: cgo.mo.getParameterArray()) {
				// wanted to test the dmr:fixedObject attribute here but couldn't find the method for it.
				// so instead assume that any non-writable object with possible multiple instances that 
				// has an Alias parameter is a fixed object. If there is no Alias parameter then its a 
				// transient object. This assumption only works with the newer data model definitions
				// that have defined Alias parameters for all the non-transient objects.
				if (  mp.getName().equals("Alias") && (
					 mp.getStatus() != Status.DEPRECATED && mp.getStatus()!=Status.OBSOLETED)){
					// valid Alias parameter found in object.
					return "eStaticInstance";
				}
			}
			// no Alias parameter found.
			return "eCPEInstance";
		}
		return cgo.isSingleInstanceObject()? "eObject": "eInstance";
	}

	/**
	 * Return true if MaxEntries and MinEntries are set to "1".
	 * @return
	 */
	private boolean isSingleInstanceObject() {
		String min = mo.getMinEntries().toString();
		String max = mo.getMaxEntries().toString();
		return min.equals("1") && max.equals("1") && (!mo.getName().endsWith("i}."));

	}
	/**
	 * return true if not instance object (".{i}.") and min=0 and max==1.
	 * @return
	 */
	private boolean isZeroOrOneObject(){
		String min = mo.getMinEntries().toString();
		String max = mo.getMaxEntries().toString();
		return min.equals("0") && max.equals("1") && (!mo.getName().endsWith("i}."));	
	}

	
	/**
	 * Return status of Model Parameter.
	 * @param mp ModelParameter reference
	 * @return true if parameter is obsoleted or 
	 *             the ignoreDeprecated is set and the parameter status is deprecated
	 *          or ignoreDeprecatedAlias is set and the parameter name is 'Alias'.
	 *         false otherwise.
	 */
	private boolean excludeParameter( ModelParameter mp ){
		return mp.getStatus()==Status.OBSOLETED 
		    || (ignoreDeprecated && mp.getStatus() == Status.DEPRECATED)
		    || (ignoreDeprecatedAlias && mp.getName().equals("Alias")
		    	&& mp.getStatus() == Status.DEPRECATED );
	}
	
	public boolean genParameterTables(BufferedWriter w, BuilderProfiles profiles)
			throws IOException {
		if (mo.sizeOfParameterArray() > 0) {
			// generate external macros
			w.write("/**@param " + getObjectPath() + " */" + eol);
			for (ModelParameter mp : mo.getParameterArray()) {
				if (genCWMPAliasGetSet && isNonFunctionalAlias(mp))
					continue;
				if (profiles.isProfileParameter(getObjectPath() + mp.getName())
						&& !excludeParameter(mp)) {
					String pName = getCodeGenName() + "_" + mp.getName();
					w.write("CPEGETFUNC(get" + pName + ");" + eol);
					if (mp.getAccess() == ParameterAccess.READ_WRITE
							|| profiles.isInitializeObj(getObjectPath()
									+ mp.getName())) {
						w.write("CPESETFUNC(set" + pName + ");" + eol);
					}
				}
			}
			if (rootObj)
				w.write("CWMPParam " + getRootName() + "_Params[]={" + eol);
			else
				w.write("CWMPParam " + getCodeGenName() + "_Params[]={" + eol);
			for (ModelParameter mp : mo.getParameterArray()) {
				if (profiles.isProfileParameter(getObjectPath() + mp.getName())
						&& !excludeParameter(mp)) {
					if (gen_scLables) // name
						w.write("\t{ sc_" + mp.getName() + ",");
					else
						w.write("\t{ \"" + mp.getName() + "\",");
					if (genCWMPAliasGetSet && isNonFunctionalAlias(mp)) {
						w.write(" cwmpGetAliasParam"        // pGetter to framework.
								+ ",");
						w.write(" cwmpSetAliasParam"        // pGetter to framework.
								+ ",");
					} else {
						w.write(" get" + getCodeGenName() + "_" + mp.getName() // pGetter
								+ ",");
						if (mp.getAccess() == ParameterAccess.READ_WRITE
								|| profiles.isInitializeObj(getObjectPath()
										+ mp.getName())) { // pSetter
							w.write(" set" + getCodeGenName() + "_"
									+ mp.getName() + ",");
						} else
							w.write(" NULL,"); //
					}
					w.write(" NULL,"); // *instanceData
					if (mp.getAccess() == ParameterAccess.READ_WRITE)
						w.write(" RPC_RW,");
					else
						w.write(" RPC_R,");
					String dt = getDataType(mp);
					if (dt.equals("instanceRef")) // override the kludge.
						dt = " eString";
					w.write(" " + dt + ","); // pType
					w.write(" " + getAttributeFlags(mp) + ","); // notify
					w.write(" " + getDataSize(mp) + "}," + eol); // pSize
				}
			}
			w.write("\t{NULL}" + eol);
			w.write("};" + eol);
			w.write("/**@endparam " + getObjectPath() + "  */" + eol + eol);
		}
		return true;
	}

	// currently only setting size for eString and eHexBinary.
	// Adding eBase64
	private String getDataSize(ModelParameter mp) {
		cwmpdatamodel.ModelSyntax sx = mp.getSyntax();
		String size = "0";
		if (sx.isSetString()) {
			cwmpdatamodel.ModelSyntax.String sxStr = sx.getString();
			cwmpdatamodel.EnumerationFacet[] ef = sxStr.getEnumerationArray();
			if ( Array.getLength(ef)> 0 ){
				size = getEnumMax(ef);
			} else {
				size = findMaxSize(sxStr.getSizeArray());
			}
		} else if (sx.isSetBase64()) {
			cwmpdatamodel.ModelSyntax.Base64 sx64 = sx.getBase64();
			size = findMaxSize(sx64.getSizeArray());
		} else if (sx.isSetHexBinary()) {
			cwmpdatamodel.ModelSyntax.HexBinary sxb = sx.getHexBinary();
			size = findMaxSize(sxb.getSizeArray());
		} else if (sx.isSetDataType() ){
			if (sx.isSetList()){
				ListFacet lf = sx.getList();
				if ( Array.getLength(lf.getSizeArray())> 0)
				size = findMaxSize(lf.getSizeArray());
			} else {			
				size = findDataTypeSize( sx.getDataType().getRef());
			}
		}
		return size;
	}

	private String getEnumMax(EnumerationFacet[] enumArray) {
		Integer max = 0;
		for ( EnumerationFacet ef : enumArray ){
			Integer n = ef.getValue().length();
			if ( n > max )
				max = n;
		}
		return max.toString();
	}

	private String findMaxSize(SizeFacet[] sizeArray) {
		BigInteger max = new BigInteger("0");
		for (SizeFacet sf : sizeArray) {
			BigInteger s = new BigInteger("0");
			if (sf.isSetMaxLength())
				s = sf.getMaxLength();
			else if (sf.isSetMinLength())
				s = sf.getMinLength();
			max = max.max(s);
		}
		return max.toString();
	}

	private String getAttributeFlags(ModelParameter mp) {
		String notify = "";
		Enum an = ActiveNotify.NORMAL;
		if (mp.isSetActiveNotify()) {
			an = mp.getActiveNotify();
			if (an == ActiveNotify.FORCE_ENABLED) {
				notify = "FORCED_ACTIVE";
			} else if (an == ActiveNotify.FORCE_DEFAULT_ENABLED) {
				notify = "DEFAULT_ACTIVE";
			} else if (an == ActiveNotify.CAN_DENY) {
				notify = "NOACTIVENOTIFY";
			}
		}
		if (mp.isSetForcedInform() && mp.getForcedInform()) {
			if (notify.length() > 0) {
				notify = "FORCED_INFORM" + "|" + notify;
			} else {
				notify = "FORCED_INFORM";
			}
		}
		if (notify.length() == 0)
			notify = "0";
		return notify;
	}

	/**
	 * Return a string containing the 'C' enumeration name for the parameter.
	 * 
	 * @param mp
	 *            ModelParameter reference.
	 * @return string containing 'C' enumeration name of type.
	 */
	public String getDataType(ModelParameter mp) {
		ModelSyntax sx = mp.getSyntax();
		String type;
		if (sx.isSetBase64())
			type = "eBase64";
		else if (sx.isSetBoolean())
			type = "eBoolean";
		else if (sx.isSetDateTime())
			type = "eDateTime";
		else if (sx.isSetHexBinary())
			type = "eHexBinary";
		else if (sx.isSetHidden() && sx.getHidden())
			type = "eStringSetOnly";
		else if (sx.isSetInt())
			type = "eInt";
		else if (sx.isSetLong())
			type = "eLong";
		else if (sx.isSetString()) {
			cwmpdatamodel.ModelSyntax.String s = sx.getString();
			if ( s.sizeOfPathRefArray()>0 ){
				type = "instanceRef";
			} else
				type = "eString";
		} else if (sx.isSetUnsignedInt())
			type = "eUnsignedInt";
		else if (sx.isSetUnsignedLong())
			type = "eUnsignedLong";
		else if (sx.isSetDataType()) {
			if ((type = findDataType(sx)) == null)
				System.err.println("dataType reference not resolved for: "
						+ oName + mp.getName());

		} else {
			type = "eString";
			System.out.println("Undetermined data type. Using eString: "
					+ oName + mp.getName());
		}
		return type;
	}

	/**
	 * Search the global dataTypeDefinitons for the dataType. TODO: need to make
	 * the search include the base definition.
	 * 
	 * @param sx
	 * @return type string.
	 */
	private String findDataType(ModelSyntax sx) {
		String type;
		String ref;
		if (sx.getDataType().isSetRef()) {
			//System.out.println("sx.getDataType().getRef()= "
			//		+ sx.getDataType().getRef());
			ref = sx.getDataType().getRef();
			for (DataTypeDefinition dtd : dataTypeDefinition) {
				if (dtd.getName().equals(ref)) {
					if (dtd.isSetBase64())
						type = "eBase64";
					else if (dtd.isSetBoolean())
						type = "eBoolean";
					else if (dtd.isSetDateTime())
						type = "eDateTime";
					else if (dtd.isSetHexBinary())
						type = "eHexBinary";
					else if (dtd.isSetInt())
						type = "eInt";
					else if (dtd.isSetLong())
						type = "eLong";
					else if (dtd.isSetString())
						type = "eString";
					else if (dtd.isSetUnsignedInt())
						type = "eUnsignedInt";
					else if (dtd.isSetUnsignedLong())
						type = "eUnsignedLong";
					else {
						type = "eString";
					}
					return type;
				}
			}
		} else if (sx.getDataType().isSetBase())
			System.out.println("sx.getDataType().getBase()= "
					+ sx.getDataType().getBase());
		return null;
	}

	private String findDataTypeSize( String ref ){
		String size = "0";
		for (DataTypeDefinition dtd : dataTypeDefinition) {
			if (dtd.getName().equals(ref)) {
				DataTypeDefinition.String dtdS = dtd.getString();
				if ( dtdS != null )
					size = findMaxSize(dtdS.getSizeArray());
				if (size.equals("0") && dtd.isSetBase()){
					return findDataTypeSize( dtd.getBase());
				}
			}
		}
		return size;	
	}



}
