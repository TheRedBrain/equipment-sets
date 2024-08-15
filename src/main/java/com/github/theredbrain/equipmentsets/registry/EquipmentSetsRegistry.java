package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.github.theredbrain.equipmentsets.data.EquipmentSetHelper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentSetsRegistry {

	public static Map<Identifier, EquipmentSet> registeredEquipmentSets = new HashMap<>();

	public static void register(Identifier equipmentSetId, EquipmentSet equipmentSet) {
		registeredEquipmentSets.put(equipmentSetId, equipmentSet);
	}

	public static EquipmentSet getEquipmentSet(Identifier equipmentSetId) {
		return registeredEquipmentSets.get(equipmentSetId);
	}

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
			loadEquipmentSets(minecraftServer.getResourceManager());
			encodeRegistry();
		});
	}

	private static void loadEquipmentSets(ResourceManager resourceManager) {
		var gson = new Gson();
		Map<Identifier, EquipmentSet> registeredEquipmentSets = new HashMap();
		// Reading all attribute files
		for (var entry : resourceManager.findResources("equipment_sets", fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
			var identifier = entry.getKey();
			var resource = entry.getValue();
			try {
				// System.out.println("Checking resource: " + identifier);
				JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream()));
				EquipmentSet equipmentSet = EquipmentSetHelper.decode(reader);
				var id = identifier
						.toString().replace("equipment_sets/", "");
				id = id.substring(0, id.lastIndexOf('.'));
				registeredEquipmentSets.put(new Identifier(id), equipmentSet);
			} catch (Exception e) {
				System.err.println("Failed to parse: " + identifier);
				e.printStackTrace();
			}
		}
		EquipmentSetsRegistry.registeredEquipmentSets = registeredEquipmentSets;
	}

	// NETWORK SYNC

	private static PacketByteBuf encodedRegisteredEquipmentSets = PacketByteBufs.create();

	public static void encodeRegistry() {
		PacketByteBuf buffer = PacketByteBufs.create();
		var gson = new Gson();
		var json = gson.toJson(registeredEquipmentSets);
//		if (ScriptBlocksMod.serverConfig.show_debug_log) {
//			EquipmentSets.LOGGER.info("Equipment Sets registry loaded: " + json);
//		}

		List<String> chunks = new ArrayList<>();
		var chunkSize = 10000;
		for (int i = 0; i < json.length(); i += chunkSize) {
			chunks.add(json.substring(i, Math.min(json.length(), i + chunkSize)));
		}

		buffer.writeInt(chunks.size());
		for (var chunk : chunks) {
			buffer.writeString(chunk);
		}

//		if (ScriptBlocksMod.serverConfig.show_debug_log) {
//			EquipmentSets.LOGGER.info("Encoded Equipment Sets registry size (with package overhead): " + buffer.readableBytes()
//					+ " bytes (in " + chunks.size() + " string chunks with the size of " + chunkSize + ")");
//		}
		encodedRegisteredEquipmentSets = buffer;
	}

	public static void decodeRegistry(PacketByteBuf buffer) {
		var chunkCount = buffer.readInt();
		String json = "";
		for (int i = 0; i < chunkCount; ++i) {
			json = json.concat(buffer.readString());
		}
//		if (EquipmentSet.serverConfig.show_debug_log) {
//			EquipmentSets.LOGGER.info("Decoded Equipment Sets registry in " + chunkCount + " string chunks");
//			EquipmentSets.LOGGER.info("Equipment Sets registry received: " + json);
//		}
		var gson = new Gson();
		Type mapType = new TypeToken<Map<String, EquipmentSet>>() {
		}.getType();
		Map<String, EquipmentSet> readRegisteredEquipmentSets = gson.fromJson(json, mapType);
		Map<Identifier, EquipmentSet> newRegisteredEquipmentSets = new HashMap();
		readRegisteredEquipmentSets.forEach((key, value) -> {
			newRegisteredEquipmentSets.put(new Identifier(key), value);
		});
		registeredEquipmentSets = newRegisteredEquipmentSets;
	}

	public static PacketByteBuf getEncodedRegistry() {
		return encodedRegisteredEquipmentSets;
	}
}
