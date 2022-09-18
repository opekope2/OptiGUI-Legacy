package opekope2.optigui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import opekope2.optigui.optifinecompat.OptiFineProperties;
import opekope2.optigui.util.InteractionCache;

public final class Replacer {
    public static final Replacer instance = new Replacer();

    private List<OptiFineProperties> properties = new ArrayList<>();

    private InteractionCache lastInteraction = new InteractionCache();
    private List<Identifier> noReplacements = new ArrayList<>();

    public void add(OptiFineProperties props) {
        properties.add(props);
    }

    public void clear() {
        properties.clear();
        lastInteraction.clear();
        noReplacements.clear();
    }

    public void useBlock(BlockPos block) {
        lastInteraction.cacheBlock(block);
    }

    public void useEntity(Entity entity) {
        lastInteraction.cacheEntity(entity);
    }

    public Identifier getReplacement(Identifier id) {
        if (noReplacements.contains(id)) {
            return id;
        }

        lastInteraction.update();
        if (lastInteraction.hasCachedReplacement(id)) {
            return lastInteraction.getCachedReplacement();
        }

        boolean hasReplacement = false;
        for (OptiFineProperties props : properties) {
            if (props.hasReplacementGuiTexture(id)) {
                hasReplacement = true;
            } else {
                continue;
            }

            if (lastInteraction.hasCachedBlock()
                    && props.hasReplacementGuiForBlock(lastInteraction.getCachedBlock())) {
                Identifier replacement = props.getReplacementTexture(id);
                lastInteraction.cacheReplacement(id, replacement);
                return replacement;
            }
            if (lastInteraction.hasCachedEntity()
                    && props.hasReplacementGuiForEntity(lastInteraction.getCachedEntity())) {
                Identifier replacement = props.getReplacementTexture(id);
                lastInteraction.cacheReplacement(id, replacement);
                return replacement;
            }
            if (props.matchesAnything()) {
                return props.getReplacementTexture(id);
            }
        }

        if (hasReplacement) {
            lastInteraction.cacheReplacement(id, id);
        } else {
            noReplacements.add(id);
        }

        return id;
    }

    private Replacer() {
    }
}
