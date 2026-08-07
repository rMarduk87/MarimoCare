# Optimize Bitmap Image Loading

The app has some locations where images are loaded manually using `setImageURI` or `setImageBitmap`, which can lead to performance issues and excessive memory usage. This plan replaces these manual operations with Glide to take advantage of its automatic caching, downsampling, and memory management.

## Proposed Changes

### [Component Name] [Image Loading Optimization]

#### [MODIFY] [ChangeWaterEventHook.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/utils/view/recyclerview/items/marimo/hooks/ChangeWaterEventHook.kt)
- Replace `imagePreview?.setImageURI(it.toUri())` with `Glide.with(context).load(it.toUri()).into(imagePreview)`.
- This ensures that images picked from the gallery are loaded efficiently and cached.

#### [MODIFY] [AddOrEditMarimoFragment.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/ui/marimo/addoredit/AddOrEditMarimoFragment.kt)
- Replace `icon.setImageBitmap(qrCode)` with `Glide.with(requireContext()).load(qrCode).into(icon)` in `showMarimoQR`.
- Even for locally generated bitmaps, Glide provides better management of the image lifecycle and memory.

### [Component Name] [General Cleanup]

#### [MODIFY] [Extensions.kt](file:///Users/marduk87/Sviluppo/MarimoCare/app/src/main/java/rpt/tool/marimocare/utils/view/Extensions.kt)
- Ensure `loadMarimoImage` is used consistently across the app. (It is already mostly consistent).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to ensure the project still builds.

### Manual Verification
- Verify that images picked for water changes are displayed correctly.
- Verify that the QR code is displayed correctly in the dialog.
- Observe app performance and memory usage during image-heavy operations (e.g., browsing the care timeline).
