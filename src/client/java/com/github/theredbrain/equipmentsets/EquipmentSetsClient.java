package com.github.theredbrain.equipmentsets;

import com.github.theredbrain.equipmentsets.config.ClientConfig;
import com.github.theredbrain.equipmentsets.registry.ClientEventsRegistry;
import com.github.theredbrain.equipmentsets.registry.ClientPacketRegistry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ClientModInitializer;

public class EquipmentSetsClient implements ClientModInitializer {
	public static ConfigHolder<ClientConfig> clientConfigHolder;
	@Override
	public void onInitializeClient() {
		// Config
		AutoConfig.register(ClientConfig.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		clientConfigHolder = AutoConfig.getConfigHolder(ClientConfig.class);

		// Packets
		ClientPacketRegistry.init();

		ClientEventsRegistry.initializeClientEvents();
	}
}