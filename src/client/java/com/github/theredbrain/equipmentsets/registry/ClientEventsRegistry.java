package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.EquipmentSetsClient;
import com.github.theredbrain.equipmentsets.config.ClientConfig;
import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientEventsRegistry {

	public static void initializeClientEvents() {
		ItemTooltipCallback.EVENT.register(ClientEventsRegistry::addEquipmentSetTooltips);
	}

	private static void addEquipmentSetTooltips(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		ClientConfig.GeneralClientConfig clientConfig = EquipmentSetsClient.clientConfigHolder.getConfig().generalClientConfig;
		if (clientPlayer == null) {
			return;
		}
		Map<String, Integer> equipmentSetCounters = new HashMap<>(((CanUseEquipmentSets) clientPlayer).equipmentsets$getEquipmentSetCounters());
		KeyBinding keybinding = MinecraftClient.getInstance().options.sneakKey;
		boolean showDetails =
				clientConfig.always_show_full_equipment_Set_tooltips || (!keybinding.isUnbound() &&
						InputUtil.isKeyPressed(
								MinecraftClient.getInstance().getWindow().getHandle(),
								((KeyBindingAccessor) keybinding).fabric_getBoundKey().getCode()
						) // FIXME uses internal api package
				);

		Set<Identifier> identifierKeys = EquipmentSetsRegistry.registeredEquipmentSets.keySet();
		EquipmentSet equipmentSet;
		for (Identifier key : identifierKeys) {
			equipmentSet = EquipmentSetsRegistry.registeredEquipmentSets.get(key);
			TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, Identifier.of(equipmentSet.item_tag_string()));
			String equipmentSetLocalizationString = equipmentSet.localization_string();
			if (stack.isIn(itemTag) && !equipmentSetLocalizationString.isEmpty()) {
				lines.add(Text.empty());
				lines.add(Text.translatable(equipmentSetLocalizationString).formatted(Formatting.GREEN));
				if (showDetails) {
					for (EquipmentSet.SetEffect setEffect : equipmentSet.set_effects()) {
						String setEffectTooltipText = setEffect.tooltip_text();
						if (!setEffectTooltipText.isEmpty()) {
							if (setEffect.equipped_item_threshold() <= equipmentSetCounters.getOrDefault(key.toString(), 0)) {
								lines.add(Text.literal(" ").append(Text.translatable(setEffectTooltipText).formatted(Formatting.GREEN)));
							} else {
								lines.add(Text.literal(" ").append(Text.translatable(setEffectTooltipText).formatted(Formatting.GRAY)));
							}
						}
					}
				} else {
					lines.add(Text.translatable("equipmentsets.tooltip.detail_hint", keybinding.getBoundKeyLocalizedText()).formatted(Formatting.GRAY));
				}
			}
		}
	}
}
