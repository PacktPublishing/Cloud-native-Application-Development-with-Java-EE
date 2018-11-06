package cloud.nativ.javaee;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@Builder
public class Buchstabiertafel {

    private final String language;

    @Singular("map")
    private final Map<String, String> tafel;

    /**
     * Get the mapped name for the given character.
     *
     * @param character the character
     * @return the mapped name or NULL
     */
    String get(String character) {
        return tafel.get(character.toUpperCase());
    }

    static Buchstabiertafel din5009() {
        return Buchstabiertafel.builder()
                .language("de")
                .map("A", "Anton")
                .map("B", "Berta")
                .map("C", "Cäsar")
                .map("D", "Dora")
                .map("E", "Emil")
                .map("F", "Friedrich")
                .map("G", "Gustav")
                .map("H", "Heinrich")
                .map("I", "Ida")
                .map("J", "Julius")
                .map("K", "Kaufmann")
                .map("L", "Ludwig")
                .map("M", "Martha")
                .map("N", "Nordpol")
                .map("O", "Otto")
                .map("P", "Paula")
                .map("Q", "Quelle")
                .map("R", "Richard")
                .map("S", "Siegfried")
                .map("T", "Theodor")
                .map("U", "Ulrich")
                .map("V", "Viktor")
                .map("W", "Wilhelm")
                .map("X", "Xanthippe")
                .map("Y", "Ypsilon")
                .map("Z", "Zeppelin")
                .map("Ä", "Ärger")
                .map("Ö", "Ökonom")
                .map("Ü", "Übermut")
                .map("ß", "Eszett")
                .build();
    }

    static Buchstabiertafel nato() {
        return Buchstabiertafel.builder()
                .language("en")
                .map("A", "Alfa")
                .map("B", "Bravo")
                .map("C", "Charlie")
                .map("D", "Delta")
                .map("E", "Echo")
                .map("F", "Foxtrot")
                .map("G", "Golf")
                .map("H", "Hotel")
                .map("I", "India")
                .map("J", "Juliett")
                .map("K", "Kilo")
                .map("L", "Lima")
                .map("M", "Mike")
                .map("N", "November")
                .map("O", "Oscar")
                .map("P", "Papa")
                .map("Q", "Quebec")
                .map("R", "Romeo")
                .map("S", "Sierra")
                .map("T", "Tango")
                .map("U", "Uniform")
                .map("V", "Victor")
                .map("W", "Whiskey")
                .map("X", "X-Ray")
                .map("Y", "Yankee")
                .map("Z", "Zulu")
                .build();
    }
}
