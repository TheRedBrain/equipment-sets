package com.github.theredbrain.equipmentsets.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record EquipmentSet(
		String localization_string,
		String item_tag_string,
		boolean stack_effects,
		List<SetEffect> set_effects
) {

	public static final Codec<EquipmentSet> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("localization_string", "").forGetter(x -> x.localization_string),
			Codec.STRING.optionalFieldOf("item_tag_string", "").forGetter(x -> x.item_tag_string),
			Codec.BOOL.optionalFieldOf("stack_effects", false).forGetter(x -> x.stack_effects),
			SetEffect.CODEC.listOf().optionalFieldOf("set_effects", List.of()).forGetter(x -> x.set_effects)
	).apply(instance, EquipmentSet::new));

	public EquipmentSet(String localization_string, String item_tag_string, boolean stack_effects, List<SetEffect> set_effects) {
		this.localization_string = localization_string != null ? localization_string : "";
		this.item_tag_string = item_tag_string != null ? item_tag_string : "";
		this.stack_effects = stack_effects;
		this.set_effects = set_effects != null ? set_effects : List.of();
	}

	public record SetEffect(
			int equipped_item_threshold,
			String tooltip_text,
			String status_effect_id,
			int status_effect_level,
			boolean show_particles,
			boolean show_icon
	) {

		public static final Codec<SetEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.optionalFieldOf("equipped_item_threshold", 0).forGetter(x -> x.equipped_item_threshold),
				Codec.STRING.optionalFieldOf("tooltip_text", "").forGetter(x -> x.tooltip_text),
				Codec.STRING.optionalFieldOf("status_effect_id", "").forGetter(x -> x.status_effect_id),
				Codec.INT.optionalFieldOf("status_effect_level", 0).forGetter(x -> x.status_effect_level),
				Codec.BOOL.optionalFieldOf("show_particles", false).forGetter(x -> x.show_particles),
				Codec.BOOL.optionalFieldOf("show_icon", false).forGetter(x -> x.show_icon)
		).apply(instance, SetEffect::new));

		public SetEffect(
				int equipped_item_threshold,
				String tooltip_text,
				String status_effect_id,
				int status_effect_level,
				boolean show_particles,
				boolean show_icon
		) {
			this.equipped_item_threshold = equipped_item_threshold;
			this.tooltip_text = tooltip_text != null ? tooltip_text : "";
			this.status_effect_id = status_effect_id != null ? status_effect_id : "";
			this.status_effect_level = status_effect_level;
			this.show_particles = show_particles;
			this.show_icon = show_icon;
		}
	}
}
