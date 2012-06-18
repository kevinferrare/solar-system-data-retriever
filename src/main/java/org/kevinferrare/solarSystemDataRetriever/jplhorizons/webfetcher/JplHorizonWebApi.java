/*
 * Solar System Data Retriever
 * Copyright 2010 and beyond, Kévin Ferrare.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.EphemerisType;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.IntervalMode;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.OutputType;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.OutputUnit;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.QuantityCode;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.ReferencePlane;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.ReferenceSystem;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.VectorCorrection;
import org.kevinferrare.solarSystemDataRetriever.utils.HttpClientTool;

/**
 * API to query the JPL HORIZONS system using its web interface.<br />
 * After setting each parameters, call the getResult() function to retrieve the JPL HORIZONS system response.<br />
 * Each set... function call will result in an http query being sent to their server.<br />
 * <br />
 * The meaning of the parameters can be found on their website :<br />
 * http://ssd.jpl.nasa.gov/horizons.cgi
 * 
 * @author Kévin FERRARE
 * 
 */
public class JplHorizonWebApi extends HttpClientTool {
	private static final DateFormat yyyymmddhhmmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public void setEphemerisType(EphemerisType type) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("table_type", type.getValue()));
		nameValuePairs.add(new BasicNameValuePair("set_table_type", "Use Selection Above"));
		postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
	}

	public void setTargetGravityObject(String targetGravityObject) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("body", targetGravityObject));
		nameValuePairs.add(new BasicNameValuePair("select_body", "Select Indicated Body"));
		postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
		//may work better using this kind of requests http://ssd.jpl.nasa.gov/horizons.cgi?find_body=1&body_group=mb&sstr=301
	}

	public void setTimeSpan(Date startTime, Date stopTime, int step, IntervalMode intervalMode) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("start_time", yyyymmddhhmmFormat.format(startTime)));
		nameValuePairs.add(new BasicNameValuePair("stop_time", yyyymmddhhmmFormat.format(stopTime)));
		nameValuePairs.add(new BasicNameValuePair("step_size", Integer.toString(step)));
		nameValuePairs.add(new BasicNameValuePair("interval_mode", intervalMode.getValue()));
		nameValuePairs.add(new BasicNameValuePair("set_time_span", "Use Specified Times"));
		postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
	}

	public void setDate(Date date) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date endDate = calendar.getTime();
		this.setTimeSpan(date, endDate, 1, IntervalMode.DAYS);
		/*
		 * could also be implemented with http requests :
		 * get http://ssd.jpl.nasa.gov/horizons.cgi?s_time=1&mode=list
		 * post time_1=date, set_time_span="Use Specified Times" to http://ssd.jpl.nasa.gov/horizons.cgi
		 */
	}

	public void setTableSettings(OutputUnit outputUnit, QuantityCode quantityCode, ReferencePlane referencePlane, ReferenceSystem referenceSystem, VectorCorrection vectorCorrection, boolean labels, boolean csvFormat, boolean objectPage) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("output_units", outputUnit.getValue()));
		nameValuePairs.add(new BasicNameValuePair("vec_quan", quantityCode.getValue()));
		nameValuePairs.add(new BasicNameValuePair("ref_plane", referencePlane.getValue()));
		nameValuePairs.add(new BasicNameValuePair("ref_system", referenceSystem.getValue()));
		nameValuePairs.add(new BasicNameValuePair("vect_corr", vectorCorrection.getValue()));
		if (labels) {
			nameValuePairs.add(new BasicNameValuePair("vec_labels", "YES"));
		}
		if (csvFormat) {
			nameValuePairs.add(new BasicNameValuePair("csv_format", "YES"));
		}
		if (objectPage) {
			nameValuePairs.add(new BasicNameValuePair("obj_data", "YES"));
		}
		nameValuePairs.add(new BasicNameValuePair("set_table", "Use Settings Above"));
		nameValuePairs.add(new BasicNameValuePair("set_table_settings", "1"));
		postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
	}

	public byte[] setOutputType(OutputType outputType) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("display", outputType.getValue()));
		nameValuePairs.add(new BasicNameValuePair("set_display", "Use Selection Above"));
		return postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
	}

	/**
	 * Sends the query to the JPL HORIZONS system and returns the raw data it returned
	 * 
	 * @return the raw data returned by the server
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public byte[] getResult() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("go", "Generate Ephemeris"));
		return postData("http://ssd.jpl.nasa.gov/horizons.cgi", nameValuePairs);
	}
}
