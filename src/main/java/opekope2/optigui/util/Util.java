package opekope2.optigui.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public final class Util {
    public static <T> List<T> listOf() {
        return new ArrayList<>();
    }

    public static Boolean getBoolean(String s) {
        if (s == null) {
            return null;
        }
        return switch (s.toLowerCase()) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };
    }

    public static <T> boolean contains(T[] array, T value) {
        if (array == null) {
            return false;
        }
        for (T t : array) {
            if (value == null) {
                if (t == null) {
                    return true;
                }
            } else if (value.equals(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChristmas() {
        LocalDateTime date = LocalDateTime.now();
        int day = date.getDayOfMonth();
        return date.getMonth() == Month.DECEMBER && (day >= 24 || day <= 26);
    }

    public static Identifier getBiomeId(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world.getRegistryManager().get(Registry.BIOME_KEY)
                .getId(mc.world.getBiome(pos).value());
    }

    private static Identifier remapFurnaceTexture(Properties properties) {
        String variants = properties.getProperty("variants", null);

        return variants == null
                ? BuiltinTexturePath.FURNACE
                : switch (variants) {
                    case "_blast", "_blast_furnace" -> BuiltinTexturePath.BLAST_FURNACE;
                    case "_smoker" -> BuiltinTexturePath.SMOKER;
                    default -> BuiltinTexturePath.FURNACE;
                };
    }

    // container -> (properties -> texture path)
    public static final Map<String, Function<Properties, Identifier>> TEXTURE_REMAPPERS = new HashMap<>();
    // container -> block id
    public static final Map<String, Identifier[]> ID_AUTO_MAPPING = new HashMap<>();
    // carpet block id -> color
    public static final Map<String, String> CARPET_TO_COLOR_MAPPING = new HashMap<>();
    // color -> shulker box block id
    public static final Map<String, Identifier> COLOR_TO_SHULKER_MAPPING = new HashMap<>();

    static {
        TEXTURE_REMAPPERS.put("anvil", p -> BuiltinTexturePath.ANVIL);
        TEXTURE_REMAPPERS.put("beacon", p -> BuiltinTexturePath.BEACON);
        TEXTURE_REMAPPERS.put("brewing_stand", p -> BuiltinTexturePath.BREWING_STAND);
        TEXTURE_REMAPPERS.put("chest", p -> BuiltinTexturePath.CHEST);
        TEXTURE_REMAPPERS.put("crafting", p -> BuiltinTexturePath.CRAFTING_TABLE);
        TEXTURE_REMAPPERS.put("dispenser", p -> BuiltinTexturePath.DISPENSER);
        TEXTURE_REMAPPERS.put("enchantment", p -> BuiltinTexturePath.ENCHANTING_TABLE);
        TEXTURE_REMAPPERS.put("furnace", Util::remapFurnaceTexture);
        TEXTURE_REMAPPERS.put("hopper", p -> BuiltinTexturePath.HOPPER);
        TEXTURE_REMAPPERS.put("shulker_box", p -> BuiltinTexturePath.SHULKER_BOX);

        TEXTURE_REMAPPERS.put("horse", p -> BuiltinTexturePath.HORSE);
        TEXTURE_REMAPPERS.put("villager", p -> BuiltinTexturePath.VILLAGER);

        ID_AUTO_MAPPING.put("anvil", new Identifier[] { ID.ANVIL, ID.CHIPPED_ANVIL, ID.DAMAGED_ANVIL });
        ID_AUTO_MAPPING.put("beacon", new Identifier[] { ID.BEACON });
        ID_AUTO_MAPPING.put("brewing_stand", new Identifier[] { ID.BREWING_STAND });
        ID_AUTO_MAPPING.put("crafting", new Identifier[] { ID.CRAFTING_TABLE });
        ID_AUTO_MAPPING.put("enchantment", new Identifier[] { ID.ENCHANTING_TABLE });
        ID_AUTO_MAPPING.put("hopper", new Identifier[] { ID.HOPPER });

        CARPET_TO_COLOR_MAPPING.put("minecraft:white_carpet", "white");
        CARPET_TO_COLOR_MAPPING.put("minecraft:orange_carpet", "orange");
        CARPET_TO_COLOR_MAPPING.put("minecraft:magenta_carpet", "magenta");
        CARPET_TO_COLOR_MAPPING.put("minecraft:light_blue_carpet", "light_blue");
        CARPET_TO_COLOR_MAPPING.put("minecraft:yellow_carpet", "yellow");
        CARPET_TO_COLOR_MAPPING.put("minecraft:lime_carpet", "lime");
        CARPET_TO_COLOR_MAPPING.put("minecraft:pink_carpet", "pink");
        CARPET_TO_COLOR_MAPPING.put("minecraft:gray_carpet", "gray");
        CARPET_TO_COLOR_MAPPING.put("minecraft:light_gray_carpet", "light_gray");
        CARPET_TO_COLOR_MAPPING.put("minecraft:cyan_carpet", "cyan");
        CARPET_TO_COLOR_MAPPING.put("minecraft:purple_carpet", "purple");
        CARPET_TO_COLOR_MAPPING.put("minecraft:blue_carpet", "blue");
        CARPET_TO_COLOR_MAPPING.put("minecraft:brown_carpet", "brown");
        CARPET_TO_COLOR_MAPPING.put("minecraft:green_carpet", "green");
        CARPET_TO_COLOR_MAPPING.put("minecraft:red_carpet", "red");
        CARPET_TO_COLOR_MAPPING.put("minecraft:black_carpet", "black");

        COLOR_TO_SHULKER_MAPPING.put("white", ID.WHITE_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("orange", ID.ORANGE_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("magenta", ID.MAGENTA_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("light_blue", ID.LIGHT_BLUE_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("yellow", ID.YELLOW_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("lime", ID.LIME_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("pink", ID.PINK_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("gray", ID.GRAY_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("light_gray", ID.LIGHT_GRAY_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("cyan", ID.CYAN_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("purple", ID.PURPLE_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("blue", ID.BLUE_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("brown", ID.BROWN_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("green", ID.GREEN_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("red", ID.RED_SHULKER_BOX);
        COLOR_TO_SHULKER_MAPPING.put("black", ID.BLACK_SHULKER_BOX);
    }

    private Util() {
    }
}
