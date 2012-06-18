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
 * Quantity code parameter.
 * 
 * @author Kévin FERRARE
 * 
 */
public enum QuantityCode {
	POSTION_COMPONENTS("1"), // position components {x,y,z} only
	STATE_VECTORS("2"), // state vector {x,y,z,vx,vy,vz}
	STATE_VECTORS_ONE_WAY_LIGHT_TIME("3"), // state vector, 1-way light-time, range, and range-rate
	POSTION_ONE_WAY_LIGHT_TIME("4"), // position, 1-way light-time, range, and range-rate)
	VELOCITY_COMPONENTS("5"), // velocity components {vx,vy,vz} only
	ONE_WAY_LIGHT_TIME("6");// 1-way light-time, range, and range-rate

	String value;

	QuantityCode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
