
package com.musichub.shared.domain.values;

import java.util.List;

/**
 * Strategy pattern for source priority determination.
 * Each bounded context can implement its own priority logic.
 */
public interface SourcePriorityProvider {
    
    /**
     * Determines if the first source has higher priority than the second.
     */
    boolean hasHigherPriority(SourceType source1, SourceType source2);
    
    /**
     * Gets the priority order for this context.
     * Lower index = higher priority.
     */
    List<SourceType> getPriorityOrder();
}
