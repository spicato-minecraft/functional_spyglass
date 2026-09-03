package functional_spyglass;

import functional_spyglass.config.SpyglassConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionalSpyglass implements ModInitializer {
	public static final String MOD_ID = "functional_spyglass";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SpyglassConfig.set(SpyglassConfig.load());
		LOGGER.info("Functional Spyglass initialized");
	}
}
