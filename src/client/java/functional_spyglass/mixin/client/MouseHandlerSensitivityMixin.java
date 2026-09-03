package functional_spyglass.mixin.client;

import functional_spyglass.client.SpyglassZoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public class MouseHandlerSensitivityMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@ModifyVariable(method = "turnPlayer", at = @At("STORE"), ordinal = 1)
	private double functionalSpyglass$scaleScopedSensitivity(double cubedSensitivity) {
		if (this.minecraft.player != null && this.minecraft.player.isScoping()) {
			return cubedSensitivity * SpyglassZoom.sensitivityScaleVsVanillaSpyglass();
		}
		return cubedSensitivity;
	}
}
