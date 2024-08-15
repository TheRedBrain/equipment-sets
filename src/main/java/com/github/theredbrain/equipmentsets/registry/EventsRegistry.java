package com.github.theredbrain.equipmentsets.registry;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class EventsRegistry {

	public static void initializeEvents() {

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			sender.sendPacket(ServerPacketRegistry.SYNC_EQUIPMENT_SETS, EquipmentSetsRegistry.getEncodedRegistry());
		});
	}
}
