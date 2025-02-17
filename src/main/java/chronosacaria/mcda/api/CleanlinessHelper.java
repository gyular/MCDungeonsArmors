package chronosacaria.mcda.api;

import chronosacaria.mcda.Mcda;
import chronosacaria.mcda.items.ArmorSets;
import chronosacaria.mcda.registry.ArmorsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class CleanlinessHelper {
    static Random random = new Random();

    public static boolean hasArmorSet(LivingEntity livingEntity, ArmorSets armorSets){
        if (Mcda.CONFIG.mcdaEnableArmorsConfig.ARMORS_SETS_ENABLED.get(armorSets)) {
            ItemStack headStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);
            ItemStack footStack = livingEntity.getEquippedStack(EquipmentSlot.FEET);

            return headStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.HEAD).asItem()
                    && chestStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.CHEST).asItem()
                    && legStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.LEGS).asItem()
                    && footStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.FEET).asItem();
        }
        return false;
    }

    public static boolean hasRobeSet(LivingEntity livingEntity, ArmorSets armorSets){
        if (Mcda.CONFIG.mcdaEnableArmorsConfig.ARMORS_SETS_ENABLED.get(armorSets)) {
            ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);


            return chestStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.CHEST).asItem()
                    && legStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.LEGS).asItem();
        }
        return false;
    }

    public static boolean hasRobeWithHatSet(LivingEntity livingEntity, ArmorSets armorSets){
        if (Mcda.CONFIG.mcdaEnableArmorsConfig.ARMORS_SETS_ENABLED.get(armorSets)) {
            ItemStack headStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
            ItemStack legStack = livingEntity.getEquippedStack(EquipmentSlot.LEGS);

            return headStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.HEAD).asItem()
                    && chestStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.CHEST).asItem()
                    && legStack.getItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.LEGS).asItem();
        }
        return false;
    }

    public static boolean isMysteryArmor(Item item, ArmorSets armorSets) {
        if (Mcda.CONFIG.mcdaEnableArmorsConfig.ARMORS_SETS_ENABLED.get(armorSets)) {
            return item.asItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.HEAD)
                    || item.asItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.CHEST)
                    || item.asItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.LEGS)
                    || item.asItem() == ArmorsRegistry.armorItems.get(armorSets).get(EquipmentSlot.FEET);
        }
        return false;
    }

    public static void onTotemDeathEffects(LivingEntity livingEntity) {
        livingEntity.setHealth(1.0F);
        livingEntity.clearStatusEffects();
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 900, 1));
        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
        livingEntity.world.sendEntityStatus(livingEntity, (byte) 35);
    }

    public static int mcdaIndexOfLargestElementInArray(int[] arr) {
        int maxVal = arr[0];
        int index = 0;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxVal) {
                maxVal = arr[i];
                index = i;
            }
        }
        return index;
    }

    public static int mcdaFindHighestDurabilityEquipment(LivingEntity livingEntity) {
        int i = 0;
        int[] armorPieceDurability = {0, 0, 0, 0};

        // Store durability percents of armor
        for (ItemStack itemStack : livingEntity.getArmorItems()) {
            float k = itemStack.getMaxDamage();
            float j = k - itemStack.getDamage();
            armorPieceDurability[i] = (int) ((j / k) * 100);
            i++;
        }

        // Find the highest durability armor's index
        return mcdaIndexOfLargestElementInArray(armorPieceDurability);
    }

    public static void mcdaRandomArmorDamage(LivingEntity livingEntity, float damagePercentage, int totalNumOfPieces, boolean missingBoots){
        Random random = new Random();
        int index = random.nextInt(totalNumOfPieces);
        if (missingBoots)
            index++;

        EquipmentSlot equipment = switch (index){
            case 0 -> EquipmentSlot.FEET;
            case 1 -> EquipmentSlot.LEGS;
            case 2 -> EquipmentSlot.CHEST;
            case 3 -> EquipmentSlot.HEAD;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
        mcdaDamageEquipment(livingEntity, equipment, damagePercentage);
    }

    public static void mcdaDamageEquipment(LivingEntity livingEntity, EquipmentSlot equipSlot, float damagePercentage) {
        ItemStack armorStack = livingEntity.getEquippedStack(equipSlot);
        int k = armorStack.getMaxDamage();
        int j = k - armorStack.getDamage();
        // Necessary for proper types.
        int breakDamage = (int) (k * damagePercentage);
        boolean toBreak = j <= breakDamage;

        if (toBreak)
            armorStack.damage(j, livingEntity,
                    (entity) -> entity.sendEquipmentBreakStatus(equipSlot));
        else
            armorStack.damage(breakDamage, livingEntity,
                    (entity) -> entity.sendEquipmentBreakStatus(equipSlot));
    }

    public static boolean mcdaCooldownCheck(LivingEntity livingEntity, int ticks){
        ItemStack chestStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);

        long currentTime = livingEntity.world.getTime();

        if (!chestStack.getOrCreateNbt().contains("time-check")) {
            chestStack.getOrCreateNbt().putLong("time-check", currentTime);
            return false;
        }
        long storedTime = chestStack.getOrCreateNbt().getLong("time-check");

        return Math.abs(currentTime - storedTime) > ticks;
    }

    public static boolean mcdaBoundingBox(PlayerEntity playerEntity, float boxSize) {
        return playerEntity.world.getBlockCollisions(playerEntity,
                playerEntity.getBoundingBox().offset(boxSize * playerEntity.getBoundingBox().getXLength(), 0,
                        boxSize * playerEntity.getBoundingBox().getZLength())).iterator().hasNext();
    }

    public static boolean mcdaCanTargetEntity(PlayerEntity playerEntity, Entity target){
        Vec3d playerVec = playerEntity.getRotationVec(0f);
        Vec3d vecHorTargetDist = new Vec3d((target.getX() - playerEntity.getX()),
                (target.getY() - playerEntity.getY()),(target.getZ() - playerEntity.getZ()));
        double horTargetDist = vecHorTargetDist.horizontalLength();
        Vec3d perpHorTargetDist =
                vecHorTargetDist.normalize().crossProduct(new Vec3d(0, 1, 0));
        Vec3d leftSideVec =
                vecHorTargetDist.normalize().multiply(horTargetDist)
                        .add(perpHorTargetDist.multiply(target.getWidth()));
        Vec3d rightSideVec =
                vecHorTargetDist.normalize().multiply(horTargetDist)
                        .add(perpHorTargetDist.multiply(-target.getWidth()));
        double playerEyeHeight = playerEntity.getEyeY() - playerEntity.getBlockPos().getY();

                return Math.max(leftSideVec.normalize().x, rightSideVec.normalize().x) >= playerVec.normalize().x
                && Math.min(leftSideVec.normalize().x, rightSideVec.normalize().x) <= playerVec.normalize().x
                && Math.max(leftSideVec.normalize().z, rightSideVec.normalize().z) >= playerVec.normalize().z
                && Math.min(leftSideVec.normalize().z, rightSideVec.normalize().z) <= playerVec.normalize().z
                && playerVec.y > -Math.atan(playerEyeHeight / horTargetDist)
                && playerVec.y < Math.atan((target.getHeight() - playerEyeHeight) / horTargetDist);
    }

    public static boolean mcdaCheckHorizontalVelocity(Vec3d vec3d, double magnitude, boolean equality) {
        double horVelocity = vec3d.horizontalLength();
        if (equality)
            return horVelocity == magnitude;
        return horVelocity > magnitude;
    }

    public static boolean percentToOccur (int chance) {
        return random.nextInt(100) <= chance;
    }

    public static void playCenteredSound (LivingEntity center, SoundEvent soundEvent, float volume, float pitch) {
        center.world.playSound(null,
                center.getX(), center.getY(), center.getZ(),
                soundEvent, SoundCategory.PLAYERS,
                volume, pitch);
    }

    public static void mcda$dropItem(LivingEntity le, Item item) {
        mcda$dropItem(le, item, 1);
    }

    public static void mcda$dropItem(LivingEntity le, ItemStack itemStack) {
        ItemEntity it = new ItemEntity(
                le.world, le.getX(), le.getY(), le.getZ(),
                itemStack);
        le.world.spawnEntity(it);
    }

    public static void mcda$dropItem(LivingEntity le, Item item, int amount) {
        mcda$dropItem(le, new ItemStack(item, amount));
    }
}
