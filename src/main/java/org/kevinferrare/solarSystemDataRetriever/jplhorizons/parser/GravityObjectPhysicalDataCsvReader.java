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
package org.kevinferrare.solarSystemDataRetriever.jplhorizons.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reads physical object data from a CSV file.<br />
 * The format is as follow :<br />
 * name,mass,density
 * 
 * @author Kévin Ferrare
 * 
 */
public class GravityObjectPhysicalDataCsvReader {

	/**
	 * Reads the data in the CSV file.<br />
	 * Constructs and returns a Map where the object name is the key and the value is a {@link GravityObject} filled with the density and mass found in the CSV file.
	 * 
	 * @param in
	 *            the Reader from which to read the CSV data
	 * @return a Map associating an object id with its data
	 * @throws IOException
	 */
	public Map<String, GravityObject> read(Reader in) throws IOException {
		Map<String, GravityObject> data = new HashMap<String, GravityObject>();
		CSVReader reader = new CSVReader(in);
		String[] line;
		while ((line = reader.readNext()) != null) {
			String id = line[0];
			if (id.isEmpty()) {
				continue;
			}
			try {
				GravityObject gravityObject = new GravityObject();
				gravityObject.setName(line[1]);
				gravityObject.setMass(Double.parseDouble(line[2]));
				gravityObject.setDensity(Double.parseDouble(line[3]));
				data.put(id, gravityObject);
			} catch (NumberFormatException ex) {
			}
		}
		reader.close();
		return data;
	}
}
