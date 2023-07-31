package MyCrawler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;




class Robot{



    public static boolean robotallow(URL url) {
        String URL = url.getProtocol() + "://" + url.getHost() + "/robots.txt"; //Get link.

        URL robot;

        try { robot = new URL(URL);
        } catch (MalformedURLException e) {

            return false;

        }   //Checks if the Given URL is Valid.

        String Commands = null;

        try
        {
            BufferedReader read = new BufferedReader(new InputStreamReader(robot.openStream())); //Opens up the webpage & reads it line by line.
            String inputLine;
            while ((inputLine = read.readLine()) != null)   //Reading line by line.
            {
                if(Commands == null)  //Saving the content of Robot.txt in strCommands.
                {
                    Commands = inputLine;

                }
                else
                    Commands += inputLine; //Adding string to the current string.
                Commands += "\n";   //Placing space between each line as a break point, necessary when reading them individually in the split process..
            }
            read.close();
            if(Commands == null) //No instructions in robot.txt
                return true;

        }
        catch (IOException e)
        {
            return true;
        }

        if (Commands.contains("Disallow:"))   //See if it contains anything.
        {
            String[] split = Commands.split("\n"); //Splits the string if it starts a new line :) Isolates the actual string, removing the \n

            ArrayList<Rule> robotRules = new ArrayList<>(); // Array of rules

            String userAgent = null;

            for (int i = 0; i < split.length; i++)
            {
                String line = split[i].trim(); //Removes spaces from both ends.
                if (line.toLowerCase().startsWith("user-agent"))
                {
                    int start = line.indexOf(":") + 1;

                    int end   = line.length();
                    userAgent = line.substring(start, end).trim().toLowerCase(); //Gets the userAgent.
                }
                else if (line.startsWith("Disallow:")) {
                    if (userAgent != null) {
                        int start = line.indexOf(":") + 1;
                        int end   = line.length();
                        Rule r = new Rule();
                        r.userAgent = userAgent;
                        r.rule = line.substring(start, end).trim();   //Adding it to a list containing the rules.
                        robotRules.add(r);

                    }
                }
            }



            for (Rule robotRule : robotRules)
            {


                if (robotRule.rule.length() == 0) continue;

                // disallow when user agent is googlebot or *

                if (robotRule.userAgent.equals("googlebot") || robotRule.userAgent.equals("*")) {

                    if (robotRule.rule.equals("/"))

                    {return false;} // disallows all


                    String Currentpath = url.getPath(); //Gets the path that the URL is in.

                    if (Currentpath.length() >= robotRule.rule.length()) {

                        String Compare = Currentpath.substring(0, robotRule.rule.length());

                        if (Compare.equals(robotRule.rule))
                        {

                            return false;    //Basically returns false if the given link/path was mentioned in the Disallows.

                        }
                    }
                }
            }
        }
        return true;
    }

}
class Rule{

    public String userAgent;

    public String rule;



}