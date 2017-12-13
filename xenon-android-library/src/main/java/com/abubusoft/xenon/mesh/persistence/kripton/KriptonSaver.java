package com.abubusoft.xenon.mesh.persistence.kripton;

import java.io.OutputStream;

import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.core.ElioRuntimeException;
import com.abubusoft.xenon.core.logger.ElioLogger;

import com.abubusoft.kripton.BinderType;
import com.abubusoft.kripton.KriptonBinder;

public class KriptonSaver {
	
	
	static void save(OutputStream output, BinderType binderType, Mesh mesh) {
		try {
			KriptonBinder.bind(binderType).serialize(mesh, output);			
		} catch (Exception e) {
			ElioLogger.fatal(e.getMessage());
			e.printStackTrace();
			throw new ElioRuntimeException(e);
		}
	}
	
	public static void saveMeshIntoXML(OutputStream output, Mesh mesh) {
		save(output, BinderType.XML, mesh);
	}
	
	public static void saveMeshIntoJSON(OutputStream output, Mesh mesh) {
		save(output, BinderType.JSON, mesh);
	}
}
