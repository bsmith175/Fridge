package src.main.java.edu.brown.cs.teams.utils;

import java.util.ArrayList;
import java.util.List;

public class temp {

    /**
     * Parses ingredients into ingredient keywords.
     * @param raw - List of ingredient text from recipe
     * @return - The inputted list with all non-keywords removed
     */
    private List<String> parseIngredients(List<String> raw) {
        //initialize return list
        List<String> ret = new ArrayList<String>();

        //regex matches any amount of whitespace
        String whiteSpace = "\\w+";

        //regex matches all non-keywords
        String regex = "[(][\\w\\W]*[)]|[,][\\w\\W]*|\\s+[Gg]{2}\\s+|\\s+[Kk]{1}[Gg]{1}\\s+|\\s+[Mm]{1}[Ll]{1}\\s+|\\s+[Pp][Tt]\\s+|[Pp]int[s]?|[Mm]illiliter[s]?|[Kk]ilogram[s]?|[Tt]ablespoon[s]?|[Pp]inch(es)?|[Dd]ash|\\d+[\\w]*|[Tt]easpoon[s]?|[Tt]ablespoon[s]?|[Jj]igger[s]?|[Dd]ash(es)?|\\s+[Tt]{1}[Bb]{1}[Ss]{1}[Pp]{1}[s]*|\\s+[Oo][Zz]\\s+|\\s+[Qq][Tt]\\s+|\\s+[Cc]\\s+|\\s+[Ff][Ll]\\s+|\\s+[Tt]\\s+|\\s+[Cc][Mm]\\s+|[Aa]bout|\\s+[Aa]\\s+|\\s+[Oo][f]\\s+|\\s+[Oo][r]\\s+|\\s+[Aa]nd\\s+|\\s+[Tt]hinly\\s+|\\s+[Hh]alf\\s+|\\s+[Hh]alve[\\w]*\\s+|\\s+[Pp]iece\\s+|[Hh]andful[\\w]*\\s+|[Tt]humb-[\\w]*\\s+|[Ww]hole[\\w]*\\s+|[Ff]ine[\\w]*\\s+|[Mm]ini[\\w]*\\s+|[Bb]unch[\\w]*\\s+|[Jj]ar[\\w]*\\s+|[Bb]ars[\\w]*\\s+|[Rr]ounded[\\w]*\\s+|[Ff]resh[\\w]*\\s+|[Pp]ack[\\w]*\\s+|[Ss]tone[\\w]*\\s+|[Tt]ub[s]*\\s+|[Cc]hop[\\w]*\\s+|[Ss]oft[\\w]*\\s+|[Tt]oasted[\\w]*\\s+|[Ss]prig[\\w]*\\s+|[Hh]eaped[\\w]*\\s+|[Ff]istful[\\w]*\\s+|\\s+[Pp]ot[\\w]*\\s+|[\\\\]{1}[^\\s]*|[Ll]arge[\\w]*\\s+|[Mm]edium[^\\s]*|[Ss]mall[^\\s]*|[Nn]atural[^\\s]*|[Ff]resh[^\\s]*|[Hh]ead[\\w]*|[^\\s]+ly|[Ss]lice[^\\s^,]*";

        for (String unparsed : raw) {
            //replace all non-keywords with a space (to prevent keywords from concatenating)
            // and ensure only single space between keywords
            ret.add(unparsed.replaceAll(regex, " ").replaceAll(whiteSpace, " ").trim());
        }
        return ret;
    }
}
