
package com.musichub.artist.domain.values;

import com.musichub.shared.domain.values.SourcePriorityProvider;
import com.musichub.shared.domain.values.SourceType;

import java.util.Collections;
import java.util.List;

/**
 * Artist-specific source priority implementation.
 * MANUAL > TIDAL > SPOTIFY > DEEZER > APPLE_MUSIC
 */
public class ArtistSourcePriorityProvider implements SourcePriorityProvider {
    
    private static final List<SourceType> ARTIST_PRIORITY_ORDER = List.of(
        SourceType.MANUAL,
        SourceType.TIDAL,
        SourceType.SPOTIFY,
        SourceType.DEEZER,
        SourceType.APPLE_MUSIC
    );
    
    @Override
    public boolean hasHigherPriority(SourceType source1, SourceType source2) {
        int index1 = ARTIST_PRIORITY_ORDER.indexOf(source1);
        int index2 = ARTIST_PRIORITY_ORDER.indexOf(source2);
        
        // Handle unknown sources (lowest priority)
        if (index1 == -1) index1 = Integer.MAX_VALUE;
        if (index2 == -1) index2 = Integer.MAX_VALUE;
        
        return index1 < index2;
    }
    
    @Override
    public List<SourceType> getPriorityOrder() {
        return Collections.unmodifiableList(ARTIST_PRIORITY_ORDER);
    }
}
