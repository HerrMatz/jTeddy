package fsm.base;

public class Superstate<E extends Enum<E>> extends State<E> {
	
	public Superstate(State<E> from, State<E> sub, Class<E> classEvent) {
		super(from, classEvent);
		parallelSubstates.add(sub);
		sub.parent = this;
	}
}
