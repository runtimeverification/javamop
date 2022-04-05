import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;

import rvm.MultipleConnectionCheckRuntimeMonitor;
import rvm.UserSessionLogRuntimeMonitor;

/**
 * Adapter to translate Postfix sample log into RV-Monitor event trace line-by-line.
 *
 * @author Philip Daian
 */
class PostfixLogAdapter {


    private static HashMap<String, String> activeUsers = new HashMap<String, String>();

    /**
     * Process each line, firing an RV-Monitor event with appropriate parameters if a given
     * regular expression matches the line
     *
     * @param line Log line to be processed
     * @param username Username of user running Postfix daemon (to parse date boundaries in log)
     */
    private static void processLogLine(String line, String username) {
        // Define a pattern for each RV-Monitor event
        Pattern connectPattern = Pattern.compile("(.*?)( " + username + ")(.*)(\\[\\d+\\]: connect from )(.*)");
        Pattern disconnectPattern = Pattern.compile("(.*?)( " + username + ")(.*)(\\[\\d+\\]: disconnect from )(.*)");
        Pattern submitMessagePattern = Pattern.compile("(.*?)( " + username + ")(.*)(\\[\\d+\\]: )(.*)(: client=)(.*)");
        Pattern failToSendPattern = Pattern.compile("(.*?)( " + username + ")(.*)(\\[\\d+\\]: )(.*)(reject: RCPT from )(.*?)(: )(.*)");
        // If any pattern matches the line being processed, fire the RV-Monitor event with appropriate parameters
        Matcher connectLineMatcher = connectPattern.matcher(line);
        while (connectLineMatcher.find()) {
            UserSessionLogRuntimeMonitor.connectEvent(getUser(connectLineMatcher.group(5)), connectLineMatcher.group(1));
            MultipleConnectionCheckRuntimeMonitor.connectEvent(getUser(connectLineMatcher.group(5)), connectLineMatcher.group(1));
        }
        Matcher disconnectLineMatcher = disconnectPattern.matcher(line);
        while (disconnectLineMatcher.find()) {
            UserSessionLogRuntimeMonitor.disconnectEvent(getUser(disconnectLineMatcher.group(5)), disconnectLineMatcher.group(1));
            MultipleConnectionCheckRuntimeMonitor.disconnectEvent(getUser(disconnectLineMatcher.group(5)), disconnectLineMatcher.group(1));
        }
        Matcher failToSendMatcher = failToSendPattern.matcher(line);
        while (failToSendMatcher.find())
            UserSessionLogRuntimeMonitor.messageFailEvent(getUser(failToSendMatcher.group(7)), failToSendMatcher.group(1));
        Matcher submitMessageMatcher = submitMessagePattern.matcher(line);
        while (submitMessageMatcher.find())
            UserSessionLogRuntimeMonitor.submitMessageEvent(getUser(submitMessageMatcher.group(7)), submitMessageMatcher.group(1));
    }

    private static String USERNAME = "avas";

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("mail.log")));
            String line;
            while ((line = br.readLine()) != null) {
                processLogLine(line, USERNAME);
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Get the cached String object representing a particular user
    private static String getUser(String user) {
        if (!activeUsers.containsKey(user)) {
            activeUsers.put(user, user);
        }
        return activeUsers.get(user);
    }
}
