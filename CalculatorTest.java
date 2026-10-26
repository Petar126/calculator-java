import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void calculateRespectsOperationPriority() {
        // multiplication is calculated before addition: 10 + 20 + 3 = 33
        assertEquals("33.0", Calculator.Run("10+5*4+3"));
    }

}
