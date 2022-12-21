package fsm.base;

public class Superstate<E extends Enum<E>> extends State<E> {
	
	public Superstate(State<E> sub, Class<E> classEvent) {
		super(null, classEvent);
		// init();
		parallelSubstates.add(sub);
		sub.parent = this;
	}
}
