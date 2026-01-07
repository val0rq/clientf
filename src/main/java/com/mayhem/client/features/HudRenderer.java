package com.mayhem.client.features;

import com.mayhem.client.config.ModConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;

import java.util.List;

public class HudRenderer {

    public static void register() {
        HudRenderCallback.EVENT.register(HudRenderer::onRender);
    }

    private static void onRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.debugEnabled || client.player == null) return;

        ModConfig config = ModConfig.get();
        TextRenderer tr = client.textRenderer;

        // 1. FPS Display
        if (config.showFps) {
            String fps = "FPS: " + client.getCurrentFps();
            context.drawTextWithShadow(tr, fps, config.fpsX, config.fpsY, 0xFFFFFF);
        }

        // 2. Coordinates
        if (config.showCoords) {
            BlockPos pos = client.player.getBlockPos();
            String coords = String.format("X: %d Y: %d Z: %d [%s]", 
                pos.getX(), pos.getY(), pos.getZ(), 
                client.player.getHorizontalFacing().asString().toUpperCase());
            context.drawTextWithShadow(tr, coords, config.coordsX, config.coordsY, 0xFFFFFF);
        }

        // 3. Minimap (Lightweight implementation)
        if (config.showMinimap) {
            renderMinimap(context, client, config);
        }
    }

    private static void renderMinimap(DrawContext context, MinecraftClient client, ModConfig config) {
        int centerX = config.minimapX + config.minimapSize / 2;
        int centerY = config.minimapY + config.minimapSize / 2;
        int radius = config.minimapSize / 2;

        // Background
        context.fill(config.minimapX, config.minimapY, 
                     config.minimapX + config.minimapSize, 
                     config.minimapY + config.minimapSize, 0x80000000);

        // Simple Player Dot
        context.fill(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFFFFF);

        // Entities (Radar)
        client.world.getEntities().forEach(entity -> {
            if (entity == client.player) return;
            
            double dx = entity.getX() - client.player.getX();
            double dz = entity.getZ() - client.player.getZ();
            
            // Rotation math to align with player yaw
            float yaw = (float) Math.toRadians(client.player.getYaw() + 180);
            double rotX = (dx * Math.cos(yaw)) - (dz * Math.sin(yaw));
            double rotY = (dx * Math.sin(yaw)) + (dz * Math.cos(yaw));

            // Scale down distance for map view (1 block = 0.5 pixel on map)
            double mapScale = 2.0; 
            double mapX = centerX + (rotX / mapScale);
            double mapY = centerY + (rotY / mapScale);

            // Clip to bounds
            if (mapX > config.minimapX && mapX < config.minimapX + config.minimapSize &&
                mapY > config.minimapY && mapY < config.minimapY + config.minimapSize) {
                
                int color = (entity instanceof LivingEntity) ? 0xFFFF0000 : 0xFF00FF00;
                if (WaypointManager.waypoints.stream().anyMatch(w -> w.name.equals(entity.getName().getString()))) {
                     color = 0xFF00FFFF; // Cyan for something special
                }
                
                context.fill((int)mapX - 1, (int)mapY - 1, (int)mapX + 1, (int)mapY + 1, color);
            }
        });
        
        // Render Waypoints on Minimap
        for (WaypointManager.Waypoint wp : WaypointManager.waypoints) {
            // Check Dimension
            if (!wp.dimension.equals(client.world.getRegistryKey().getValue().toString())) continue;

            double dx = wp.x - client.player.getX();
            double dz = wp.z - client.player.getZ();

            float yaw = (float) Math.toRadians(client.player.getYaw() + 180);
            double rotX = (dx * Math.cos(yaw)) - (dz * Math.sin(yaw));
            double rotY = (dx * Math.sin(yaw)) + (dz * Math.cos(yaw));

            double mapScale = 2.0; 
            double mapX = centerX + (rotX / mapScale);
            double mapY = centerY + (rotY / mapScale);
            
            if (mapX > config.minimapX && mapX < config.minimapX + config.minimapSize &&
                mapY > config.minimapY && mapY < config.minimapY + config.minimapSize) {
                context.fill((int)mapX - 2, (int)mapY - 2, (int)mapX + 2, (int)mapY + 2, wp.color);
            }
        }
        
        // Border
        context.drawBorder(config.minimapX, config.minimapY, config.minimapSize, config.minimapSize, 0xFFFFFFFF);
    }
}