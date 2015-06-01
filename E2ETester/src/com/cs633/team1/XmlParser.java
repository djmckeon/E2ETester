package com.cs633.team1;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class XmlParser {
	private String xmlString;
	private String xmlTag;
	private String responseValue;
	
	// Default constructor - initialize fields to empty
	public XmlParser() {
		this.setXmlString("");
		this.setXmlTag("");
		this.setResponseValue("");
	}
	
	// Overridden constructor - initialize fields to value provided
	public XmlParser(String xmlString, String xmlTag) {
		this.setXmlString(xmlString);
		this.setXmlTag(xmlTag);
		this.setResponseValue("");  // Initialize the output field
	}
	
	public void parseXmlDocument(String xmlString, String xmlTag, String responseValue) {
		this.setXmlString(xmlString);
		this.setXmlTag(xmlTag);
		try {
			parseXmlDocument();
		} catch (SAXException e) {
			responseValue = "Error";
		}
		responseValue = this.getResponseValue();
	}
	
	public void parseXmlDocument() throws SAXException {
		
		try {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
	 
		DefaultHandler handler = new DefaultHandler() {
			boolean bTagFound = false;

			public void startElement(String uri, String localName, String qName, 
	                Attributes attributes) throws SAXException {
	 
				if (qName.equalsIgnoreCase(getXmlTag())) {
					bTagFound = true;
				}
	 
			}
			
			public void characters(char ch[], int start, int length) throws SAXException {
				if (bTagFound) {
					setResponseValue(new String(ch, start, length));
					bTagFound = false;
				}		 
			}

		};
       saxParser.parse(new InputSource(new ByteArrayInputStream(getXmlString().getBytes("utf-8"))), handler);
		} catch (Exception e) {
			setResponseValue("Error");
		}

	}
	

public String getXmlString() {
	return xmlString;
}

public void setXmlString(String xmlString) {
	this.xmlString = xmlString;
}

public String getXmlTag() {
	return xmlTag;
}

public void setXmlTag(String xmlTag) {
	this.xmlTag = xmlTag;
}

public String getResponseValue() {
	return responseValue;
}

public void setResponseValue(String responseValue) {
	this.responseValue = responseValue;
}
 
}