package com.github.anopensaucedev.fasterrandom.util.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FasterRandomConfig {

	private static final Logger configLogger = LoggerFactory.getLogger("Faster Random Config");

	private static final String configVersion = "0.0.1";

	public static final Path FASTER_RANDOM_CONFIG_PATH = Path.of("./config/faster-random");

	public static boolean UNSUPPORTED_SETTINGS_ENABLED = false;

	public static ConfigPatchSet getOrCreateConfig(){

		ConfigPatchSet result;

		try {

			if(Files.exists(FASTER_RANDOM_CONFIG_PATH.resolve("config.txt"))){

				result = readConfigFile(FASTER_RANDOM_CONFIG_PATH.resolve("config.txt").toFile());

			}else {
				configLogger.error("config not found, creating config and using defaults!");

				result = ConfigPatchSet.getDefaults();

				File cfg = FASTER_RANDOM_CONFIG_PATH.resolve("config.txt").toFile();
				FASTER_RANDOM_CONFIG_PATH.toFile().mkdirs();
				writeConfig(ConfigPatchSet.getDefaults(),cfg);
				return result;
			}

		} catch (Exception e) {
			configLogger.error("Config loading failed! Getting default config values.");
			configLogger.error("Error Message: {}",e.getMessage());
			result = ConfigPatchSet.getDefaults();
		}

		return result;
	}

	private static ConfigPatchSet readConfigFile(File file) {

		ConfigPatchSet configHolder;

		try(BufferedReader reader = new BufferedReader(new FileReader(file))){

			String line;

			String EngineName = null;
			boolean mergeRand = false;

			do {
				line = reader.readLine();

				if(line == null){ continue;}

				if(line.isBlank()){continue;}

				var tokens = line.split(" ");

				if (tokens[0].contains("#")){ continue; }

				if(tokens[0].contains("random_engine_name")){
					EngineName = tokens[1];
				}

				if(tokens[0].contains("patches.merge_entity_random")){
					mergeRand = Boolean.getBoolean(tokens[1]);
				}

			} while (line != null);

			if(EngineName == null){
				throw new Exception("Unacceptably broken config detected! Please delete this config to re-generate a working one! Using default values.");
			}

			if(mergeRand || !EngineName.equals("L64X128MixRandom")){
				UNSUPPORTED_SETTINGS_ENABLED = true;
				configLogger.warn("Unsupported settings have been enabled! When reporting an issue, consider disabling this to see if anything remains broken before making an issue.");
				configLogger.warn("Current config, RNG Engine: {}, Merge Patch: {}",EngineName,mergeRand);
			}

			configHolder = new ConfigPatchSet(EngineName,mergeRand);

		}catch (Exception e){
			e.printStackTrace();
			return  ConfigPatchSet.getDefaults();
		}
		return configHolder;
	}

	private static boolean writeConfig(ConfigPatchSet values, File file){
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(("# Enables future versions of Faster Random deal with older configs, don't edit this yourself! \n"));
			writer.write(("version: " + configVersion + "\n"));
			writer.write(("# The engine to use for Faster Random. The default engine is " + "L64X128MixRandom" + ". Other engines have not been tested, and may cause worse performance/crashes! \n"));
			writer.write(("random_engine_name: " + values.RANDOM_ENGINE_NAME + "\n"));
			writer.write(("# Patches for Faster Random, these may cause instability, or more crashes with other mods. Most of these patches exist to improve performance. \n \n"));
			writer.write(("# Merge random generators for entities. Improves entity random performance, but may make entity behaviour less predictable. \n"));
			writer.write(("patches.mergeEntityRandom: " + values.MERGE_ENTITY_RANDOM + "\n"));
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static class ConfigPatchSet{

		public ConfigPatchSet(String engineName, boolean mergeEntityRandom){
			this.RANDOM_ENGINE_NAME = engineName;
			this.MERGE_ENTITY_RANDOM = mergeEntityRandom;
		}

		public String RANDOM_ENGINE_NAME;

		public boolean MERGE_ENTITY_RANDOM;

		public static ConfigPatchSet getDefaults(){
			return new ConfigPatchSet("L64X128MixRandom", false);
		}

	}

}
