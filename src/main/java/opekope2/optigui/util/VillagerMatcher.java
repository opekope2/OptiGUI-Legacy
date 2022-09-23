package opekope2.optigui.util;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.predicate.NumberRange.IntRange;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public final class VillagerMatcher {
    private static final Identifier wanderingTraderProfession = new Identifier("minecraft", "_wandering_trader");

    private final Identifier profession;
    private final Iterable<IntRange> levels;

    public VillagerMatcher(Identifier profession, Iterable<IntRange> levels) {
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

    public boolean matchesWanderingTrader(WanderingTraderEntity trader) {
        return wanderingTraderProfession.equals(profession);
    }
}
