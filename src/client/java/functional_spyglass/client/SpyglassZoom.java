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
	 * {@code lookSpeed * (currentFov / vanillaSpyglassFov) ^ lookCurve} on vanilla's
	 * scoped {@code f} path. Tunable from pack Debug admin.
	 */
	public static double sensitivityScale() {
		float unaided = Math.max(1.0F, Minecraft.getInstance().options.fov().get().intValue());
		SpyglassConfig config = SpyglassConfig.get();
		return ZoomSensitivity.scopedScale(currentFov(), unaided, config.lookCurve(), config.lookSpeed());
	}
}
