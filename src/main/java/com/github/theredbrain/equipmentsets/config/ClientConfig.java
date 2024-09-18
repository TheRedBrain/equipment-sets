package com.github.theredbrain.equipmentsets.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(
		name = "equipmentsets"
)
public class ClientConfig extends PartitioningSerializer.GlobalData {
	@ConfigEntry.Category("generalClientConfig")
	@ConfigEntry.Gui.TransitiveObject
	public GeneralClientConfig generalClientConfig = new GeneralClientConfig();

	public ClientConfig() {
	}

	@Config(
			name = "generalClientConfig"
	)
	public static class GeneralClientConfig implements ConfigData {

		public boolean always_show_full_equipment_Set_tooltips = false;

		public GeneralClientConfig() {
		}

	}
}
