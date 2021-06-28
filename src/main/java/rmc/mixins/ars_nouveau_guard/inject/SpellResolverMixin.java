package rmc.mixins.ars_nouveau_guard.inject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rmc.libs.event_factory.EventFactory;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = SpellResolver.class)
public abstract class SpellResolverMixin {

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;onCastOnBlock(Lnet/minecraft/util/math/BlockRayTraceResult;Lnet/minecraft/entity/LivingEntity;)V",
            remap = false,
            cancellable = true,
            at = @At(value = "INVOKE",
                     target = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractCastMethod;onCastOnBlock(Lnet/minecraft/util/math/BlockRayTraceResult;Lnet/minecraft/entity/LivingEntity;Ljava/util/List;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V"))
    private void guardBlockCast(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, CallbackInfo mixin) {
        if (!EventFactory.testBlockBreak(EventFactory.convert(caster), caster.level, blockRayTraceResult.getBlockPos())) {
            mixin.cancel();
        }
    }

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;onCastOnBlock(Lnet/minecraft/item/ItemUseContext;)V",
            remap = false,
            cancellable = true,
            at = @At(value = "INVOKE",
                     target = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractCastMethod;onCastOnBlock(Lnet/minecraft/item/ItemUseContext;Ljava/util/List;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V"))
    private void guardDirectBlockCast(ItemUseContext context, CallbackInfo mixin) {
        if (!EventFactory.testBlockBreak(EventFactory.convert(context.getPlayer()), context.getLevel(), context.getClickedPos())) {
            mixin.cancel();
        }
    }

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;onCastOnEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)V",
            remap = false,
            cancellable = true,
            at = @At(value = "INVOKE",
                     target = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractCastMethod;onCastOnEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Ljava/util/List;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;)V"))
    private void guardEntityCast(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand, CallbackInfo mixin) {
        if (!EventFactory.testEntityInteract(EventFactory.convert(playerIn), playerIn.level, target)) {
            mixin.cancel();
        }
    }

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/spell/SpellResolver;resolveEffects(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/RayTraceResult;Lcom/hollingsworth/arsnouveau/api/spell/Spell;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;)V",
            remap = false,
            cancellable = true,
            at = @At(value = "INVOKE",
                     target = "Lcom/hollingsworth/arsnouveau/api/spell/AbstractEffect;onResolve(Lnet/minecraft/util/math/RayTraceResult;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Ljava/util/List;Lcom/hollingsworth/arsnouveau/api/spell/SpellContext;)V"))
    private static void guardEffectResolve(World world, LivingEntity shooter, RayTraceResult result, Spell spell, SpellContext spellContext, CallbackInfo mixin) {
        boolean cancelled = false;
        if (result instanceof BlockRayTraceResult) {
            cancelled = !EventFactory.testBlockBreak(EventFactory.convert(shooter), world, ((BlockRayTraceResult) result).getBlockPos());
        }
        else if (result instanceof EntityRayTraceResult) {
            cancelled = !EventFactory.testEntityInteract(EventFactory.convert(shooter), world, ((EntityRayTraceResult) result).getEntity());
        }
        if (cancelled) {
            mixin.cancel();
        }
    }

}