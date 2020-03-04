package ca.retrylife.frc.tba.nightbotparser;

import org.junit.Test;

public class NextMatchTest {

    public @Test
    void testReadNext() {
        // Create a new NextMatch for testing
        NextMatch match = new NextMatch(5024);
        match.test = true;
        match.refresh();

        System.out.println(match.toString());

        // Ensure we correctly parsed a response
        assert match.getTeamNum() == 5024;
        assert match.getEventCode().equals("2020onto1");
        assert match.getTimeType() == NextMatch.MatchTimeType.PREDICTED;
        assert match.getMatchString().equals("f2");
        assert match.hasMatch() == true;
    }

}