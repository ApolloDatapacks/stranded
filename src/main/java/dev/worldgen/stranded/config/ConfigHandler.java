package dev.worldgen.stranded.config;

import dev.worldgen.stranded.Stranded;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.worldgen.stranded.mixin.MinecraftServerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class ConfigHandler {
    private static Path FILE_PATH;
    private static final Map<String, Object> DEFAULT_CONFIG_VALUES = new LinkedHashMap<>(){
        {
            put("island_size", 15);
            put("compatible_mode", false);
        }
    };
    private static Map<String, Object> CONFIG_VALUES = new HashMap<>();

    public static void loadOrCreateDefaultConfig(MinecraftServer server) {
        FILE_PATH = getConfigFilePath(server);
        if (!Files.isRegularFile(FILE_PATH)) {
            Stranded.LOGGER.info("Config file for "+Stranded.MOD_ID+" not found, creating file with default values...");
            try(BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
                writer.write("{}");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            JsonElement json = JsonParser.parseReader(reader);
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> configValues : jsonObject.entrySet()) {
                if (DEFAULT_CONFIG_VALUES.containsKey(configValues.getKey())) {
                    String key = configValues.getKey();
                    Object value = configValues.getValue();
                    CONFIG_VALUES.put(key, value);
                }
            }
            for (Map.Entry<String, Object> defaultConfigValues : DEFAULT_CONFIG_VALUES.entrySet()) {
                CONFIG_VALUES.putIfAbsent(defaultConfigValues.getKey(), defaultConfigValues.getValue());
            }
            writeToFile(CONFIG_VALUES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getConfigFilePath(MinecraftServer server) {
        if (server.isDedicated()) {
            return FabricLoader.getInstance().getConfigDir().resolve("stranded.json");
        }
        Path rootSavePath = ((MinecraftServerAccessor)server).getSession().getWorldDirectory(World.OVERWORLD);
        File configPath = new File(String.valueOf(rootSavePath), "config");
        if (!configPath.mkdirs()) {
            Stranded.LOGGER.debug("Failed to create config folder :(");
        }
        return new File(configPath, "stranded.json").toPath();
    }
    public static Object getConfigValue(String key) {
        Object value;
        if (CONFIG_VALUES.containsKey(key)) {
            value = CONFIG_VALUES.get(key);
        } else if (DEFAULT_CONFIG_VALUES.containsKey(key)) {
            value = DEFAULT_CONFIG_VALUES.get(key);
        } else {
            throw new NullPointerException("Could not find config key: "+key);
        }
        return value;
    }

    public static void setConfigValue(String key, Object value) {
        CONFIG_VALUES.put(key, value);
    }

    private static void writeToFile(Map<String, Object> input) {
        try(BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(input));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetValues(MinecraftServer minecraftServer) {
        CONFIG_VALUES.clear();
        CONFIG_VALUES.putAll(DEFAULT_CONFIG_VALUES);
    }
}