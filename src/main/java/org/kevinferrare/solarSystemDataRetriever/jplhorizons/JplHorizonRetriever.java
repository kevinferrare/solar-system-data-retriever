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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.parser.GravityObject;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.parser.GravityObjectCsvWriter;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.parser.GravityObjectPhysicalDataCsvReader;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.parser.JplHorizonsDataParser;
import org.kevinferrare.solarSystemDataRetriever.jplhorizons.webfetcher.JplHorizonRawDataRetriever;

/**
 * Main for the data fetcher / processor program<br />
 * Some usage examples :<br />
 * Fetch :<br />
 * --raw_data_folder /home/kevin/gravityData/jplrawdata/<br />
 * --action FETCH<br />
 * --orbit_date "2007-01-01 00:00"<br />
 * --object_list "SB:C/2011 W3"<br />
 * <br />
 * Process :<br />
 * --raw_data_folder /home/kevin/gravityData/jplrawdata/<br />
 * --object_physical_data_file /home/kevin/gravityData/solarSystemDataCorrections.csv<br />
 * --action PROCESS<br />
 * --orbit_date "2007-01-01 00:00"<br />
 * --output_file /home/kevin/solarSystem.csv<br />
 * 
 * @author Kévin Ferrare
 * 
 */
public class JplHorizonRetriever {
	private static Log log = LogFactory.getLog(JplHorizonRetriever.class);
	private Options options = new Options();
	private CommandLine commandLine;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static void main(String[] args) throws Exception {
		JplHorizonRetriever instance = new JplHorizonRetriever(args);
		instance.process();
	}

	public JplHorizonRetriever(String[] args) throws ParseException {
		options.addOption(null, "action", true, "What to do ? FETCH : Fetch data from the JPL and store them to raw_data_folder, PROCESS : process fetched data in the raw_data_folder");
		options.addOption(null, "raw_data_folder", true, "Folder containing the files downloaded from the horizons system");
		options.addOption(null, "object_physical_data_file", true, "A file containing additional physical data (mass, density) because the files fetched from JPL are inaccurate !");
		options.addOption(null, "object_list", true, "A list of object ids to be fetched separated by a space (only when action is set to FETCH)");
		options.addOption(null, "major_objects", false, "Same as object_list, but uses a hardcoded list of major objects to fetch (only when action is set to FETCH)");
		options.addOption(null, "orbit_date", true, "Date of the orbit data to fetch (yyyy-MM-dd HH:mm format)");
		options.addOption(null, "output_file", true, "CSV file where the data will be written (only when action is set to PROCESS)");
		options.addOption(null, "help", false, "Print help");
		CommandLineParser parser = new PosixParser();
		commandLine = parser.parse(options, args);
	}

