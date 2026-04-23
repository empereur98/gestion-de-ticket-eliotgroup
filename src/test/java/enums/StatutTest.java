package enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - Statut (transitions)")
class StatutTest {

    // ─────────── Transitions VALIDES ───────────

    @Test
    @DisplayName("OUVERT peut transitionner vers EN_COURS")
    void ouvertVersEnCours() {
        assertTrue(Statut.OUVERT.peutTransitionnerVers(Statut.EN_COURS));
    }

    @Test
    @DisplayName("EN_COURS peut transitionner vers RESOLUE")
    void enCoursVersResolue() {
        assertTrue(Statut.EN_COURS.peutTransitionnerVers(Statut.RESOLUE));
    }

    @Test
    @DisplayName("RESOLUE peut transitionner vers FERMER")
    void resolueVersFermer() {
        assertTrue(Statut.RESOLUE.peutTransitionnerVers(Statut.FERMER));
    }

    // ─────────── Transitions INVALIDES ───────────

    @Test
    @DisplayName("OUVERT ne peut pas transitionner vers RESOLUE")
    void ouvertNePeutPasAllerVersResolue() {
        assertFalse(Statut.OUVERT.peutTransitionnerVers(Statut.RESOLUE));
    }

    @Test
    @DisplayName("OUVERT ne peut pas transitionner vers FERMER")
    void ouvertNePeutPasAllerVersFermer() {
        assertFalse(Statut.OUVERT.peutTransitionnerVers(Statut.FERMER));
    }

    @Test
    @DisplayName("OUVERT ne peut pas transitionner vers lui-même")
    void ouvertNePeutPasAllerVersLuiMeme() {
        assertFalse(Statut.OUVERT.peutTransitionnerVers(Statut.OUVERT));
    }

    @Test
    @DisplayName("EN_COURS ne peut pas transitionner vers OUVERT")
    void enCoursNePeutPasAllerVersOuvert() {
        assertFalse(Statut.EN_COURS.peutTransitionnerVers(Statut.OUVERT));
    }

    @Test
    @DisplayName("EN_COURS ne peut pas transitionner vers FERMER")
    void enCoursNePeutPasAllerVersFermer() {
        assertFalse(Statut.EN_COURS.peutTransitionnerVers(Statut.FERMER));
    }

    @Test
    @DisplayName("RESOLUE ne peut pas transitionner vers OUVERT")
    void resolueNePeutPasAllerVersOuvert() {
        assertFalse(Statut.RESOLUE.peutTransitionnerVers(Statut.OUVERT));
    }

    @Test
    @DisplayName("RESOLUE ne peut pas transitionner vers EN_COURS")
    void resolueNePeutPasAllerVersEnCours() {
        assertFalse(Statut.RESOLUE.peutTransitionnerVers(Statut.EN_COURS));
    }

    @Test
    @DisplayName("FERMER (statut final) ne peut transitionner vers aucun statut")
    void fermerNePeutTransitionnerVersRien() {
        for (Statut s : Statut.values()) {
            assertFalse(Statut.FERMER.peutTransitionnerVers(s),
                    "FERMER ne devrait pas pouvoir transitionner vers " + s);
        }
    }

    // ─────────── Ordres ───────────

    @Test
    @DisplayName("Les ordres des statuts sont corrects")
    void ordresCorrects() {
        assertEquals(1, Statut.OUVERT.getOrdre());
        assertEquals(2, Statut.EN_COURS.getOrdre());
        assertEquals(3, Statut.RESOLUE.getOrdre());
        assertEquals(4, Statut.FERMER.getOrdre());
    }

    @Test
    @DisplayName("L'enum contient exactement 4 statuts")
    void enumContient4Statuts() {
        assertEquals(4, Statut.values().length);
    }
}
