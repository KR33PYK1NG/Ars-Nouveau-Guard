package rmc.mixins.ars_nouveau_guard;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

import com.mojang.authlib.GameProfile;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = ArsNouveauGuard.class)
public abstract class ArsNouveauGuard {

    public static final GameProfile AOE_FAKE = new GameProfile(UUID.fromString("E218FF91-E3F6-4D4F-B559-F4B24A19841D"), "[ArsNouveauAOE]");
    public static LivingEntity aoeOwner;

}
