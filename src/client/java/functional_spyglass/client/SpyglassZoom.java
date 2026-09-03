package functional_spyglass.client;

import functional_spyglass.config.SpyglassConfig;
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
	 * Vanilla spyglass FOV is {@code options.fov * 0.1}. Scale mouse travel so a
	 * given hand motion covers a similar on-screen arc at every zoom step.
	 */
	public static double sensitivityScaleVsVanillaSpyglass() {
		float vanillaScoped = Math.max(1.0F, Minecraft.getInstance().options.fov().get().intValue())
				* (float) SpyglassConfig.VANILLA_SPYGLASS_FOV_SCALE;
		return currentFov() / vanillaScoped;
	}
}
