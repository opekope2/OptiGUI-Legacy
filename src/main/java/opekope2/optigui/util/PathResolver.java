package opekope2.optigui.util;

import java.nio.file.InvalidPathException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

import net.minecraft.util.Identifier;

public class PathResolver {
    public static Identifier resolve(String resourcePath, String path) throws InvalidPathException {
        Deque<String> pathStack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(resourcePath, "/");
        while (tokenizer.hasMoreTokens()) {
            pathStack.push(tokenizer.nextToken());
        }

        tokenizer = new StringTokenizer(path, ":/", true);
        String namespace = "minecraft";
        int nToken = -1;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            nToken++;

            if (nToken == 0 && "~".equals(token)) {
                pathStack.clear();
                pathStack.push("optifine");
                continue;
            } else if (":".equals(token)) {
                if (nToken == 1) {
                    namespace = pathStack.pop();
                    pathStack.clear();
                }
            } else if ("..".equals(token)) {
                if (pathStack.isEmpty()) {
                    return null;
                }
                pathStack.pop();
            } else if (!"/".equals(token) && !".".equals(token)) {
                pathStack.push(token);
            }
        }

        StringBuilder pathBuilder = new StringBuilder();
        boolean first = true;

        while (!pathStack.isEmpty()) {
            if (first) {
                first = false;
            } else {
                pathBuilder.append("/");
            }
            pathBuilder.append(pathStack.removeLast());
        }

        return new Identifier(namespace, pathBuilder.toString());
    }
}
