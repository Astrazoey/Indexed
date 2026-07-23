package com.astrazoey.indexed.criterion;


import com.mojang.serialization.Codec;
import net.minecraft.advancements.triggers.*;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;

public class OverchargeItemCriterion extends SimpleCriterionTrigger<OverchargeItemCriterion.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player) {
        trigger(player, Conditions::requirementsMet);
    }

    public record Conditions(Optional<ContextAwarePredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static Codec<OverchargeItemCriterion.Conditions> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
                .xmap(Conditions::new, Conditions::player).codec();

        @Override
        public Optional<ContextAwarePredicate> player() {
            return playerPredicate;
        }

        public boolean requirementsMet() {
            return true;
        }
    }
}

