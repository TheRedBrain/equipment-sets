package com.github.theredbrain.equipmentsets.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.lang.reflect.Type;

public class EquipmentSetHelper {

	private static final Type registeredEquipmentSetsFileFormat = new TypeToken<EquipmentSet>() {}.getType();

	public static EquipmentSet decode(Reader reader) {
		var gson = new Gson();
		EquipmentSet equipmentSet = gson.fromJson(reader, registeredEquipmentSetsFileFormat);
		return equipmentSet;
	}

	public static EquipmentSet decode(JsonReader json) {
		return new Gson().fromJson(json, registeredEquipmentSetsFileFormat);
	}

	public static String encode(EquipmentSet equipmentSet) {
		var gson = new Gson();
		return gson.toJson(equipmentSet);
	}
}
