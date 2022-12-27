package fsm.examples.Complex;

import java.util.List;

import fsm.base.State;
import fsm.base.Superstate;

public class Active extends Superstate<Event> {

	public Active(State<Event> from) {
		super(from, Event.class);
		TRANSITION(Event.exit, (e -> ENTER(new FSM.Inactive(this))));
	}

	@Override
	protected State<Event> defaultExit() {
		return new FSM.Inactive(this);
	}
	@Override
	protected List<State<Event>> defaultEntry() {
		return List.of(new Sub1(null), new Sub2(null));
	}

	public static class Sub1 extends Superstate<Event> {
		public Sub1(State<Event> from) {
			super(from, Event.class);
		}

		@Override
		protected List<State<Event>> defaultEntry() {
			return List.of(new A(null));
		}

		public static class A extends State<Event> {
			public A(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> ENTER(new B(this))));
			}
		}

		public static class B extends Superstate<Event> {
			public B(State<Event> from) {
				super(from, Event.class);
			}

			@Override
			protected List<State<Event>> defaultEntry() {
				return List.of(new B1(null));
			}
	
			public static class B1 extends State<Event> {
				public B1(State<Event> from) {
					super(from, Event.class);
					TRANSITION(Event.advance, (e -> ENTER(new B2(this))));
				}
			}

			public static class B2 extends State<Event> {
				public B2(State<Event> from) {
					super(from, Event.class);
					TRANSITION(Event.advance, (e -> EXIT()));
				}
			}

		}
	}

	public static class Sub2 extends Superstate<Event> {
		public Sub2(State<Event> from) {
			super(from, Event.class);
		}

		@Override
		protected List<State<Event>> defaultEntry() {
			return List.of(new C(null));
		}

		public static class C extends State<Event> {
			public C(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> ENTER(new D(this))));
				TRANSITION(Event.toD, (e -> ENTER(new D(this))));
			}
		}

		public static class D extends State<Event> {
			public D(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> EXIT()));
			}
		}

	}

}
