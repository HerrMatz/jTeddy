// package fsm.base;

// import java.util.List;

// public class Superstate<E extends Enum<E>, P> extends State<E, P> {
	
// 	/**
// 	 * Use for explicit entry
// 	 * @param from Original state that this new state replaces
// 	 * @param sub Explicit entry state to enter
// 	 * @param eventClass Class of the event type this state handles
// 	 */
// 	public Superstate(State<E, P> from, State<E, P> sub, Class<E> eventClass) {
// 		this(from, List.of(sub), eventClass);
// 	}

// 	private Superstate(State<E, P> from, List<State<E, P>> parallelSubs, Class<E> classEvent) {
// 		super(from, classEvent);
// 		enterSubstates(parallelSubs);
// 	}

// 	public Superstate(State<E, P> from, Class<E> classEvent) {
// 		super(from, classEvent);
// 		enterSubstates(defaultEntry());
// 	}

// 	private void enterSubstates(List<State<E, P>> parallelSubs) {
// 		for(State<E, P> sub : parallelSubs) {
// 			copyActionConfigTo(sub);
// 			parallelSubstates.add(sub);
// 			sub.parent = this;
// 		}
// 	}

// 	public void start(State<E, P> s) {
// 		copyActionConfigTo(s);
// 		parallelSubstates.add(s);
// 		s.parent = this;
// 		s.entryAction();
// 	}

// }
