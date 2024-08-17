package com.github.theredbrain.equipmentsets.registry;

import com.github.theredbrain.equipmentsets.data.EquipmentSet;
import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			addEquipmentSetTooltips(stack, context, lines);
		});
	}

	private static void addEquipmentSetTooltips(ItemStack stack, TooltipContext context, List<Text> lines) {
		ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
		if (clientPlayer == null) {
			return;
		}
		Map<String, Integer> equipmentSetCounters = new HashMap<>(((CanUseEquipmentSets) clientPlayer).equipmentsets$getEquipmentSetCounters());
		KeyBinding keybinding = MinecraftClient.getInstance().options.sneakKey;
		boolean showDetails =
				/*config.alwaysShowFullTooltip
				|| */
				(!keybinding.isUnbound() && InputUtil.isKeyPressed(
				MinecraftClient.getInstance().getWindow().getHandle(),
				((KeyBindingAccessor) keybinding).fabric_getBoundKey().getCode()) // FIXME uses internal api package
		);

		Set<Identifier> identifierKeys = EquipmentSetsRegistry.registeredEquipmentSets.keySet();
		EquipmentSet equipmentSet;
		for (Identifier key : identifierKeys) {
			equipmentSet = EquipmentSetsRegistry.registeredEquipmentSets.get(key);
			TagKey<Item> itemTag = TagKey.of(RegistryKeys.ITEM, new Identifier(equipmentSet.itemTagString()));
			if (stack.isIn(itemTag)) {
				lines.add(Text.empty());
				lines.add(Text.translatable(equipmentSet.localizationString()).formatted(Formatting.GREEN));
				if (showDetails) {
					for (EquipmentSet.SetEffect setEffect : equipmentSet.setEffects()) {
						if (setEffect.equippedItemThreshold() <= equipmentSetCounters.getOrDefault(key.toString(), 0)) {
							lines.add(Text.literal(" ").append(Text.translatable(setEffect.toolTipText()).formatted(Formatting.GREEN)));
						} else {
							lines.add(Text.literal(" ").append(Text.translatable(setEffect.toolTipText()).formatted(Formatting.GRAY)));
						}
					}
				} else {
					lines.add(Text.translatable("equipmentsets.tooltip.detail_hint", keybinding.getBoundKeyLocalizedText()).formatted(Formatting.GRAY));
				}
			}
		}
	}
}
