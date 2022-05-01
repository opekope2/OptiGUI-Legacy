package opekope2.optigui.callback;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import opekope2.optigui.Replacer;

public class UseCallback implements UseBlockCallback, UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockEntity entity = world.getBlockEntity(hitResult.getBlockPos());
        BlockState state = world.getBlockState(hitResult.getBlockPos());
        Replacer.instance.setLastUsedBlock(state.getBlock(), entity, state);
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity,
            @Nullable EntityHitResult hitResult) {
        Registry.ENTITY_TYPE.getId(entity.getType());

        Replacer.instance.setLastUsedEntity(entity);
        return ActionResult.PASS;
    }
}
