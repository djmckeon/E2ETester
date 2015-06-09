package com.cs633.team1;

import java.io.IOException;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Consumes a RESTful web service
 * @author mckeon
 *
 */
public class ConsumeWebService {

	private String endPoint;
	private String inputParam;
	private String xmlTag;
	private String webServiceResponse;
	
	/**
	 *  Default constructor - initialize the fields
	 */
	public ConsumeWebService() {
		this.setEndPoint("");
		this.setInputParam("");
		this.setXmlTag("");
		this.setWebServiceResponse("");
	}
	
	/**
	 *  Overridden constructor - set the fields to the passed in values
	 * @param endPoint
	 * @param inputParam
	 * @param xmlTag
	 */
	public ConsumeWebService(String endPoint, String inputParam, String xmlTag) {
		this.setEndPoint(endPoint);
		this.setInputParam(inputParam);
		this.setXmlTag(xmlTag);
		this.setWebServiceResponse("");  // This is an output field, so initialize it
	}
	
	/**
	 * Calls the RESTful web service defined in the endPoint and inputParm fields.
	 * @throws Exception
	 */
    public void callRestService() throws Exception {
    	HttpGet httpget;
    	
    	if (this.getEndPoint().equals("")) {
    		throw new Exception("Web service end point not specified");
    	}
    	
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
        	if (this.getInputParam().equals("")) {
        		httpget = new HttpGet(this.getEndPoint());
        	} else {
        		httpget = new HttpGet(this.getEndPoint() + this.getInputParam());
        	}

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };
            String responseBody = httpclient.execute(httpget, responseHandler);
            
            // Parse the XML to get the desired tag
            XmlParser parser = new XmlParser(responseBody, this.getXmlTag());
            parser.parseXmlDocument();
            
            this.setWebServiceResponse(parser.getResponseValue());
        } finally {
            httpclient.close();
        }
    }

    /**
     * Getter for the endPoint field 
     * @return endPoint
     */
	public String getEndPoint() {
		return endPoint;
	}

	/**
	 * Setter for the endpoint field
	 * @param endPoint
	 */
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Getter for the inputParm field
	 * @return inputParm
	 */
	public String getInputParam() {
		return inputParam;
	}

	/**
	 * Setter for the inputParm field
	 * @param inputParam
	 */
	public void setInputParam(String inputParam) {
		this.inputParam = inputParam;
	}

	/**
	 * Getter for the webServiceResponse field
	 * @return webServiceResponse
	 */
	public String getWebServiceResponse() {
		return webServiceResponse;
	}

	/**
	 * Setter for the webServiceResponse field
	 * @param webServiceResponse
	 */
	public void setWebServiceResponse(String webServiceResponse) {
		this.webServiceResponse = webServiceResponse;
	}

	/**
	 * Getter for the xmlTag field
	 * @return xmlTag
	 */
	public String getXmlTag() {
		return xmlTag;
	}

	/**
	 * Setter for the xmlTag field
	 * @param xmlTag
	 */
	public void setXmlTag(String xmlTag) {
		this.xmlTag = xmlTag;
	}

}

