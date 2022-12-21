package fsm;

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
