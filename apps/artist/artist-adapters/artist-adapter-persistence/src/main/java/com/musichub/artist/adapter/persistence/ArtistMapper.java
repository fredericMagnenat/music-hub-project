package com.musichub.artist.adapter.persistence;

import com.musichub.artist.domain.model.Artist;

import com.musichub.shared.domain.id.ArtistId;
import com.musichub.shared.domain.values.ISRC;

import java.util.stream.Collectors;

// This is a simple mapper class. For more complex scenarios, a library like MapStruct could be used.
public class ArtistMapper {

    public static ArtistEntity toDbo(Artist domain) {
        ArtistEntity dbo = new ArtistEntity();
        dbo.id = domain.getId().value();
        dbo.name = domain.getName();
        dbo.status = domain.getStatus();
        dbo.trackReferences = domain.getTrackReferences().stream()
                .map(ISRC::value)
                .collect(Collectors.toSet());
        return dbo;
    }

    public static Artist toDomain(ArtistEntity dbo) {
        return Artist.from(
            new ArtistId(dbo.id),
            dbo.name,
            dbo.status,
            dbo.trackReferences.stream()
                    .map(ISRC::new)
                    .collect(Collectors.toSet())
        );
    }
}