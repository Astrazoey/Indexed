package com.astrazoey.indexed.criterion;


import com.mojang.serialization.Codec;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.*;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class UseCrystalGlobeCriterion implements Criterion<UseCrystalGlobeCriterion.Conditions> {

    public UseCrystalGlobeCriterion() {

    }

    public void trigger(ServerPlayerEntity player) {

    }

    public void beginTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<UseCrystalGlobeCriterion.Conditions> conditions) {
    }

    public void endTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<UseCrystalGlobeCriterion.Conditions> conditions) {
    }

    public void endTracking(PlayerAdvancementTracker tracker) {
    }

    public Codec<UseCrystalGlobeCriterion.Conditions> getConditionsCodec() {
        return UseCrystalGlobeCriterion.Conditions.CODEC;
    }


    public static record Conditions() implements CriterionConditions {
        public static final Codec<UseCrystalGlobeCriterion.Conditions> CODEC = Codec.unit(new UseCrystalGlobeCriterion.Conditions());

        public Conditions() {
        }

        public void validate(LootContextPredicateValidator validator) {
        }

        public boolean matches(ServerPlayerEntity player) {
            return true;
        }
    }






}

