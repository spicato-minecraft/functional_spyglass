package functional_spyglass.clientgametest;

import functional_spyglass.client.SpyglassZoom;
import functional_spyglass.config.SpyglassConfig;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.CameraType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

final class SpyglassClientSupport {
	static final double[] DEFAULT_LEVEL_FOVS = {50.0, 16.0, 6.0};

	private SpyglassClientSupport() {
	}

	static void useDefaultZoom() {
		SpyglassConfig defaults = SpyglassConfig.defaults();
		for (int index = 0; index < DEFAULT_LEVEL_FOVS.length; index++) {
			double configured = defaults.fovForIndex(index);
			if (Double.compare(configured, DEFAULT_LEVEL_FOVS[index]) != 0) {
				throw new AssertionError("Default FOV for level " + (index + 1) + " is " + configured
						+ ", expected " + DEFAULT_LEVEL_FOVS[index]);
			}
		}
		SpyglassConfig.setForTest(defaults);
		SpyglassZoom.STATE.reset();
	}

	static void giveSpyglass(ClientGameTestContext context, TestSingleplayerContext world) {
		world.getConnection().waitForChunksDownload();
		world.getServer().runOnServer(server -> {
			ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
			player.getInventory().setSelectedSlot(0);
			player.getInventory().setSelectedItem(new ItemStack(Items.SPYGLASS));
			player.setXRot(-90.0F);
			player.setYRot(0.0F);
		});
		context.waitFor(client -> client.player != null && client.player.getMainHandItem().is(Items.SPYGLASS));
		context.runOnClient(client -> {
			client.options.setCameraType(CameraType.FIRST_PERSON);
			client.options.smoothCamera = false;
			client.player.setXRot(-90.0F);
			client.player.setYRot(0.0F);
		});
	}

	static void beginScope(ClientGameTestContext context) {
		SpyglassZoom.STATE.reset();
		context.getInput().holdKey(options -> options.keyUse);
		context.waitFor(client -> client.player != null && client.player.isScoping() && client.gui.screen() == null);
		context.runOnClient(client -> SpyglassZoom.STATE.reset());
		context.waitTick();
		assertLevel(context, 1);
	}

	static void stopScope(ClientGameTestContext context) {
		context.getInput().releaseKey(options -> options.keyUse);
		context.waitFor(client -> client.player != null && !client.player.isScoping());
		context.waitTick();
	}

	static void releaseHeldKeys(ClientGameTestContext context) {
		context.getInput().releaseKey(options -> options.keyUse);
		context.getInput().releaseKey(options -> options.keyAttack);
	}

	static void assertLevel(ClientGameTestContext context, int level) {
		context.runOnClient(client -> {
			if (client.player == null) {
				throw new AssertionError("Client player is missing at level " + level);
			}
			int actual = SpyglassZoom.STATE.level();
			if (actual != level) {
				throw new AssertionError("Zoom level " + actual + " != " + level);
			}
		});
	}
}
