package fsm;

import fsm.examples.VL.Event;
import fsm.examples.VL.VL_FSM;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
	{
		VL_FSM fsm = new VL_FSM();
		fsm.handleEvent(Event.start);
		System.out.println( "Hello World!" );
	}
}
