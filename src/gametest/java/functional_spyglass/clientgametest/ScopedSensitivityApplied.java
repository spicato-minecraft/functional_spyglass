package functional_spyglass.clientgametest;

import functional_spyglass.client.SpyglassZoom;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.client.MouseHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * While scoped, {@code MouseHandlerSensitivityMixin} multiplies two stores in {@code turnPlayer}
 * by {@link SpyglassZoom#sensitivityScale()}: the adjusted sensitivity before it is cubed, and the
 * horizontal turn delta after that cubed value is applied. First person uses the cubed value.
 */
public class ScopedSensitivityApplied implements FabricClientGameTest {
	/** {@code MouseHandler.turnPlayer} sensitivity * this + {@link #SENSITIVITY_OFFSET}. */
	private static final double SENSITIVITY_SCALE = 0.6000000238418579D;
	private static final double SENSITIVITY_OFFSET = 0.20000000298023224D;
	/** {@code Entity.turn} converts the mouse delta into degrees. */
	private static final float TURN_DEGREES_PER_UNIT = 0.15F;
	private static final double MOUSE_DX = 20.0D;

	@Override
	public void runTest(ClientGameTestContext context) {
		SpyglassClientSupport.useDefaultZoom();
		try (var world = context.worldBuilder().create()) {
			try {
				SpyglassClientSupport.giveSpyglass(context, world);
				SpyglassClientSupport.beginScope(context);
				context.runOnClient(client -> {
					SpyglassZoom.STATE.cycle();
					SpyglassZoom.STATE.cycle();
				});
				SpyglassClientSupport.assertLevel(context, 3);
				assertTurnScale(context);
			} finally {
				SpyglassClientSupport.releaseHeldKeys(context);
			}
		}
	}

	private static void assertTurnScale(ClientGameTestContext context) {
		context.runOnClient(client -> {
			if (client.player == null || !client.player.isScoping()) {
				throw new AssertionError("Expected to be scoping at level 3");
			}
			if (!client.options.getCameraType().isFirstPerson()) {
				throw new AssertionError("Scoped sensitivity is applied on the first-person turn path");
			}
			double sensitivity = client.options.sensitivity().get();
			double adjusted = sensitivity * SENSITIVITY_SCALE + SENSITIVITY_OFFSET;
			double scale = SpyglassZoom.sensitivityScale();
			double scaledAdjusted = adjusted * scale;
			double cubed = scaledAdjusted * scaledAdjusted * scaledAdjusted;
			if (!(cubed > 1.0e-9)) {
				throw new AssertionError("Mouse sensitivity cubed to " + cubed);
			}
			boolean invertX = client.options.invertMouseX().get();
			double signedDx = invertX ? -MOUSE_DX : MOUSE_DX;
			float expectedYaw = ((float) (signedDx * cubed * scale)) * TURN_DEGREES_PER_UNIT;

			client.player.setXRot(0.0F);
			client.player.setYRot(0.0F);
			try {
				Field accumulatedDx = MouseHandler.class.getDeclaredField("accumulatedDX");
				Field accumulatedDy = MouseHandler.class.getDeclaredField("accumulatedDY");
				accumulatedDx.setAccessible(true);
				accumulatedDy.setAccessible(true);
				accumulatedDx.setDouble(client.mouseHandler, MOUSE_DX);
				accumulatedDy.setDouble(client.mouseHandler, 0.0D);
				Method turnPlayer = MouseHandler.class.getDeclaredMethod("turnPlayer", double.class);
				turnPlayer.setAccessible(true);
				turnPlayer.invoke(client.mouseHandler, 1.0D);
			} catch (ReflectiveOperationException e) {
				throw new AssertionError("Could not apply a scoped mouse turn", e);
			}

			float yaw = client.player.getYRot();
			if (Float.compare(yaw, expectedYaw) != 0) {
				double implied = (yaw / TURN_DEGREES_PER_UNIT) / (signedDx * cubed);
				throw new AssertionError("Scoped turn scale " + implied + " != " + scale
						+ " (yaw " + yaw + ", expected " + expectedYaw + ")");
			}
		});
	}
}
