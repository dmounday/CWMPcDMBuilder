package com.falcontechnology.tr69.acsdatamodel;

import cwmpdatamodel.DataTypeDefinition;
import cwmpdatamodel.RangeFacet;
import cwmpdatamodel.SizeFacet;

public class ImportDataTypeDef {

	private String name;
	private DataTypeDefinition dataTypeRef;
	private boolean	referenced;
	public ImportDataTypeDef(String n, DataTypeDefinition dtd) {
		name = n;
		dataTypeRef = dtd;
		
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setReferenced(){
		referenced = true;
	}
	public boolean isReferenced() {
		return referenced;
	}
	/**
	 * @return the dataTypeRef
	 */
	public DataTypeDefinition getDataTypeRef() {
		return dataTypeRef;
	}

	public String getTypeStr(SizeFacet size) {
		String typeStr;
		DataTypeDefinition dtd = dataTypeRef;
		
		if (dtd.isSetBase64()) {
			typeStr = "base64";
			DataTypeDefinition.Base64 b64 = dtd.getBase64();
			if (b64.sizeOfSizeArray() > 0) {
				SizeFacet sf = b64.getSizeArray(0);
				if (sf.isSetMaxLength()) {
					typeStr += "(" + sf.getMaxLength().toString() + ")";
				}
			}
		} else if (dtd.isSetBoolean())
			typeStr ="boolean";
		
		else if (dtd.isSetDateTime())
			typeStr ="dateTime";
		
		else if (dtd.isSetInt()) {
			typeStr = "int";
			DataTypeDefinition.Int sxs = dtd.getInt();
			if (sxs.sizeOfRangeArray() > 0) {
				RangeFacet rf = sxs.getRangeArray(0);
				if (rf.isSetMaxInclusive()) {
					typeStr += "[" + rf.getMinInclusive().toString() + ":"
							+ rf.getMaxInclusive().toString() + "]";
				} else if (rf.isSetMinInclusive()) {
					typeStr += "[" + rf.getMinInclusive().toString() + ":]";

				}
			}
		} else if (dtd.isSetUnsignedInt()) {
			typeStr = "unsignedInt";
			DataTypeDefinition.UnsignedInt sxs = dtd.getUnsignedInt();
			if (sxs.sizeOfRangeArray() > 0) {
				RangeFacet rf = sxs.getRangeArray(0);
				if (rf.isSetMaxInclusive()) {
					typeStr += "[" + rf.getMinInclusive().toString() + ":"
							+ rf.getMaxInclusive().toString() + "]";
				} else if (rf.isSetMinInclusive()) {
					typeStr += "[" + rf.getMinInclusive().toString() + ":]";
				}
			}
		} else if (dtd.isSetString()) {
			typeStr = "string";
			SizeFacet sf = null;
			if ( size != null )
				sf = size;
			else {
				DataTypeDefinition.String ss = dtd.getString();
				if (ss.sizeOfSizeArray() > 0) {
					sf = ss.getSizeArray(0);
				} 
			}
			if ( sf != null ){				
				if (sf.isSetMaxLength()) {
					typeStr += "(" + sf.getMaxLength().toString() + ")";
				}
			}
		} else if (dtd.isSetLong()) {
			typeStr = "Unimplemented Data Type: long";
		} else if (dtd.isSetUnsignedLong()){
			typeStr = "Unimplemented Data Type: unsignedLong";
		}else {
			typeStr = "Unknow unimplemented data type";
			System.err.println("Unknow unimplemented datat type");
		}

		return typeStr;
	}

}
