package com.github.theredbrain.equipmentsets.mixin.entity.mob;

import com.github.theredbrain.equipmentsets.entity.CanUseEquipmentSets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {


	protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "equipStack", at = @At("TAIL"))
	public void equipmentsets$equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
		((CanUseEquipmentSets) this).equipmentsets$setShouldTickEquipmentSets(true);
	}
}
