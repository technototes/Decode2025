package com.technototes.library.subsystem;

// This should be promoted to a library interface, so it's available for anything to use/implement
public interface TargetAcquisition {
    // This should be in *inches*
    double getDistance();
    // Returns the location, in degrees, of the target relative to the current bot perspective
    // Clockwise is negative, Counter-clockwise is positive
    double getHorizontalPosition();
    // Returns the location, in degrees, of the target relative to the current bot perspective.
    // "Above perpsective" is positive, "Below perspective" is negative
    double getVerticalPosition();
}
