package com.github.theredbrain.equipmentsets;

import com.github.theredbrain.equipmentsets.registry.EquipmentSetsRegistry;
import com.github.theredbrain.equipmentsets.registry.EventsRegistry;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipmentSets implements ModInitializer {
	public static final String MOD_ID = "equipmentsets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");


		// Registry
		EquipmentSetsRegistry.init();
		EventsRegistry.initializeEvents();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}