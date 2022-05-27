package opekope2.optigui;

import static opekope2.optigui.util.Util.listOf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import opekope2.optigui.optifinecompat.OptiFineProperties;

public final class Replacer {
    public static final Replacer instance = new Replacer();

    private List<OptiFineProperties> properties = listOf();

    private BlockPos lastBlock;
    private Entity lastEntity;

    private Replacer() {
    }

    public void add(OptiFineProperties props) {
        properties.add(props);
    }

    public void clear() {
        properties.clear();
    }

    public void useBlock(BlockPos block) {
        lastBlock = block;
        lastEntity = null;
    }

    public void useEntity(Entity entity) {
        lastBlock = null;
        lastEntity = entity;
    }

    public Identifier getReplacement(Identifier id) {
        for (OptiFineProperties props : properties) {
            if (lastBlock != null && props.hasReplacement(id) && props.matchesBlock(lastBlock)) {
                return props.getReplacementTexture(id);
            }
            if (lastEntity != null && props.hasReplacement(id) && props.matchesEntity(lastEntity)) {
                return props.getReplacementTexture(id);
            }
            if (props.hasReplacement(id) && props.matchesAnything()) {
                return props.getReplacementTexture(id);
            }
        }
        return id;
    }
}
