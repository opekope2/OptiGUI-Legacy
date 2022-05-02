package opekope2.optigui.optifinecompat;

import static opekope2.optigui.util.OptifineParser.parseList;
import static opekope2.optigui.util.Util.contains;
import static opekope2.optigui.util.Util.getBoolean;

import java.io.IOException;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import opekope2.optigui.OptiGUIClient;
import opekope2.optigui.VillagerMatcher;
import opekope2.optigui.optifinecompat.OptifineResourceLoader.ReloadContext;
import opekope2.optigui.util.*;

// https://optifine.readthedocs.io/custom_guis.html
// https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/custom_guis.properties
public class OptifineProperties {
    private static final EnumProperty<ChestType> CHEST_TYPE_ENUM = EnumProperty.of("type", ChestType.class);

    private static final Map<String, Identifier> textureAutoMapping = new HashMap<>();
    private static final Map<String, Identifier[]> idAutoMapping = new HashMap<>();
    private static final Map<String, String> carpetColorMapping = new HashMap<>();

    private final Map<String, ContainerRemapper> idRemapper = new HashMap<>();
    private final Map<Identifier, BlockMatcher> blockMatchers = new HashMap<>();
    private final Map<Identifier, EntityMatcher> entityMatchers = new HashMap<>();

    static {
        textureAutoMapping.put("anvil", TextureResourcePath.ANVIL);
        textureAutoMapping.put("beacon", TextureResourcePath.BEACON);
        textureAutoMapping.put("brewing_stand", TextureResourcePath.BREWING_STAND);
        textureAutoMapping.put("chest", TextureResourcePath.CHEST);
        textureAutoMapping.put("crafting", TextureResourcePath.CRAFTING_TABLE);
        textureAutoMapping.put("dispenser", TextureResourcePath.DISPENSER);
        textureAutoMapping.put("enchantment", TextureResourcePath.ENCHANTING_TABLE);
        textureAutoMapping.put("furnace", TextureResourcePath.FURNACE);
        textureAutoMapping.put("hopper", TextureResourcePath.HOPPER);
        textureAutoMapping.put("shulker_box", TextureResourcePath.SHULKER_BOX);

        textureAutoMapping.put("horse", TextureResourcePath.HORSE);
        textureAutoMapping.put("villager", TextureResourcePath.VILLAGER);

        idAutoMapping.put("anvil", new Identifier[] { ID.ANVIL, ID.CHIPPED_ANVIL, ID.DAMAGED_ANVIL });
        idAutoMapping.put("beacon", new Identifier[] { ID.BEACON });
        idAutoMapping.put("brewing_stand", new Identifier[] { ID.BREWING_STAND });
        idAutoMapping.put("crafting", new Identifier[] { ID.CRAFTING_TABLE });
        idAutoMapping.put("enchantment", new Identifier[] { ID.ENCHANTING_TABLE });
        idAutoMapping.put("furnace", new Identifier[] { ID.FURNACE }); // blast, smoker?
        idAutoMapping.put("hopper", new Identifier[] { ID.HOPPER });

        carpetColorMapping.put("minecraft:white_carpet", "white");
        carpetColorMapping.put("minecraft:orange_carpet", "orange");
        carpetColorMapping.put("minecraft:magenta_carpet", "magenta");
        carpetColorMapping.put("minecraft:light_blue_carpet", "light_blue");
        carpetColorMapping.put("minecraft:yellow_carpet", "yellow");
        carpetColorMapping.put("minecraft:lime_carpet", "lime");
        carpetColorMapping.put("minecraft:pink_carpet", "pink");
        carpetColorMapping.put("minecraft:gray_carpet", "gray");
        carpetColorMapping.put("minecraft:light_gray_carpet", "light_gray");
        carpetColorMapping.put("minecraft:cyan_carpet", "cyan");
        carpetColorMapping.put("minecraft:purple_carpet", "purple");
        carpetColorMapping.put("minecraft:blue_carpet", "blue");
        carpetColorMapping.put("minecraft:brown_carpet", "brown");
        carpetColorMapping.put("minecraft:green_carpet", "green");
        carpetColorMapping.put("minecraft:red_carpet", "red");
        carpetColorMapping.put("minecraft:black_carpet", "black");
    }

    {
        idRemapper.put("chest", this::remapChest);
        idRemapper.put("dispenser", this::remapDispenser);
        idRemapper.put("shulker_box", this::remapShulkerBlock);
        idRemapper.put("horse", this::remapHorse);
        idRemapper.put("villager", this::remapVillager);

        blockMatchers.put(ID.CHEST, this::matchesChest);
        blockMatchers.put(ID.TRAPPED_CHEST, this::matchesChest);
        blockMatchers.put(ID.ENDER_CHEST, this::matchesChest);
        blockMatchers.put(ID.BEACON, this::matchesBeacon);

        entityMatchers.put(ID.LLAMA, this::matchesLlama);
        entityMatchers.put(ID.VILLAGER, this::matchesVillager);
    }

    private Map<Identifier, Identifier> textureRemaps = new HashMap<>();

    private boolean isEntity = false;

    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;

