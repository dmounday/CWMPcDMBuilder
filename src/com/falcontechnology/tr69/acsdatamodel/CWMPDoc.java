package com.falcontechnology.tr69.acsdatamodel;

import cwmpdatamodel.ComponentDefinition;
import cwmpdatamodel.ComponentReference;
import cwmpdatamodel.DocumentDocument;
import cwmpdatamodel.DocumentDocument.Document;
import cwmpdatamodel.Import;
import cwmpdatamodel.Model;

public class CWMPDoc {

	private Document	doc;
	
	public CWMPDoc( DocumentDocument docDoc){
		doc = docDoc.getDocument();	
	}
	
	public Model getModel(String name){
		for ( Model m: doc.getModelArray()){
			if ( name.equals(m.getName())){
				System.out.println("Found model: "+ m.getName()+ " base:"+ m.getBase());
				return m;
			}
		}
		return null;
	}
	
	public Document getDocument(){
		return doc;
	}
	
	public ComponentDefinition getComponentDef(ComponentReference ref ){
		for ( ComponentDefinition cd: doc.getComponentArray()){
			if ( ref.getRef().equals(cd.getName())){
				return cd;
			}
		}
		return null;
	}

	public ComponentDefinition getComponentDef(String ref ){
		for ( ComponentDefinition cd: doc.getComponentArray()){
			if ( ref.equals(cd.getName())){
				return cd;
			}
		}
		return null;
	}
	public Import findImportForComponent(String name){
		for ( Import im: doc.getImportArray()){
			for ( Import.Component imc: im.getComponentArray()){
				if ( imc.getName().equals(name)){
					return im;
				}
			}
		}
		return null;
	}
	
	public Import.Component findImportComponent( String refName){
		for ( Import im: doc.getImportArray()){
			for ( Import.Component imc: im.getComponentArray()){
				if ( imc.getName().equals(refName)){
					return imc;
				}
			}
		}
		return null;		
	}
	
	public Import findImportForModel( String name ){
		for ( Import im: doc.getImportArray()){
			for ( Import.Model imm: im.getModelArray()){
				if ( imm.getName().equals(name)){
					return im;
				}
			}
		}
		return null;		
	}
	
	
	
}
