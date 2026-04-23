package repository;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - TicketRepositoryImplement")
class TicketRepositoryImplementTest {

    private TicketRepositoryImplement repository;
    private Ticket ticket1;
    private Ticket ticket2;

    @BeforeEach
    void setUp() {
        repository = new TicketRepositoryImplement();
        ticket1 = new Ticket("ID-001", "Bug login", "Impossible de se connecter",
                "Alice", Service.SUPPORT, Priorite.ELEVEE);
        ticket2 = new Ticket("ID-002", "Imprimante HS", "Imprimante ne fonctionne pas",
                "Bob", Service.LOGISTIQUE, Priorite.MOYEN);
    }

    // ─────────── sauvegarder() ───────────

    @Nested
    @DisplayName("sauvegarder()")
    class Sauvegarder {

        @Test
        @DisplayName("Sauvegarde un ticket valide")
        void sauvegardeTicketValide() {
            repository.sauvegarder(ticket1);
            Optional<Ticket> found = repository.findById("ID-001");
            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("Ne lève pas d'exception si le ticket est null")
        void pasExceptionSiNull() {
            assertDoesNotThrow(() -> repository.sauvegarder(null));
        }

        @Test
        @DisplayName("Écrase un ticket existant avec le même ID")
        void ecraseTicketExistant() {
            repository.sauvegarder(ticket1);
            Ticket updated = new Ticket("ID-001", "Bug modifié", "description",
                    "Alice", Service.SUPPORT, Priorite.CRITIQUE);
            repository.sauvegarder(updated);
            Optional<Ticket> found = repository.findById("ID-001");
            assertTrue(found.isPresent());
            assertEquals("Bug modifié", found.get().getTitle());
        }
    }

    // ─────────── findAll() ───────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Retourne une liste vide si aucun ticket")
        void listeVideSiAucunTicket() {
            assertTrue(repository.findAll().isEmpty());
        }

        @Test
        @DisplayName("Retourne tous les tickets sauvegardés")
        void retourneTousLesTickets() {
            repository.sauvegarder(ticket1);
            repository.sauvegarder(ticket2);
            List<Ticket> result = repository.findAll();
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("findAll() retourne une copie défensive")
        void retourneCopieDéfensive() {
            repository.sauvegarder(ticket1);
            List<Ticket> list1 = repository.findAll();
            List<Ticket> list2 = repository.findAll();
            assertNotSame(list1, list2);
        }
    }

    // ─────────── findById() ───────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Retourne le ticket pour un ID existant")
        void retourneTicketExistant() {
            repository.sauvegarder(ticket1);
            Optional<Ticket> result = repository.findById("ID-001");
            assertTrue(result.isPresent());
            assertEquals("Bug login", result.get().getTitle());
        }

        @Test
        @DisplayName("Retourne Optional vide pour un ID inexistant")
        void retourneVidePourIdInexistant() {
            Optional<Ticket> result = repository.findById("INEXISTANT");
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Retourne Optional vide pour un ID null")
        void retourneVidePourNull() {
            Optional<Ticket> result = repository.findById(null);
            assertFalse(result.isPresent());
        }
    }

    // ─────────── supprimer() ───────────

    @Nested
    @DisplayName("supprimer()")
    class Supprimer {

        @Test
        @DisplayName("Supprime un ticket existant")
        void supprimeTicketExistant() {
            repository.sauvegarder(ticket1);
            repository.supprimer("ID-001");
            assertFalse(repository.findById("ID-001").isPresent());
        }

        @Test
        @DisplayName("Ne lève pas d'exception si l'ID n'existe pas")
        void pasExceptionSiIdInexistant() {
            assertDoesNotThrow(() -> repository.supprimer("ID-INEXISTANT"));
        }

        @Test
        @DisplayName("Ne supprime pas les autres tickets")
        void neSupprimePassAutresTickets() {
            repository.sauvegarder(ticket1);
            repository.sauvegarder(ticket2);
            repository.supprimer("ID-001");
            assertTrue(repository.findById("ID-002").isPresent());
        }
    }
}
