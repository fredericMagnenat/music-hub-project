// Dans apps/bootstrap/src/test/java/com/musichub/integration/
@QuarkusTest
@TestTransaction
@DisplayName("Producer Registration Integration Tests")
class ProducerRegistrationIntegrationTest {

    @Test
    @DisplayName("Should register track successfully end-to-end")
    void shouldRegisterTrackEndToEnd() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"isrc\":\"FRLA12400001\"}")
        .when()
            .post("/api/v1/producers")
        .then()
            .statusCode(202)
            .body("producerCode.value", equalTo("FRLA1"));
    }
}
