package functional_spyglass.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpyglassConfigTest {

	@AfterEach
	void resetSingleton() {
		SpyglassConfig.resetForTest();
	}

	@Test
	void fovForIndexUsesConfiguredLevels() {
		SpyglassConfig config = SpyglassConfig.defaults();
		config.zoom.level_1_fov = 48;
		config.zoom.level_2_fov = 15;
		config.zoom.level_3_fov = 6;
		assertEquals(48.0, config.fovForIndex(0));
		assertEquals(15.0, config.fovForIndex(1));
		assertEquals(6.0, config.fovForIndex(2));
	}

	@Test
	void reloadFromReadsJsonWithoutGameConfigDir(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("functional_spyglass.json");
		Files.writeString(file, """
				{
				  "zoom": {
				    "level_1_fov": 40.0,
				    "level_2_fov": 12.0,
				    "level_3_fov": 5.0,
				    "look_curve": 0.25,
				    "look_speed": 0.8
				  }
				}
				""");
		SpyglassConfig loaded = SpyglassConfig.reloadFrom(tempDir);
		assertEquals(40.0, loaded.fovForIndex(0));
		assertEquals(12.0, loaded.fovForIndex(1));
		assertEquals(5.0, loaded.fovForIndex(2));
		assertEquals(0.25, loaded.lookCurve());
		assertEquals(0.8, loaded.lookSpeed());
		assertEquals(40.0, SpyglassConfig.get().fovForIndex(0));
	}

	@Test
	void clampRejectsOutOfRangeFov() {
		SpyglassConfig config = SpyglassConfig.defaults();
		config.zoom.level_1_fov = 0;
		config.zoom.level_3_fov = 999;
		config.normalize();
		assertEquals(SpyglassConfig.FOV_MIN, config.zoom.level_1_fov);
		assertEquals(SpyglassConfig.FOV_MAX, config.zoom.level_3_fov);
	}

	@Test
	void missingLookKeysUseDefaults(@TempDir Path tempDir) throws Exception {
		Path file = tempDir.resolve("functional_spyglass.json");
		Files.writeString(file, """
				{
				  "zoom": {
				    "level_1_fov": 40.0,
				    "level_2_fov": 12.0,
				    "level_3_fov": 5.0
				  }
				}
				""");
		SpyglassConfig loaded = SpyglassConfig.reloadFrom(tempDir);
		assertEquals(SpyglassConfig.LOOK_CURVE_DEFAULT, loaded.lookCurve());
		assertEquals(SpyglassConfig.LOOK_SPEED_DEFAULT, loaded.lookSpeed());
	}
}
