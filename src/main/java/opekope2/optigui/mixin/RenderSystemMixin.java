package opekope2.optigui.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.util.Identifier;
import opekope2.optigui.GuiTextureReplacer;

// Inject before Animatica for full compatibility
@Mixin(value = RenderSystem.class, priority = 900)
public final class RenderSystemMixin {
	@ModifyVariable(method = "_setShaderTexture(ILnet/minecraft/util/Identifier;)V", at = @At("HEAD"), index = 1)
	private static Identifier setShaderTextureMixin(Identifier id) {
		return GuiTextureReplacer.instance.getReplacement(id);
	}
}
