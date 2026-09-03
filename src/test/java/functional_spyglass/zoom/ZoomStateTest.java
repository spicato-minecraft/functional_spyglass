package functional_spyglass.zoom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoomStateTest {

	@Test
	void cycleWrapsThreeLevels() {
		ZoomState state = new ZoomState();
		assertEquals(0, state.index());
		state.cycle();
		assertEquals(1, state.index());
		state.cycle();
		assertEquals(2, state.index());
		state.cycle();
		assertEquals(0, state.index());
	}

	@Test
	void tickScopingResetsWhenLowered() {
		ZoomState state = new ZoomState();
		state.cycle();
		state.cycle();
		assertEquals(2, state.index());
		state.tickScoping(true);
		assertEquals(2, state.index());
		state.tickScoping(false);
		assertEquals(0, state.index());
	}
}
