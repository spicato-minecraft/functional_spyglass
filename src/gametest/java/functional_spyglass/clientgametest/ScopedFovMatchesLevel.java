package functional_spyglass.clientgametest;

import functional_spyglass.client.SpyglassZoom;
import functional_spyglass.config.SpyglassConfig;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.Minecraft;

/**
 * Scoped FOV follows {@code AbstractClientPlayerFovMixin}: configured level FOV divided by the option FOV.
 */
public class ScopedFovMatchesLevel implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		SpyglassClientSupport.useDefaultZoom();
		try (var world = context.worldBuilder().create()) {
			try {
				SpyglassClientSupport.giveSpyglass(context, world);
				SpyglassClientSupport.beginScope(context);
				for (int index = 0; index < SpyglassClientSupport.DEFAULT_LEVEL_FOVS.length; index++) {
					int levelIndex = index;
					if (levelIndex > 0) {
						context.runOnClient(client -> SpyglassZoom.STATE.cycle());
					}
					assertScopedFov(context, levelIndex);
				}
			} finally {
				SpyglassClientSupport.releaseHeldKeys(context);
			}
		}
	}

	private static void assertScopedFov(ClientGameTestContext context, int index) {
		context.runOnClient(client -> {
			if (client.player == null || !client.player.isScoping()) {
				throw new AssertionError("Expected to be scoping at level " + (index + 1));
			}
			if (SpyglassZoom.STATE.index() != index) {
				throw new AssertionError("Zoom index " + SpyglassZoom.STATE.index() + " != " + index);
			}
			double configuredFov = SpyglassConfig.get().fovForIndex(index);
			if (Double.compare(configuredFov, SpyglassClientSupport.DEFAULT_LEVEL_FOVS[index]) != 0) {
				throw new AssertionError("Level " + (index + 1) + " FOV " + configuredFov
						+ " != " + SpyglassClientSupport.DEFAULT_LEVEL_FOVS[index]);
			}
			float baseFov = baseFov(client);
			float expected = (float) configuredFov / baseFov;
			float actual = client.player.getFieldOfViewModifier(true, 1.0F);
			if (Float.compare(actual, expected) != 0) {
				throw new AssertionError("FOV modifier at level " + (index + 1) + " was " + actual
						+ ", expected " + configuredFov + " / " + baseFov + " = " + expected);
			}
		});
	}

	/**
	 * Same base the mixin uses: {@code max(1, options.fov)} as a float.
	 */
	private static float baseFov(Minecraft client) {
		return Math.max(1.0F, client.options.fov().get().intValue());
	}
}
