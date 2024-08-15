package com.github.theredbrain.equipmentsets.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(value = EnvType.CLIENT)
public class ClientPacketRegistry {

	public static void init() {

		ClientPlayNetworking.registerGlobalReceiver(ServerPacketRegistry.SYNC_EQUIPMENT_SETS, (client, handler, buffer, responseSender) -> { // TODO convert to packet
			EquipmentSetsRegistry.decodeRegistry(buffer);
		});
	}
}
