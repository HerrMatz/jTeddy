package fsm.examples.Complex2;

import java.util.List;

import fsm.base.State;
import fsm.base.Superstate;

public class Active extends Superstate<Event> {

	public StringBuilder data = new StringBuilder();

	public Active(State<Event> from) {
		super(from, Event.class);
		TRANSITION(Event.exit, (e -> ENTER(new FSM.Inactive(this))));
		TRANSITION(Event.inner, (e -> APPEND(new SubN(null))));
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
			return List.of(new B(null));
		}

		public static class A extends State<Event> {
			public A(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> EXIT()));
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

			protected State<Event> defaultExit() {
				return new Sub1.A(this);
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

	public static class SubN extends Superstate<Event> {
		public SubN(State<Event> from) {
			super(from, Event.class);
		}

		@Override
		protected void entryAction() {
			((Active)parent).data.append("iS");
		}
		@Override
		protected void exitAction() {
			((Active)parent).data.append("oS");
		}
		@Override
		protected void pauseAction() {
			((Active)parent).data.append("pS");
		}
		@Override
		protected void unpauseAction() {
			((Active)parent).data.append("uS");
		}

		@Override
		protected List<State<Event>> defaultEntry() {
			return List.of(new E(null));
		}

		public static class E extends State<Event> {
			public E(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.advance, (e -> ENTER(new F(this))));
				TRANSITION(Event.toF, (e -> ENTER(new F(this))));
			}
			@Override
			protected void entryAction() {
				((Active)(((SubN)parent).parent)).data.append("iE");
			}
			@Override
			protected void exitAction() {
				((Active)(((SubN)parent).parent)).data.append("oE");
			}
			@Override
			protected void pauseAction() {
				((Active)(((SubN)parent).parent)).data.append("pE");
			}
			@Override
			protected void unpauseAction() {
				((Active)(((SubN)parent).parent)).data.append("uE");
			}
		}

		public static class F extends State<Event> {
			public F(State<Event> from) {
				super(from, Event.class);
				TRANSITION(Event.endN, (e -> EXIT()));
			}
			@Override
			protected void entryAction() {
				((Active)(((SubN)parent).parent)).data.append("iF");
			}
			@Override
			protected void exitAction() {
				((Active)(((SubN)parent).parent)).data.append("oF");
			}
			@Override
			protected void pauseAction() {
				((Active)(((SubN)parent).parent)).data.append("pF");
			}
			@Override
			protected void unpauseAction() {
				((Active)(((SubN)parent).parent)).data.append("uF");
			}
	
			}
	}

}
