package opekope2.optigui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import opekope2.optigui.optifinecompat.OptiFineProperties;
import opekope2.optigui.util.InteractionCache;
import opekope2.optigui.util.InteractionInfo;

public final class TextureReplacer {
    public static final TextureReplacer instance = new TextureReplacer();

    private List<OptiFineProperties> properties = new ArrayList<>();

    private InteractionCache lastInteraction = new InteractionCache();
    private Set<Identifier> noReplacements = new HashSet<>();

    public void add(OptiFineProperties props) {
        properties.add(props);
    }

    public void clear() {
        properties.clear();
        clearCaches();
    }

    public void clearCaches() {
        lastInteraction.clear();
        noReplacements.clear();
    }

    public void useBlock(BlockPos block) {
        lastInteraction.cacheBlock(block);
    }

    public void useEntity(Entity entity) {
        lastInteraction.cacheEntity(entity);
    }

    public void refreshInteraction() {
        lastInteraction.refreshInteraction();
    }

    public Identifier getReplacement(Identifier id) {
        if (noReplacements.contains(id)) {
            return id;
        }

        if (lastInteraction.hasCachedReplacement(id)) {
            return lastInteraction.getCachedReplacement();
        }

        InteractionInfo interaction = lastInteraction.getInteraction();
        if (!interaction.isValid()) {
            return id;
        }

        boolean hasReplacement = false;
        for (OptiFineProperties props : properties) {
            if (props.canReplaceTexture(id)) {
                hasReplacement = true;
            } else {
                continue;
            }

            if (interaction.isBlock() && props.matchesBlock(interaction)) {
                Identifier replacement = props.replaceTexture(id);
                lastInteraction.cacheReplacement(id, replacement);
                return replacement;
            }
            if (interaction.isEntity() && props.matchesEntity(interaction)) {
                Identifier replacement = props.replaceTexture(id);
                lastInteraction.cacheReplacement(id, replacement);
                return replacement;
            }
            if (props.matchesAnythingElse(interaction)) {
                return props.replaceTexture(id);
            }
        }

        if (hasReplacement) {
            lastInteraction.cacheReplacement(id, id);
        } else {
            noReplacements.add(id);
        }

        return id;
    }

    private TextureReplacer() {
    }
}
