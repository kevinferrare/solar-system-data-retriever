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
package org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters;

/**
 * API to query the JPL HORIZONS system.<br />
 * Output unit parameter.
 * 
 * @author Kévin FERRARE
 * 
 */
public enum OutputUnit {
	AU_PER_DAY("AU-D"), // velocity in AU/d
	KILOMETER_PER_DAY("KM-D"), // velocity in km/d
	KILOMETER_PER_SECOND("KM-S");// velocity in km/s
	String value;

	OutputUnit(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
