package fsm;

public class VL_Superstate extends Superstate {

	public static class Initial extends State {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public Initial() {
			this(self);
		}
		public Initial(State from) {
			super(from);
			transitions.put(Event.start, (payload -> 
				ENTER(new Beta(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Gamma(this))
			));
		}
	}

	public static class Beta extends State {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public Beta(State from) {
			transitions.put(Event.start, (payload -> 
				ENTER(new Gamma(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Initial(this))
			));
		}
	}

	public static class Gamma extends State {
		@Override
		public void entryAction() {
			System.out.println("Bin in Initial");
		}
		public Gamma(State from) {
			transitions.put(Event.start, (payload -> 
				ENTER(new Initial(this))
			));

			transitions.put(Event.bypass, (payload -> 
				ENTER(new Beta(this))
			));
		}
	}

	public static class Sub extends Superstate {
		@Override
		public void entryAction() {
		}
		public Sub() {
			super(new Normal());

			transitions.put(Event.reset, (payload ->
				ENTER(new Sub())
			));
			
			// transitions.put(Event.error, (payload ->
			// 	EXIT()
			// ));
			
		}

		public static class Normal extends State {
			public Normal() {

			}
		}
	}

	public VL_Superstate() {
		super(new Initial());
		// parallelSubstates.add(new Initial());
		// self = new Initial();
	}
}