    private String name = null;
    private List<Identifier> biomes = null;
    private List<IntRange> heights = null;
    private List<IntRange> levels = null;
    private List<VillagerMatcher> professions = null;
    private List<String> colors = null;

    private Identifier[] ids = new Identifier[0];

    private OptifineProperties(ReloadContext context) {
        loadFromReloadContext(context);
    }

    private void loadFromReloadContext(ReloadContext ctx) {
        String container = ctx.getProperties().getProperty("container", null);
        if (container == null) {
            return;
        }

        Identifier[] ids = idAutoMapping.getOrDefault(container, null);
        if (ids != null) {
            this.ids = ids;
        }

        ContainerRemapper remapper = idRemapper.getOrDefault(container, null);
        if (remapper != null) {
            remapper.remapContainer(ctx.getProperties());
        }

        loadProperties(ctx.getProperties());
        loadTextureRemaps(ctx);
    }

    public boolean matches(Block block, BlockEntity entity, BlockState state) {
        boolean matchesBiome = true;
        if (biomes != null && entity != null) {
            World world = entity.getWorld();
            Identifier biome = world.getRegistryManager().get(Registry.BIOME_KEY)
                    .getId(world.getBiome(entity.getPos()).value());
            matchesBiome = biomes.contains(biome);
        }

        boolean matchesHeight = true;
        if (heights != null && entity != null) {
            matchesBiome = false;
            for (IntRange height : heights) {
                if (height.test(entity.getPos().getY())) {
                    matchesHeight = true;
                    break;
                }
            }
        }

        Identifier blockId = Registry.BLOCK.getId(block);
        boolean matchesBlock = true;
        BlockMatcher matcher = blockMatchers.getOrDefault(blockId, null);
        if (matcher != null) {
            matchesBlock = matcher.matchesBlock(block, entity, state);
        }

        return matchesBlock && matchesHeight && matchesBiome && !isEntity && contains(ids, blockId);
    }

    public boolean matches(Entity entity) {
        boolean matchesBiome = true;
        if (biomes != null) {
            World world = entity.getWorld();
            Identifier biome = world.getRegistryManager().get(Registry.BIOME_KEY)
                    .getId(world.getBiome(entity.getBlockPos()).value());
            matchesBiome = biomes.contains(biome);
        }

        boolean matchesHeight = true;
        if (heights != null) {
            matchesBiome = false;
            for (IntRange height : heights) {
                if (height.test(entity.getBlockPos().getY())) {
                    matchesHeight = true;
                    break;
                }
            }
        }

        Identifier entityId = Registry.ENTITY_TYPE.getId(entity.getType());
        boolean matchesEntity = true;

        EntityMatcher matcher = entityMatchers.getOrDefault(entityId, null);
        if (matcher != null) {
            matchesEntity = matcher.matchesEntity(entity);
        }

        return matchesEntity && matchesHeight && matchesBiome && isEntity && contains(ids, entityId);
    }

    public boolean hasReplacement(Identifier original) {
        return textureRemaps.containsKey(original);
    }

    public Identifier getReplacementTexture(Identifier original) {
        return textureRemaps.getOrDefault(original, original);
    }

    private void loadProperties(Properties props) {
        String biomes = props.getProperty("biomes", null);
        if (biomes != null) {
            this.biomes = OptifineParser.parseIdentifierList(biomes);
        }

        String heights = props.getProperty("heights", null);
        if (heights != null) {
            this.heights = OptifineParser.parseRangeList(heights);
        }

        String levels = props.getProperty("levels", null);
        if (levels != null) {
            this.levels = OptifineParser.parseRangeList(levels);
        }

        String professions = props.getProperty("professions", null);
        if (professions != null) {
            this.professions = OptifineParser.parseProfessionList(professions);
        }

        String colors = props.getProperty("colors", null);
        if (colors != null) {
            this.colors = OptifineParser.parseList(colors);
        }
    }

    private void loadTextureRemaps(ReloadContext ctx) {
        String container = ctx.getProperties().getProperty("container", null);
        String texture = ctx.getProperties().getProperty("texture", null);
        if (texture == null) {
            return;
        }
        texture = texture.trim();

        String resFolder = ctx.getResourceId().toString();
        resFolder = resFolder.substring(resFolder.indexOf(":") + 1, resFolder.lastIndexOf("/"));

        Identifier id = PathResolver.resolve(resFolder, texture);
        Identifier foundId = ctx.findResource(id);
        if (foundId == null) {
            OptiGUIClient.LOGGER.warn("Resource '{}' is missing!", id.toString());
        } else {
            textureRemaps.put(textureAutoMapping.get(container), foundId);
        }
    }

    // region Block remaps
    private void remapChest(Properties packProps) {
        large = getBoolean(packProps.getProperty("large", null));
        trapped = getBoolean(packProps.getProperty("trapped", null));
        christmas = getBoolean(packProps.getProperty("christmas", null));
        ender = getBoolean(packProps.getProperty("ender", null));

        if (ender != null && ender) {
            ids = new Identifier[] { ID.ENDER_CHEST };
        } else if (trapped != null && trapped) {
            ids = new Identifier[] { ID.TRAPPED_CHEST };
        } else {
            ids = new Identifier[] { ID.CHEST };
        }
    }

