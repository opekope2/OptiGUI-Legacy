package opekope2.optigui.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import static opekope2.optigui.util.Util.getBiomeId;

public class InteractionCache {
    private boolean valid = false;

    private Identifier biome;
    private int height;
    private Identifier id;

    private BlockPos pos;
    private Entity entity;

    private Identifier original;
    private Identifier replacement;

    public void cacheBlock(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();

        biome = getBiomeId(mc, pos);
        height = pos.getY();
        id = Registry.BLOCK.getId(mc.world.getBlockState(pos).getBlock());

        this.pos = pos;
        this.entity = null;

        clearCachedReplacement();

        valid = true;
    }

    public void cacheEntity(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();
        BlockPos pos = entity.getBlockPos();

        biome = getBiomeId(mc, pos);
        height = pos.getY();
        id = Registry.ENTITY_TYPE.getId(entity.getType());

        this.pos = null;
        this.entity = entity;

        clearCachedReplacement();

        valid = true;
    }

    public void cacheReplacement(Identifier original, Identifier replacement) {
        this.original = original;
        this.replacement = replacement;
    }

    public boolean hasCachedBlock() {
        return valid && pos != null;
    }

    public boolean hasCachedEntity() {
        return valid && entity != null;
    }

    public BlockPos getCachedBlock() {
        return valid ? pos : null;
    }

    public Entity getCachedEntity() {
        return valid ? entity : null;
    }

    public boolean hasCachedReplacement(Identifier id) {
        return original == id;
    }

    public Identifier getCachedReplacement() {
        return replacement;
    }

    public void update() {
        if (pos != null) {
            updateCachedBlock();
        }
        if (entity != null) {
            UpdateCachedEntity();
        }
    }

    private void updateCachedBlock() {
        if (!valid) {
            return;
        }

        boolean updated = false;

        MinecraftClient mc = MinecraftClient.getInstance();

        Identifier blockId = Registry.BLOCK.getId(mc.world.getBlockState(pos).getBlock());
        if (!blockId.equals(id)) {
            id = blockId;
            updated = true;
        }

        if (updated) {
            clearCachedReplacement();
        }
    }

    private void UpdateCachedEntity() {
        if (!valid) {
            return;
        }

        if (!entity.isAlive() || entity.isRemoved()) {
            valid = false;
            return;
        }

        boolean updated = false;

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockPos pos = entity.getBlockPos();

        Identifier biomeId = getBiomeId(mc, pos);
        if (!biomeId.equals(biome)) {
            biome = biomeId;
            updated = false;
        }
        int y = pos.getY();
        if (y != height) {
            height = y;
            updated = false;
        }
        Identifier entityId = Registry.ENTITY_TYPE.getId(entity.getType());
        if (!entityId.equals(id)) {
            id = entityId;
            valid = false;
        }

        if (updated) {
            clearCachedReplacement();
        }
    }

    private void clearCachedReplacement() {
        original = null;
        replacement = null;
    }

    public void clear() {
        valid = false;
        clearCachedReplacement();
    }
}
