package com.mayhem.client;

import com.mayhem.client.config.ModConfig;
import com.mayhem.client.features.HudRenderer;
import com.mayhem.client.features.KeyInputHandler;
import com.mayhem.client.features.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ClientInitializer implements ClientModInitializer {
    public static final String MOD_ID = "mayhemclient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Mounts of Mayhem Client...");

        // Load Config
        ModConfig.load();

        // Load Waypoints
        WaypointManager.load();

        // Register Keybinds
        KeyInputHandler.register();

        // Register Rendering
        HudRenderer.register();
    }
}