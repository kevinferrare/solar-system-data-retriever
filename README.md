# Introduction
This program is a command line tool to query and parse the data from the [NASA JPL HORIZONS system](http://ssd.jpl.nasa.gov/?horizons) which is a very powerful online tool that provides accurate orbital data for a lot of objects in the solar system (planets, moons, asteroids and spacecrafts).

With the web interface of the HORIZONS system, you have to search for each object individually, set the parameters, download the data and repeat this process. This is time consuming when you want data for hundreds of objects. This could be automated using the telnet interface but you would need a program to generate the command list and store the responses anyway.

Also the data is split in one file per object and is in a format that is non trivial to parse (part of it seems hand written), making direct exploitation difficult.


This program attempts to solve those problems by allowing you to :
 * Batch download the raw orbital and physical data from the horizon system for a given list of objects.
 * Parse and process the downloaded data, and output some relevant items in a well formatted [CSV](http://en.wikipedia.org/wiki/Comma-separated_values) file.

# Use case

Let's say that we want to get orbital vectors for the [Pluto system](http://en.wikipedia.org/wiki/Moons_of_Pluto) at the date "2012-01-01 00:00" in order to input them in a simulator.

Here is how to proceed with this program :

## Search for their IDs in the HORIZONS system (Pluto and its 4 satellites)

The 4 currently known satellites of Pluto are : [Charon](http://en.wikipedia.org/wiki/Charon_(moon)), [Nix](http://en.wikipedia.org/wiki/Nix_(moon)), [Hydra](http://en.wikipedia.org/wiki/Hydra_(moon)), and the recently discovered [S/2011P1](http://en.wikipedia.org/wiki/S/2011_P_1)

`java -jar target/SolarSystemDataRetriever-1.0-jar-with-dependencies.jar --action SEARCH --object_list "pluto charon nix hydra S/2011P1"`
```
MB:999
MB:901
MB:902
MB:903
MB:904
```

With the 5 IDs above, we can unambiguously refer the objects in the JPL HORIZONS system


## Download the orbital data for the IDs we found at the date we want in a folder

`java -jar target/SolarSystemDataRetriever-1.0-jar-with-dependencies.jar --action FETCH --raw_data_folder /home/kevin/jplhorizon --orbit_date "2012-01-01 00:00" --object_list "MB:999 MB:902 MB:903 MB:901 MB:904"`

After this step, the folder /home/kevin/jplhorizon is filled with files containing raw data from the HORIZONS system for each objects.

## Extract the data from the downloaded files

`java -jar target/SolarSystemDataRetriever-1.0-jar-with-dependencies.jar --action PROCESS --raw_data_folder /home/kevin/jplhorizon --orbit_date "2012-01-01 00:00" --output_file /home/kevin/jplhorizon/plutoSystem.csv`

The output file /home/kevin/jplhorizon/plutoSystem.csv contains data about the objects (their masses, their densities, ... and their orbital state vectors ((x,y,z), (vx,vy,vz)).

If we plug the data we obtained in a simulator, we get the following result :

![bad](https://github.com/kevinferrare/solar-system-data-retriever/blob/master/src/doc/PlutoSystemBad.png)

*Problem* : this is different from the real orbits !!

![wikipedia](https://github.com/kevinferrare/solar-system-data-retriever/blob/master/src/doc/PlutoSystemWikipedia.jpg)

The reason is that the object masses provided by the HORIZONS system (at the date of this writing) are not accurate :
 * Mass of Charon from the HORIZONS system : "Mass (10^21 kg )        =   1.90 +- 0.04"
 * Mass of Charon from [wikipedia](http://en.wikipedia.org/wiki/Charon_(moon)) : 1.52*10^21 kg

If we want an accurate simulation we need to override the physical data from the HORIZONS system.

## Get an accurate files with correct physical properties

Download the [data correction file](https://github.com/kevinferrare/solar-system-data-retriever/blob/master/src/doc/solarSystemDataCorrections.csv) which contains more accurate physical data.

Run the program with the new data (provide it with the parameter --object_physical_data_file) :

`java -jar target/SolarSystemDataRetriever-1.0-jar-with-dependencies.jar --action PROCESS --raw_data_folder /home/kevin/jplhorizon --orbit_date "2012-01-01 00:00" --object_physical_data_file /home/kevin/jplhorizon/solarSystemDataCorrections.csv --output_file /home/kevin/jplhorizon/plutoSystem.csv`

Here is what we get :

![good](https://github.com/kevinferrare/solar-system-data-retriever/blob/master/src/doc/PlutoSystemGood.png)

Better, isn't it ?

# Proxies
The program retrieves the data using HTTP.

If you are going through a proxy, please make sure that Java is correctly configured.

You can also specify the proxy options to the JVM using command lines options like this :
` -Dhttp.proxyHost=webcache.mydomain.com -Dhttp.proxyPort=8080`

# For developpers
The project can be compiled using [maven](http://maven.apache.org/).

Just download the source code and compile it with the following command :

`mvn clean package`

# Author
  * Kévin Ferrare

# License
This project is licensed under the GNU LGPL v3
