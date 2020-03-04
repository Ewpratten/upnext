package ca.retrylife.frc.tba.nightbotparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.retrylife.simplelogger.SimpleLogger;

public class NextMatch {

    /* API Constants */
    private static final String TWITCH_USERNAME = "nextmatchbot";
    private static URL NIGHTBOT_ENDPOINT;
    private static final Pattern PARSER_PATTERN = Pattern.compile(String
            .format("@%s, \\[(.*)] Team (.*) will be playing in match (.*), (.*) to start at .* ([0-9]*):([0-9]*).*", TWITCH_USERNAME));

    /* Unit Testing */
    protected boolean test = false;
    protected String testResponse = "@nextmatchbot, [2020onto1] Team 5024 will be playing in match f2, predicted to start at Wed 12:21 ";


    public static void main(String[] args) {

        // Create a new NextMatch for testing
        NextMatch match = new NextMatch(5024);

        System.out.println(match.toString());
    }

    /**
     * Match time info type
     */
    public enum MatchTimeType {
        UNKNOWN, PREDICTED, SCHEDULED;

        public static MatchTimeType fromString(String str) {
            switch (str) {
                case "predicted":
                    return PREDICTED;
                case "scheduled":
                    return SCHEDULED;
                default:
                    return UNKNOWN;
            }

        }
    }

    /* Match Data */
    private int team;
    private boolean hasMatch;
    private String matchStr;
    private MatchTimeType timeType;
    private Date matchDate;
    private String eventCode;
    private int[] timeHM = new int[]{0,0};

    public NextMatch(int team) {
        this.team = team;

        // Set up a URL
        try {
            NIGHTBOT_ENDPOINT = new URL(String.format("https://www.thebluealliance.com/_/nightbot/status/%d?user=%s",
                    team, TWITCH_USERNAME));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Refresh request to nightbot api
        refresh();
    }

    public void refresh() {

        // Clear all internal data
        matchStr = "";
        timeType = null;
        matchDate = null;
        eventCode = "";

        SimpleLogger.log("NextMatch", String.format("Refreshing with test mode set to: %b", test));

        // Make a connection to the API
        try {
            HttpURLConnection con = (HttpURLConnection) NIGHTBOT_ENDPOINT.openConnection();
            con.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();


            String parsed = content.toString();
            // Tests
            if(test){
                parsed = testResponse;
                SimpleLogger.log("NextMatch", "Using test response data: "+parsed);
            }

            hasMatch = true;

            // Parse out data
            Matcher m = PARSER_PATTERN.matcher(parsed);
            if (m.matches()) {
                eventCode = m.group(1);
                matchStr = m.group(3);
                timeType = MatchTimeType.fromString(m.group(4));

                timeHM[0] = Integer.parseInt(m.group(5));
                timeHM[1] = Integer.parseInt(m.group(6));
            }
        } catch (IOException e) {
            hasMatch = false;
            SimpleLogger.log("NextMatch", String.format("Failed Parsing:%s", e.toString()));
            return;
        }

    }

    public boolean hasMatch() {
        return hasMatch;
    }

    public int getTeamNum() {
        return team;
    }

    public String getMatchString() {
        return matchStr;
    }

    public MatchTimeType getTimeType() {
        return timeType;
    }

    public int[] getTime() {
        int[] output= new int[]{0,0};

        // Calc diff in hours


        return output;
    }

    public String getEventCode() {
        return eventCode;
    }

    @Override
    public String toString() {
        if (hasMatch) {
            return String.format("NextMatch<hasMatch:%b, team:%d, matchStr: %s, timeType:%s, date:[%d;%d], event:%s>",
                    hasMatch, team, matchStr, timeType.toString(), getTime()[0], getTime()[1], eventCode);
        } else {
            return String.format("NextMatch<hasMatch:%b, team:%d, matchStr: %s, timeType:%s, date:%s, event:%s>", false,
                    team, "", MatchTimeType.UNKNOWN.toString(), "???", "");
        }
    }
}