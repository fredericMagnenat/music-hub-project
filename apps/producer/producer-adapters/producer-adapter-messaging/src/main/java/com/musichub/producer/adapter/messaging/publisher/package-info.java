/**
 * Event publishing adapters for the Producer bounded context.
 * 
 * <p>This package contains adapters responsible for publishing domain events
 * from the Producer context to the internal event bus (Vert.x EventBus).
 * 
 * <p>Classes in this package implement outbound ports from the application layer
 * and handle the technical details of event publication.
 * 
 * @see com.musichub.producer.application.ports.out.EventPublisherPort
 */
package com.musichub.producer.adapter.messaging.publisher;