package rmc.mixins.ars_nouveau_guard.inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.hollingsworth.arsnouveau.api.util.SpellUtil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import rmc.libs.event_factory.EventFactory;
import rmc.mixins.ars_nouveau_guard.ArsNouveauGuard;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = SpellUtil.class)
public abstract class SpellUtilMixin {

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/util/SpellUtil;calcAOEBlocks(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockRayTraceResult;IIII)Ljava/util/List;",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "INVOKE_ASSIGN",
                     target = "Lnet/minecraft/util/Direction;getNormal()Lnet/minecraft/util/math/vector/Vector3i;"))
    private static void fillAOEOwner(LivingEntity caster, BlockPos origin, BlockRayTraceResult mop, int width, int height, int depth, int distance, CallbackInfoReturnable<List<BlockPos>> mixin, Vector3i hitVec) {
        if (caster instanceof ServerPlayerEntity
         && !(caster instanceof FakePlayer)) {
            ArsNouveauGuard.aoeOwner = caster;
        }
    }

    @Inject(method = "Lcom/hollingsworth/arsnouveau/api/util/SpellUtil;calcAOEBlocks(Lnet/minecraft/util/math/vector/Vector3i;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockRayTraceResult;IIII)Ljava/util/List;",
            remap = false,
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(value = "TAIL"))
    private static void guardAOEBlocks(Vector3i facingVec, BlockPos origin, BlockRayTraceResult mop, int width, int height, int depth, int distance, CallbackInfoReturnable<List<BlockPos>> mixin, int x, int y, int z, BlockPos start, ArrayList<BlockPos> builder) {
        LivingEntity aoeOwner = ArsNouveauGuard.aoeOwner;
        if (aoeOwner == null
         && ServerLifecycleHooks.getCurrentServer() != null) {
            aoeOwner = FakePlayerFactory.get(ServerLifecycleHooks.getCurrentServer().overworld(), ArsNouveauGuard.AOE_FAKE);
        }
        Iterator<BlockPos> it = builder.iterator();
        while (it.hasNext()) {
            if (!EventFactory.testBlockBreak(EventFactory.convert(aoeOwner), aoeOwner.level, it.next())) {
                builder.clear();
                break;
            }
        }
    }

}
