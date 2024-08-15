package com.github.theredbrain.equipmentsets;

import com.github.theredbrain.equipmentsets.registry.ClientPacketRegistry;
import net.fabricmc.api.ClientModInitializer;

public class EquipmentSetsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		// Packets
		ClientPacketRegistry.init();

	}
}