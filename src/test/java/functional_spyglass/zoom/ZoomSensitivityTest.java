package functional_spyglass.zoom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoomSensitivityTest {

	@Test
	void curveZeroIgnoresFov() {
		assertEquals(1.0, ZoomSensitivity.scopedScale(50.0, 70.0, 0.0, 1.0), 1e-9);
		assertEquals(0.8, ZoomSensitivity.scopedScale(50.0, 70.0, 0.0, 0.8), 1e-9);
	}

	@Test
	void curveHalfIsSqrt() {
		assertEquals(Math.sqrt(50.0 / 7.0), ZoomSensitivity.scopedScale(50.0, 70.0, 0.5, 1.0), 1e-9);
	}

	@Test
	void curveOneScalesLinearlyWithFov() {
		assertEquals(50.0 / 7.0, ZoomSensitivity.scopedScale(50.0, 70.0, 1.0, 1.0), 1e-9);
	}

	@Test
	void vanillaSpyglassFovStaysAtLookSpeed() {
		assertEquals(1.0, ZoomSensitivity.scopedScale(7.0, 70.0, 0.5, 1.0), 1e-9);
		assertEquals(0.9, ZoomSensitivity.scopedScale(7.0, 70.0, 0.5, 0.9), 1e-9);
	}
}
