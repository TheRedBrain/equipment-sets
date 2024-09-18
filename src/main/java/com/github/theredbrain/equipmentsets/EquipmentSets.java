package com.github.theredbrain.equipmentsets;

import com.github.theredbrain.equipmentsets.registry.EquipmentSetsRegistry;
import com.github.theredbrain.equipmentsets.registry.EventsRegistry;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Predicate;

public class EquipmentSets implements ModInitializer {
	public static final String MOD_ID = "equipmentsets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final boolean isTrinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");

	public static int getEquippedTrinketsAmount(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
		if (isTrinketsLoaded) {
			Optional<TrinketComponent> trinkets = TrinketsApi.getTrinketComponent(livingEntity);
			if (trinkets.isPresent()) {
				return trinkets.get().getEquipped(predicate).size();
			}
		}
		return 0;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Equipment comes in sets now!");

		// Registry
		EquipmentSetsRegistry.init();
		EventsRegistry.initializeEvents();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}