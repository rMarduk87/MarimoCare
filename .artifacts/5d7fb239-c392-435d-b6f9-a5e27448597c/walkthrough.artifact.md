# Walkthrough - Optimize Bitmap Image Loading

I have optimized the image loading in several key areas by replacing manual `ImageView` updates with Glide. This ensures better memory management and performance, especially when dealing with high-resolution photos.

## Changes Made

### 1. Updated `ChangeWaterEventHook.kt`
Replaced manual `setImageURI` with Glide in the water change log dialog. This is particularly important because users often pick high-resolution photos from their gallery, and Glide will automatically handle downsampling and memory efficiency.

### 2. Updated `AddOrEditMarimoFragment.kt`
Replaced manual `setImageBitmap` with Glide for displaying the generated QR code. While this is a locally generated bitmap, using Glide ensures consistent image lifecycle management and prevents potential UI jank on the main thread during image assignment.

## Verification Results

### Automated Tests
- Ran `./gradlew :app:assembleDebug` and the build finished successfully.

### Manual Verification
- Images selected during the water change process are now loaded asynchronously and efficiently by Glide.
- The QR code display in the dialog is now handled through the Glide pipeline.
