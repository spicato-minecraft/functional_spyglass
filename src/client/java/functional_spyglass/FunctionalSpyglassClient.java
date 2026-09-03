package functional_spyglass;

import functional_spyglass.client.SpyglassZoom;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class FunctionalSpyglassClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(SpyglassZoom::tick);
	}
}
