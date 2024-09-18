package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.EquipmentSets;
import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EquipmentSetsRegistry {

	public static Map<Identifier, EquipmentSet> registeredEquipmentSets = new HashMap<>();
	private static final Type registeredEquipmentSetsFileFormat = new TypeToken<EquipmentSet>() {}.getType();

	public static void init() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public Identifier getFabricId() {
						return EquipmentSets.identifier("equipment_sets");
					}

					@Override
					public void reload(ResourceManager resourceManager) {
						registeredEquipmentSets = new HashMap<>();
						for (var entry : resourceManager.findResources("equipment_sets", fileName -> fileName.getPath().endsWith(".json")).entrySet()) {
							var identifier = entry.getKey();
							var resource = entry.getValue();
							try {
								JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream()));
								EquipmentSet equipmentSet = new Gson().fromJson(reader, registeredEquipmentSetsFileFormat);
								var id = identifier
										.toString().replace("equipment_sets/", "");
								id = id.substring(0, id.lastIndexOf('.'));
								registeredEquipmentSets.put(Identifier.of(id), equipmentSet);
							} catch (Exception e) {
								System.err.println("Failed to parse: " + identifier);
								e.printStackTrace();
							}
						}
					}
				}
		);
	}
}
