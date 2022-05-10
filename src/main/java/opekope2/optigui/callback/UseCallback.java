package opekope2.optigui.callback;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import opekope2.optigui.Replacer;

public final class UseCallback implements UseBlockCallback, UseEntityCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient) {
            Replacer.instance.useBlock(hitResult.getBlockPos());
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity,
            @Nullable EntityHitResult hitResult) {
        if (world.isClient) {
            Replacer.instance.useEntity(entity);
        }
        return ActionResult.PASS;
    }
}
