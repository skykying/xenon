package com.abubusoft.xenon.settings;

import java.util.ArrayList;

import org.abubu.elio.Uncryptable;
import org.abubu.elio.logger.ElioLoggerLevelType;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

@BindType
public class LoggerSettings implements Uncryptable {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public ElioLoggerLevelType level = ElioLoggerLevelType.NONE;

	@Bind
	@BindXml
	public ArrayList<LoggerAppenderSettings> appenders = new ArrayList<LoggerAppenderSettings>();
}
