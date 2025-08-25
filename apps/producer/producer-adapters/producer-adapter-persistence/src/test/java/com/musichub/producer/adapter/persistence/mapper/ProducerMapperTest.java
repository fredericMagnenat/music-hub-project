package com.musichub.producer.adapter.persistence.mapper;

import com.musichub.producer.adapter.persistence.entity.ProducerEntity;
import com.musichub.producer.adapter.persistence.entity.TrackEntity;
import com.musichub.producer.domain.model.Producer;
import com.musichub.producer.domain.model.Track;
import com.musichub.producer.domain.values.ProducerId;
import com.musichub.producer.domain.values.Source;
import com.musichub.producer.domain.values.TrackStatus;
import com.musichub.shared.domain.values.ISRC;
import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerMapper Unit Tests - OneToMany Relationship")
class ProducerMapperTest {

    @Nested
    @DisplayName("toDbo() - Domain to Entity Mapping")
    class ToDboTests {

        @Test
        @DisplayName("Should convert complete Producer to ProducerEntity with TrackEntity relationships")
        void toDbo_shouldConvertCompleteProducerWithTrackEntities() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA1");
            String producerName = "Test Label";
            
            Track track1 = Track.of(
                    ISRC.of("FRLA12400001"), 
                    "Track 1", 
                    List.of("Artist 1"),
                    List.of(Source.of("SPOTIFY", "spot-123")),
                    TrackStatus.PROVISIONAL
            );
            Track track2 = Track.of(
                    ISRC.of("FRLA12400002"), 
                    "Track 2", 
                    List.of("Artist 2"),
                    List.of(Source.of("APPLE_MUSIC", "apple-456")),
                    TrackStatus.VERIFIED
            );
            
            Set<Track> tracks = new LinkedHashSet<>();
            tracks.add(track1);
            tracks.add(track2);
            
            Producer domain = Producer.from(producerId, producerCode, producerName, tracks);

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Entity should not be null");
            assertEquals(producerId.value(), entity.id, "Producer ID should be mapped correctly");
            assertEquals("FRLA1", entity.producerCode, "Producer code should be mapped correctly");
            assertEquals("Test Label", entity.name, "Producer name should be mapped correctly");
            
            assertNotNull(entity.tracks, "Tracks should not be null");
            assertEquals(2, entity.tracks.size(), "Should have 2 track entities");
            
            // Verify track entities are properly mapped and have bidirectional relationship
            TrackEntity[] trackArray = entity.tracks.toArray(new TrackEntity[0]);
            
            TrackEntity trackEntity1 = trackArray[0];
            assertEquals("FRLA12400001", trackEntity1.getIsrc(), "First track ISRC should match");
            assertEquals("Track 1", trackEntity1.getTitle(), "First track title should match");
            assertEquals(List.of("Artist 1"), trackEntity1.getArtistNames(), "First track artists should match");
            assertEquals("PROVISIONAL", trackEntity1.getStatus(), "First track status should match");
            assertSame(entity, trackEntity1.getProducer(), "Track should reference its producer");
            
            TrackEntity trackEntity2 = trackArray[1];
            assertEquals("FRLA12400002", trackEntity2.getIsrc(), "Second track ISRC should match");
            assertEquals("Track 2", trackEntity2.getTitle(), "Second track title should match");
            assertEquals(List.of("Artist 2"), trackEntity2.getArtistNames(), "Second track artists should match");
            assertEquals("VERIFIED", trackEntity2.getStatus(), "Second track status should match");
            assertSame(entity, trackEntity2.getProducer(), "Track should reference its producer");
        }

        @Test
        @DisplayName("Should handle null Producer gracefully")
        void toDbo_shouldHandleNullProducer() {
            // When
            ProducerEntity entity = ProducerMapper.toDbo(null);

            // Then
            assertNull(entity, "Should return null for null input");
        }

        @Test
        @DisplayName("Should handle Producer with no tracks")
        void toDbo_shouldHandleProducerWithNoTracks() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA2");
            String producerName = "Empty Label";
            
            Producer domain = Producer.from(producerId, producerCode, producerName, Set.of());

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Entity should not be null");
            assertEquals(producerId.value(), entity.id, "Producer ID should be mapped correctly");
            assertEquals("FRLA2", entity.producerCode, "Producer code should be mapped correctly");
            assertEquals("Empty Label", entity.name, "Producer name should be mapped correctly");
            
