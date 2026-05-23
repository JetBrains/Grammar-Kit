# Verify Tests

Run `./gradlew test` (or the affected module's test task). Report the exact pass count from output. If any test fails, do not claim
success — list the failures and propose fixes. If golden files need regeneration, ensure OVERWRITE_TESTDATA is forwarded to the test JVM.
