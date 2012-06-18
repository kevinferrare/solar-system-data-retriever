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
package org.kevinferrare.solarSystemDataRetriever.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filter for folder listing that will only accept files with the given extension
 * 
 * @author Kévin FERRARE
 * 
 */
public class OnlyExtensionFilter implements FilenameFilter {
	String extension;

	public OnlyExtensionFilter(String extension) {
		this.extension = "." + extension;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}
}
