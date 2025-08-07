package com.musichub.artist.domain;

import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findByName(String name);
    void save(Artist artist);
}
