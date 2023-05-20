package dev.worldgen.stranded.worldgen.densityfunction;

import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.stranded.config.ConfigHandler;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class StrandedContinentsDensityFunction implements DensityFunction {
    public static Codec<StrandedContinentsDensityFunction> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
            Codec.BOOL.fieldOf("ignore_compatible_mode_setting").forGetter(config -> {
                return config.ignoreCompatMode;
            })).apply(instance, StrandedContinentsDensityFunction::new);
    });

    public static CodecHolder<StrandedContinentsDensityFunction> CODEC_HOLDER = CodecHolder.of(CODEC);
    private final Boolean ignoreCompatMode;

    public StrandedContinentsDensityFunction(Boolean ignoreCompatMode) {
        this.ignoreCompatMode = ignoreCompatMode;
    }

    @Override
    public double sample(NoisePos pos) {
        Boolean configValue;
        if (ConfigHandler.getConfigValue("compatible_mode") instanceof Boolean) {
            configValue = (Boolean) ConfigHandler.getConfigValue("compatible_mode");
        } else {
            configValue = ((JsonPrimitive)ConfigHandler.getConfigValue("compatible_mode")).getAsBoolean();
        }
        if (!configValue && !ignoreCompatMode) {
            return 64; // UNRECOVERABLY DENSE
        }
        double distFromOrigin = Math.sqrt(pos.blockX()*pos.blockX() + pos.blockZ()*pos.blockZ());
        Integer islandSize;
        if (ConfigHandler.getConfigValue("island_size") instanceof Integer) {
            islandSize = (Integer) ConfigHandler.getConfigValue("island_size");
        } else {
            islandSize = ((JsonPrimitive)ConfigHandler.getConfigValue("island_size")).getAsInt();
        }
        islandSize = islandSize*100;
        double finalValue = ((1/(double)islandSize)*distFromOrigin*-1)+0.81;
        return Math.min(Math.max(finalValue, -0.8), 0.8);
    }

    @Override
    public void fill(double[] densities, EachApplier applier) {
        applier.fill(densities, this);
    }

    @Override
    public DensityFunction apply(DensityFunctionVisitor visitor) {
        return visitor.apply(this);
    }

    @Override
    public double minValue() {
        return -0.8d;
    }

    @Override
    public double maxValue() {
        return 64d;
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodecHolder() {
        return CODEC_HOLDER;
    }
}