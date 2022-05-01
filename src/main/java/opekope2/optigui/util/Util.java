package opekope2.optigui.util;

import java.util.ArrayList;
import java.util.List;

public final class Util {
    public static <T> List<T> listOf() {
        return new ArrayList<>();
    }

    public static <T> List<T> listOf(T item) {
        List<T> result = listOf();
        result.add(item);
        return result;
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

    private Util() {
    }
}
