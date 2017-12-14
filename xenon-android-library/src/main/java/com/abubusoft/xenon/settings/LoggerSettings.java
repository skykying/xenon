package com.abubusoft.xenon.settings;

import java.util.ArrayList;

import com.abubusoft.xenon.core.Uncryptable;
import com.abubusoft.xenon.core.logger.LoggerLevelType;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindType;
import com.abubusoft.kripton.annotation.BindXml;
import com.abubusoft.kripton.xml.XmlType;

@BindType
public class LoggerSettings implements Uncryptable {

	@Bind
	@BindXml(xmlType=XmlType.ATTRIBUTE)
	public LoggerLevelType level = LoggerLevelType.NONE;

	@Bind
	@BindXml
	public ArrayList<LoggerAppenderSettings> appenders = new ArrayList<LoggerAppenderSettings>();
}
