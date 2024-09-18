package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.network.packet.EquipmentSetsSyncPacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventsRegistry {
	public static void initializeEvents() {
		PayloadTypeRegistry.playS2C().register(EquipmentSetsSyncPacket.PACKET_ID, EquipmentSetsSyncPacket.PACKET_CODEC);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.player, new EquipmentSetsSyncPacket(EquipmentSetsRegistry.registeredEquipmentSets));
		});
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
			for (ServerPlayerEntity player : PlayerLookup.all(server)) {
				ServerPlayNetworking.send(player, new EquipmentSetsSyncPacket(EquipmentSetsRegistry.registeredEquipmentSets));
			}
		});
	}
}
