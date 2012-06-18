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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.EphemerisType;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.OutputType;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.OutputUnit;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.QuantityCode;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.ReferencePlane;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.ReferenceSystem;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.parameters.VectorCorrection;
import org.kevinferrare.solarSystemDataRetriever.utils.OnlyExtensionFilter;

/**
 * Tool to fetch physical and orbital data from the JPL HORIZONS system.<br />
 * The data are queried using a {@link JplHorizonWebApi} and saved to a given folder.<br />
 * The file name is the queried object id with the .jplrawdata extension.
 * 
 * @author Kévin FERRARE
 * 
 */
public class JplHorizonRawDataRetriever {
	private static Log log = LogFactory.getLog(JplHorizonRawDataRetriever.class);
	private static final String RAW_DATA_FILE_EXTENSION = "jplrawdata";
	private JplHorizonWebApi jplHorizonSettingsSetter = new JplHorizonWebApi();
	private Date date;
	private String folderName = null;

	/**
	 * Gets the date that is used for the orbital data
	 * 
	 * @return the date that is used for the orbital data
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date for the orbital data
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the name of the folder in which to save / load the raw data files
	 * 
	 * @return the folder name
	 */
	public String getFolderName() {
		return folderName;
	}

	/**
	 * Sets the name of the folder in which to save / read the raw data files
	 * 
	 * @param folderName
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	/**
	 * Starts to setup the stateful JPL HORIZONS system to output the data we want (the setup occurs once for all objects)
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected void setup() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		jplHorizonSettingsSetter.setEphemerisType(EphemerisType.VECTORS);
		jplHorizonSettingsSetter.setTableSettings(OutputUnit.KILOMETER_PER_SECOND, QuantityCode.STATE_VECTORS, ReferencePlane.ECLIPTIC, ReferenceSystem.J2000, VectorCorrection.NONE, true, true, true);
		jplHorizonSettingsSetter.setOutputType(OutputType.TEXT);

		jplHorizonSettingsSetter.setDate(date);
	}

	/**
	 * Fetches data from the JPL HORIZONS system.<br />
	 * The system will be queried with the given list of ids, the function will then return a Map containing the id as the key and the result as the value
	 * 
	 * @param ids
	 *            the list of ids of the objects to query
	 * @return a Map with the fetched data
	 * @throws Exception
	 */
	public Map<String, String> fetchRawData(List<String> ids) throws Exception {
		setup();
		Map<String, String> rawDataMap = new HashMap<String, String>();
		for (String id : ids) {
			if(id.isEmpty()){
				log.error("Empty id present !");
				continue;
			}
			jplHorizonSettingsSetter.setTargetGravityObject(id);
			String rawResult = new String(jplHorizonSettingsSetter.getResult());
			rawDataMap.put(id, rawResult);
			log.info("Fetched " + id);
			if (folderName != null) {
				saveEntryToFolder(id, rawResult);
			}
		}
		return rawDataMap;
	}

	/**
	 * Saves the given Map to the folder that was set using setFolderName.<br />
	 * A single file is created for each key and the file content is the value
	 * 
	 * @param rawDataMap
	 *            the data to save as a Map
	 * @throws IOException
	 */
	public void saveToFolder(Map<String, String> rawDataMap) throws IOException {
		for (Entry<String, String> rawData : rawDataMap.entrySet()) {
			saveEntryToFolder(rawData.getKey(), rawData.getValue());
		}
	}

	/**
	 * Save the given content in a file whose name is the given "name" parameter.<br />
	 * The file will be saved in the folder that was set using setFolderName and will have the .jplrawdata extension.
	 * 
	 * @param name
	 *            the file name
	 * @param content
	 *            the content of the file
	 * @throws IOException
	 */
	protected void saveEntryToFolder(String name, String content) throws IOException {
		name = name.replace("*", "").replace("/", "");
		String path = folderName + "/" + name + "." + RAW_DATA_FILE_EXTENSION;
		FileWriter fstream = new FileWriter(path);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
	}

	/**
	 * Loads data from the folder that was set using setFolderName.<br />
	 * The loaded keys are the file names and the associated value is the file content.
	 * 
	 * @return a Map with the data loaded from the folder
	 * @throws IOException
	 */
	public Map<String, String> loadFromFolder() throws IOException {
		Map<String, String> rawDataMap = new HashMap<String, String>();
		File folder = new File(folderName);
		if (!folder.exists()) {
			log.error("Invalid folder " + folderName);
			return null;
		}
		File[] listOfFiles = folder.listFiles(new OnlyExtensionFilter(RAW_DATA_FILE_EXTENSION));
		for (File file : listOfFiles) {
			String name = file.getName().replace("." + RAW_DATA_FILE_EXTENSION, "");
			FileInputStream stream = new FileInputStream(file);
			FileChannel fileChannel = stream.getChannel();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			String content = Charset.forName("ASCII").decode(mappedByteBuffer).toString();
			stream.close();
			rawDataMap.put(name, content);
		}
		return rawDataMap;
	}
}
