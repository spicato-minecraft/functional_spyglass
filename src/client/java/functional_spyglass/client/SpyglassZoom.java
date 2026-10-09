package functional_spyglass.client;

import functional_spyglass.config.SpyglassConfig;
import functional_spyglass.zoom.ZoomSensitivity;
import functional_spyglass.zoom.ZoomState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Client-only zoom index and FOV/sensitivity helpers.
 */
public final class SpyglassZoom {
	public static final ZoomState STATE = new ZoomState();

	private SpyglassZoom() {
	}

	public static void tick(Minecraft client) {
		LocalPlayer player = client.player;
		if (player == null || client.screen != null) {
			STATE.tickScoping(false);
			return;
		}
		boolean scoping = player.isScoping();
		if (scoping) {
			while (client.options.keyAttack.consumeClick()) {
				STATE.cycle();
			}
		}
		STATE.tickScoping(scoping);
	}

	public static float currentFov() {
		return (float) SpyglassConfig.get().fovForIndex(STATE.index());
	}

	/**
	 * {@code lookSpeed} times a min→max blend shaped by {@code lookCurve}.
	 * Tunable from pack Debug admin.
	 */
	public static double sensitivityScale() {
		SpyglassConfig config = SpyglassConfig.get();
		return ZoomSensitivity.scopedScale(
				currentFov(),
				config.fovForIndex(0),
				config.fovForIndex(2),
				config.lookCurve(),
				config.lookMin(),
				config.lookMax(),
				config.lookSpeed()
		);
	}
}
