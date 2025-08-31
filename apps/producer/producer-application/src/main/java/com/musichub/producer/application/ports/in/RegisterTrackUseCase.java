package com.musichub.producer.application.ports.in;

import com.musichub.producer.domain.model.Producer;

public interface RegisterTrackUseCase {
    Producer registerTrack(String isrc);
}