	public void process() throws Exception {
		if (commandLine.hasOption("help")) {
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp("Available options :", options);
			return;
		}
		String rawDataFolderName = commandLine.getOptionValue("raw_data_folder");
		if (rawDataFolderName == null || rawDataFolderName.isEmpty()) {
			log.error("raw_data_folder option not set !");
			return;
		}
		String action = commandLine.getOptionValue("action");
		if (action == null || action.isEmpty()) {
			log.error("action option not set !");
			return;
		}
		String orbitDateString = commandLine.getOptionValue("orbit_date");
		if (orbitDateString == null || orbitDateString.isEmpty()) {
			log.error("orbit_date not set !");
			return;
		}
		Date orbitDate = dateFormat.parse(orbitDateString);
		JplHorizonRawDataRetriever fetcher = new JplHorizonRawDataRetriever();
		fetcher.setFolderName(rawDataFolderName);

		if ("PROCESS".equals(action)) {
			String outputFileString = commandLine.getOptionValue("output_file");
			if (outputFileString == null || outputFileString.isEmpty()) {
				log.error("output_file not set !");
				return;
			}
			Map<String, GravityObject> additionalData = null;
			String objectPhysicalDataFileString = commandLine.getOptionValue("object_physical_data_file");
			if (objectPhysicalDataFileString != null) {
				File file = new File(objectPhysicalDataFileString);
				if (!file.exists()) {
					log.error("file set for object_physical_data_file does not exist !");
					return;
				}
				GravityObjectPhysicalDataCsvReader reader = new GravityObjectPhysicalDataCsvReader();
				additionalData = reader.read(new FileReader(file));
			}
			log.info("Reading files...");
			Map<String, String> rawDataMap = fetcher.loadFromFolder();
			if (rawDataMap == null) {
				return;
			}
			log.info("Read " + rawDataMap.size() + " files");

			log.info("Now parsing data ...");
			JplHorizonsDataParser parser = new JplHorizonsDataParser(additionalData);
			List<GravityObject> gravityObjects = parser.parseInfos(rawDataMap);
			log.info("Parsed " + gravityObjects.size() + " objects");

			GravityObjectCsvWriter writer = new GravityObjectCsvWriter();
			writer.write(new FileOutputStream(outputFileString), gravityObjects, orbitDate, "From JPL horizon data");
			log.info("Wrote objects to file " + outputFileString);
			return;
		} else if ("FETCH".equals(action)) {
			String objectListString = commandLine.getOptionValue("object_list");
			if (objectListString == null) {
				objectListString = "";
			}
			if (commandLine.hasOption("major_objects")) {
				objectListString += "MB:10 MB:-125544 MB:-127783 MB:-128485 MB:-130 MB:-134381 MB:-136134 MB:-136395 MB:-137872 MB:-140 MB:-150 MB:-151 MB:-163 MB:-165 MB:-176 MB:-177 MB:-178 MB:-18 MB:-181 MB:-198 MB:199 MB:-20 MB:-203 MB:-204 MB:-205 MB:-206 MB:-21 MB:-226 MB:-227 MB:-23 MB:-234 MB:-234900 MB:-235 MB:-236 MB:-24 MB:-248 MB:-25 MB:-253 MB:-254 MB:-29 MB:299 MB:-29900 MB:-30 MB:301 MB:-31 MB:-32 MB:-344 MB:399 MB:-40 MB:401 MB:402 MB:-41 MB:-47 MB:-47900 MB:-48 MB:-486 MB:-489 MB:499 MB:-5 MB:501 MB:502 MB:503 MB:504 MB:505 MB:506 MB:507 MB:508 MB:509 MB:510 MB:511 MB:512 MB:513 MB:514 MB:515 MB:516 MB:517 MB:518 MB:519 MB:520 MB:521 MB:522 MB:523 MB:524 MB:525 MB:526 MB:527 MB:528 MB:529 MB:-53 MB:530 MB:531 MB:532 MB:533 MB:534 MB:535 MB:536 MB:537 MB:538 MB:539 MB:540 MB:541 MB:542 MB:543 MB:544 MB:545 MB:546 MB:547 MB:548 MB:549 MB:-55 MB:550 MB:55060 MB:55061 MB:55062 MB:55063 MB:55064 MB:55065 MB:55066 MB:55067 MB:55068 MB:55069 MB:55070 MB:55071 MB:55072 MB:55073 MB:-557 MB:599 MB:-6 MB:601 MB:602 MB:603 MB:604 MB:605 MB:606 MB:607 MB:608 MB:609 MB:-61 MB:-610 MB:610 MB:611 MB:612 MB:613 MB:614 MB:615 MB:616 MB:617 MB:618 MB:619 MB:620 MB:621 MB:622 MB:623 MB:624 MB:625 MB:626 MB:627 MB:628 MB:629 MB:630 MB:631 MB:632 MB:633 MB:634 MB:635 MB:636 MB:637 MB:638 MB:639 MB:-64 MB:640 MB:641 MB:642 MB:643 MB:644 MB:645 MB:646 MB:647 MB:648 MB:649 MB:650 MB:65035 MB:65040 MB:65041 MB:65045 MB:65048 MB:65050 MB:65055 MB:65056 MB:-651 MB:651 MB:-652 MB:652 MB:653 MB:699 MB:-70 MB:701 MB:702 MB:703 MB:704 MB:705 MB:706 MB:707 MB:708 MB:709 MB:710 MB:711 MB:712 MB:713 MB:714 MB:715 MB:716 MB:717 MB:718 MB:719 MB:720 MB:721 MB:722 MB:723 MB:724 MB:725 MB:726 MB:727 MB:-74 MB:-74900 MB:-76 MB:-760 MB:-77 MB:-78 MB:-79 MB:799 MB:801 MB:802 MB:803 MB:804 MB:805 MB:806 MB:807 MB:808 MB:809 MB:810 MB:811 MB:812 MB:813 MB:-82 MB:-84 MB:-85 MB:899 MB:901 MB:902 MB:903 MB:904 MB:-93 MB:-98 MB:-996 MB:-997 MB:-998 MB:999 SB:1 SB:90377 SB:136199 SB:136108 SB:136472 SB:90482 SB:50000 SB:225088 SB:1P";
			}
			List<String> names = Arrays.asList(objectListString.split(" "));
			if (names.isEmpty()) {
				log.error("put at least one name in object_list or use predefined sets like major_objects !");
				return;
			}
			fetcher.setDate(orbitDate);
			fetcher.fetchRawData(names);
		} else {
			log.error("The action " + action + " is undefined !");
		}
	}
}

/*
Fetch :
--raw_data_folder /home/kevin/Desktop/perso/gravityData/jplrawdata/
--action FETCH
--orbit_date "2007-01-01 00:00"
--object_list "SB:C/2011 W3"

Process :
--raw_data_folder /home/kevin/Desktop/perso/gravityData/jplrawdata/
--object_physical_data_file /home/kevin/Desktop/perso/gravityData/solarSystemDataCorrections.csv
--action PROCESS
--orbit_date "2007-01-01 00:00"
--output_file /home/kevin/solarSystem.csv
 */