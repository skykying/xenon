package com.abubusoft.xenon.settings;

import org.abubu.elio.Uncryptable;
import org.abubu.elio.logger.ElioLoggerLevelType;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

/**
 * Configurazione di un appender di log
 * 
 * @author Francesco Benincasa
 * 
 */
@BindType
public class LoggerAppenderSettings implements Uncryptable {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public String tag = "";

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public ElioLoggerLevelType level;

}
