package functional_spyglass.clientgametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;

/**
 * Loads every client mixin target. A renamed class fails as soon as it is resolved on the client thread.
 */
public class ClientMixinApplySweep implements FabricClientGameTest {
	private static final String[] TARGETS = {
			"net.minecraft.client.player.AbstractClientPlayer",
			"net.minecraft.client.MouseHandler"
	};

	@Override
	public void runTest(ClientGameTestContext context) {
		context.computeOnClient(client -> {
			for (String name : TARGETS) {
				try {
					Class.forName(name);
				} catch (ClassNotFoundException e) {
					throw new AssertionError("Mixin target failed to load: " + name, e);
				}
			}
			return null;
		});
	}
}
