package functional_spyglass.zoom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZoomSensitivityTest {

	@Test
	void curveZeroStaysAtMin() {
		assertEquals(0.7, ZoomSensitivity.scopedScale(50.0, 50.0, 6.0, 0.0, 0.7, 1.4, 1.0), 1e-9);
		assertEquals(0.7, ZoomSensitivity.scopedScale(6.0, 50.0, 6.0, 0.0, 0.7, 1.4, 1.0), 1e-9);
		assertEquals(0.56, ZoomSensitivity.scopedScale(6.0, 50.0, 6.0, 0.0, 0.7, 1.4, 0.8), 1e-9);
	}

	@Test
	void lowZoomUsesMin() {
		assertEquals(0.7, ZoomSensitivity.scopedScale(50.0, 50.0, 6.0, 0.5, 0.7, 1.4, 1.0), 1e-9);
		assertEquals(0.7, ZoomSensitivity.scopedScale(50.0, 50.0, 6.0, 1.0, 0.7, 1.4, 1.0), 1e-9);
	}

	@Test
	void highZoomUsesMaxWhenCurveIsOne() {
		assertEquals(1.4, ZoomSensitivity.scopedScale(6.0, 50.0, 6.0, 1.0, 0.7, 1.4, 1.0), 1e-9);
		assertTrue(ZoomSensitivity.scopedScale(6.0, 50.0, 6.0, 1.0, 0.7, 1.4, 1.0)
				> ZoomSensitivity.scopedScale(50.0, 50.0, 6.0, 1.0, 0.7, 1.4, 1.0));
	}

	@Test
	void midZoomIsBetweenMinAndMax() {
		double linear = ZoomSensitivity.scopedScale(16.0, 50.0, 6.0, 1.0, 0.7, 1.4, 1.0);
		assertTrue(linear > 0.7);
		assertTrue(linear < 1.4);
		double sqrt = ZoomSensitivity.scopedScale(16.0, 50.0, 6.0, 0.5, 0.7, 1.4, 1.0);
		assertTrue(sqrt > linear);
		assertTrue(sqrt < 1.4);
	}
}
