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
 * Output type parameter.
 * 
 * @author Kévin FERRARE
 * 
 */
public enum OutputType {
	HTML("HTML"), //
	TEXT("TEXT"), //
	SAVE("SAVE"); // useful for us ?
	String value;

	OutputType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
