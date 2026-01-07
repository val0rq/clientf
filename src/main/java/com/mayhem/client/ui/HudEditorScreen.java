package com.mayhem.client.ui;

import com.mayhem.client.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.Rectangle;

public class HudEditorScreen extends Screen {
    private final Screen parent;
    private ModConfig config;
    
    // Tracking drag
    private String draggingElement = null;
    private int dragOffsetX, dragOffsetY;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("HUD Editor"));
        this.parent = parent;
        this.config = ModConfig.get();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        context.drawCenteredTextWithShadow(textRenderer, "Drag elements to move. ESC to save.", width / 2, 10, 0xFFFFFF);

        // Draw selection boxes
        drawBox(context, "FPS", config.fpsX, config.fpsY, 50, 10, mouseX, mouseY);
        drawBox(context, "COORDS", config.coordsX, config.coordsY, 150, 10, mouseX, mouseY);
        drawBox(context, "MINIMAP", config.minimapX, config.minimapY, config.minimapSize, config.minimapSize, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBox(DrawContext context, String name, int x, int y, int w, int h, int mx, int my) {
        boolean hover = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = hover ? 0x8000FF00 : 0x80FF0000;
        
        context.fill(x, y, x + w, y + h, color);
        context.drawBorder(x, y, w, h, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, name, x + 2, y + 2, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isHovering(config.fpsX, config.fpsY, 50, 10, mouseX, mouseY)) {
                draggingElement = "FPS";
                dragOffsetX = (int)mouseX - config.fpsX;
                dragOffsetY = (int)mouseY - config.fpsY;
            } else if (isHovering(config.coordsX, config.coordsY, 150, 10, mouseX, mouseY)) {
                draggingElement = "COORDS";
                dragOffsetX = (int)mouseX - config.coordsX;
                dragOffsetY = (int)mouseY - config.coordsY;
            } else if (isHovering(config.minimapX, config.minimapY, config.minimapSize, config.minimapSize, mouseX, mouseY)) {
                draggingElement = "MINIMAP";
                dragOffsetX = (int)mouseX - config.minimapX;
                dragOffsetY = (int)mouseY - config.minimapY;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingElement = null;
        ModConfig.save();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingElement != null) {
            int newX = (int)mouseX - dragOffsetX;
            int newY = (int)mouseY - dragOffsetY;
            
            switch (draggingElement) {
                case "FPS" -> { config.fpsX = newX; config.fpsY = newY; }
                case "COORDS" -> { config.coordsX = newX; config.coordsY = newY; }
                case "MINIMAP" -> { config.minimapX = newX; config.minimapY = newY; }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private boolean isHovering(int x, int y, int w, int h, double mx, double my) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public void close() {
        ModConfig.save();
        client.setScreen(parent);
    }
}