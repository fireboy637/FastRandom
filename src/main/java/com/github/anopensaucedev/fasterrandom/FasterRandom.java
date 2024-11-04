package com.github.anopensaucedev.fasterrandom;

import com.github.anopensaucedev.fasterrandom.util.math.random.RandomGeneratorRandom;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class FasterRandom implements ModInitializer {
	public static final String MOD_ID = "faster-random";
	public static final String MOD_NAME = "Faster Random";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static RandomGeneratorRandom GLOBAL_LOCAL_INSTANCE;

	@Override
	public void onInitialize() {
		GLOBAL_LOCAL_INSTANCE = new RandomGeneratorRandom(ThreadLocalRandom.current().nextLong());
	}

}
