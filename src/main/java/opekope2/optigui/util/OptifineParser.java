package opekope2.optigui.util;

import static opekope2.optigui.util.Util.listOf;

import java.util.List;
import java.util.StringTokenizer;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.util.Identifier;
import opekope2.optigui.VillagerMatcher;

public class OptifineParser {
    private static final Converter<String, String> stringConverter = s -> s;

    public static IntRange parseRange(String input) {
        try {
            StringReader reader = new StringReader(input);
            return IntRange.parse(reader);
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static List<String> parseList(String input) {
        return parseList(input, stringConverter, " \t", false);
    }

    private static <T> List<T> parseList(String input, Converter<String, T> converter, String separators,
            Boolean removeNulls) {
        StringTokenizer tokenizer = new StringTokenizer(input, separators);
        List<T> result = listOf();
        while (tokenizer.hasMoreTokens()) {
            T converted = converter.convert(tokenizer.nextToken());
            if (!removeNulls || converted != null) {
                result.add(converted);
            }
        }
        return result;
    }

    public static List<Identifier> parseIdentifierList(String input) {
        return parseList(input, s -> new Identifier(s), " \t", false);
    }

    public static List<IntRange> parseRangeList(String input) {
        return parseList(input, OptifineParser::parseRange, " \t", true);
    }

    private static VillagerMatcher parseProfession(String input) {
        List<String> tokens = parseList(input, stringConverter, ":", false);
        String namespace = "minecraft";
        String profession;
        List<IntRange> levels = listOf(IntRange.ANY);

        if (tokens.size() == 1) {
            profession = tokens.get(0);
        } else if (tokens.size() == 2) {
            char c = tokens.get(1).charAt(0);
            if (c >= '0' && c <= '9') {
                profession = tokens.get(0);
                levels = parseRangeList(tokens.get(1));
            } else {
                namespace = tokens.get(0);
                profession = tokens.get(1);
            }
        } else if (tokens.size() == 3) {
            namespace = tokens.get(0);
            profession = tokens.get(1);
            levels = parseRangeList(tokens.get(2));
        } else {
            return null;
        }

        return new VillagerMatcher(new Identifier(namespace, profession), levels);
    }

    public static List<VillagerMatcher> parseProfessionList(String input) {
        return parseList(input, OptifineParser::parseProfession, " \t", true);
    }

    private static interface Converter<T, TResult> {
        public TResult convert(T input);
    }

    private OptifineParser() {
    }
}
