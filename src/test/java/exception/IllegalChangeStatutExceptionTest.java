package exception;

import enums.Statut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - IllegalChangeStatutException")
class IllegalChangeStatutExceptionTest {

    @Test
    @DisplayName("L'exception conserve le statut actuel")
    void exceptionConserveStatutActuel() {
        IllegalChangeStatutException ex =
                new IllegalChangeStatutException(Statut.OUVERT, Statut.FERMER);
        assertEquals(Statut.OUVERT, ex.getStatutActuel());
    }

    @Test
    @DisplayName("L'exception conserve le statut demandé")
    void exceptionConserveStatutDemande() {
        IllegalChangeStatutException ex =
                new IllegalChangeStatutException(Statut.EN_COURS, Statut.OUVERT);
        assertEquals(Statut.OUVERT, ex.getStatutDemande());
    }

    @Test
    @DisplayName("Le message de l'exception contient les statuts")
    void messageContientStatuts() {
        IllegalChangeStatutException ex =
                new IllegalChangeStatutException(Statut.OUVERT, Statut.FERMER);
        assertTrue(ex.getMessage().contains("OUVERT"));
        assertTrue(ex.getMessage().contains("FERMER"));
    }

    @Test
    @DisplayName("IllegalChangeStatutException est bien un RuntimeException")
    void estUnRuntimeException() {
        assertInstanceOf(RuntimeException.class,
                new IllegalChangeStatutException(Statut.OUVERT, Statut.RESOLUE));
    }
}
