package fsm.base;

import java.util.List;

public class Superstate<E extends Enum<E>> extends State<E> {
	
	/**
	 * Use for explicit entry
	 * @param from Original state that this new state replaces
	 * @param sub Explicit entry state to enter
	 * @param eventClass Class of the event type this state handles
	 */
	public Superstate(State<E> from, State<E> sub, Class<E> eventClass) {
		this(from, List.of(sub), eventClass);
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
