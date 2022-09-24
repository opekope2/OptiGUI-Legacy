package opekope2.optigui.optifinecompat;

import static opekope2.optigui.util.OptiFineParser.*;
import static opekope2.optigui.util.Util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FilenameUtils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.resource.Resource;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import opekope2.optigui.OptiGUIClient;
import opekope2.optigui.interfaces.RegexMatcher;
import opekope2.optigui.interfaces.TextureRemapper;
import opekope2.optigui.optifinecompat.OptiFineResourceLoader.ResourceLoadContext;
import opekope2.optigui.util.*;

// https://optifine.readthedocs.io/custom_guis.html
// https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/custom_guis.properties
public final class OptiFineProperties {
    private static final EnumProperty<ChestType> CHEST_TYPE_ENUM = EnumProperty.of("type", ChestType.class);

    private final Map<String, ContainerRemapper> remappers = new HashMap<>();
    private final Map<Identifier, InteractionMatcher> blockMatchers = new HashMap<>();
    private final Map<Identifier, InteractionMatcher> entityMatchers = new HashMap<>();

    private static final String texturePathPrefix = "texture.";

    // region Initializers
    {
        remappers.put("chest", this::remapChest);
        remappers.put("dispenser", this::remapDispenser);
        remappers.put("furnace", this::remapFurnace);
        remappers.put("shulker_box", this::remapShulkerBox);
        remappers.put("horse", this::remapHorse);
        remappers.put("villager", this::remapVillager);

        blockMatchers.put(ID.BARREL, this::matchesChest);
        blockMatchers.put(ID.BEACON, this::matchesBeacon);
        blockMatchers.put(ID.BLAST_FURNACE, this::matchesFurnace);
        blockMatchers.put(ID.CHEST, this::matchesChest);
        blockMatchers.put(ID.ENDER_CHEST, this::matchesChest);
        blockMatchers.put(ID.FURNACE, this::matchesFurnace);
        blockMatchers.put(ID.SMOKER, this::matchesFurnace);
        blockMatchers.put(ID.TRAPPED_CHEST, this::matchesChest);

        entityMatchers.put(ID.LLAMA, this::matchesLlama);
        entityMatchers.put(ID.VILLAGER, this::matchesVillager);
        entityMatchers.put(ID.WANDERING_TRADER, this::matchesVillager);
    }
    // endregion

    private Map<Identifier, Identifier> textureRemaps = new HashMap<>();

    private boolean isEntity = false;

    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;

    // region OptiFine extensions
    private Boolean _barrel = null;
    // endregion

    private RegexMatcher nameMatcher = null;
    private Set<Identifier> biomes = null;
    private Iterable<IntRange> heights = null;
    private Iterable<IntRange> levels = null;
    private Iterable<VillagerMatcher> professions = null;
    private Set<String> colors = null;

    private Set<Identifier> ids = null;

    // region Construction
    private OptiFineProperties(ResourceLoadContext context) {
        String container = context.getProperties().getProperty("container", null);
        if (container == null) {
            return;
        }

        Set<Identifier> ids = ID_AUTO_MAPPING.getOrDefault(container, null);
        if (ids != null) {
            this.ids = ids;
        }

        ContainerRemapper remapper = remappers.getOrDefault(container, null);
        if (remapper != null) {
            remapper.remapContainer(context.getProperties());
        }

        loadProperties(context.getProperties());
        loadTextureRemaps(context);
    }

    private void loadProperties(Properties props) {
        String name = props.getProperty("name", null);
        if (name != null) {
            this.nameMatcher = parseRegex(name);
        }

        String biomes = props.getProperty("biomes", null);
        if (biomes != null) {
            this.biomes = new HashSet<>(parseIdentifierList(biomes));
        }

        String heights = props.getProperty("heights", null);
        if (heights != null) {
            this.heights = parseRangeList(heights);
        }

        String levels = props.getProperty("levels", null);
        if (levels != null) {
            this.levels = parseRangeList(levels);
        }

        String professions = props.getProperty("professions", null);
        if (professions != null) {
            this.professions = parseProfessionList(professions);
        }

        String colors = props.getProperty("colors", null);
        if (colors != null) {
            this.colors = new HashSet<>(parseList(colors));
        }
    }

    private void loadTextureRemaps(ResourceLoadContext ctx) {
        String texture = ctx.getProperties().getProperty("texture", null);
        String resFolder = new File(ctx.getResourceId().getPath()).getParent();

        if (texture != null) {
            Identifier id = PathResolver.resolve(resFolder, texture);
            Identifier foundId = ctx.findResource(id);
            if (foundId == null) {
                OptiGUIClient.logger.warn(
                        "Texture '{}' is missing!\nIn resource pack '{}', resource '{}'.",
                        id.toString(), ctx.getResourcePackName(), ctx.getResourceId());
            } else {
                for (Identifier textureToRemap : getTextureToRemap(ctx.getProperties())) {
                    textureRemaps.put(textureToRemap, foundId);
                }
            }
        }

        for (var property : ctx.getProperties().entrySet()) {
            String key = (String) property.getKey();

            if (key.startsWith(texturePathPrefix)) {
                String value = (String) property.getValue();
                Identifier id = PathResolver.resolve(resFolder, value);
                Identifier foundId = ctx.findResource(id);
                if (foundId == null) {
                    OptiGUIClient.logger.warn(
                            "Resource '{}' is missing!\nIn resource pack '{}', resource '{}'.",
                            id.toString(), ctx.getResourcePackName(), ctx.getResourceId());
                } else {
                    String texturePath = key.substring(texturePathPrefix.length());
                    textureRemaps.put(PathResolver.resolve("textures/gui", texturePath), foundId);
                }
            }
        }
    }

