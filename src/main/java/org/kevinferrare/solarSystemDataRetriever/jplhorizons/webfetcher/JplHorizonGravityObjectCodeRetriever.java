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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.kevinferrare.solarSystemDataRetriever.utils.HttpClientTool;

/**
 * Tool to retrieve an object id from its name using the JPL HORIZONS system
 * 
 * @author Kévin FERRARE
 * 
 */
public class JplHorizonGravityObjectCodeRetriever extends HttpClientTool {
	/**
	 * Tries to find an object id for the given object name
	 * 
	 * @param name
	 *            the name of the object to search
	 * @return a String containing the object id if found or null else
	 */
	public String getGravityObjectCode(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		String code = getGravityObjectCodeFromLocalDatabase(name);
		if (code == null) {
			code = getGravityObjectCodeFromNetwork(name);
		}
		return code;
	}

	/**
	 * Search the given name from a local database (the JPL search page is only for small bodies so planets and major moons are not there)
	 * 
	 * @param name
	 *            the name of the object to search
	 * @return a String containing the object id if found or null else
	 */
	protected String getGravityObjectCodeFromLocalDatabase(String name) {
		name = name.toUpperCase();
		for (String[] bodyDescription : JplHorizonsMajorBodiesList.majorBodies) {
			if (bodyDescription[0].toUpperCase().contains(name)) {
				return bodyDescription[1];
			}
		}
		return null;
	}

	/**
	 * Queries the JPL HORIZONS system and returns the object id for the given name, or null if not found
	 * 
	 * @param name
	 *            the name of the object to search
	 * @return a String containing the object id if found or null else
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected String getGravityObjectCodeFromNetwork(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		String result = new String(getResult(name));
		String[] lines = result.split("\n");
		for (String line : lines) {
			if (line.contains("find_body")) {
				String[] parameters = line.split("find_body")[1].split("\"")[0].split("&amp;");
				String objectGroup = "";
				String sstr = "";
				for (String parameter : parameters) {
					if (parameter.contains("body_group")) {
						objectGroup = parameter.split("=")[1].toUpperCase();
					}
					if (parameter.contains("sstr")) {
						sstr = parameter.split("=")[1].toUpperCase();
					}
				}
				return objectGroup + ":" + sstr;
			}
		}
		return null;
	}

	/**
	 * Queries the JPL HORIZONS system over HTTP and returns the raw data
	 * 
	 * @param name
	 *            the object name
	 * @return the raw data returned by the server
	 */
	protected byte[] getResult(String name) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("sstr", name));
		return postData("http://ssd.jpl.nasa.gov/sbdb.cgi", nameValuePairs);
	}
}
