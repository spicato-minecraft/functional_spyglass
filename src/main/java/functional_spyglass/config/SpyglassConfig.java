package functional_spyglass.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import functional_spyglass.FunctionalSpyglass;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Client-facing knobs at {@code config/functional_spyglass.json}. Minecraft FOV:
 * lower is more zoom. Vanilla spyglass uses a 0.1 FOV scale (~7 at option FOV 70).
 */
public final class SpyglassConfig {
	private static final Logger LOGGER = LogManager.getLogger(FunctionalSpyglass.MOD_ID + ".config");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = FunctionalSpyglass.MOD_ID + ".json";
	public static final double FOV_MIN = 1.0;
	public static final double FOV_MAX = 70.0;
	public static final double VANILLA_SPYGLASS_FOV_SCALE = 0.1;
	public static final double LOOK_CURVE_MIN = 0.0;
	public static final double LOOK_CURVE_MAX = 1.0;
	public static final double LOOK_CURVE_DEFAULT = 0.5;
	public static final double LOOK_SPEED_MIN = 0.25;
	public static final double LOOK_SPEED_MAX = 2.0;
	public static final double LOOK_SPEED_DEFAULT = 1.0;
	public static final double LOOK_MIN_DEFAULT = 0.7;
	public static final double LOOK_MAX_DEFAULT = 1.4;
	public static final double LOOK_RANGE_MIN = 0.05;
	public static final double LOOK_RANGE_MAX = 3.0;

	private static volatile SpyglassConfig instance;
	private static volatile boolean skipDiskReload;

	public Zoom zoom = new Zoom();

	public static final class Zoom {
		public double level_1_fov = 50.0;
		public double level_2_fov = 16.0;
		public double level_3_fov = 6.0;
		/** 0 = look min at every zoom; 1 = linear min→max; 0.5 = sqrt. */
		public Double look_curve;
		/** Extra multiplier after min/max blend. */
		public Double look_speed;
		/** Look scale at lowest magnification (level 1). */
		public Double look_min;
		/** Look scale at highest magnification (level 3). */
		public Double look_max;
	}

	public static SpyglassConfig get() {
		if (skipDiskReload) {
			SpyglassConfig current = instance;
			if (current == null) {
				current = defaults();
				instance = current;
			}
			return current;
		}
		SpyglassConfig current = instance;
		if (current == null) {
			current = load();
			instance = current;
		}
		return current;
	}

	public static void set(SpyglassConfig config) {
		instance = config;
		skipDiskReload = false;
	}

	public static void setForTest(SpyglassConfig config) {
		instance = config;
		skipDiskReload = true;
	}

	public static void resetForTest() {
		instance = null;
		skipDiskReload = false;
	}

	public static SpyglassConfig reload() {
		skipDiskReload = false;
		instance = load();
		return instance;
	}

	public static SpyglassConfig reloadFrom(Path configDir) {
		skipDiskReload = true;
		instance = loadFrom(configDir.resolve(FILE_NAME));
		return instance;
	}

	public static SpyglassConfig load() {
		return loadFrom(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
	}

	static SpyglassConfig loadFrom(Path path) {
		SpyglassConfig config = defaults();
		if (Files.isRegularFile(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				SpyglassConfig fromFile = GSON.fromJson(reader, SpyglassConfig.class);
				if (fromFile != null) {
					config = fromFile;
				}
			} catch (IOException | RuntimeException e) {
				LOGGER.error("Failed to read config {}, using defaults", path, e);
			}
		} else {
			try {
				Files.createDirectories(path.getParent());
				config.saveTo(path);
			} catch (IOException e) {
				LOGGER.warn("Could not write default config to {}", path, e);
			}
		}
		config.normalize();
		return config;
	}

	public static SpyglassConfig defaults() {
		SpyglassConfig config = new SpyglassConfig();
		config.normalize();
		return config;
	}

	public double fovForIndex(int index) {
		if (zoom == null) {
			zoom = new Zoom();
		}
		return switch (index) {
			case 1 -> clamp(zoom.level_2_fov);
			case 2 -> clamp(zoom.level_3_fov);
			default -> clamp(zoom.level_1_fov);
		};
	}

	public double lookCurve() {
		if (zoom == null || zoom.look_curve == null) {
			return LOOK_CURVE_DEFAULT;
		}
		return clampRange(zoom.look_curve, LOOK_CURVE_MIN, LOOK_CURVE_MAX, LOOK_CURVE_DEFAULT);
	}

	public double lookSpeed() {
		if (zoom == null || zoom.look_speed == null) {
			return LOOK_SPEED_DEFAULT;
		}
		return clampRange(zoom.look_speed, LOOK_SPEED_MIN, LOOK_SPEED_MAX, LOOK_SPEED_DEFAULT);
	}

	public double lookMin() {
		if (zoom == null || zoom.look_min == null) {
			return LOOK_MIN_DEFAULT;
		}
		return clampRange(zoom.look_min, LOOK_RANGE_MIN, LOOK_RANGE_MAX, LOOK_MIN_DEFAULT);
	}

	public double lookMax() {
		if (zoom == null || zoom.look_max == null) {
			return LOOK_MAX_DEFAULT;
		}
		return clampRange(zoom.look_max, LOOK_RANGE_MIN, LOOK_RANGE_MAX, LOOK_MAX_DEFAULT);
	}

	public void save() {
		try {
			saveTo(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
		} catch (IOException e) {
			LOGGER.error("Failed to save config {}", FILE_NAME, e);
		}
	}

	void saveTo(Path path) throws IOException {
		normalize();
		Files.createDirectories(path.getParent());
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(this, writer);
		}
	}

	void normalize() {
		if (zoom == null) {
			zoom = new Zoom();
		}
		zoom.level_1_fov = clamp(zoom.level_1_fov);
		zoom.level_2_fov = clamp(zoom.level_2_fov);
		zoom.level_3_fov = clamp(zoom.level_3_fov);
		zoom.look_curve = lookCurve();
		zoom.look_speed = lookSpeed();
		zoom.look_min = lookMin();
		zoom.look_max = lookMax();
	}

	static double clampRange(double value, double min, double max, double fallback) {
		if (Double.isNaN(value)) {
			return fallback;
		}
		return Math.min(max, Math.max(min, value));
	}

	static double clamp(double fov) {
		if (Double.isNaN(fov)) {
			return FOV_MAX;
		}
		return Math.min(FOV_MAX, Math.max(FOV_MIN, fov));
	}
}
