# Implementation Plan - Add Restyling Migration Balloon

Add a new informative balloon to the `DashboardFragment` that notifies users about the ongoing graphical restyling. This balloon will appear at the end of the existing balloon sequence and only once if the user hasn't seen it yet.

## Proposed Changes

### Build Configuration & Utils

#### [MODIFY] [AppUtils.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/utils/AppUtils.kt)
- Add `SHOW_MIGRATION_BALLOON` constant with value `"is_show_migration_ui"`.

#### [MODIFY] [SharedPreferencesManager.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/utils/managers/SharedPreferencesManager.kt)
- Add `isShowMigrationUI` property to manage the visibility of the new balloon.

### Resources

#### [MODIFY] [strings.xml](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/res/values/strings.xml)
- Add `migration_balloon_text`: "The app is undergoing a graphical redesign (new technology) and for now this will concern the Settings and Feedback page.".

### UI Components

#### [NEW] [MigrationBalloonFactory.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/utils/balloon/migration/MigrationBalloonFactory.kt)
- Create a new balloon factory for the migration announcement.

#### [MODIFY] [DashboardFragment.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/ui/dashboard/DashboardFragment.kt)
- Declare and initialize `migrationBalloon` using the new factory.
- Update `checkAndShowBalloons()` to include the migration balloon at the end of the logic chain, checking `SharedPreferencesManager.isShowMigrationUI`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project builds correctly.

### Manual Verification
- Ensure `is_show_migration_ui` is set to `false` (default).
- Open the app and check the Dashboard.
- Verify the new balloon appears after all other balloons (if any).
- Verify the balloon is anchored appropriately (e.g., to the settings/feedback area).
- Dismiss the balloon and verify it doesn't reappear on subsequent launches.
