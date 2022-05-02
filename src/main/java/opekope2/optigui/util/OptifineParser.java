package opekope2.optigui.util;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static opekope2.optigui.util.Util.listOf;
import static org.apache.commons.text.StringEscapeUtils.unescapeJava;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.util.Identifier;
import opekope2.optigui.VillagerMatcher;

public class OptifineParser {
    private static final Map<String, Function<String, Pattern>> regexParsers = new HashMap<>();

    static {
        regexParsers.put("pattern:", s -> Pattern.compile(wildcardToRegex(s)));
        regexParsers.put("ipattern:", s -> Pattern.compile(wildcardToRegex(s), CASE_INSENSITIVE));
        regexParsers.put("regex:", s -> Pattern.compile(s));
        regexParsers.put("iregex:", s -> Pattern.compile(s, CASE_INSENSITIVE));
    }

    public static IntRange parseRange(String input) {
        try {
            StringReader reader = new StringReader(input);
            return IntRange.parse(reader);
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static List<String> parseList(String input) {
        return parseList(input, s -> s, " \t", false);
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
        List<String> tokens = parseList(input, s -> s, ":", false);
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

    private static String wildcardToRegex(String widlcard) {
        StringBuilder result = new StringBuilder("^");
        for (int i = 0; i < widlcard.length(); ++i) {
            final char c = widlcard.charAt(i);
            result.append(switch (c) {
                case '*' -> ".*";
                case '?' -> '.';
                case '.' -> "\\.";
                case '\\' -> "\\\\";
                case '+' -> "\\+";
                case '^' -> "\\^";
                case '$' -> "\\$";
                case '[' -> "\\[";
                case ']' -> "\\]";
                case '{' -> "\\{";
                case '}' -> "\\}";
                case '(' -> "\\(";
                case ')' -> "\\)";
                case '|' -> "\\|";
                case '/' -> "\\/";
                default -> c;
            });
        }
        result.append('$');
        return result.toString();
    }

    public static RegexMatcher parseRegex(String input) {
        if (input == null) {
            return null;
        }

        for (var parser : regexParsers.entrySet()) {
            String key = parser.getKey();
            Function<String, Pattern> value = parser.getValue();

            if (input.startsWith(key)) {
                input = unescapeJava(input.substring(key.length()));
                Pattern regex = value.apply(input);
                return text -> regex.matcher(text).matches();
            }
        }

        String unescaped = unescapeJava(input);
        return text -> unescaped.equals(text);
    }

    private static interface Converter<T, TResult> {
        public TResult convert(T input);
    }

    private OptifineParser() {
    }
}
