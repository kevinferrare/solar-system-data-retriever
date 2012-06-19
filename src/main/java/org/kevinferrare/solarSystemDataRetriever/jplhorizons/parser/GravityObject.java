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

/**
 * Stores data about a gravity object : 
 * <ul>
 *  <li>The mass (in kilogram)</li>
 *  <li>The density (in kilogram per cube meter)</li>
 *  <li>The x,y,z position (in meter)</li>
 *  <li>The vx,vy,vz velocity (in meter per second)</li>
 * </ul>
 * @author Kévin Ferrare
 * 
 */
public class GravityObject {
	private String id;
	private String name;
	private double density;//density in kg/m3
	private double mass;//mass in kilograms
	private GravityObjectType type;
	private double x;
	private double y;
	private double z;
	private double vx;
	private double vy;
	private double vz;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDensity() {
		return density;
	}

	public void setDensity(double density) {
		this.density = density;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public GravityObjectType getType() {
		return type;
	}

	public void setType(GravityObjectType type) {
		this.type = type;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getVx() {
		return vx;
	}

	public void setVx(double vx) {
		this.vx = vx;
	}

	public double getVy() {
		return vy;
	}

	public void setVy(double vy) {
		this.vy = vy;
	}

	public double getVz() {
		return vz;
	}

	public void setVz(double vz) {
		this.vz = vz;
	}

}
