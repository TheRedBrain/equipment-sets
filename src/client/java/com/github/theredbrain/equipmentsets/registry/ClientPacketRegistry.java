package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.network.packet.EquipmentSetsSyncPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(value = EnvType.CLIENT)
public class ClientPacketRegistry {

	public static void init() {

		ClientPlayNetworking.registerGlobalReceiver(EquipmentSetsSyncPacket.PACKET_ID, (payload, context) -> {
			EquipmentSetsRegistry.registeredEquipmentSets = payload.registeredEquipmentSets();
		});
	}
}
