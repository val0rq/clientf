package com.mayhem.client.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WaypointManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File WAYPOINTS_FILE = FabricLoader.getInstance().getConfigDir().resolve("mayhem_waypoints.json").toFile();
    public static List<Waypoint> waypoints = new ArrayList<>();

    public static class Waypoint {
        public String name;
        public int x, y, z;
        public String dimension;
        public int color; // Hex color

        public Waypoint(String name, int x, int y, int z, String dimension, int color) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.color = color;
        }
    }

    public static void add(String name, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        BlockPos pos = client.player.getBlockPos();
        String dim = client.world.getRegistryKey().getValue().toString();
        
        waypoints.add(new Waypoint(name, pos.getX(), pos.getY(), pos.getZ(), dim, color));
        save();
    }
    
    public static void remove(int index) {
        if (index >= 0 && index < waypoints.size()) {
            waypoints.remove(index);
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(WAYPOINTS_FILE)) {
            GSON.toJson(waypoints, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        if (!WAYPOINTS_FILE.exists()) return;
        try (FileReader reader = new FileReader(WAYPOINTS_FILE)) {
            Type listType = new TypeToken<ArrayList<Waypoint>>(){}.getType();
            List<Waypoint> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) waypoints = loaded;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}