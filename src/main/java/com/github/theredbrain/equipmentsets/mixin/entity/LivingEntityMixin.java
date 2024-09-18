package com.github.theredbrain.equipmentsets.mixin.entity;

import com.github.theredbrain.equipmentsets.EquipmentSets;
import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import com.github.theredbrain.equipmentsets.entity.CustomDataHandlers;
import com.github.theredbrain.equipmentsets.registry.EquipmentSetsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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

	@Shadow
	public abstract boolean addStatusEffect(StatusEffectInstance effect);

	@Shadow
	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Unique
	private static final TrackedData<Map<String, Integer>> EQUIPMENT_SET_COUNTERS = DataTracker.registerData(LivingEntity.class, CustomDataHandlers.STRING_INTEGER_MAP);

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "initDataTracker", at = @At("TAIL"))
	private void equipmentsets$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
		builder.add(EQUIPMENT_SET_COUNTERS, new HashMap<>());
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void equipmentsets$tick(CallbackInfo ci) {
		if (!this.getWorld().isClient && this.getWorld().getTime() % 80L == 0L) {
			this.equipmentsets$tickEquipmentSets();
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void equipmentsets$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {

		int sideEntrancesSize = nbt.getInt("newEquipmentSetCountersSize");
		Map<String, Integer> newEquipmentSetCounters = new HashMap<>(Map.of());
		for (int i = 0; i < sideEntrancesSize; i++) {
			String key = nbt.getString("key_" + i);
			int counter = nbt.getInt("counter_" + i);
			newEquipmentSetCounters.put(key, counter);
		}

		((CanUseEquipmentSets) this).equipmentsets$setEquipmentSetCounters(newEquipmentSetCounters);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void equipmentsets$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		Map<String, Integer> newEquipmentSetCounters = ((CanUseEquipmentSets) this).equipmentsets$getEquipmentSetCounters();
		List<String> keyList = newEquipmentSetCounters.keySet().stream().toList();
		int newEquipmentSetCountersSize = newEquipmentSetCounters.keySet().size();
		nbt.putInt("newEquipmentSetCountersSize", newEquipmentSetCountersSize);
		for (int i = 0; i < newEquipmentSetCountersSize; i++) {
			String key = keyList.get(i);
			nbt.putString("key_" + i, key);
			nbt.putInt("counter_" + i, newEquipmentSetCounters.get(key));
		}

	}

	@Unique
	private void equipmentsets$tickEquipmentSets() {
		List<Pair<Identifier, TagKey<Item>>> equipmentSetItemTags = new ArrayList<>();

		Set<Identifier> identifierKeys = EquipmentSetsRegistry.registeredEquipmentSets.keySet();
		EquipmentSet equipmentSet;
		for (Identifier key : identifierKeys) {
			equipmentSet = EquipmentSetsRegistry.registeredEquipmentSets.get(key);
			TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, Identifier.of(equipmentSet.item_tag_string()));
			equipmentSetItemTags.add(new Pair<>(key, itemTag));
		}

		Map<String, Integer> equipmentSetCounters = new HashMap<>();

		for (Pair<Identifier, TagKey<Item>> pair : equipmentSetItemTags) {
			equipmentSetCounters.put(pair.getLeft().toString(), this.equipmentsets$getAmountEquipped(pair.getRight()));
		}
		this.equipmentsets$setEquipmentSetCounters(equipmentSetCounters);

		Set<String> equipmentSetKeys = this.equipmentsets$getEquipmentSetCounters().keySet();
		for (String key : equipmentSetKeys) {
			int setCounter = this.equipmentsets$getEquipmentSetCounters().get(key);
			equipmentSet = EquipmentSetsRegistry.registeredEquipmentSets.get(Identifier.tryParse(key));
			List<StatusEffectInstance> newSetEffects = new ArrayList<>();
			int currentThreshold = 0;
			for (EquipmentSet.SetEffect setEffect : equipmentSet.set_effects()) {
				int f = setEffect.equipped_item_threshold();
				Optional<RegistryEntry.Reference<StatusEffect>> statusEffect = Registries.STATUS_EFFECT.getEntry(Identifier.tryParse(setEffect.status_effect_id()));
				if (setCounter >= f && f >= currentThreshold) {
					if (statusEffect.isPresent()) {
						if (!equipmentSet.stack_effects()) {
							newSetEffects.clear();
						}
						newSetEffects.add(new StatusEffectInstance(statusEffect.get(), 120, setEffect.status_effect_level(), true, setEffect.show_particles(), setEffect.show_icon()));
					}
					currentThreshold = f;
				}
			}

			for (StatusEffectInstance instance : newSetEffects) {
				this.addStatusEffect(instance);
			}
		}
	}

	@Unique
	private int equipmentsets$getAmountEquipped(TagKey<Item> itemTag) {
		int counter = 0;

		Predicate<ItemStack> predicate = stack -> stack.isIn(itemTag);

		counter = counter + EquipmentSets.getEquippedTrinketsAmount(((LivingEntity) (Object) this), predicate);

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

	@Override
	public Map<String, Integer> equipmentsets$getEquipmentSetCounters() {
		return this.dataTracker.get(EQUIPMENT_SET_COUNTERS);
	}

	@Override
	public void equipmentsets$setEquipmentSetCounters(Map<String, Integer> equipmentSetCounters) {
		this.dataTracker.set(EQUIPMENT_SET_COUNTERS, equipmentSetCounters);
	}
}
