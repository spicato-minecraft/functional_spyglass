package functional_spyglass.zoom;

/**
 * Local zoom index while the spyglass is raised. Wrap 0→1→2→0. Reset when not scoping.
 */
public final class ZoomState {
	public static final int LEVEL_COUNT = 3;

	private int index;

	public int index() {
		return index;
	}

	/** 1-based level for UI copy. */
	public int level() {
		return index + 1;
	}

	public void cycle() {
		index = (index + 1) % LEVEL_COUNT;
	}

	public void reset() {
		index = 0;
	}

	public void tickScoping(boolean scoping) {
		if (!scoping) {
			reset();
		}
	}
}
