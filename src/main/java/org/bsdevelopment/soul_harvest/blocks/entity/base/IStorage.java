package org.bsdevelopment.soul_harvest.blocks.entity.base;

import org.bsdevelopment.soul_harvest.utilities.storage.BulkStorage;

public interface IStorage {
    BulkStorage getBulkStorage();

    void saveStorage();
}