    private void remapDispenser(Properties properties) {
        String variants = properties.getProperty("variants", null);
        if (variants == null) {
            ids = new Identifier[] { ID.DISPENSER, ID.DROPPER };
            return;
        }
        ids = switch (variants) {
            case "", "dispenser" -> new Identifier[] { ID.DISPENSER };
            case "dropper" -> new Identifier[] { ID.DROPPER };
            default -> ids;
        };
    }

    private void remapShulkerBlock(Properties properties) {
        String colors = properties.getProperty("colors", null);
        if (colors == null) {
            this.ids = new Identifier[] {
                    ID.WHITE_SHULKER_BOX,
                    ID.ORANGE_SHULKER_BOX,
                    ID.MAGENTA_SHULKER_BOX,
                    ID.LIGHT_BLUE_SHULKER_BOX,
                    ID.YELLOW_SHULKER_BOX,
                    ID.LIME_SHULKER_BOX,
                    ID.PINK_SHULKER_BOX,
                    ID.GRAY_SHULKER_BOX,
                    ID.LIGHT_GRAY_SHULKER_BOX,
                    ID.CYAN_SHULKER_BOX,
                    ID.PURPLE_SHULKER_BOX,
                    ID.BLUE_SHULKER_BOX,
                    ID.BROWN_SHULKER_BOX,
                    ID.GREEN_SHULKER_BOX,
                    ID.RED_SHULKER_BOX,
                    ID.BLACK_SHULKER_BOX
            };
            return;
        }

        List<String> colorList = parseList(colors);
        Identifier[] ids = new Identifier[colorList.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = new Identifier("minecraft", colorList.get(i) + "_shulker_box");
        }
        this.ids = ids;
    }
    // endregion

    // region Entity remaps
    private void remapHorse(Properties properties) {
        isEntity = true;
        String variants = properties.getProperty("variants", null);
        if (variants == null) {
            ids = new Identifier[] { ID.HORSE, ID.DONKEY, ID.MULE, ID.LLAMA };
            return;
        }
        ids = switch (variants) {
            case "horse" -> new Identifier[] { ID.HORSE };
            case "donkey" -> new Identifier[] { ID.DONKEY };
            case "mule" -> new Identifier[] { ID.MULE };
            case "llama" -> new Identifier[] { ID.LLAMA };
            default -> ids;
        };
    }

    private void remapVillager(Properties properties) {
        isEntity = true;
        ids = new Identifier[] { ID.VILLAGER };
    }
    // endregion

    // region Block matchers
    private boolean matchesChest(Block block, BlockEntity entity, BlockState state) {
        Identifier id = Registry.BLOCK.getId(block);
        Comparable<?> type = state.getEntries().get(CHEST_TYPE_ENUM);

        boolean matchesLarge = large == null || (type == null || large != type.equals(ChestType.SINGLE));

        if (ID.CHEST.equals(id)) {
            return matchesLarge && !Boolean.TRUE.equals(trapped) && !Boolean.TRUE.equals(ender);
        } else if (ID.TRAPPED_CHEST.equals(id)) {
            return matchesLarge && !Boolean.FALSE.equals(trapped) && !Boolean.TRUE.equals(ender);
        } else if (ID.ENDER_CHEST.equals(id)) {
            return matchesLarge && !Boolean.TRUE.equals(trapped) && !Boolean.FALSE.equals(ender);
        }
        return false;
    }

    private boolean matchesBeacon(Block block, BlockEntity entity, BlockState state) {
        if (levels == null) {
            return true;
        }
        int beaconLevel = entity.createNbt().getInt("Levels");
        for (IntRange level : levels) {
            if (level.test(beaconLevel)) {
                return true;
            }
        }
        return false;
    }
    // endregion

    // region Entity remaps
    private boolean matchesLlama(Entity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        NbtElement nbtDecor = nbt.get("DecorItem");
        if (nbtDecor != null && nbtDecor instanceof NbtCompound compound) {
            String carpet = compound.getString("id");
            if (carpet == null) {
                return colors.isEmpty();
            }
            carpet = carpetColorMapping.getOrDefault(carpet, null);
            if (carpet != null) {
                return colors.contains(carpet);
            }
        }
        return false;
    }

    private boolean matchesVillager(Entity entity) {
        VillagerEntity villager = (VillagerEntity) entity;
        if (professions == null) {
            return true;
        }
        for (VillagerMatcher matcher : professions) {
            if (matcher.matchesVillager(villager)) {
                return true;
            }
        }
        return false;
    }
    // endregion

    public static OptifineProperties parse(ReloadContext context) throws IOException {
        Properties properties = new Properties();
        properties.load(context.getResource().getInputStream());
        context.setProperties(properties);
        return new OptifineProperties(context);
    }

    private static interface ContainerRemapper {
        void remapContainer(Properties packProps);
    }

    private static interface BlockMatcher {
        boolean matchesBlock(Block block, BlockEntity entity, BlockState state);
    }

    private static interface EntityMatcher {
        boolean matchesEntity(Entity entity);
    }
}
