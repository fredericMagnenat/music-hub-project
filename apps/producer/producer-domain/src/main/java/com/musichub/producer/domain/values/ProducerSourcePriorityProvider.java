package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.SourcePriorityProvider;
import com.musichub.shared.domain.values.SourceType;

import java.util.Collections;
import java.util.List;

/**
 * Producer-specific source priority implementation.
 * MANUAL > SPOTIFY > TIDAL > DEEZER > APPLE_MUSIC
 */
public class ProducerSourcePriorityProvider implements SourcePriorityProvider {

    private static final List<SourceType> PRODUCER_PRIORITY_ORDER = List.of(
            SourceType.MANUAL,
            SourceType.SPOTIFY,  // ✅ Différent d'Artist !
            SourceType.TIDAL,
            SourceType.DEEZER,
            SourceType.APPLE_MUSIC
    );

    @Override
    public boolean hasHigherPriority(SourceType source1, SourceType source2) {
        int index1 = PRODUCER_PRIORITY_ORDER.indexOf(source1);
        int index2 = PRODUCER_PRIORITY_ORDER.indexOf(source2);

        if (index1 == -1) index1 = Integer.MAX_VALUE;
        if (index2 == -1) index2 = Integer.MAX_VALUE;

        return index1 < index2;
    }

    @Override
    public List<SourceType> getPriorityOrder() {
        return Collections.unmodifiableList(PRODUCER_PRIORITY_ORDER);
    }
}