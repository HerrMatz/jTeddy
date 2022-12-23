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

	public static class A extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public A(VL_FSMState from) {
			super(from);
			transitions.put(Event.start, (payload -> 
				ENTER(new Sub(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new B(this))
			));
		}
	}

	public static class B extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public B(VL_FSMState from) {
			super(from);
			transitions.put(Event.up, (payload -> 
				ENTER(new Sub(this, new Sub.High(null)))
			));

		}
	}

	public static class C extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public C(State<Event> from) {
			super(from);

			transitions.put(Event.bypass, (payload -> 
				ENTER(new D(this))
			));
		}
	}

	public static class D extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public D(State<Event> from) {
			super(from);
			// transitions.put(Event.start, (payload -> 
			// 	ENTER(new Initial(this))
			// ));

			// transitions.put(Event.reset, (payload -> 
			// 	ENTER_DEEP(new Delta(this))
			// ));
		}
	}

	public static class E extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public E(State<Event> from) {
			super(from);
		}
	}

	public static class Sub extends VL_FSMSuperstate {

		public static class Normal extends VL_FSMState {
			public Normal(VL_FSMState from) {
				super(from);

				transitions.put(Event.up, (payload ->
					ENTER(new High(this))
				));
			}
		}

		public static class Low extends VL_FSMState {
			public Low(VL_FSMState from) {
				super(from);

				transitions.put(Event.up, (payload ->
					ENTER(new High(this))
				));
				transitions.put(Event.clear, (payload ->
					ENTER(new Normal(this))
				));
				transitions.put(Event.exceed, (payload ->
					EXIT()
				));
				transitions.put(Event.last, (payload ->
					EXIT(new C(parent))
				));
			}
		}

		public static class High extends VL_FSMState {
			public High(VL_FSMState from) {
				super(from);

				transitions.put(Event.down, (payload ->
					ENTER(new Low(this))
				));

				// transitions.put(Event.exceed, (payload ->
				// 	//exit
				// ));
			}
		}

		@Override
		public VL_FSMState defaultExit() {
			return new D(this);
		}

		@Override
		public void entryAction() {
		}
		public Sub(State<Event> from, VL_FSMState entry) {
			super(from, entry);
		}
		public Sub(State<Event> from) {
			this(from, new Normal(null));

			transitions.put(Event.reset, (payload ->
				ENTER(new Sub(this))
			));
			
			transitions.put(Event.error, (payload -> 
				ENTER(new E(this))
			));

			// transitions.put(Event.error, (payload ->
			// 	EXIT(Error)
			// ));
			
		}
	}

	public VL_FSM() {
		super(null, new A(null), Event.class);
	}
}