            assertNotNull(entity.tracks, "Tracks should not be null");
            assertTrue(entity.tracks.isEmpty(), "Tracks should be empty");
        }

        @Test
        @DisplayName("Should handle Producer with single track")
        void toDbo_shouldHandleProducerWithSingleTrack() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA3");
            String producerName = "Single Track Label";
            
            Track singleTrack = Track.of(
                    ISRC.of("FRLA32400001"), 
                    "Only Track", 
                    List.of("Solo Artist"),
                    List.of(Source.of("MANUAL", "manual-001")),
                    TrackStatus.PROVISIONAL
            );
            
            Producer domain = Producer.from(producerId, producerCode, producerName, Set.of(singleTrack));

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertNotNull(entity.tracks, "Tracks should not be null");
            assertEquals(1, entity.tracks.size(), "Should have 1 track entity");
            
            TrackEntity trackEntity = entity.tracks.iterator().next();
            assertEquals("FRLA32400001", trackEntity.getIsrc(), "Should contain the track ISRC");
            assertEquals("Only Track", trackEntity.getTitle(), "Track title should match");
            assertSame(entity, trackEntity.getProducer(), "Track should reference its producer");
        }

        @Test
        @DisplayName("Should preserve track order in LinkedHashSet")
        void toDbo_shouldPreserveTrackOrder() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA4");
            
            Track track1 = Track.of(ISRC.of("FRLA42400001"), "Track 1", List.of("Artist"), 
                                  List.of(Source.of("SPOTIFY", "1")), TrackStatus.PROVISIONAL);
            Track track2 = Track.of(ISRC.of("FRLA42400002"), "Track 2", List.of("Artist"), 
                                  List.of(Source.of("SPOTIFY", "2")), TrackStatus.PROVISIONAL);
            Track track3 = Track.of(ISRC.of("FRLA42400003"), "Track 3", List.of("Artist"), 
                                  List.of(Source.of("SPOTIFY", "3")), TrackStatus.PROVISIONAL);
            
            Set<Track> orderedTracks = new LinkedHashSet<>();
            orderedTracks.add(track1);
            orderedTracks.add(track2);
            orderedTracks.add(track3);
            
            Producer domain = Producer.from(producerId, producerCode, "Ordered Label", orderedTracks);

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertEquals(3, entity.tracks.size(), "Should have 3 track entities");
            
            // Verify that entity.tracks is a LinkedHashSet (insertion order preserved)
            assertTrue(entity.tracks instanceof LinkedHashSet, "Should use LinkedHashSet for order preservation");
            
            TrackEntity[] trackArray = entity.tracks.toArray(new TrackEntity[0]);
            assertEquals("FRLA42400001", trackArray[0].getIsrc(), "First track should be preserved");
            assertEquals("FRLA42400002", trackArray[1].getIsrc(), "Second track should be preserved");
            assertEquals("FRLA42400003", trackArray[2].getIsrc(), "Third track should be preserved");
        }
    }

    @Nested
    @DisplayName("toDomain() - Entity to Domain Mapping")
    class ToDomainTests {

        @Test
        @DisplayName("Should convert complete ProducerEntity with TrackEntities to Producer with complete track data")
        void toDomain_shouldConvertCompleteEntityWithFullTrackData() {
            // Given
            ProducerEntity entity = new ProducerEntity();
            entity.id = UUID.randomUUID();
            entity.producerCode = "FRLA1";
            entity.name = "Test Label";
            entity.tracks = new LinkedHashSet<>();
            
            // Create complete TrackEntities (not just ISRC strings)
            TrackEntity trackEntity1 = new TrackEntity();
            trackEntity1.setId(UUID.randomUUID());
            trackEntity1.setIsrc("FRLA12400001");
            trackEntity1.setTitle("Complete Track 1");
            trackEntity1.setArtistNames(List.of("Real Artist 1", "Featuring Artist"));
            trackEntity1.setSources(List.of(
                    Source.of("SPOTIFY", "spotify-123"),
                    Source.of("APPLE_MUSIC", "apple-456")
            ));
            trackEntity1.setStatus("VERIFIED");
            trackEntity1.setProducer(entity);
            
            TrackEntity trackEntity2 = new TrackEntity();
            trackEntity2.setId(UUID.randomUUID());
            trackEntity2.setIsrc("FRLA12400002");
            trackEntity2.setTitle("Complete Track 2");
            trackEntity2.setArtistNames(List.of("Real Artist 2"));
            trackEntity2.setSources(List.of(Source.of("TIDAL", "tidal-789")));
            trackEntity2.setStatus("PROVISIONAL");
            trackEntity2.setProducer(entity);
            
            entity.tracks.add(trackEntity1);
            entity.tracks.add(trackEntity2);

            // When
            Producer domain = ProducerMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertEquals(new ProducerId(entity.id), domain.id(), "Producer ID should be converted correctly");
            assertEquals(ProducerCode.of("FRLA1"), domain.producerCode(), "Producer code should be converted correctly");
            assertEquals("Test Label", domain.name(), "Producer name should be mapped correctly");
            
            assertEquals(2, domain.tracks().size(), "Should have 2 complete tracks");
            
            // Verify complete track data is preserved (not default/reconstructed values)
            Track[] trackArray = domain.tracks().toArray(new Track[0]);
            
            Track track1 = trackArray[0];
            assertEquals(ISRC.of("FRLA12400001"), track1.isrc(), "First track ISRC should match");
            assertEquals("Complete Track 1", track1.title(), "Should preserve actual title, not 'Unknown Title'");
            assertEquals(List.of("Real Artist 1", "Featuring Artist"), track1.artistNames(), "Should preserve actual artists");
            assertEquals(TrackStatus.VERIFIED, track1.status(), "Should preserve actual status");
            assertEquals(2, track1.sources().size(), "Should have multiple sources");
            
            Track track2 = trackArray[1];
            assertEquals(ISRC.of("FRLA12400002"), track2.isrc(), "Second track ISRC should match");
            assertEquals("Complete Track 2", track2.title(), "Should preserve actual title");
            assertEquals(List.of("Real Artist 2"), track2.artistNames(), "Should preserve actual artists");
            assertEquals(TrackStatus.PROVISIONAL, track2.status(), "Should preserve actual status");
            assertEquals(1, track2.sources().size(), "Should have correct number of sources");
        }

        @Test
        @DisplayName("Should handle null ProducerEntity gracefully")
        void toDomain_shouldHandleNullEntity() {
            // When
            Producer domain = ProducerMapper.toDomain(null);

            // Then
            assertNull(domain, "Should return null for null input");
        }

        @Test
        @DisplayName("Should handle ProducerEntity with null tracks")
        void toDomain_shouldHandleEntityWithNullTracks() {
            // Given
            ProducerEntity entity = new ProducerEntity();
            entity.id = UUID.randomUUID();
            entity.producerCode = "FRLA2";
            entity.name = "No Tracks Label";
            entity.tracks = null; // Explicitly null

            // When
            Producer domain = ProducerMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertEquals(new ProducerId(entity.id), domain.id(), "Producer ID should be converted correctly");
            assertEquals(ProducerCode.of("FRLA2"), domain.producerCode(), "Producer code should be converted correctly");
            assertEquals("No Tracks Label", domain.name(), "Producer name should be mapped correctly");
            
            assertTrue(domain.tracks().isEmpty(), "Should have empty tracks set when entity tracks is null");
        }

        @Test
        @DisplayName("Should handle ProducerEntity with empty tracks")
        void toDomain_shouldHandleEntityWithEmptyTracks() {
            // Given
            ProducerEntity entity = new ProducerEntity();
            entity.id = UUID.randomUUID();
            entity.producerCode = "FRLA3";
            entity.name = "Empty Tracks Label";
            entity.tracks = new LinkedHashSet<>(); // Empty but not null

            // When
            Producer domain = ProducerMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertTrue(domain.tracks().isEmpty(), "Should have empty tracks set when entity tracks is empty");
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Tests")
    class BidirectionalTests {

        @Test
        @DisplayName("Should maintain complete data integrity in round-trip conversion")
        void roundTrip_shouldMaintainCompleteDataIntegrity() {
            // Given - Original domain object with complete track data
            ProducerId originalId = new ProducerId(UUID.randomUUID());
            ProducerCode originalCode = ProducerCode.of("FRLA1");
            String originalName = "Round Trip Label";
            
            Track track1 = Track.of(ISRC.of("FRLA12400001"), "Original Track 1", List.of("Artist 1", "Artist 2"),
                                  List.of(Source.of("SPOTIFY", "spot-123"), Source.of("TIDAL", "tidal-456")), TrackStatus.VERIFIED);
            Track track2 = Track.of(ISRC.of("FRLA12400002"), "Original Track 2", List.of("Solo Artist"),
                                  List.of(Source.of("APPLE_MUSIC", "apple-789")), TrackStatus.PROVISIONAL);
            
            Set<Track> originalTracks = new LinkedHashSet<>();
            originalTracks.add(track1);
            originalTracks.add(track2);
            
            Producer originalDomain = Producer.from(originalId, originalCode, originalName, originalTracks);

            // When - Round trip: Domain -> Entity -> Domain
            ProducerEntity entity = ProducerMapper.toDbo(originalDomain);
            Producer reconstructedDomain = ProducerMapper.toDomain(entity);

            // Then - Verify COMPLETE data preservation (not just identity and metadata)
            assertEquals(originalDomain.id(), reconstructedDomain.id(), "Producer ID should be preserved");
            assertEquals(originalDomain.producerCode(), reconstructedDomain.producerCode(), "Producer code should be preserved");
            assertEquals(originalDomain.name(), reconstructedDomain.name(), "Producer name should be preserved");
            assertEquals(originalDomain.tracks().size(), reconstructedDomain.tracks().size(), "Track count should be preserved");
            
            // Verify ALL track data is preserved (titles, artists, sources, statuses)
            Track[] originalArray = originalDomain.tracks().toArray(new Track[0]);
            Track[] reconstructedArray = reconstructedDomain.tracks().toArray(new Track[0]);
            
            for (int i = 0; i < originalArray.length; i++) {
                Track original = originalArray[i];
                Track reconstructed = reconstructedArray[i];
                
                assertEquals(original.isrc(), reconstructed.isrc(), "Track ISRC should be preserved");
                assertEquals(original.title(), reconstructed.title(), "Track title should be preserved");
                assertEquals(original.artistNames(), reconstructed.artistNames(), "Track artists should be preserved");
                assertEquals(original.sources().size(), reconstructed.sources().size(), "Track sources count should be preserved");
                assertEquals(original.status(), reconstructed.status(), "Track status should be preserved");
                
                // Verify source details
                for (int j = 0; j < original.sources().size(); j++) {
                    Source originalSource = original.sources().get(j);
                    Source reconstructedSource = reconstructed.sources().get(j);
                    assertEquals(originalSource.sourceName(), reconstructedSource.sourceName(), "Source name should be preserved");
                    assertEquals(originalSource.sourceId(), reconstructedSource.sourceId(), "Source ID should be preserved");
                }
            }
        }

        @Test
        @DisplayName("Should handle round-trip with minimal valid producer")
        void roundTrip_shouldHandleMinimalValidProducer() {
            // Given - Minimal producer
            ProducerId minimalId = new ProducerId(UUID.randomUUID());
            ProducerCode minimalCode = ProducerCode.of("FRLA2");
            String minimalName = "Minimal Label";
            
            Producer originalDomain = Producer.from(minimalId, minimalCode, minimalName, Set.of());

            // When - Round trip
            ProducerEntity entity = ProducerMapper.toDbo(originalDomain);
            Producer reconstructedDomain = ProducerMapper.toDomain(entity);

            // Then
            assertEquals(originalDomain.id(), reconstructedDomain.id(), "ID should be preserved");
            assertEquals(originalDomain.producerCode(), reconstructedDomain.producerCode(), "Code should be preserved");
            assertEquals(originalDomain.name(), reconstructedDomain.name(), "Name should be preserved");
            assertTrue(reconstructedDomain.tracks().isEmpty(), "Empty tracks should be preserved");
        }
    }

    @Nested
    @DisplayName("Bidirectional Relationship Tests")
    class BidirectionalRelationshipTests {

        @Test
        @DisplayName("Should establish correct bidirectional relationships in toDbo")
        void toDbo_shouldEstablishCorrectBidirectionalRelationships() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA1");
            
            Track track1 = Track.of(ISRC.of("FRLA12400001"), "Track 1", List.of("Artist"),
                                  List.of(Source.of("SPOTIFY", "1")), TrackStatus.PROVISIONAL);
            Track track2 = Track.of(ISRC.of("FRLA12400002"), "Track 2", List.of("Artist"),
                                  List.of(Source.of("SPOTIFY", "2")), TrackStatus.PROVISIONAL);
            
            Set<Track> tracks = Set.of(track1, track2);
            Producer domain = Producer.from(producerId, producerCode, "Test Label", tracks);

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Producer entity should not be null");
            assertEquals(2, entity.tracks.size(), "Should have 2 track entities");
            
            // Verify that all track entities have correct back-reference to producer
            for (TrackEntity trackEntity : entity.tracks) {
                assertNotNull(trackEntity.getProducer(), "Track should have producer reference");
                assertSame(entity, trackEntity.getProducer(), "Track should reference the correct producer entity");
                assertEquals(entity.id, trackEntity.getProducer().id, "Track producer ID should match");
                assertEquals(entity.producerCode, trackEntity.getProducer().producerCode, "Track producer code should match");
            }
        }

        @Test
        @DisplayName("Should handle complex track relationships correctly")
        void shouldHandleComplexTrackRelationshipsCorrectly() {
            // Given - Producer with multiple tracks with different complexities
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLAA");
            
            Track simpleTrack = Track.of(ISRC.of("FRLAA2400001"), "Simple", List.of("Artist"),
                                       List.of(Source.of("MANUAL", "manual-001")), TrackStatus.PROVISIONAL);
            
            Track complexTrack = Track.of(ISRC.of("FRLAA2400002"), "Complex Track Title", 
                                        List.of("Main Artist", "Featured Artist", "Producer Credit"),
                                        List.of(Source.of("SPOTIFY", "spot-123"), Source.of("APPLE_MUSIC", "apple-456"), Source.of("TIDAL", "tidal-789")), 
                                        TrackStatus.VERIFIED);
            
            Set<Track> tracks = new LinkedHashSet<>();
            tracks.add(simpleTrack);
            tracks.add(complexTrack);
            
            Producer domain = Producer.from(producerId, producerCode, "Complex Label", tracks);

            // When - Round trip
            ProducerEntity entity = ProducerMapper.toDbo(domain);
            Producer reconstructed = ProducerMapper.toDomain(entity);

            // Then - Verify complex data is preserved
            assertEquals(2, reconstructed.tracks().size(), "Should preserve all tracks");
            
            Track[] reconstructedArray = reconstructed.tracks().toArray(new Track[0]);
            
            // Find the complex track
            Track reconstructedComplex = null;
            for (Track track : reconstructedArray) {
                if ("Complex Track Title".equals(track.title())) {
                    reconstructedComplex = track;
                    break;
                }
            }
            
            assertNotNull(reconstructedComplex, "Complex track should be found");
            assertEquals(3, reconstructedComplex.artistNames().size(), "Should preserve all artists");
            assertEquals(3, reconstructedComplex.sources().size(), "Should preserve all sources");
            assertEquals(TrackStatus.VERIFIED, reconstructedComplex.status(), "Should preserve verified status");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle producer with null name")
        void toDbo_shouldHandleProducerWithNullName() {
            // Given
            ProducerId producerId = new ProducerId(UUID.randomUUID());
            ProducerCode producerCode = ProducerCode.of("FRLA9");
            
            Producer domain = Producer.from(producerId, producerCode, null, Set.of());

            // When
            ProducerEntity entity = ProducerMapper.toDbo(domain);

            // Then
            assertNotNull(entity, "Entity should not be null");
            assertNull(entity.name, "Entity name should be null when domain name is null");
        }

        @Test
        @DisplayName("Should handle entity with null name in toDomain")
        void toDomain_shouldHandleEntityWithNullName() {
            // Given
            ProducerEntity entity = new ProducerEntity();
            entity.id = UUID.randomUUID();
            entity.producerCode = "FRLA9";
            entity.name = null;
            entity.tracks = new LinkedHashSet<>();

            // When
            Producer domain = ProducerMapper.toDomain(entity);

            // Then
            assertNotNull(domain, "Domain should not be null");
            assertNull(domain.name(), "Domain name should be null when entity name is null");
        }
    }
}