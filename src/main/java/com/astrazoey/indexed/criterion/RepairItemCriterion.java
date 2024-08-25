package com.astrazoey.indexed.criterion;


import com.mojang.serialization.Codec;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.*;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
public class RepairItemCriterion implements Criterion<RepairItemCriterion.Conditions> {

    public RepairItemCriterion() {

    }

    public void beginTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<RepairItemCriterion.Conditions> conditions) {
    }

    public void endTrackingCondition(PlayerAdvancementTracker manager, ConditionsContainer<RepairItemCriterion.Conditions> conditions) {
    }

    public void endTracking(PlayerAdvancementTracker tracker) {
    }

    public Codec<RepairItemCriterion.Conditions> getConditionsCodec() {
        return RepairItemCriterion.Conditions.CODEC;
    }

    public static record Conditions() implements CriterionConditions {
        public static final Codec<RepairItemCriterion.Conditions> CODEC = Codec.unit(new RepairItemCriterion.Conditions());

        public Conditions() {
        }

        public void validate(LootContextPredicateValidator validator) {
        }
    }


}

