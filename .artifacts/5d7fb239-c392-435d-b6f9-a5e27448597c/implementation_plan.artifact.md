# Fix Unresolved Reference 'OPENAI_API_KEY'

The build error `Unresolved reference 'OPENAI_API_KEY'` occurs because the `OPENAI_API_KEY` property is missing from the project configuration. The project uses the `secrets-gradle-plugin`, which expects secrets to be defined in `local.properties`.

## Proposed Changes

### Build Configuration

#### [NEW] [secrets.defaults.properties](file:///Users/marduk87/Sviluppo/MarimoCare/secrets.defaults.properties)
- Create a new file to hold default values for secrets. This ensures the build passes even if the actual secret is missing from `local.properties`.
- Add `OPENAI_API_KEY=` to this file.

#### [MODIFY] [app/build.gradle](file:///Users/marduk87/Sviluppo/MarimoCare/app/build.gradle)
- Add a `secrets` configuration block to specify `secrets.defaults.properties` as the default source for secrets.

#### [MODIFY] [local.properties](file:///Users/marduk87/Sviluppo/MarimoCare/local.properties)
- Add `OPENAI_API_KEY=` placeholder to guide the user on where to put their actual API key.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project builds successfully.

### Manual Verification
- Verify that `BuildConfig.OPENAI_API_KEY` is now resolved in `OpenAiApi.kt`.