    private Set<Identifier> getTextureToRemap(Properties properties) {
        String container = properties.getProperty("container", null);
        Identifier containerTexture = TEXTURE_AUTO_MAPPING.get(container);
        if (containerTexture != null) {
            return setOf(containerTexture);
        }

        TextureRemapper remapper = TEXTURE_REMAPPERS.get(container);
        return remapper != null ? remapper.remap(properties) : setOf();
    }
    // endregion

    // region Replacing
    public boolean matchesBlock(InteractionInfo interaction) {
        if (isEntity) {
            return false;
        }

        if (biomes != null && !biomes.contains(interaction.getBiome())) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = false;
            int y = interaction.getBlockPos().getY();
            for (IntRange height : heights) {
                if (height.test(y)) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        if (nameMatcher != null
                && !nameMatcher.matches(interaction.hasCustomName() ? interaction.getCustomName() : "")) {
            return false;
        }

        if (!ids.contains(interaction.getId())) {
            return false;
        }

        InteractionMatcher matcher = blockMatchers.getOrDefault(interaction.getId(), null);
        if (matcher != null && !matcher.matchesInteraction(interaction)) {
            return false;
        }

        return true;
    }

    public boolean matchesEntity(InteractionInfo interaction) {
        if (!isEntity) {
            return false;
        }

        if (biomes != null && !biomes.contains(interaction.getBiome())) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = false;
            int y = interaction.getBlockPos().getY();
            for (IntRange height : heights) {
                if (height.test(y)) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        if (nameMatcher != null
                && !nameMatcher.matches(interaction.hasCustomName() ? interaction.getCustomName() : "")) {
            return false;
        }

        if (!ids.contains(interaction.getId())) {
            return false;
        }

        InteractionMatcher matcher = entityMatchers.getOrDefault(interaction.getId(), null);
        if (matcher != null && !matcher.matchesInteraction(interaction)) {
            return false;
        }

        return true;
    }

    public boolean matchesAnythingElse(InteractionInfo interaction) {
        if (ids != null) {
            return false;
        }

        if (biomes != null && !biomes.contains(interaction.getBiome())) {
            return false;
        }

        if (heights != null) {
            boolean matchesHeight = true;
            int y = interaction.getBlockPos().getY();
            for (IntRange height : heights) {
                if (height.test(y)) {
                    matchesHeight = true;
                    break;
                }
            }
            if (!matchesHeight) {
                return false;
            }
        }

        return true;
    }

    public boolean canReplaceTexture(Identifier original) {
        if (textureRemaps.containsKey(original)) {
            return true;
        }
        String namespace = original.getNamespace(), path = original.getPath();
        path = FilenameUtils.removeExtension(path);
        return textureRemaps.containsKey(new Identifier(namespace, path));
    }

    public Identifier replaceTexture(Identifier original) {
        if (textureRemaps.containsKey(original)) {
            return textureRemaps.getOrDefault(original, original);
        }
        String namespace = original.getNamespace(), path = original.getPath();
        path = FilenameUtils.removeExtension(path);
        return textureRemaps.getOrDefault(new Identifier(namespace, path), original);
    }
    // endregion

    // region Block remaps
    private void remapChest(Properties packProps) {
        large = getBoolean(packProps.getProperty("large", null));
        trapped = getBoolean(packProps.getProperty("trapped", null));
        christmas = getBoolean(packProps.getProperty("christmas", null));
        ender = getBoolean(packProps.getProperty("ender", null));
        _barrel = getBoolean(packProps.getProperty("_barrel", null));

        Set<Identifier> variants = new HashSet<>();
        variants.add(ID.CHEST);
        if (ender != null && ender) {
            variants.add(ID.ENDER_CHEST);
        }
        if (trapped != null && trapped) {
            variants.add(ID.TRAPPED_CHEST);
        }
        if (_barrel != null && _barrel) {
            variants.add(ID.BARREL);
        }
        ids = variants;
    }

    private void remapDispenser(Properties properties) {
        String variants = properties.getProperty("variants", null);

        if (variants == null) {
            this.ids = setOf(ID.DISPENSER, ID.DROPPER);
            return;
        }

        Set<Identifier> ids = new HashSet<>();

        for (String variant : parseList(variants)) {
            switch (variant) {
                case "", "dispenser" -> ids.add(ID.DISPENSER);
                case "dropper" -> ids.add(ID.DROPPER);
            }
        }

        this.ids = ids;
    }

    private void remapFurnace(Properties properties) {
        String variants = properties.getProperty("variants", null);

        if (variants == null) {
            this.ids = setOf(ID.FURNACE, ID.BLAST_FURNACE, ID.SMOKER);
            return;
        }

        Set<Identifier> ids = new HashSet<>();

        for (String variant : parseList(variants)) {
            switch (variant) {
                case "", "_furnace" -> ids.add(ID.FURNACE);
                case "_blast", "_blast_furnace" -> ids.add(ID.BLAST_FURNACE);
                case "_smoker" -> ids.add(ID.SMOKER);
            }
        }

        this.ids = ids;
    }

    private void remapShulkerBox(Properties properties) {
        String colors = properties.getProperty("colors", null);
        Set<Identifier> ids = new HashSet<>();

        if (colors == null) {
            for (Identifier shulker : COLOR_TO_SHULKER_MAPPING.values()) {
                ids.add(shulker);
            }
        } else {
            Iterable<String> colorList = parseList(colors);
            for (String color : colorList) {
                Identifier shulker = COLOR_TO_SHULKER_MAPPING.getOrDefault(color, null);
                if (shulker != null) {
                    ids.add(shulker);
                }
            }
        }

        this.ids = ids;
    }
    // endregion

    // region Entity remaps
    private void remapHorse(Properties properties) {
        isEntity = true;
        String variants = properties.getProperty("variants", null);
        if (variants == null) {
            ids = setOf(ID.HORSE, ID.DONKEY, ID.MULE, ID.LLAMA);
            return;
        }
        ids = switch (variants) {
            case "horse" -> setOf(ID.HORSE);
            case "donkey" -> setOf(ID.DONKEY);
            case "mule" -> setOf(ID.MULE);
            case "llama" -> setOf(ID.LLAMA);
            default -> ids;
        };
    }

    private void remapVillager(Properties properties) {
        isEntity = true;
        ids = setOf(ID.VILLAGER, ID.WANDERING_TRADER);
    }
    // endregion

    // region Block matchers
    private boolean matchesChest(InteractionInfo interaction) {
        Identifier id = interaction.getId();
        Comparable<?> type = interaction.getBlockState().getEntries().get(CHEST_TYPE_ENUM);

        boolean matchesLarge = large == null || (type == null || large != type.equals(ChestType.SINGLE));
        boolean matchesChristmas = christmas == null ? true : christmas == isChristmas();

        if (ID.CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.TRAPPED_CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.FALSE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.ENDER_CHEST.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.FALSE.equals(ender)
                    && !Boolean.TRUE.equals(_barrel);
        } else if (ID.BARREL.equals(id)) {
            return matchesLarge && matchesChristmas && !Boolean.TRUE.equals(trapped) && !Boolean.TRUE.equals(ender)
                    && !Boolean.FALSE.equals(_barrel);
        }
        return false;
    }

    private boolean matchesBeacon(InteractionInfo interaction) {
        if (levels == null) {
            return true;
        }

        BlockEntity entity = interaction.getBlockEntity();
        int beaconLevel = entity.createNbt().getInt("Levels");

        for (IntRange level : levels) {
            if (level.test(beaconLevel)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesFurnace(InteractionInfo interaction) {
        Identifier id = interaction.getId();
        return ID.BLAST_FURNACE.equals(id) || ID.FURNACE.equals(id) || ID.SMOKER.equals(id);
    }
    // endregion

    // region Entity matchers
    private boolean matchesLlama(InteractionInfo interaction) {
        LlamaEntity llama = (LlamaEntity) interaction.getEntity();
        DyeColor color = llama.getCarpetColor();

        if (color == null) {
            return colors == null;
        } else {
            return colors != null ? colors.contains(color.getName()) : true;
        }
    }

    private boolean matchesVillager(InteractionInfo interaction) {
        if (professions == null) {
            return true;
        }

        Entity entity = interaction.getEntity();
        if (entity instanceof VillagerEntity villager) {
            for (VillagerMatcher matcher : professions) {
                if (matcher.matchesVillager(villager)) {
                    return true;
                }
            }
        } else if (entity instanceof WanderingTraderEntity trader) {
            for (VillagerMatcher matcher : professions) {
                if (matcher.matchesWanderingTrader(trader)) {
                    return true;
                }
            }
        }
        return false;
    }
    // endregion

    public static OptiFineProperties parse(ResourceLoadContext context) throws IOException {
        Properties properties = new Properties();
        Optional<Resource> resource = context.getResource();
        if (resource.isPresent()) {
            properties.load(resource.get().getInputStream());
        }
        context.setProperties(properties);
        return new OptiFineProperties(context);
    }

    private static interface ContainerRemapper {
        public void remapContainer(Properties packProps);
    }

    private static interface InteractionMatcher {
        public boolean matchesInteraction(InteractionInfo interaction);
    }
}
