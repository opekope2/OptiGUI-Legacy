package opekope2.optigui;

import static opekope2.optigui.util.Util.listOf;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import opekope2.optigui.optifinecompat.OptifineProperties;

public class Replacer {
    public static final Replacer instance = new Replacer();

    private List<OptifineProperties> properties = listOf();

    private Block lastUsedBlock;
    private BlockEntity lastUsedBlockEntity;
    private BlockState lastUsedBlockState;
    private Entity lastUsedEntity;

    private Replacer() {
    }

    public void add(OptifineProperties props) {
        properties.add(props);
    }

    public void clear() {
        properties.clear();
    }

    public void setLastUsedBlock(Block block, BlockEntity entity, BlockState state) {
        lastUsedBlock = block;
        lastUsedBlockEntity = entity;
        lastUsedBlockState = state;
        lastUsedEntity = null;
    }

    public void setLastUsedEntity(Entity entity) {
        lastUsedBlock = null;
        lastUsedBlockEntity = null;
        lastUsedBlockState = null;
        lastUsedEntity = entity;
    }

    @SuppressWarnings("resource")
    public Identifier getReplacement(Identifier id) {
        for (OptifineProperties props : properties) {
            if (lastUsedBlock != null && props.hasReplacement(id)
                    && props.matches(lastUsedBlock, lastUsedBlockEntity, lastUsedBlockState)) {
                return props.getReplacementTexture(id);
            }
            if (lastUsedEntity != null && props.hasReplacement(id) && props.matches(lastUsedEntity)) {
                return props.getReplacementTexture(id);
            }
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && props.hasReplacement(id) && props.matches(player)) {
                return props.getReplacementTexture(id);
            }
        }
        return id;
    }
}
