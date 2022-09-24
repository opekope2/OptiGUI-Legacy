package opekope2.optigui.util;

import static opekope2.optigui.util.OptiFineParser.parseList;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import opekope2.optigui.interfaces.Setter;
import opekope2.optigui.interfaces.TextureRemapper;

public final class Util {
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

    @SafeVarargs
    public static <T> Set<T> setOf(T... args) {
        Set<T> result = new HashSet<T>();

        for (T arg : args) {
            result.add(arg);
        }

        return result;
    }

    public static <T> boolean setAndCheckIfUpdated(Setter<T> setter, T oldValue, T newValue) {
        boolean updated = oldValue == null ? newValue != null : !oldValue.equals(newValue);

        if (updated) {
            setter.set(newValue);
        }

        return updated;
    }

    public static boolean isChristmas() {
        LocalDateTime date = LocalDateTime.now();
        int day = date.getDayOfMonth();
        return date.getMonth() == Month.DECEMBER && (day >= 24 || day <= 26);
    }

    public static Identifier getBiomeId(MinecraftClient mc, BlockPos pos) {
        return mc.world.getRegistryManager().get(Registry.BIOME_KEY)
                .getId(mc.world.getBiome(pos).value());
    }

    private static Set<Identifier> remapFurnaceTexture(Properties properties) {
        String variants = properties.getProperty("variants", null);

        if (variants == null) {
            return setOf(BuiltinTexturePath.FURNACE);
        }

        Set<Identifier> ids = new HashSet<>();

        for (String variant : parseList(variants)) {
            switch (variant) {
                case "", "_furnace" -> ids.add(BuiltinTexturePath.FURNACE);
                case "_blast", "_blast_furnace" -> ids.add(BuiltinTexturePath.BLAST_FURNACE);
                case "_smoker" -> ids.add(BuiltinTexturePath.SMOKER);
            }
        }

        return ids;
    }

    // container -> texture path
    public static final Map<String, Identifier> TEXTURE_AUTO_MAPPING = new HashMap<>();
    // container -> (properties -> texture paths)
    public static final Map<String, TextureRemapper> TEXTURE_REMAPPERS = new HashMap<>();
    // container -> block id
    public static final Map<String, Set<Identifier>> ID_AUTO_MAPPING = new HashMap<>();
    // carpet block id -> color
    public static final Map<String, String> CARPET_TO_COLOR_MAPPING = new HashMap<>();
    // color -> shulker box block id
    public static final Map<String, Identifier> COLOR_TO_SHULKER_MAPPING = new HashMap<>();

    static {
        TEXTURE_AUTO_MAPPING.put("anvil", BuiltinTexturePath.ANVIL);
        TEXTURE_AUTO_MAPPING.put("beacon", BuiltinTexturePath.BEACON);
        TEXTURE_AUTO_MAPPING.put("brewing_stand", BuiltinTexturePath.BREWING_STAND);
        TEXTURE_AUTO_MAPPING.put("_cartography_table", BuiltinTexturePath.CARTOGRAPHY_TABLE);
        TEXTURE_AUTO_MAPPING.put("chest", BuiltinTexturePath.CHEST);
        TEXTURE_AUTO_MAPPING.put("crafting", BuiltinTexturePath.CRAFTING_TABLE);
        TEXTURE_AUTO_MAPPING.put("dispenser", BuiltinTexturePath.DISPENSER);
        TEXTURE_AUTO_MAPPING.put("enchantment", BuiltinTexturePath.ENCHANTING_TABLE);
        TEXTURE_AUTO_MAPPING.put("_grindstone", BuiltinTexturePath.GRINDSTONE);
        TEXTURE_AUTO_MAPPING.put("hopper", BuiltinTexturePath.HOPPER);
        TEXTURE_AUTO_MAPPING.put("_loom", BuiltinTexturePath.LOOM);
        TEXTURE_AUTO_MAPPING.put("shulker_box", BuiltinTexturePath.SHULKER_BOX);
        TEXTURE_AUTO_MAPPING.put("_smithing_table", BuiltinTexturePath.SMITHING_TABLE);
        TEXTURE_AUTO_MAPPING.put("_stonecutter", BuiltinTexturePath.STONECUTTER);

        TEXTURE_AUTO_MAPPING.put("horse", BuiltinTexturePath.HORSE);
        TEXTURE_AUTO_MAPPING.put("villager", BuiltinTexturePath.VILLAGER);

        TEXTURE_REMAPPERS.put("furnace", Util::remapFurnaceTexture);

        ID_AUTO_MAPPING.put("anvil", setOf(ID.ANVIL, ID.CHIPPED_ANVIL, ID.DAMAGED_ANVIL));
        ID_AUTO_MAPPING.put("beacon", setOf(ID.BEACON));
        ID_AUTO_MAPPING.put("brewing_stand", setOf(ID.BREWING_STAND));
        ID_AUTO_MAPPING.put("crafting", setOf(ID.CRAFTING_TABLE));
        ID_AUTO_MAPPING.put("enchantment", setOf(ID.ENCHANTING_TABLE));
        ID_AUTO_MAPPING.put("hopper", setOf(ID.HOPPER));

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
