package fsm.examples.Complex;

import java.util.List;

import fsm.base.State;
import fsm.base.Superstate;

public class Active extends Superstate<Event> {

	public Active(State<Event> from) {
		super(from, List.of(new Sub1(null), new Sub2(null)), Event.class);
	}

	public static class Sub1 extends Superstate<Event> {
		public Sub1(State<Event> from) {
			super(from, new A(null), Event.class);
		}

		public static class A extends State<Event> {
			public A(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> ENTER(new B(this))));
			}
		}

		public static class B extends Superstate<Event> {
			public B(State<Event> from) {
				super(from, new B1(null), Event.class);
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
			super(from, new C(null), Event.class);
		}

		public static class C extends State<Event> {
			public C(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> ENTER(new D(this))));
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
