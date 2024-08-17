package com.github.theredbrain.equipmentsets.data;

public record EquipmentSet(String localizationString, String itemTagString, SetEffect[] setEffects) {

	public record SetEffect(int equippedItemThreshold, String toolTipText, String statusEffectId, int statusEffectLevel, boolean showParticles, boolean showIcon) {
	}

}
