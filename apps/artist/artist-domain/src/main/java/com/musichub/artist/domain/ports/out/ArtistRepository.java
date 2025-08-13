package com.musichub.artist.domain.ports.out;

import com.musichub.artist.domain.Artist;

import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findByName(String name);
    void save(Artist artist);
}
