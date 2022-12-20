package fsm;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        VL_Superstate fsm = new VL_Superstate();
        fsm.handleEvent(Event.start);
        System.out.println( "Hello World!" );
    }
}
