package spoons

import org.Junit.Assert;

public class TestCard {

    @test
    public void testGetValue() {

        Card card5 = new Card(5)

        Assert.assertequals(5, card5.getValue());

    }
}
