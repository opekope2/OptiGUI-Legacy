package opekope2.optigui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import opekope2.optigui.callback.UseCallback;
import opekope2.optigui.optifinecompat.OptiFineResourceLoader;

public final class OptiGUIClient implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger logger = LoggerFactory.getLogger("OptiGUI");

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new OptiFineResourceLoader());
		UseCallback useCallback = new UseCallback();
		UseBlockCallback.EVENT.register(useCallback);
		UseEntityCallback.EVENT.register(useCallback);
		ClientTickEvents.END_WORLD_TICK.register(world -> GuiTextureReplacer.instance.updateCachedBlockOrEntity());
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> GuiTextureReplacer.instance.clearCaches());
		logger.info("OptiGUI initialized.");
	}
}
