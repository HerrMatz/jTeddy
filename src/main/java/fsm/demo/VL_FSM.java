package fsm.demo;

import fsm.base.State;
import fsm.base.Superstate;

public class VL_FSM extends Superstate<Event> {

	public static class VL_FSMState extends State<Event> {

		// public VL_FSMState() {
		// 	super(Event.class);
		// }

		public VL_FSMState(VL_FSMState from) {
			super(from, Event.class);
		}

	}

	public static class VL_FSMSuperstate extends Superstate<Event> {

		public VL_FSMSuperstate() {
			super(null, Event.class);
		}

		public VL_FSMSuperstate(VL_FSMSuperstate from) {
			super(from, Event.class);
		}

	}

	public static class Initial extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		// public Initial() {
		// 	this(null);
		// }
		public Initial(VL_FSMState from) {
			super(from);
			transitions.put(Event.start, (payload -> 
				ENTER(new Beta(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Gamma(this))
			));
		}
	}

	public static class Beta extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public Beta(VL_FSMState from) {
			super(from);
			transitions.put(Event.start, (payload -> 
				ENTER(new Gamma(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Initial(this))
			));
		}
	}

	public static class Gamma extends VL_FSMState {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public Gamma(VL_FSMState from) {
			super(from);
			transitions.put(Event.start, (payload -> 
				ENTER(new Initial(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Beta(this))
			));
		}
	}

	public static class Sub extends VL_FSMState {
		@Override
		public void entryAction() {
		}
		public Sub(VL_FSMState from) {
			super(new Normal(from));

			transitions.put(Event.reset, (payload ->
				ENTER(new Sub(this))
			));
			
			// transitions.put(Event.error, (payload ->
			// 	EXIT()
			// ));
			
		}

		public static class Normal extends VL_FSMState {
			public Normal(VL_FSMState from) {
				super(from);

			}
		}
	}

	public VL_FSM() {
		super(new Initial(null), Event.class);
		// parallelSubstates.add(new Initial());
		// self = new Initial();
	}
}
