/*
package com.astrazoey.indexed.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FillCrystalGlobeCriterion extends AbstractCriterion<FillCrystalGlobeCriterion.Conditions> {

    static final Identifier ID = new Identifier("fill_crystal_globe");

    @Override
    public Identifier getId() {
        return ID;
    }

    public FillCrystalGlobeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended player, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new FillCrystalGlobeCriterion.Conditions(player);
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, (conditions) -> {
            return conditions.matches(player);
        });
    }


    public static class Conditions extends AbstractCriterionConditions {

        public Conditions(EntityPredicate.Extended player) {
            super(FillCrystalGlobeCriterion.ID, player);
        }

        public static FillCrystalGlobeCriterion.Conditions create() {
            return new FillCrystalGlobeCriterion.Conditions(EntityPredicate.Extended.EMPTY);
        }

        public boolean matches(ServerPlayerEntity player) {
            return true;
        }
    }
}

 */
