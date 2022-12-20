package fsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

public class VL_SuperstateTest {
	
	@Test
	public void simple() {
		VL_Superstate fsm = new VL_Superstate();
		assertThat(fsm.getSubstates().get(0).getState(), instanceOf(VL_Superstate.Initial.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0).getState(), instanceOf(VL_Superstate.Beta.class));
		fsm.handleEvent(Event.start);
		assertThat(fsm.getSubstates().get(0).getState(), instanceOf(VL_Superstate.Gamma.class));
	}
}
