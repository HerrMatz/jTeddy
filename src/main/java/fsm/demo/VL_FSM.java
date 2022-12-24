package fsm.demo;

import fsm.base.State;
import fsm.base.Superstate;

public class VL_FSM extends Superstate<Event> {

	public static class VL_FSMState extends State<Event> {

		public VL_FSMState(State<Event> from) {
			super(from, Event.class);
		}
	}

	public static class VL_FSMSuperstate extends Superstate<Event> {

		public VL_FSMSuperstate(State<Event> sub) {
			this(null, sub);
		}

		public VL_FSMSuperstate(State<Event> from, State<Event> sub) {
			super(from, sub, Event.class);
		}

	}

	public StringBuilder data = new StringBuilder();

	public static class A extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in A");
			get(VL_FSM.class, StringBuilder.class, "data").append("iA");
		}

		@Override
		public void exitAction() {
			System.out.println("Bin in A");
			get(VL_FSM.class, StringBuilder.class, "data").append("oA");
		}

		public A(VL_FSMState from) {
			super(from);
			TRANSITION(Event.start, (payload -> ENTER(new Sub(this))));
			TRANSITION(Event.bypass, (payload -> ENTER(new B(this))));
		}
	}

	public static class B extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in B");
		}

		public B(VL_FSMState from) {
			super(from);
			TRANSITION(Event.up, (payload -> ENTER(new Sub(this, new Sub.High(null)))));
		}
	}

	public static class C extends VL_FSMState {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iC");
		}

		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oC");
		}

		public C(State<Event> from) {
			super(from);
			TRANSITION(Event.bypass, (payload -> ENTER(new D(this))));
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class D extends VL_FSMState {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iD");
		}

		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oD");
		}

		public D(State<Event> from) {
			super(from);
		}
	}

	public static class E extends VL_FSMState {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iE");
		}

		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oE");
		}

		public E(State<Event> from) {
			super(from);
			TRANSITION(Event.toF, (payload -> ENTER(new F(this))));
		}
	}

	public static class F extends VL_FSMState {
		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iF");
		}

		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oF");
		}

		public F(State<Event> from) {
			super(from);
			TRANSITION(Event.clear, (payload -> ENTER_DEEP(new Sub(null))));
		}
	}

	public static class Sub extends VL_FSMSuperstate {

		public static class Normal extends VL_FSMState {
			public Normal(VL_FSMState from) {
				super(from);
				TRANSITION(Event.up, (payload -> ENTER(new High(this))));
			}

			@Override
			public void entryAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("iN");
			}

			@Override
			public void exitAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("oN");
			}
		}

		public static class Low extends VL_FSMState {
			public Low(VL_FSMState from) {
				super(from);
				TRANSITION(Event.up, (payload -> ENTER(new High(this))));
				TRANSITION(Event.clear, (payload -> ENTER(new Normal(this))));
				TRANSITION(Event.exceed, (payload -> EXIT()));
				TRANSITION(Event.last, (payload -> EXIT(new C(parent))));
			}
			@Override
			public void entryAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("iL");
			}

			@Override
			public void exitAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("oL");
			}
		}

		public static class High extends VL_FSMState {
			public High(VL_FSMState from) {
				super(from);
				TRANSITION(Event.down, (payload -> ENTER(new Low(this))));
				TRANSITION(Event.exceed, (payload -> EXIT()));
			}

			@Override
			public void entryAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("iH");
			}

			@Override
			public void exitAction() {
				get(VL_FSM.class, StringBuilder.class, "data").append("oH");
			}
		}

		@Override
		public VL_FSMState defaultExit() {
			return new D(this);
		}

		@Override
		public void entryAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("iS");
		}

		@Override
		public void exitAction() {
			get(VL_FSM.class, StringBuilder.class, "data").append("oS");
		}

		public Sub(State<Event> from, VL_FSMState entry) {
			super(from, entry);
			TRANSITION(Event.reset, (payload -> ENTER(new Sub(this))));
			TRANSITION(Event.error, (payload -> ENTER(new E(this))));
		}

		public Sub(State<Event> from) {
			this(from, new Normal(null));
		}
	}

	public VL_FSM() {
		super(null, null, Event.class);
		setPauseActionIsExitAction(true);
		setUnpauseActionIsEntryAction(true);
		start(new A(null));
	}
}
