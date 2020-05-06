package edu.brown.cs.teams.database;


import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing ingredients into ingredient tokens.
 */
public class Tokenizer {

  /**
   * Parses ingredients into ingredient keywords.
   *
   * @param raw - List of ingredient text from recipe
   * @return - The inputted list with all non-keywords removed
   */
  public List<String> parseIngredients(List<String> raw) {
    //initialize return list
    List<String> ret = new ArrayList<String>();

    //regex matches any amount of whitespace
    String whiteSpace = "\\w+";

    //regex matches all non-keywords
    //String regex = "[(][\\w\\W]*[)]|[,][\\w\\W]*|\\s+[Gg]{1}\\s+|\\s+[Kk]{1}[Gg]{1}\\s+|\\s+[Mm]{1}[Ll]{1}\\s+|\\s+[Pp][Tt]\\s+|[Pp]int[s]?|[Mm]illiliter[s]?|[Kk]ilogram[s]?|[Tt]ablespoon[s]?|[Pp]inch(es)?|[Dd]ash|\\d+[\\w]*|[Tt]easpoon[s]?|[Tt]ablespoon[s]?|[Jj]igger[s]?|[Dd]ash(es)?|\\s+[Tt]{1}[Bb]{1}[Ss]{1}[Pp]{1}[s]*|[Oo][Zz]\\s|\\s+[Qq][Tt]\\s+|\\s+[Cc]\\s+|\\s+[Ff][Ll]\\s+|\\s+[Tt]\\s+|\\s+[Cc][Mm]\\s+|[Aa]bout|\\s+[Aa]\\s+|\\s+[Oo][f]\\s+|\\s+[Oo][r][\\s]{1}[\\w\\W]+|[Aa]nd\\s+|[Tt]hinly\\s+|[Hh]alf\\s+|[Hh]alve[\\w]*\\s+|[Pp]iece\\s+|[Hh]andful[\\w]*\\s+|[Tt]humb-[\\w]*\\s+|[Ww]hole[\\w]*\\s+|[Ff]ine[\\w]*\\s+|[Mm]ini[\\w]*\\s+|[Bb]unch[\\w]*\\s+|[Jj]ar[\\w]*\\s+|[Bb]ars[\\w]*\\s+|[Rr]ounded[\\w]*\\s+|[Ff]resh[\\w]*\\s+|[Pp]ack[\\w]*\\s+|[Ss]tone[\\w]*\\s+|[Tt]ub[s]*\\s+|[Cc]hop[\\w]*\\s+|[Ss]oft[\\w]*\\s+|[Tt]oasted[\\w]*\\s+|[Ss]prig[\\w]*|[Hh]eaped[\\w]*\\s+|[Ff]istful[\\w]*\\s+|\\s+[Pp]ot[\\w]*\\s+|[\\\\]{1}[^\\s]*|[Ll]arge[\\w]*\\s+|[Mm]edium[^\\s]*|[Ss]mall[^\\s]*|[Nn]atural[^\\s]*|[Ff]resh[^\\s]*|[Hh]ead[\\w]*|[^\\s]+ly|[Ss]lice[^\\s^,]*|[Tt]{1}[Ss]{1}[Pp]{1}[s]*|[Cc]an[s]*\\s+|[Ll]ittle[\\s]+|[^\\s][Ff]or[^\\s]|\\s+[Tt]he\\s+|[\\/]+[^\\s]*|[-]\\s+|[Cc]arton[^\\s]*|[.]+|[%]|[Ff]ew[^\\s]*|[Oo]\\s|[Ll]oaf[\\w]*\\s+|[Vv]irgin[^\\s]*|[Ee]xtra[^\\s]*|[\\s]+[Ii]n[\\s]+|[Ss]eason[^\\s]*|[Ff]rom[^\\s]*|[Pp]eeled[^\\s]*|[Ee]ach[^\\s]*|[Tt]hick[^\\s]*|[Tt]hin[^\\s]*|[\\w\\W]*[Ss]ized|[Dd]rizzle[\\w]?[^\\s]*|[Hh]ot[^\\s]*|[Vv]ery[^\\s]*|[Ss]ustainable[^\\s]*|[Mm]elted[^\\s]*|[Ss]kimmed[^\\s]*|[Gg]ood[^\\s]*|[Qq]uality[^\\s]*|[Ii]n\\soil[^\\s]*|[Ll]ean[^\\s]*|[Ww]edg[^\\s]*|[Ss]queeze[^\\s]*|[Cc]up[^\\s]*|[Pp][Kk][Tt]\\s+|[Ll]itre[^\\s]*|[Bb]ag[^\\s]*|[Bb]all[^\\s]*|[\\s]+[Ll]ot[^\\s]*|[Gg]rat[eding\\s]*|[Aa]pprox[^\\s]*[\\s\\S]*|[a-zA-Z]*-[a-zA-Z]*|[Gg]round[^\\s]*|[^\\s]*[Rr]ipe[^\\s]*|[Ww]eighing[\\w\\W]*[\\s\\S]*|[\\s]+[Ii]n[\\s]+|[\\s]+[Aa]n[\\s]+|[Pp]eel[^\\s]*|[Pp]ouch[^\\s]*|[Rr]unny[^\\s]*|\\s+[Ii]f[\\w\\W]*|[Cc]old[^\\s]*|\\s\\w\\s|[Cc]ouple[^\\s]*|\\s+[Rr]aw[\\s]+|[^\\s]*[Ee]xtra\\slean[^\\s]*|[Ss]alted[^\\s]*|[Pp]iece[^\\s]*|[Pp]iece[^\\s]*|[Ee]dible[^\\s]*|[Cc]rush[^\\s]*|[Ss]tem[^\\s]*|\\s[Ee]s\\s|[Bb]lock[^\\s]*|[Ff]rom[\\w\\W]*|[Ss]hred[^\\s]*|[Ff]ragrant[^\\s]*|[Cc]overed[^\\s]*|[Cc]overed[^\\s]*|[Hh]alf[^\\s]*|[Cc]heap[^\\s]*|[Bb]oiling[^\\s]*|[\\s]+[Nn]ew[^\\s]*|[Ss]teamed[^\\s]*|[Ss]eason[^\\s]*|[Cc]ooked[^\\s]*|[Mm]ixed[^\\s]*|[Cc]rystallize[^\\s]*|[Bb]ottle[^\\s]*|[Ll]ight[^\\s]*|[Ss]have[^\\s]*|[Ss]nipped[^\\s]*|[\\s]+[Aa]nd[\\s]+[\\s\\S]*|[Ss]trand[^\\s]*|\\s]+[Oo]n[\\s]{1}[\\s\\S]*|[Yy]oung[^\\s]*|[Ss]lim[^\\s]*|[\\s]+[Dd]ry[^\\s]*|[Bb]ig[^\\s]*|\\s{2,5}|[¼½¾⅐⅑⅒⅓⅔⅕⅖⅗⅘⅙⅚⅛⅜⅝⅞]\n";
    //String regex = "[(][\\w\\W]*[)]|[,][\\w\\W]*|\\s+[Gg]{1}\\s+|\\s+[Kk]{1}[Gg]{1}\\s+|\\s+[Mm]{1}[Ll]{1}\\s+|\\s+[Pp][Tt]\\s+|[Pp]int[s]?|[Mm]illiliter[s]?|[Kk]ilogram[s]?|[Tt]ablespoon[s]?|[Pp]inch(es)?|[Dd]ash|\\d+[\\w]*|[Tt]easpoon[s]?|[Tt]ablespoon[s]?|[Jj]igger[s]?|[Dd]ash(es)?|\\s+[Tt]{1}[Bb]{1}[Ss]{1}[Pp]{1}[s]*|[Oo][Zz]\\s|\\s+[Qq][Tt]\\s+|\\s+[Cc]\\s+|\\s+[Ff][Ll]\\s+|\\s+[Tt]\\s+|\\s+[Cc][Mm]\\s+|[Aa]bout|\\s+[Aa]\\s+|\\s+[Oo][f]\\s+|\\s+[Oo][r][\\s]{1}[\\w\\W]+|[Aa]nd\\s+|[Tt]hinly\\s+|[Hh]alf\\s+|[Hh]alve[\\w]*\\s+|[Pp]iece\\s+|[Hh]andful[\\w]*\\s+|[Tt]humb-[\\w]*\\s+|[Ww]hole[\\w]*\\s+|[Ff]ine[\\w]*\\s+|[Mm]ini[\\w]*\\s+|[Bb]unch[\\w]*\\s+|[Jj]ar[\\w]*\\s+|[Bb]ars[\\w]*\\s+|[Rr]ounded[\\w]*\\s+|[Ff]resh[\\w]*\\s+|[Pp]ack[\\w]*\\s+|[Ss]tone[\\w]*\\s+|[Tt]ub[s]*\\s+|[Cc]hop[\\w]*\\s+|[Ss]oft[\\w]*\\s+|[Tt]oasted[\\w]*\\s+|[Ss]prig[\\w]*|[Hh]eaped[\\w]*\\s+|[Ff]istful[\\w]*\\s+|\\s+[Pp]ot[\\w]*\\s+|[\\\\]{1}[^\\s]*|[Ll]arge[\\w]*\\s+|[Mm]edium[^\\s]*|[Ss]mall[^\\s]*|[Nn]atural[^\\s]*|[Ff]resh[^\\s]*|[Hh]ead[\\w]*|[^\\s]+ly|[Ss]lice[^\\s^,]*|[Tt]{1}[Ss]{1}[Pp]{1}[s]*|[Cc]an[s]*\\s+|[Ll]ittle[\\s]+|[^\\s][Ff]or[^\\s]|\\s+[Tt]he\\s+|[\\/]+[^\\s]*|[-]\\s+|[Cc]arton[^\\s]*|[.]+|[%]|[Ff]ew[^\\s]*|\\s[Oo]\\s|[Ll]oaf[\\w]*\\s+|[Vv]irgin[^\\s]*|[Ee]xtra[^\\s]*|[\\s]+[Ii]n[\\s]+|[Ss]eason[^\\s]*|[Ff]rom[^\\s]*|[Pp]eeled[^\\s]*|[Ee]ach[^\\s]*|[Tt]hick[^\\s]*|[Tt]hin[^\\s]*|[\\w\\W]*[Ss]ized|[Dd]rizzle[\\w]?[^\\s]*|[Hh]ot[^\\s]*|[Vv]ery[^\\s]*|[Ss]ustainable[^\\s]*|[Mm]elted[^\\s]*|[Ss]kimmed[^\\s]*|[Gg]ood[^\\s]*|[Qq]uality[^\\s]*|[Ii]n\\soil[^\\s]*|[Ll]ean[^\\s]*|[Ww]edg[^\\s]*|[Ss]queeze[^\\s]*|[Cc]up[^\\s]*|[Pp][Kk][Tt]\\s+|[Ll]itre[^\\s]*|[Bb]ag[^\\s]*|[Bb]all[^\\s]*|[\\s]+[Ll]ot[^\\s]*|[Gg]rat[eding]*|[Aa]pprox[^\\s]*[\\s\\S]*|[a-zA-Z]*-[a-zA-Z]*|[Gg]round[^\\s]*|[^\\s]*[Rr]ipe[^\\s]*|[Ww]eighing[\\w\\W]*[\\s\\S]*|[\\s]+[Ii]n[\\s]+|[\\s]+[Aa]n[\\s]+|[Pp]eel[^\\s]*|[Pp]ouch[^\\s]*|[Rr]unny[^\\s]*|\\s+[Ii]f[\\w\\W]*|[Cc]old[^\\s]*|\\s\\w\\s|[Cc]ouple[^\\s]*|\\s+[Rr]aw[\\s]+|[^\\s]*[Ee]xtra\\slean[^\\s]*|[^\\s]*[Ss]alted[^\\s]*|[Pp]iece[^\\s]*|[Pp]iece[^\\s]*|[Ee]dible[^\\s]*|[Cc]rush[^\\s]*|[Ss]tem[^\\s]*|\\s[Ee]s\\s|[Bb]lock[^\\s]*|[Ff]rom[\\w\\W]*|[Ss]hred[^\\s]*|[Ff]ragrant[^\\s]*|[Cc]overed[^\\s]*|[Cc]overed[^\\s]*|[Hh]alf[^\\s]*|[Cc]heap[^\\s]*|[Bb]oiling[^\\s]*|[\\s]+[Nn]ew[^\\s]*|[Ss]teamed[^\\s]*|[Ss]eason[^\\s]*|[Cc]ooked[^\\s]*|[Mm]ixed[^\\s]*|[Cc]rystallize[^\\s]*|[Bb]ottle[^\\s]*|[Ll]ight[^\\s]*|[Ss]have[^\\s]*|[Ss]nipped[^\\s]*|[\\s]+[Aa]nd[\\s]+[\\s\\S]*|[Ss]trand[^\\s]*|[\\s]+[Oo]n[\\s]{1}[\\s\\S]*|[Yy]oung[^\\s]*|[Ss]lim[^\\s]*|[\\s]+[Dd]ry[^\\s]*|[Bb]ig[^\\s]*|\\s{2,5}|^[\\w]\\s|^[Oo]r\\s|[Dd]iced|[Ss]izzling|^[Ss]ticks|[Ss]trong|[Pp]unnet[^\\s]*|[Bb]aby|\\s{2,5}|^[Ll]ump\\s";
    String regex = "[(][\\w\\W]*[)]|[,][\\w\\W]*|\\s+[Gg]{1}\\s+|\\s+[Kk]{1}[Gg]{1}\\s+|\\s+[Mm]{1}[Ll]{1}\\s+|\\s+[Pp][Tt]\\s+|[Pp]int[s]?|[Mm]illiliter[s]?|[Kk]ilogram[s]?|[Tt]ablespoon[s]?|[Pp]inch(es)?|[Dd]ash|\\d+[\\w]*|[Tt]easpoon[s]?|[Tt]ablespoon[s]?|[Jj]igger[s]?|[Dd]ash(es)?|\\s+[Tt]{1}[Bb]{1}[Ss]{1}[Pp]{1}[s]*|\\s[Oo][Zz]\\s|^oz\\s|\\soz$|\\s+[Qq][Tt]\\s+|\\s+[Cc]\\s+|\\s+[Ff][Ll]\\s+|\\s+[Tt]\\s+|\\s+[Cc][Mm]\\s+|[Aa]bout|\\s+[Aa]\\s+|\\s+[Oo][f]\\s+|\\s[Oo][r]\\s[\\w\\W]+|[Aa]nd\\s+|[Tt]hinly\\s+|[Hh]alf\\s+|[Hh]alve[\\w]*\\s+|[Pp]iece\\s+|[Hh]andful[\\w]*\\s+|[Tt]humb-[\\w]*\\s+|[Ww]hole[\\w]*\\s+|[Ff]ine[\\w]*\\s+|[Mm]ini[\\w]*\\s+|[Bb]unch[\\w]*\\s+|[Jj]ar[\\w]*\\s+|[Bb]ars[\\w]*\\s+|[Rr]ounded[\\w]*\\s+|[Ff]resh[\\w]*\\s+|[Pp]ack[\\w]*\\s+|[Ss]tone[\\w]*\\s+|[Tt]ub[s]*\\s+|[Cc]hop[\\w]*\\s+|[Ss]oft[\\w]*\\s+|[Tt]oasted[\\w]*\\s+|[Ss]prig[\\w]*|[Hh]eaped[\\w]*\\s+|[Ff]istful[\\w]*\\s+|\\s+[Pp]ot[\\w]*\\s+|[\\\\]{1}[^\\s]*|[Ll]arge[\\w]*\\s+|[Mm]edium[^\\s]*|[Ss]mall[^\\s]*|[Nn]atural[^\\s]*|[Ff]resh[^\\s]*|[Hh]ead[\\w]*|[^\\s]+ly|[Ss]lice[^\\s^,]*|[Tt]{1}[Ss]{1}[Pp]{1}[s]*|\\s+[Cc]an[s]*\\s+|[Ll]ittle[\\s]+|\\sfor$|^[Ff]or\\s|\\s[Ff]or\\s|\\s[Tt]he\\s|[\\/]+[^\\s]*|[Cc]arton[^\\s]*|[.]+|[%]|[Ff]ew[^\\s]*|\\s[Oo]\\s|[Ll]oaf[\\w]*\\s+|[Vv]irgin[^\\s]*|[Ee]xtra[^\\s]*|\\s[Ii]n\\s|[Ss]eason[^\\s]*|[Ff]rom[^\\s]*|[Pp]eeled[^\\s]*|[Ee]ach[^\\s]*|[Tt]hick[^\\s]*|[Tt]hin[^\\s]*|[\\w\\W]*[Ss]ized|[Dd]rizzle[\\w]?[^\\s]*|\\s[Hh]ot$|^[Hh]ot\\s|\\s[Hh]ot\\s|[Vv]ery[^\\s]*|[Ss]ustainable[^\\s]*|[Mm]elted[^\\s]*|[Ss]kimmed[^\\s]*|[Gg]ood[^\\s]*|[Qq]uality[^\\s]*|[Ii]n\\soil[^\\s]*|[Ll]ean[^\\s]*|[Ww]edg[^\\s]*|[Ss]queeze[^\\s]*|[Cc]up[^\\s]*|[Pp][Kk][Tt]\\s+|[Ll]itre[^\\s]*|[Bb]ag[^\\s]*|[Bb]all[^\\s]*|[\\s]+[Ll]ot[^\\s]*|[Gg]rat[eding]*|[Aa]pprox[^\\s]*[\\s\\S]*|[\\w]*-[\\w]*|[Gg]round[^\\s]*|[^\\s]*[Rr]ipe[^\\s]*|[Ww]eighing[\\w\\W]*[\\s\\S]*|[\\s]+[Ii]n[\\s]+|[\\s]+[Aa]n[\\s]+|[Pp]eel[^\\s]*|[Pp]ouch[^\\s]*|[Rr]unny[^\\s]*|\\s+[Ii]f[\\w\\W]*|[Cc]old[^\\s]*|\\s\\w\\s|[Cc]ouple[^\\s]*|\\s+[Rr]aw[\\s]+|[^\\s]*[Ee]xtra\\slean[^\\s]*|[^\\s]*[Ss]alted[^\\s]*|[Pp]iece[^\\s]*|[Pp]iece[^\\s]*|[Ee]dible[^\\s]*|[Cc]rush[^\\s]*|[Ss]tem[^\\s]*|\\s[Ee]s\\s|[Bb]lock[^\\s]*|[Ff]rom[\\w\\W]*|[Ss]hred[^\\s]*|[Ff]ragrant[^\\s]*|[Cc]overed[^\\s]*|[Cc]overed[^\\s]*|[Hh]alf[^\\s]*|[Cc]heap[^\\s]*|[Bb]oiling[^\\s]*|[\\s]+[Nn]ew[^\\s]*|[Ss]teamed[^\\s]*|[Ss]eason[^\\s]*|[Cc]ooked[^\\s]*|[Mm]ixed[^\\s]*|[Cc]rystallize[^\\s]*|[Bb]ottle[^\\s]*|[Ll]ight[^\\s]*|[Ss]have[^\\s]*|[Ss]nipped[^\\s]*|[\\s]+[Aa]nd[\\s]+[\\s\\S]*|[Ss]trand[^\\s]*|[\\s]+[Oo]n[\\s]{1}[\\s\\S]*|[Yy]oung[^\\s]*|[Ss]lim[^\\s]*|\\s[Dd]ry\\s|\\s[Dd]ry$|^[Dd]ry\\s|\\s[Bb]ig\\s|\\s[Bb]ig$|^[Bb]ig\\s|^[\\w]\\s|^[Oo]r\\s|[Dd]iced|[Ss]izzling|^[Ss]ticks|[Ss]trong|[Pp]unnet[^\\s]*|[Bb]aby|\\s{2,5}|^[Ll]ump\\s|^as\\s|\\sas$|\\sas\\s|^pe\\s|\\w*–\\w*|\\\\\\\\w*";
    for (String unparsed : raw) {
      //replace all non-keywords with a space (to prevent keywords from concatenating)
      // and ensure only single space between keywords
      System.out.println(unparsed);
      String parsed = unparsed.replaceAll(regex, " ").trim();

      parsed = parsed.replaceAll("[\\\\]{1}[^\\s]*", " ");
      parsed = parsed.replaceAll("[\"']", "");
      parsed = parsed.replaceAll("[\u00BC-\u00BE\u2150-\u215E\u2189]+", " ");
      parsed = parsed.replaceAll(regex, " ").trim();
      parsed = parsed.replaceAll(regex, " ").trim();
      parsed = parsed.replaceAll("un ", "").trim();
      parsed = parsed.replaceAll("of ", "").trim();
      parsed = parsed.replaceAll("tbsp", "").trim();
      String[] arrOfStr = parsed.split(" ");
      if (arrOfStr.length > 2) {
        if (arrOfStr[arrOfStr.length - 2].length() == 1) {
          parsed = String.join(arrOfStr[arrOfStr.length - 1]);
        } else {
          parsed = String.join(" ", arrOfStr[arrOfStr.length - 2], arrOfStr[arrOfStr.length - 1]);
        }

      }
      if (!(arrOfStr.length == 0) || arrOfStr != null) {
        ret.add(parsed);
      }
      System.out.println(parsed);
    }
    return ret;
  }
}
