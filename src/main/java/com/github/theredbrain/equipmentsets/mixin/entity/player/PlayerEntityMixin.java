package com.github.theredbrain.equipmentsets.mixin.entity.player;

import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "equipStack", at = @At("TAIL"))
	public void equipmentsets$equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
		((CanUseEquipmentSets)this).equipmentsets$setShouldTickEquipmentSets(true);
	}
}
