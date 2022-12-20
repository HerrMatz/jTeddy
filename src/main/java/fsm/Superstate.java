package fsm;

public class Superstate extends State {
	
	public Superstate(State sub) {
		init();
		parallelSubstates.add(sub);
		sub.parent = this;
	}
}
