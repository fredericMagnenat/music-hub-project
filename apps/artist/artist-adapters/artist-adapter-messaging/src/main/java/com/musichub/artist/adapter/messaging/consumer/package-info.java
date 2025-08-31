/**
 * Event consuming adapters for the Artist bounded context.
 * 
 * <p>This package contains adapters responsible for consuming domain events
 * from other bounded contexts via the internal event bus (Vert.x EventBus).
 * 
 * <p>Event handlers in this package receive external events and delegate
 * to the appropriate application layer use cases for processing.
 * 
 * @see com.musichub.artist.application.ports.in.ArtistTrackRegistrationUseCase
 */
package com.musichub.artist.adapter.messaging.consumer;