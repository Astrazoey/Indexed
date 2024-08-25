package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

public class VanillaEnchantmentsMixin {
}

@Mixin(AquaAffinityEnchantment.class)
class AquaAffinityEnchantmentMixin extends Enchantment {
    protected AquaAffinityEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(ChannelingEnchantment.class)
class ChannelingEnchantmentMixin extends Enchantment {

    protected ChannelingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public int getMaxLevel() { return 5; }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(DamageEnchantment.class)
class DamageEnchantmentMixin extends Enchantment {

    protected DamageEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

    //Nerf sharpness
    @ModifyConstant(method = "getAttackDamage", constant = @Constant(floatValue = 0.5f, ordinal = 0))
    public float modifySharpnessDamage(float damageMultiplier) {
        if(ConfigMain.enableEnchantmentNerfs) {
            return 0.4f;
        } else {
            return 0.5f;
        }
    }
}

@Mixin(DepthStriderEnchantment.class)
class DepthStriderEnchantmentMixin extends Enchantment {
    protected DepthStriderEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
    */
     @Overwrite
     public int getMaxLevel() {
     return 5;
     }

     @Override
     public boolean isAvailableForEnchantedBookOffer() {
         return !ConfigMain.enableVillagerNerfs;
     }
}

@Mixin(EfficiencyEnchantment.class)
class EfficiencyEnchantmentMixin extends Enchantment {
    protected EfficiencyEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(FireAspectEnchantment.class)
class FireAspectEnchantmentMixin extends Enchantment {
    protected FireAspectEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(FlameEnchantment.class)
class FlameEnchantmentMixin extends Enchantment {
    protected FlameEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(FrostWalkerEnchantment.class)
class FrostWalkerEnchantmentMixin {
    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }
}

@Mixin(ImpalingEnchantment.class)
class ImpalingEnchantmentMixin extends Enchantment {
    protected ImpalingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.UNCOMMON, type, slotTypes);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(target instanceof LivingEntity && target.isTouchingWaterOrRain()) {
            DamageSource source = target.getWorld().getDamageSources().mobAttack(user);

            target.damage(source, level*2.5f);
        }
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return 0.0F;
    }



}

@Mixin(InfinityEnchantment.class)
class InfinityEnchantmentMixin extends Enchantment {
    protected InfinityEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(KnockbackEnchantment.class)
class KnockbackEnchantmentMixin extends Enchantment {
    protected KnockbackEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(LoyaltyEnchantment.class)
class LoyaltyEnchantmentMixin extends Enchantment {
    protected LoyaltyEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.RARE, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }
}

@Mixin(LuckEnchantment.class)
class LuckEnchantmentMixin extends Enchantment {
    protected LuckEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

}

@Mixin(LureEnchantment.class)
class LureEnchantmentMixin extends Enchantment {
    protected LureEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(MendingEnchantment.class)
class MendingEnchantmentMixin extends Enchantment {
    protected MendingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */

    public int getMaxLevel() {
        return 5;
    }

    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        if(ConfigMain.mendingIsTreasure) {
            return true;
        } else {
            return !ConfigMain.enableVillagerNerfs;
        }
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return !ConfigMain.mendingIsTreasure;
    }

    @Override
    public boolean isTreasure() {
        return ConfigMain.mendingIsTreasure;
    }

}

@Mixin(MultishotEnchantment.class)
class MultishotEnchantmentMixin extends Enchantment {
    protected MultishotEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.VERY_RARE, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

}

@Mixin(PiercingEnchantment.class)
class PiercingEnchantmentMixin extends Enchantment {
    protected PiercingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.UNCOMMON, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(PowerEnchantment.class)
class PowerEnchantmentMixin extends Enchantment {
    protected PowerEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(ProtectionEnchantment.class)
class ProtectionEnchantmentMixin extends Enchantment {
    protected ProtectionEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes, ProtectionEnchantment.Type protectionType) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(PunchEnchantment.class)
class PunchEnchantmentMixin extends Enchantment {
    protected PunchEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}


@Mixin(QuickChargeEnchantment.class)
class QuickChargeEnchantmentMixin extends Enchantment {
    protected QuickChargeEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.RARE, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}


@Mixin(RespirationEnchantment.class)
class RespirationEnchantmentMixin extends Enchantment {
    protected RespirationEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazpey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(RiptideEnchantment.class)
class RiptideEnchantmentMixin extends Enchantment {
    protected RiptideEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return ((Enchantment) (Object) this) != other;
    }
}

@Mixin(SilkTouchEnchantment.class)
class SilkTouchEnchantmentMixin extends Enchantment {
    protected SilkTouchEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(Rarity.RARE, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(SoulSpeedEnchantment.class)
class SoulSpeedEnchantmentMixin extends Enchantment {
    protected SoulSpeedEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }
}

@Mixin(SweepingEnchantment.class)
class SweepingEnchantmentMixin extends Enchantment {
    protected SweepingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}

@Mixin(SwiftSneakEnchantment.class)
class SwiftSneakEnchantmentMixin extends Enchantment {
    protected SwiftSneakEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }
}


@Mixin(ThornsEnchantment.class)
class ThornsEnchantmentMixin extends Enchantment {
    protected ThornsEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}


@Mixin(UnbreakingEnchantment.class)
class UnbreakingEnchantmentMixin extends Enchantment {
    protected UnbreakingEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    /**
     * @author Astrazoey
     */
    @Overwrite
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return !ConfigMain.enableVillagerNerfs;
    }
}



