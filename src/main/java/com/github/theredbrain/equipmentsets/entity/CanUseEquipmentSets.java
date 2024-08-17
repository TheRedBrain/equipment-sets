package com.github.theredbrain.equipmentsets.entity;

import net.minecraft.util.Identifier;

import java.util.Map;

public interface CanUseEquipmentSets {
	void equipmentsets$setShouldTickEquipmentSets(boolean shouldTickEquipmentSets);
	void equipmentsets$setEquipmentSetCounters(Map<String, Integer> equipmentSetCounters);
	Map<String, Integer> equipmentsets$getEquipmentSetCounters();
}
