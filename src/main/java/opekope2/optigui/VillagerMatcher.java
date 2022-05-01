package opekope2.optigui;

import java.util.List;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class VillagerMatcher {
    private final Identifier profession;
    private final List<IntRange> levels;

    public VillagerMatcher(Identifier profession, List<IntRange> levels) {
        this.profession = profession;
        this.levels = levels;
    }

    public boolean matchesVillager(VillagerEntity villager) {
        VillagerData data = villager.getVillagerData();
        if (!profession.equals(Registry.VILLAGER_PROFESSION.getId(data.getProfession()))) {
            return false;
        }
        for (IntRange level : levels) {
            if (level.test(data.getLevel())) {
                return true;
            }
        }
        return false;
    }
}
