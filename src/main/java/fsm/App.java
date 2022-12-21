package fsm;

import fsm.demo.Event;
import fsm.demo.VL_FSM;

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
