package ca.retrylife.frc.tba.nightbotparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NextMatch {

    /* API Constants */
    private static final String TWITCH_USERNAME = "nextmatchbot";
    private static URL NIGHTBOT_ENDPOINT;
    private static final Pattern PARSER_PATTERN = Pattern.compile(String
            .format("@%s, \\[(.*)] Team (.*) will be playing in match (.*), (.*) to start at (.*)", TWITCH_USERNAME));
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("%a %H:%M %Z");

    static {
        TIME_FORMAT.setLenient(false);
    }

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

    public NextMatch(int team) {
        this.team = team;

        // Set up a URL
        NIGHTBOT_ENDPOINT = new URL(
                String.format("https://www.thebluealliance.com/_/nightbot/status/%d?user=%s", team, TWITCH_USERNAME));

        // Refresh request to nightbot api
        refresh();
    }

    public void refresh() {

        // Clear all internal data
        matchStr = "";
        timeType = null;
        matchDate = null;
        eventCode = "";

        // Make a connection to the API
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

        // Determine if there is a match for this team yet
        hasMatch = content.toString().contains("playing");
        if (!hasMatch) {
            return;
        }

        // Parse out data
        Matcher m = PARSER_PATTERN.matcher(content.toString());
        if (m.matches()) {
            eventCode = m.group(1);
            matchStr = m.group(3);
            timeType = MatchTimeType.fromString(m.group(4));
            matchDate = TIME_FORMAT.parse(m.group(5));
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

    public Date getDate() {
        return matchDate;
    }

    public String getEventCode() {
        return eventCode;
    }

    @Override
    public String toString() {
        if (hasMatch) {
            return String.format("NextMatch<hasMatch:%b, team:%d, matchStr: %s, timeType:%s, date:%s, event:%s>",
                    hasMatch, team, matchStr, timeType.toString(), matchDate.toString(), eventCode);
        } else {
            return String.format("NextMatch<hasMatch:%b, team:%d, matchStr: %s, timeType:%s, date:%s, event:%s>", false,
                    team, "", MatchTimeType.UNKNOWN.toString(), "???", "");
        }
    }
}