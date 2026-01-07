package com.mayhem.client.ui;

import com.mayhem.client.features.WaypointManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class WaypointScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;

    public WaypointScreen(Screen parent) {
        super(Text.literal("Waypoint Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        nameField = new TextFieldWidget(textRenderer, centerX - 100, centerY - 20, 200, 20, Text.literal("Name"));
        addDrawableChild(nameField);

        // Add Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Create Waypoint Here"), button -> {
            String name = nameField.getText();
            if (name.isEmpty()) name = "Waypoint";
            WaypointManager.add(name, 0xFFFF0000); // Default Red
            this.close();
        }).dimensions(centerX - 100, centerY + 10, 200, 20).build());

        // Clear All Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear All Waypoints"), button -> {
            WaypointManager.waypoints.clear();
            WaypointManager.save();
            this.close();
        }).dimensions(centerX - 100, centerY + 40, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, "New Waypoint", width / 2, height / 2 - 40, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}