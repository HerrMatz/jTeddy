package fsm.base;

import java.util.List;

public class Superstate<E extends Enum<E>> extends State<E> {
	
	/**
	 * Use for explicit entry
	 * @param from
	 * @param sub
	 * @param classEvent
	 */
	public Superstate(State<E> from, State<E> sub, Class<E> classEvent) {
		this(from, List.of(sub), classEvent);
	}

	// public Superstate(State<E> from, Class<E> classEvent) {
	// 	this(from, List.of(), classEvent);
	// }

	private Superstate(State<E> from, List<State<E>> orthogonalSubstates, Class<E> classEvent) {
		super(from, classEvent);
		for(State<E> sub : orthogonalSubstates) {
			copyActionConfigTo(sub);
			parallelSubstates.add(sub);
			sub.parent = this;
		}
	}

	public Superstate(State<E> from, Class<E> classEvent) {
		super(from, classEvent);
		for(State<E> sub : defaultEntry()) {
			copyActionConfigTo(sub);
			parallelSubstates.add(sub);
			sub.parent = this;
		}
	}

	public void start(State<E> s) {
		copyActionConfigTo(s);
		parallelSubstates.add(s);
		s.parent = this;
		s.entryAction();
	}

}
