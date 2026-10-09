package functional_spyglass.zoom;

/**
 * Scoped mouse is vanilla spyglass {@code f} times {@code lookSpeed} times a
 * blend from {@code lookMin} (lowest zoom / highest FOV) to {@code lookMax}
 * (highest zoom / lowest FOV). {@code lookCurve} 0 stays at min for every
 * level; raising it pulls high magnification toward max.
 */
public final class ZoomSensitivity {
	private ZoomSensitivity() {
	}

	public static double scopedScale(
			double currentFov,
			double lowZoomFov,
			double highZoomFov,
			double lookCurve,
			double lookMin,
			double lookMax,
			double lookSpeed
	) {
		double min = lookMin;
		double max = lookMax;
		if (lookCurve <= 0.0) {
			return lookSpeed * min;
		}
		double t = zoomAmount(currentFov, lowZoomFov, highZoomFov);
		double shaped = Math.pow(t, lookCurve);
		return lookSpeed * (min + (max - min) * shaped);
	}

	static double zoomAmount(double currentFov, double lowZoomFov, double highZoomFov) {
		double span = lowZoomFov - highZoomFov;
		if (!(Math.abs(span) > 1.0e-9) || Double.isNaN(currentFov)) {
			return 0.0;
		}
		double t = (lowZoomFov - currentFov) / span;
		return Math.min(1.0, Math.max(0.0, t));
	}
}
