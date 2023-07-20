package ewewukek.musketmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CleaningRodItem extends Item implements DispensibleContainerItem {

    public CleaningRodItem(Properties properties) {
        super(properties);
    }
/*    @Override
    public final Item getCraftingRemainingItem() {
        return this;
    }*/

    public boolean emptyContents(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        return false;
    }

}