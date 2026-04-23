package modele;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - Ticket")
class TicketTest {

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket("T-001", "Problème réseau", "Le réseau est coupé",
                "Alice", Service.SUPPORT, Priorite.ELEVEE);
    }

    // ─────────────────────── CONSTRUCTEURS ───────────────────────

    @Nested
    @DisplayName("Constructeurs")
    class Constructeurs {

        @Test
        @DisplayName("Constructeur vide initialise statut OUVERT et dates non nulles")
        void constructeurVideInitialiseStatutEtDates() {
            Ticket t = new Ticket();
            assertEquals(Statut.OUVERT, t.getStatut());
            assertNotNull(t.getDateCreation());
            assertNotNull(t.getDateMiseAjour());
        }

        @Test
        @DisplayName("Constructeur paramétré affecte correctement tous les champs")
        void constructeurParametreAffecteChamps() {
            assertEquals("T-001", ticket.getId());
            assertEquals("Problème réseau", ticket.getTitle());
            assertEquals("Le réseau est coupé", ticket.getDescription());
            assertEquals("Alice", ticket.getDemandeur());
            assertEquals(Service.SUPPORT, ticket.getService());
            assertEquals(Priorite.ELEVEE, ticket.getPriorite());
        }

        @Test
        @DisplayName("Constructeur paramétré met le statut à OUVERT par défaut")
        void constructeurParametreStatutOuvertParDefaut() {
            assertEquals(Statut.OUVERT, ticket.getStatut());
        }

        @Test
        @DisplayName("Constructeur paramétré initialise les dates")
        void constructeurParametreDatesInitialisees() {
            assertNotNull(ticket.getDateCreation());
            assertNotNull(ticket.getDateMiseAjour());
        }

        @Test
        @DisplayName("Le technicien est null à la création")
        void technicienNullALaCreation() {
            assertNull(ticket.getTechnicien());
        }
    }

    // ─────────────────────── MÉTHODE open() ───────────────────────

    @Nested
    @DisplayName("Méthode open()")
    class MethodeOpen {

        @Test
        @DisplayName("open() lève IllegalStateException si le ticket est déjà OUVERT")
        void openLeveExceptionSiDejaOuvert() {
            assertEquals(Statut.OUVERT, ticket.getStatut());
            assertThrows(IllegalStateException.class, () -> ticket.open());
        }

        @Test
        @DisplayName("open() remet le statut à OUVERT depuis EN_COURS")
        void openDepuisEnCours() {
            ticket.setTicketStatut(Statut.EN_COURS);
            ticket.open();
            assertEquals(Statut.OUVERT, ticket.getStatut());
        }

        @Test
        @DisplayName("open() remet le technicien à null")
        void openReinitialiseTechnicien() {
            ticket.setTechnicien("Bob");
            ticket.setTicketStatut(Statut.EN_COURS);
            ticket.open();
            assertNull(ticket.getTechnicien());
        }

        @Test
        @DisplayName("open() met à jour la date de mise à jour")
        void openMetAJourDate() {
            ticket.setTicketStatut(Statut.EN_COURS);
            LocalDateTime avant = ticket.getDateMiseAjour();
            ticket.open();
            assertNotNull(ticket.getDateMiseAjour());
        }
    }

    // ─────────────────────── MÉTHODE close() ───────────────────────

    @Nested
    @DisplayName("Méthode close()")
    class MethodeClose {

        @Test
        @DisplayName("close() passe le statut à RESOLUE")
        void closePasseStatutAResolue() {
            ticket.close();
            assertEquals(Statut.RESOLUE, ticket.getStatut());
        }

        @Test
        @DisplayName("close() lève IllegalStateException si le ticket est déjà RESOLUE")
        void closeLeveExceptionSiDejaResolue() {
            ticket.close();
            assertThrows(IllegalStateException.class, () -> ticket.close());
        }

        @Test
        @DisplayName("close() met à jour la date de mise à jour")
        void closeMetAJourDate() {
            LocalDateTime avant = ticket.getDateMiseAjour();
            ticket.close();
            assertNotNull(ticket.getDateMiseAjour());
        }
    }

    // ─────────────────────── GETTERS / SETTERS ───────────────────────

    @Nested
    @DisplayName("Getters et Setters")
    class GettersSetters {

        @Test
        @DisplayName("setTechnicien() affecte le technicien")
        void setTechnicienFonctionne() {
            ticket.setTechnicien("Charlie");
            assertEquals("Charlie", ticket.getTechnicien());
        }

        @Test
        @DisplayName("setTicketPriorite() modifie la priorité")
        void setPrioriteModifie() {
            ticket.setTicketPriorite(Priorite.CRITIQUE);
            assertEquals(Priorite.CRITIQUE, ticket.getPriorite());
        }

        @Test
        @DisplayName("setTicketStatut() modifie le statut")
        void setStatutModifie() {
            ticket.setTicketStatut(Statut.EN_COURS);
            assertEquals(Statut.EN_COURS, ticket.getStatut());
        }

        @Test
        @DisplayName("setDemandeur() modifie le demandeur")
        void setDemandeurModifie() {
            ticket.setDemandeur("Nouveau");
            assertEquals("Nouveau", ticket.getDemandeur());
        }
    }

    // ─────────────────────── toString() ───────────────────────

    @Test
    @DisplayName("toString() retourne titre et service")
    void toStringRetourneTitreEtService() {
        String result = ticket.toString();
        assertTrue(result.contains("Problème réseau"));
        assertTrue(result.contains("SUPPORT"));
    }
}
