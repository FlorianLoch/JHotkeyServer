package de.fdloch.jhotkeyserver; //Hier sollte der (korrekte) Name des Packages stehen, wird keines verwendet, so kann diese Zeile gelöscht werden

/**
 * Diese Klasse bietet die Möglichkeit auf einfache Art und Weise den Hash-Wert eines Strings zu berrechnen.
 * Hash-Werte stellen eine verschlüsselte Form eines Textes dar, diese lässt sich jedoch nicht entschlüsseln.
 * Trotzdem lassen sich Hash-Werte ideal zum Verschlüsseln von Passwörtern verwenden (zum Beispiel in der UserLib.txt), indem
 * das vom Nutzer eingegebene Passwort ebenfalls verschlüsselt und anschließend auf Gleichheit mit dem bereits verschlüsselten geprüft.
 * (So könnte eine Person zwar die UserLib.txt öffnen und lesen, das Passwort würde er jedoch nur in verschlüsselter Form sehen -> Es wäre für ihn wertlos!
 *
 * Bitte bei der Verwendung daran denken, hinter die Methode, welche diese Funktion verwendet, den Zusatz "throws Exception" zu schreiben.
 * Bsp.: public void test() throws Exception  {
 *  ...
 * }
 * Alternativ kann der Aufruf auch einfach in einen Try-Catch-Block gesetzt werden:
 * try {
 *  hash = SHA1.makeHash("Test");
 *  } catch (Exception ex) {
 *      //Der Hash konnte nicht berechnet werden, eine Fehlermeldung wird jedoch nicht ausgegeben, auch kommt es nicht zu einem Programmabsturz
 *  }
 *
 * Überprüfen ob ein eingegebenes Passwort nun richtig ist ließe sich beispielweise auf folgende Art und Weise erledigen (Psedudocode):
 * Einlesen des Benutzernamens in "benutzer"
 * Einlesen des Passwortes in "passwort"
 * Die "UserLib.txt" zeilenweise durchgehen und prüfen, ob...
 *  a) ...der Benutzername gleich "benutzer" ist
 *  b) ...das Passwort gleich SHA1.makeHash("passwort") ist
 * (HINWEIS: Beim Vergleichen von zwei Strings miteinander sollte statt den "=="-Operator lieber die Methode "equals()" des Strings Objekts verwendet werden, welche ein "Wahr-oder-Falsch"-Ergebnis zurückliefert.
 * String str1 = "hallo";
 * String str2 = "Welt"
 * if (str1.equals(str2) ==  false) {
 *  //Die beiden Strings stimmen nicht überein
 * }
 *
 * Um auch in der "UserLib.txt" ein "gehashtes" Passwort eintragen zu können müsst Ihr diesen Hash natürlich einmalig manuell generieren. Dies könnt Ihr beispielweise
 * mit dem beiliegenden JAR-Programm "SHA1_Hasher.jar" erledigen.
 * Bei Fragen könnt ihr diese gerne in das Forum schreiben, oder aber an mich per E-Mail!
 *
 * Viele Grüße,
 * Florian L.
 *
 * DIE KLASSE STAMMT VON UNTEN GENANNTER ADRESSE, ICH HABE SIE LEDIGLICH KOMMENTIERT UND GERINGFÜGIG STRUKTURIERT
 * @author Übernommen von http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml, strukturiert und kommentiert von Florian Loch
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Diese Klasse generiert SHA-1-Hashes von Strings.
 * Bsp.: hash = makeHash("Test");
 *       hash -> 640ab2bae07bedc4c163f679a746f7ab7fb5d1fa
 * @author Übernommen von http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml, strukturiert und kommentiert von Florian Loch
 */
public class SHA1 {

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Diese Methode dient zur Generierung des Hashes, welcher aus dem übergebenen String erstellt und ebenfalls als String zurückgegeben wird.
     * Bsp.: hash = makeHash("Test");
     * hash -> 640ab2bae07bedc4c163f679a746f7ab7fb5d1fa
     * @param String text
     * @return String
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String makeHash(String text)
        throws NoSuchAlgorithmException, UnsupportedEncodingException  {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}

