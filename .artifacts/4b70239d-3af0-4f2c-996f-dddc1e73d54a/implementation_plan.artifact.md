# Gradient Style for Epidemic Event Button

The user wants to style the "Epidemic Event" button with a specific color gradient using four hex values: #0b7519, #57a60f, #021e0f, and #042618.

## User Review Required

> [!NOTE]
> I will apply the gradient to the background of the "Epidemic Event" button. To do this effectively in Compose, I will:
> 1. Define a `Brush` using the provided hex colors.
> 2. Use a `Button` with a transparent container color and apply the `Modifier.background(brush)` to give it the gradient look.
> 3. Ensure the text color is appropriate for the new background (likely white or high-contrast).

## Proposed Changes

### [app]

#### [MODIFY] [App.kt](file:///C:/Users/Joshua/StudioProjects/pandemic-infection-tracker/app/src/commonMain/kotlin/com/pandemic/infectiontracker/App.kt)
- Update the "Epidemic Event" button implementation.
- Replace `FilledTonalButton` with a custom-styled `Button` or `Surface` wrapper to accommodate the linear gradient.
- Add necessary imports for `Brush` and `Color`.

## Verification Plan

### Automated Tests
- Build the project using `gradle_build` to ensure no syntax errors.

### Manual Verification
- Render the `AppPreview` to visually verify the gradient on the button.
- Deploy the app to a device if available to check the button's appearance in a real environment.
