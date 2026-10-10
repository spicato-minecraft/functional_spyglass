package functional_spyglass.clientgametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/**
 * Left click while scoping cycles 1 → 2 → 3 → 1. Lowering the spyglass returns to level 1.
 */
public class AttackCyclesAndReset implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		SpyglassClientSupport.useDefaultZoom();
		try (var world = context.worldBuilder().create()) {
			try {
				SpyglassClientSupport.giveSpyglass(context, world);
				SpyglassClientSupport.beginScope(context);
				pressAttack(context);
				assertStillScoping(context, 2);
				pressAttack(context);
				assertStillScoping(context, 3);
				pressAttack(context);
				assertStillScoping(context, 1);
				SpyglassClientSupport.stopScope(context);
				SpyglassClientSupport.assertLevel(context, 1);
			} finally {
				SpyglassClientSupport.releaseHeldKeys(context);
			}
		}
	}

	private static void pressAttack(ClientGameTestContext context) {
		context.getInput().pressKey(options -> options.keyAttack);
	}

	private static void assertStillScoping(ClientGameTestContext context, int level) {
		context.runOnClient(client -> {
			if (client.player == null || !client.player.isScoping()) {
				throw new AssertionError("Attack dropped the spyglass before level " + level);
			}
		});
		SpyglassClientSupport.assertLevel(context, level);
	}
}
