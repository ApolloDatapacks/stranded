package dev.worldgen.stranded;

import dev.worldgen.stranded.config.ConfigHandler;
import dev.worldgen.stranded.worldgen.densityfunction.StrandedContinentsDensityFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Stranded implements ModInitializer {
    public static final String MOD_ID = "stranded";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final RegistryKey<WorldPreset> STRANDED = RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(Stranded.MOD_ID, "stranded"));

    @Override
    public void onInitialize() {
        Registry.register(Registry.DENSITY_FUNCTION_TYPE, new Identifier(MOD_ID, "continents"), StrandedContinentsDensityFunction.CODEC);
        ServerLifecycleEvents.SERVER_STARTING.register(ConfigHandler::loadOrCreateDefaultConfig);
        ServerLifecycleEvents.SERVER_STOPPED.register(ConfigHandler::resetValues);
    }
}