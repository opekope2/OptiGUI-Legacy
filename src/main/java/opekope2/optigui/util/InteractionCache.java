package opekope2.optigui.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import opekope2.optigui.OptiGUIClient;

public class InteractionCache {

    private final InteractionInfo interaction = new InteractionInfo();

    private Identifier original;
    private Identifier replacement;

    public void cacheBlock(BlockPos pos) {
        interaction.fill(pos);
        clearCachedReplacement();
    }

    public void cacheEntity(Entity entity) {
        interaction.fill(entity);
        clearCachedReplacement();
    }

    public void cacheReplacement(Identifier original, Identifier replacement) {
        this.original = original;
        this.replacement = replacement;
    }

    public InteractionInfo getInteraction() {
        return interaction;
    }

    public boolean hasCachedReplacement(Identifier id) {
        return original == id;
    }

    public Identifier getCachedReplacement() {
        return replacement;
    }

    public void updateCachedBlockOrEntity() {
        try {
            if (interaction.update()) {
                clearCachedReplacement();
            }
        } catch (IllegalStateException e) {
            OptiGUIClient.logger.warn(e.getMessage());
            clearCachedReplacement();
        }
    }

    private void clearCachedReplacement() {
        original = null;
        replacement = null;
    }

    public void clear() {
        interaction.clear();
        clearCachedReplacement();
    }
}
