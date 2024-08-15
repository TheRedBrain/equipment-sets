package com.github.theredbrain.equipmentsets.mixin.screen;

import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = {"net/minecraft/screen/PlayerScreenHandler$1"})
public class PlayerScreenHandlerEquipmentSlotsMixin {

	@Shadow
	@Final
	PlayerEntity field_39410;

	@Inject(method = "setStack", at = @At("RETURN"))
	public void equipmentsets$setStack(ItemStack stack, CallbackInfo ci) {
		((CanUseEquipmentSets) (Object) this.field_39410).equipmentsets$setShouldTickEquipmentSets(true);
	}
}
