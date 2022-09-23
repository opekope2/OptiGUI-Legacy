package opekope2.optigui.util;

import static opekope2.optigui.util.Util.getBiomeId;
import static opekope2.optigui.util.Util.setAndCheckIfUpdated;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class InteractionInfo {
    private boolean valid = false;

    private Identifier biome;
    private BlockPos blockPos;
    private BlockState blockState;
    private BlockEntity blockEntity;
    private Entity entity;
    private boolean hasCustomName;
    private String customName;
    private Identifier id;

    /**
     * @return Should clear
     */
    public boolean fill(BlockPos pos) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // Fix Quilt issues: #16, #17
        if (mc.world == null) {
            valid = false;
            throw new IllegalStateException("Received game tick after leaving world!");
        }

        boolean updated = false;

        updated |= setAndCheckIfUpdated(x -> blockPos = x, blockPos, pos);
        updated |= setAndCheckIfUpdated(x -> biome = x, biome, getBiomeId(mc, pos));
        updated |= setAndCheckIfUpdated(x -> blockState = x, blockState, mc.world.getBlockState(pos));
        updated |= setAndCheckIfUpdated(x -> blockEntity = x, blockEntity, mc.world.getBlockEntity(pos));
        updated |= setAndCheckIfUpdated(x -> entity = x, entity, (Entity) null);
        {
            blockEntity = mc.world.getBlockEntity(pos);
            if (blockEntity != null && blockEntity instanceof Nameable nameable) {
                updated |= setAndCheckIfUpdated(x -> hasCustomName = x, hasCustomName, nameable.hasCustomName());
                updated |= setAndCheckIfUpdated(x -> customName = x, customName,
                        nameable.hasCustomName() ? nameable.getCustomName().getString() : null);
            } else {
                updated |= setAndCheckIfUpdated(x -> hasCustomName = x, hasCustomName, false);
                updated |= setAndCheckIfUpdated(x -> customName = x, customName, (String) null);
            }
        }
        updated |= setAndCheckIfUpdated(x -> id = x, id, Registry.BLOCK.getId(blockState.getBlock()));

        valid = true;

        return updated;
    }

    /**
     * @return Should clear
     */
    public boolean fill(Entity entity) {
        MinecraftClient mc = MinecraftClient.getInstance();

        boolean updated = false;

        updated |= setAndCheckIfUpdated(x -> blockPos = x, blockPos, entity.getBlockPos());
        updated |= setAndCheckIfUpdated(x -> biome = x, biome, getBiomeId(mc, blockPos));
        updated |= setAndCheckIfUpdated(x -> blockState = x, blockState, (BlockState) null);
        updated |= setAndCheckIfUpdated(x -> blockEntity = x, blockEntity, (BlockEntity) null);
        updated |= setAndCheckIfUpdated(x -> this.entity = x, this.entity, entity);
        {
            updated |= setAndCheckIfUpdated(x -> hasCustomName = x, hasCustomName, entity.hasCustomName());
            updated |= setAndCheckIfUpdated(x -> customName = x, customName,
                    entity.hasCustomName() ? entity.getCustomName().getString() : null);
        }
        {
            updated |= setAndCheckIfUpdated(x -> id = x, id, Registry.ENTITY_TYPE.getId(entity.getType()));
        }

        valid = true;

        return updated;
    }

    /**
     * @return Should clear
     */
    public boolean refresh() {
        if (entity != null) {
            return fill(entity);
        } else if (blockPos != null) {
            return fill(blockPos);
        }
        return true;
    }

    public void clear() {
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isBlock() {
        return valid && entity == null && blockPos != null;
    }

    public boolean isEntity() {
        return valid && entity != null;
    }

    public Identifier getBiome() {
        return valid ? biome : null;
    }

    public BlockPos getBlockPos() {
        return valid ? blockPos : null;
    }

    public BlockState getBlockState() {
        return valid ? blockState : null;
    }

    public BlockEntity getBlockEntity() {
        return valid ? blockEntity : null;
    }

    public Entity getEntity() {
        return valid ? entity : null;
    }

    public boolean hasCustomName() {
        return valid && hasCustomName;
    }

    public String getCustomName() {
        return valid ? customName : null;
    }

    public Identifier getId() {
        return valid ? id : null;
    }
}
