/*
package com.astrazoey.indexed.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class MultishotCrossbowCriterion extends AbstractCriterion<MultishotCrossbowCriterion.Conditions> {

    static final Identifier ID = new Identifier("multishot_crossbow");

    @Override
    public Identifier getId() {
        return ID;
    }

    public MultishotCrossbowCriterion.Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended player, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new MultishotCrossbowCriterion.Conditions(player);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, (conditions) -> {
            return conditions.matches(player);
        });
    }


    public static class Conditions extends AbstractCriterionConditions {

        public Conditions(EntityPredicate.Extended player) {
            super(MultishotCrossbowCriterion.ID, player);
        }

        public static MultishotCrossbowCriterion.Conditions create() {
            return new MultishotCrossbowCriterion.Conditions(EntityPredicate.Extended.EMPTY);
        }

        public boolean matches(ServerPlayerEntity player) {
            return true;
        }
    }
}


 */