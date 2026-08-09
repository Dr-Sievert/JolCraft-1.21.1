package net.sievert.jolcraft.datagen.advancement;

import com.mojang.serialization.DataResult;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.player.advancement.custom.AdvancementTrigger;
import net.sievert.jolcraft.data.id.advancement.JolCraftAdvancementIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.datagen.base.output.JolCraftFileNameBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class JolCraftAdvancementBuilder implements JolCraftEmissionBuilder<Consumer<AdvancementHolder>> {

    private @Nullable String idPath;
    private @Nullable ResourceLocation parentId;
    private @Nullable ItemLike icon;
    private @Nullable ResourceLocation background;
    private @Nullable AdvancementType type;

    private boolean showToast = true;
    private boolean announce = true;
    private boolean hidden = false;

    private final List<Criterion<?>> criteria = new ArrayList<>();

    private JolCraftAdvancementBuilder() {}

    public static @NotNull JolCraftAdvancementBuilder create() {
        return new JolCraftAdvancementBuilder();
    }

    public @NotNull JolCraftAdvancementBuilder idPath(@Nullable String idPath) {
        this.idPath = normalize(idPath);
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder parent(@Nullable ResourceLocation parentId) {
        this.parentId = parentId;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder root() {
        this.parentId = null;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder icon(@Nullable ItemLike icon) {
        this.icon = icon;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder background(@Nullable ResourceLocation background) {
        this.background = background;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder noBackground() {
        this.background = null;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder type(@Nullable AdvancementType type) {
        this.type = type;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder toast(boolean showToast) {
        this.showToast = showToast;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder announce(boolean announce) {
        this.announce = announce;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder display(
            boolean showToast,
            boolean announce,
            boolean hidden
    ) {
        this.showToast = showToast;
        this.announce = announce;
        this.hidden = hidden;
        return this;
    }

    public @NotNull JolCraftAdvancementBuilder criterion(@Nullable Criterion<?> criterion) {
        if (criterion != null) {
            this.criteria.add(criterion);
        }
        return this;
    }

    public final @NotNull JolCraftAdvancementBuilder criteria(@Nullable Criterion<?>... criteria) {
        if (criteria == null) {
            return this;
        }

        for (Criterion<?> criterion : criteria) {
            criterion(criterion);
        }

        return this;
    }

    public @NotNull JolCraftAdvancementBuilder dummyChild() {
        return icon(Items.CHISELED_DEEPSLATE)
                .noBackground()
                .type(AdvancementType.TASK)
                .display(false, false, true);
    }

    public @NotNull JolCraftAdvancementBuilder dummyChildOfFirstRoot() {
        return dummyChild()
                .parent(JolCraft.location(JolCraftAdvancementIds.ROOT_1))
                .criterion(AdvancementTrigger.has(JolCraft.location(JolCraftAdvancementIds.ROOT_1)));
    }

    public @NotNull JolCraftAdvancementBuilder dummyChild(@Nullable ResourceLocation parentId) {
        return dummyChild()
                .parent(parentId)
                .criterion(parentId == null ? null : AdvancementTrigger.has(parentId));
    }

    @Override
    public @NotNull DataResult<JolCraftDataEmission<Consumer<AdvancementHolder>>> buildValidated() {
        List<String> errors = validateState();

        DataResult<String> fileNameResult = JolCraftFileNameBuilder.validateBaseName(idPath);
        if (fileNameResult.error().isPresent()) {
            errors.add(fileNameResult.error().get().message());
        }

        String partial = fileNameResult.result().orElse(idPath == null ? "" : idPath);
        if (!errors.isEmpty()) {
            return DataResult.error(
                    () -> String.join("; ", errors),
                    new JolCraftDataEmission<>(
                            partial.isBlank() ? JolCraftDictionary.UNKNOWN : partial,
                            (target, path) -> {}
                    )
            );
        }

        String fileName = fileNameResult.result().orElseThrow();
        ResourceLocation savedParentId = this.parentId;
        ItemLike savedIcon = this.icon;
        ResourceLocation savedBackground = this.background;
        AdvancementType savedType = this.type;
        boolean savedShowToast = this.showToast;
        boolean savedAnnounce = this.announce;
        boolean savedHidden = this.hidden;
        List<Criterion<?>> savedCriteria = List.copyOf(this.criteria);

        if (savedIcon == null || savedType == null) {
            throw new IllegalStateException("advancement builder validated without icon/type");
        }

        return DataResult.success(new JolCraftDataEmission<>(
                fileName,
                (target, path) -> saveToTarget(
                        target,
                        path,
                        savedParentId,
                        savedIcon,
                        savedBackground,
                        savedType,
                        savedShowToast,
                        savedAnnounce,
                        savedHidden,
                        savedCriteria
                )
        ));
    }

    @SuppressWarnings("removal")
    private static void saveToTarget(
            @NotNull Consumer<AdvancementHolder> target,
            @NotNull String path,
            @Nullable ResourceLocation parentId,
            @NotNull ItemLike icon,
            @Nullable ResourceLocation background,
            @NotNull AdvancementType type,
            boolean showToast,
            boolean announce,
            boolean hidden,
            @NotNull List<Criterion<?>> criteria
    ) {
        String id = JolCraft.location(path).toString();

        Advancement.Builder builder = Advancement.Builder.advancement();

        if (parentId != null) {
            builder.parent(parentId);
        }

        builder.display(
                icon,
                Component.translatable(titleKey(path)),
                Component.translatable(descriptionKey(path)),
                background,
                type,
                showToast,
                announce,
                hidden
        );

        for (int i = 0; i < criteria.size(); i++) {
            builder.addCriterion(criterionKey(path, i), criteria.get(i));
        }

        builder.save(target, id);
    }

    private @NotNull List<String> validateState() {
        List<String> errors = new ArrayList<>();

        if (idPath == null || idPath.isBlank()) {
            errors.add("idPath is required");
        }

        if (icon == null) {
            errors.add("icon is required");
        }

        if (type == null) {
            errors.add("type is required");
        }

        if (criteria.isEmpty()) {
            errors.add("at least one criterion is required");
        }

        return errors;
    }

    private static @Nullable String normalize(@Nullable String raw) {
        if (raw == null) {
            return null;
        }

        String value = raw.trim();
        return value.isEmpty() ? null : value;
    }

    private static @NotNull String titleKey(@NotNull String path) {
        return JolCraftStrings.dotted(
                JolCraftDictionary.ADVANCEMENT,
                JolCraft.MOD_ID,
                path.replace('/', '.'),
                JolCraftDictionary.TITLE
        );
    }

    private static @NotNull String descriptionKey(@NotNull String path) {
        return JolCraftStrings.dotted(
                JolCraftDictionary.ADVANCEMENT,
                JolCraft.MOD_ID,
                path.replace('/', '.'),
                JolCraftDictionary.DESCRIPTION
        );
    }

    private static @NotNull String criterionKey(@NotNull String path, int index) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.CRITERION,
                path.replace('/', '_'),
                String.valueOf(index)
        );
    }
}