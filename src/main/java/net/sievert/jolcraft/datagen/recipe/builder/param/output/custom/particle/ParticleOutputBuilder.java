package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle;

import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleProducer;
import net.sievert.jolcraft.data.recipe.param.output.custom.particle.ParticleSpec;
import net.sievert.jolcraft.data.recipe.param.quantity.DoubleRange;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ParticleOutputBuilder {

    private @Nullable RecipeLookups lookups;
    private @Nullable ParticleSpec spec;
    private @Nullable IntRange count = IntRange.ONE;
    private @NotNull DoubleRange speed = DoubleRange.ZERO;
    private @NotNull DoubleRange offsetX = DoubleRange.ZERO;
    private @NotNull DoubleRange offsetY = DoubleRange.ZERO;
    private @NotNull DoubleRange offsetZ = DoubleRange.ZERO;
    private @NotNull DoubleRange spreadX = DoubleRange.ZERO;
    private @NotNull DoubleRange spreadY = DoubleRange.ZERO;
    private @NotNull DoubleRange spreadZ = DoubleRange.ZERO;

    private ParticleOutputBuilder() {}

    public static @NotNull ParticleOutputBuilder builder() {
        return new ParticleOutputBuilder();
    }

    public @NotNull ParticleOutputBuilder lookups(@Nullable RecipeLookups lookups) {
        this.lookups = lookups;
        return this;
    }

    public @NotNull ParticleOutputBuilder spec(@Nullable ParticleSpec spec) {
        this.spec = spec;
        return this;
    }

    public @NotNull ParticleOutputBuilder particle(@Nullable ParticleOptions particle) {
        if (particle == null || lookups == null) {
            this.spec = null;
            return this;
        }

        ResourceLocation id = resolveParticleId(lookups.particles(), particle.getType());
        if (id == null) {
            this.spec = null;
            return this;
        }

        this.spec = new ParticleSpec(new ParticleProducer(id), particle);
        return this;
    }

    public @NotNull ParticleOutputBuilder count(@Nullable IntRange count) {
        this.count = count;
        return this;
    }

    public @NotNull ParticleOutputBuilder countFixed(int value) {
        this.count = IntRange.fixed(value);
        return this;
    }

    public @NotNull ParticleOutputBuilder speed(@Nullable DoubleRange speed) {
        this.speed = speed != null ? speed : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder speed(double speed) {
        this.speed = DoubleRange.fixed(speed);
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetX(@Nullable DoubleRange offsetX) {
        this.offsetX = offsetX != null ? offsetX : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetX(double offsetX) {
        this.offsetX = DoubleRange.fixed(offsetX);
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetY(@Nullable DoubleRange offsetY) {
        this.offsetY = offsetY != null ? offsetY : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetY(double offsetY) {
        this.offsetY = DoubleRange.fixed(offsetY);
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetZ(@Nullable DoubleRange offsetZ) {
        this.offsetZ = offsetZ != null ? offsetZ : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder offsetZ(double offsetZ) {
        this.offsetZ = DoubleRange.fixed(offsetZ);
        return this;
    }

    public @NotNull ParticleOutputBuilder offset(double x, double y, double z) {
        this.offsetX = DoubleRange.fixed(x);
        this.offsetY = DoubleRange.fixed(y);
        this.offsetZ = DoubleRange.fixed(z);
        return this;
    }

    public @NotNull ParticleOutputBuilder offset(
            @Nullable DoubleRange x,
            @Nullable DoubleRange y,
            @Nullable DoubleRange z
    ) {
        this.offsetX = x != null ? x : DoubleRange.ZERO;
        this.offsetY = y != null ? y : DoubleRange.ZERO;
        this.offsetZ = z != null ? z : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadX(@Nullable DoubleRange spreadX) {
        this.spreadX = spreadX != null ? spreadX : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadX(double spreadX) {
        this.spreadX = DoubleRange.fixed(spreadX);
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadY(@Nullable DoubleRange spreadY) {
        this.spreadY = spreadY != null ? spreadY : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadY(double spreadY) {
        this.spreadY = DoubleRange.fixed(spreadY);
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadZ(@Nullable DoubleRange spreadZ) {
        this.spreadZ = spreadZ != null ? spreadZ : DoubleRange.ZERO;
        return this;
    }

    public @NotNull ParticleOutputBuilder spreadZ(double spreadZ) {
        this.spreadZ = DoubleRange.fixed(spreadZ);
        return this;
    }

    public @NotNull ParticleOutputBuilder spread(double x, double y, double z) {
        this.spreadX = DoubleRange.fixed(x);
        this.spreadY = DoubleRange.fixed(y);
        this.spreadZ = DoubleRange.fixed(z);
        return this;
    }

    public @NotNull ParticleOutputBuilder spread(
            @Nullable DoubleRange x,
            @Nullable DoubleRange y,
            @Nullable DoubleRange z
    ) {
        this.spreadX = x != null ? x : DoubleRange.ZERO;
        this.spreadY = y != null ? y : DoubleRange.ZERO;
        this.spreadZ = z != null ? z : DoubleRange.ZERO;
        return this;
    }

    public @NotNull DataResult<ParticleOutput> buildValidated() {
        if (spec == null) {
            return DataResult.error(() -> "Missing required field: 'spec'");
        }

        if (count == null) {
            return DataResult.error(() -> "Missing required field: 'count'");
        }

        return new ParticleOutput(
                spec,
                count,
                speed,
                offsetX,
                offsetY,
                offsetZ,
                spreadX,
                spreadY,
                spreadZ
        ).validate();
    }

    public @NotNull ParticleOutput build() {
        return buildValidated().getOrThrow(IllegalStateException::new);
    }

    private static @Nullable ResourceLocation resolveParticleId(
            @NotNull HolderLookup.RegistryLookup<ParticleType<?>> registry,
            @NotNull ParticleType<?> type
    ) {
        return registry.listElements()
                .filter(ref -> ref.value() == type)
                .findFirst()
                .flatMap(ref -> ref.unwrapKey().map(ResourceKey::location))
                .orElse(null);
    }
}