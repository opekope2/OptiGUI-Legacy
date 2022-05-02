package opekope2.optigui.util;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

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

    private Util() {
    }
}
