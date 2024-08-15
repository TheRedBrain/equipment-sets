package com.github.theredbrain.equipmentsets.mixin.entity;

import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import com.github.theredbrain.equipmentsets.registry.EquipmentSetsRegistry;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CanUseEquipmentSets {

	@Shadow public abstract boolean removeStatusEffect(StatusEffect type);

	@Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

	@Unique
	private boolean shouldTickEquipmentSets = false;

	@Unique
	private Map<EquipmentSet, Integer> equipmentSetCounters = new HashMap<>();

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void equipmentsets$tick(CallbackInfo ci) {
		if (!this.getWorld().isClient && this.shouldTickEquipmentSets) {
			this.equipmentsets$tickEquipmentSets();
		}
	}

	@Unique
	private void equipmentsets$tickEquipmentSets() {
		List<Pair<EquipmentSet, TagKey<Item>>> equipmentSetItemTags = new ArrayList<>();

		Set<Identifier> identifierKeys = EquipmentSetsRegistry.registeredEquipmentSets.keySet();
		EquipmentSet equipmentSet;
		for (Identifier key : identifierKeys) {
			equipmentSet = EquipmentSetsRegistry.registeredEquipmentSets.get(key);
			TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, new Identifier(equipmentSet.itemTagString()));
			equipmentSetItemTags.add(new Pair<>(equipmentSet, itemTag));
		}

		this.equipmentSetCounters.clear();

		for (Pair<EquipmentSet, TagKey<Item>> pair : equipmentSetItemTags) {
			this.equipmentSetCounters.put(pair.getLeft(), this.equipmentsets$getAmountEquipped(pair.getRight()));
		}

		Set<EquipmentSet> equipmentSetKeys = this.equipmentSetCounters.keySet();
		for (EquipmentSet key : equipmentSetKeys) {
			int setCounter = this.equipmentSetCounters.get(key);
			List<StatusEffect> effectsToBeRemoved = new ArrayList<>();
			StatusEffect newSetEffect = null;
			StatusEffect removeThisEffect = null;
			int currentThreshold = 0;
			int statusEffectLevel = 0;
			boolean showParticles = false;
			boolean showIcon = false;
			for (EquipmentSet.SetEffect setEffect : key.setEffects()) {
				int f = setEffect.equippedItemThreshold();
//					Optional<RegistryEntry.Reference<StatusEffect>> optionalStatusEffect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(setEffect.statusEffectId()));
				StatusEffect statusEffect = Registries.STATUS_EFFECT.get(Identifier.tryParse(setEffect.statusEffectId()));
				if (setCounter >= f && f >= currentThreshold) {
//						if (optionalStatusEffect.isPresent()) {
					if (statusEffect != null) {

						if (newSetEffect != null) {
							removeThisEffect = newSetEffect;
						}
						newSetEffect = statusEffect;
						statusEffectLevel = setEffect.statusEffectLevel();
						showParticles = setEffect.showParticles();
						showIcon = setEffect.showIcon();
					}
					currentThreshold = f;
				} else {
//						if (optionalStatusEffect.isPresent()) {
					if (statusEffect != null) {
						removeThisEffect = statusEffect;
					}
				}
				if (removeThisEffect != null) {
					effectsToBeRemoved.add(removeThisEffect);
				}
			}

			if (newSetEffect != null) {
				if (!this.hasStatusEffect(newSetEffect)) {
					this.addStatusEffect(new StatusEffectInstance(newSetEffect, -1, statusEffectLevel, true, showParticles, showIcon));
				}
			}

			for (StatusEffect statusEffect : effectsToBeRemoved) {
				this.removeStatusEffect(statusEffect);
			}
		}
		this.shouldTickEquipmentSets = false;
	}

	@Unique
	private int equipmentsets$getAmountEquipped(TagKey<Item> itemTag) {
		int counter = 0;

		Predicate<ItemStack> predicate = stack -> stack.isIn(itemTag);

		Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent((LivingEntity) (Object) this);
		if (trinkets.isPresent()) {
			counter = trinkets.get().getEquipped(predicate).size();
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.MAINHAND))) {
			++counter;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.OFFHAND))) {
			++counter;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.FEET))) {
			++counter;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.LEGS))) {
			++counter;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.CHEST))) {
			++counter;
		}
		if (predicate.test(this.getEquippedStack(EquipmentSlot.HEAD))) {
			++counter;
		}
		return counter;
	}

	public void equipmentsets$setShouldTickEquipmentSets(boolean shouldTickEquipmentSets) {
		this.shouldTickEquipmentSets = shouldTickEquipmentSets;
	}
}