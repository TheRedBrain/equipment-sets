package com.github.theredbrain.equipmentsets.entity;

import java.util.Map;

public interface CanUseEquipmentSets {
	void equipmentsets$setEquipmentSetCounters(Map<String, Integer> equipmentSetCounters);

	Map<String, Integer> equipmentsets$getEquipmentSetCounters();
}
