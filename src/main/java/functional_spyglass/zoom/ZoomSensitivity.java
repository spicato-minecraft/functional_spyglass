package functional_spyglass.zoom;

/**
 * Scoped mouse is vanilla spyglass {@code f} times
 * {@code lookSpeed * (currentFov / vanillaSpyglassFov) ^ lookCurve}.
 * Curve 0 keeps yaw constant; 1 matches unaided pan; 0.5 is sqrt.
 */
public final class ZoomSensitivity {
	public static final double VANILLA_SPYGLASS_FOV_SCALE = 0.1;

	private ZoomSensitivity() {
	}

	public static double scopedScale(double currentFov, double unaidedFov, double lookCurve, double lookSpeed) {
		if (!(unaidedFov > 0.0) || !(currentFov > 0.0)) {
			return lookSpeed;
		}
		double vanillaScopedFov = unaidedFov * VANILLA_SPYGLASS_FOV_SCALE;
		double ratio = currentFov / vanillaScopedFov;
		if (!(ratio > 0.0)) {
			return lookSpeed;
		}
		return lookSpeed * Math.pow(ratio, lookCurve);
	}
}
