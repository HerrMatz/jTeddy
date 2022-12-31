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

	private Superstate(State<E> from, List<State<E>> parallelSubs, Class<E> classEvent) {
		super(from, classEvent);
		enterSubstates(parallelSubs);
	}

	public Superstate(State<E> from, Class<E> classEvent) {
		super(from, classEvent);
		enterSubstates(defaultEntry());
	}

	private void enterSubstates(List<State<E>> parallelSubs) {
		for(State<E> sub : parallelSubs) {
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
