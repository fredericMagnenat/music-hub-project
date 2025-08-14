package com.musichub.producer.domain.values;

import com.musichub.shared.domain.values.ProducerCode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProducerIdTest {

    @Test
    void sameProducerCodeYieldsSameUuid() {
        ProducerCode code1 = ProducerCode.of("FRLA1");
        ProducerCode code2 = ProducerCode.of("FRLA1");

        ProducerId id1 = ProducerId.fromProducerCode(code1);
        ProducerId id2 = ProducerId.fromProducerCode(code2);

        assertEquals(id1, id2);
        assertEquals(id1.value(), id2.value());
    }

    @Test
    void differentProducerCodesYieldDifferentUuids() {
        ProducerCode code1 = ProducerCode.of("FRLA1");
        ProducerCode code2 = ProducerCode.of("FRLA2");

        ProducerId id1 = ProducerId.fromProducerCode(code1);
        ProducerId id2 = ProducerId.fromProducerCode(code2);

        assertNotEquals(id1, id2);
    }

    @Test
    void valueCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new ProducerId(null));
    }

    @Test
    void deterministicKnownValueSnapshot() {
        ProducerCode code = ProducerCode.of("FRLA1");
        ProducerId id = ProducerId.fromProducerCode(code);
        // Snapshot derived from current implementation and namespace constant
        UUID expected = UUID.fromString("f36e54fa-ce8b-5498-9713-c231236ef2e8");
        assertEquals(expected, id.value());
    }
}
