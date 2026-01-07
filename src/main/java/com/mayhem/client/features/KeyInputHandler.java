package com.mayhem.client.features;

import com.mayhem.client.config.ModConfig;
import com.mayhem.client.ui.HudEditorScreen;
import com.mayhem.client.ui.WaypointScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    
    public static KeyBinding hudEditorKey;
    public static KeyBinding waypointKey;
    public static KeyBinding toggleMinimap;
    public static KeyBinding toggleFps;

    public static void register() {
        hudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mayhem.editor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.mayhem.general"
        ));

        waypointKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mayhem.waypoint",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.mayhem.general"
        ));
        
        toggleMinimap = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mayhem.minimap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            "category.mayhem.general"
        ));

        toggleFps = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.mayhem.fps",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "category.mayhem.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hudEditorKey.wasPressed()) {
                client.setScreen(new HudEditorScreen(client.currentScreen));
            }
            while (waypointKey.wasPressed()) {
                client.setScreen(new WaypointScreen(client.currentScreen));
            }
            while (toggleMinimap.wasPressed()) {
                ModConfig.get().showMinimap = !ModConfig.get().showMinimap;
                ModConfig.save();
            }
            while (toggleFps.wasPressed()) {
                ModConfig.get().showFps = !ModConfig.get().showFps;
                ModConfig.get().showCoords = ModConfig.get().showFps;
                ModConfig.save();
            }
        });
    }
}