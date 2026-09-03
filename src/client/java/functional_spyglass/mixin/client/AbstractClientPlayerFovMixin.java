package functional_spyglass.mixin.client;

import functional_spyglass.client.SpyglassZoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerFovMixin {
	@Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
	private void functionalSpyglass$scopedFov(boolean applyScoping, float partialTick, CallbackInfoReturnable<Float> cir) {
		AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
		if (applyScoping && self.isScoping()) {
			float baseFov = Math.max(1.0F, Minecraft.getInstance().options.fov().get().intValue());
			cir.setReturnValue(SpyglassZoom.currentFov() / baseFov);
		}
	}
}
