package com.github.theredbrain.equipmentsets.network.packet;

import com.github.theredbrain.equipmentsets.EquipmentSets;
import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record EquipmentSetsSyncPacket(Map<Identifier, EquipmentSet> registeredEquipmentSets) implements CustomPayload {
	public static final CustomPayload.Id<EquipmentSetsSyncPacket> PACKET_ID = new CustomPayload.Id<>(EquipmentSets.identifier("equipment_sets_sync"));
	public static final PacketCodec<RegistryByteBuf, EquipmentSetsSyncPacket> PACKET_CODEC = PacketCodec.of(EquipmentSetsSyncPacket::write, EquipmentSetsSyncPacket::read);

	public static EquipmentSetsSyncPacket read(RegistryByteBuf registryByteBuf) {
		Map<Identifier, EquipmentSet> newEquipmentSets = new HashMap<>();
		int i = registryByteBuf.readInt();
		for (int j = 0; j < i; j++) {
			Identifier identifier = registryByteBuf.readIdentifier();
			EquipmentSet equipmentSet = registryByteBuf.decodeAsJson(EquipmentSet.CODEC);
			newEquipmentSets.put(identifier, equipmentSet);
		}
		return new EquipmentSetsSyncPacket(newEquipmentSets);
	}

	private void write(RegistryByteBuf registryByteBuf) {
		registryByteBuf.writeInt(registeredEquipmentSets.size());
		for (var entry : registeredEquipmentSets.entrySet()) {
			registryByteBuf.writeIdentifier(entry.getKey());
			registryByteBuf.encodeAsJson(EquipmentSet.CODEC, entry.getValue());
		}
	}

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return PACKET_ID;
	}
}