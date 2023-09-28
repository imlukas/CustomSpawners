package dev.imlukas.ultraspawners.registry;

import dev.imlukas.ultraspawners.data.SpawnerData;
import dev.imlukas.ultraspawners.utils.registry.Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SpawnerDataFactory extends Factory<String, SpawnerData> {

    private final List<String> dataIdentifiers = new ArrayList<>();

    public void register(Supplier<SpawnerData> dataSupplier) {
        SpawnerData data = dataSupplier.get();

        String identifier = data.getIdentifier();
        dataIdentifiers.add(identifier);

        register(identifier, dataSupplier);
    }

    public List<String> getIdentifiers() {
        return dataIdentifiers;
    }
}
