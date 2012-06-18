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
package org.kevinferrare.solarSystemDataRetriever.jplhorizons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.JplHorizonGravityObjectCodeRetriever;

/**
 * Main for the object id search program.<br />
 * The program will search for the ids of given objects.<br />
 * The ids for the found objects are printed on stdout.<br />
 * The objects are input by their name passed to the program as argument.<br />
 * 
 * @author Kévin Ferrare
 * 
 */
public class JplHorizonGravityObjectIdSearch {
	private static Log log = LogFactory.getLog(JplHorizonGravityObjectIdSearch.class);

	public static void main(String[] args) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		JplHorizonGravityObjectCodeRetriever retriever = new JplHorizonGravityObjectCodeRetriever();
		for (int i = 0; i < args.length; i++) {
			String name = args[i];
			String objectCode = retriever.getGravityObjectCode(name);
			if (objectCode == null) {
				log.error("Could not find code for " + name);
			} else {
				System.out.println(objectCode);
			}
		}
	}
}