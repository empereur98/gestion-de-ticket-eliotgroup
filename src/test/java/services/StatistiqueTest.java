package services;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - Statistique")
class StatistiqueTest {

    private List<Ticket> tickets;

    private Ticket creerTicket(String id, Statut statut, Service service, Priorite priorite) {
        Ticket t = new Ticket(id, "Titre " + id, "Description", "Demandeur",
                service, priorite);
        t.setTicketStatut(statut);
        return t;
    }

    @BeforeEach
    void setUp() {
        tickets = new ArrayList<>();
        // 3 OUVERT, 2 EN_COURS, 1 RESOLUE
        tickets.add(creerTicket("1", Statut.OUVERT,   Service.SUPPORT,    Priorite.ELEVEE));
        tickets.add(creerTicket("2", Statut.OUVERT,   Service.RH,         Priorite.FAIBLE));
        tickets.add(creerTicket("3", Statut.OUVERT,   Service.COMPTABILITES, Priorite.CRITIQUE));
        tickets.add(creerTicket("4", Statut.EN_COURS, Service.SUPPORT,    Priorite.MOYEN));
        tickets.add(creerTicket("5", Statut.EN_COURS, Service.LOGISTIQUE, Priorite.FAIBLE));
        tickets.add(creerTicket("6", Statut.RESOLUE,  Service.SUPPORT,    Priorite.ELEVEE));
    }

    // ─────────── totalTicket() ───────────

    @Nested
    @DisplayName("totalTicket()")
    class TotalTicket {

        @Test
        @DisplayName("Retourne le nombre total de tickets")
        void retourneTotalCorrect() {
            assertEquals(6, Statistique.totalTicket(tickets));
        }

        @Test
        @DisplayName("Retourne 0 pour une liste vide")
        void retourneZeroListeVide() {
            assertEquals(0, Statistique.totalTicket(new ArrayList<>()));
        }

        @Test
        @DisplayName("Retourne 1 pour une liste d'un ticket")
        void retourneUnPourUnTicket() {
            assertEquals(1, Statistique.totalTicket(List.of(tickets.get(0))));
        }
    }

    // ─────────── totalTicketByStatut() ───────────

    @Nested
    @DisplayName("totalTicketByStatut()")
    class TotalTicketByStatut {

        @Test
        @DisplayName("Compte correctement les tickets OUVERT")
        void compteTicketsOuvert() {
            assertEquals(3, Statistique.totalTicketByStatut(tickets, Statut.OUVERT));
        }

        @Test
        @DisplayName("Compte correctement les tickets EN_COURS")
        void compteTicketsEnCours() {
            assertEquals(2, Statistique.totalTicketByStatut(tickets, Statut.EN_COURS));
        }

        @Test
        @DisplayName("Compte correctement les tickets RESOLUE")
        void compteTicketsResolue() {
            assertEquals(1, Statistique.totalTicketByStatut(tickets, Statut.RESOLUE));
        }

        @Test
        @DisplayName("Retourne 0 si aucun ticket avec ce statut")
        void retourneZeroSiAucun() {
            assertEquals(0, Statistique.totalTicketByStatut(tickets, Statut.FERMER));
        }

        @Test
        @DisplayName("Retourne 0 pour liste vide")
        void retourneZeroListeVide() {
            assertEquals(0, Statistique.totalTicketByStatut(new ArrayList<>(), Statut.OUVERT));
        }
    }

    // ─────────── totalTicketByService() ───────────

    @Nested
    @DisplayName("totalTicketByService()")
    class TotalTicketByService {

        @Test
        @DisplayName("Compte les tickets du service SUPPORT")
        void compteTicketsSupport() {
            // tickets 1, 4, 6 sont SUPPORT
            assertEquals(3, Statistique.totalTicketByService(tickets, Service.SUPPORT));
        }

        @Test
        @DisplayName("Retourne 0 pour un service sans tickets")
        void retourneZeroServiceSansTickets() {
            assertEquals(0, Statistique.totalTicketByService(tickets, Service.DIRECTION));
        }

        @Test
        @DisplayName("Retourne 0 pour liste vide")
        void retourneZeroListeVide() {
            assertEquals(0, Statistique.totalTicketByService(new ArrayList<>(), Service.SUPPORT));
        }
    }

    // ─────────── totalTicketByPriorite() ───────────

    @Nested
    @DisplayName("totalTicketByPriorite()")
    class TotalTicketByPriorite {

        @Test
        @DisplayName("Compte les tickets ELEVEE")
        void compteTicketsElevee() {
            // tickets 1 et 6 sont ELEVEE
            assertEquals(2, Statistique.totalTicketByPriorite(tickets, Priorite.ELEVEE));
        }

        @Test
        @DisplayName("Compte les tickets FAIBLE")
        void compteTicketsFaible() {
            // tickets 2 et 5 sont FAIBLE
            assertEquals(2, Statistique.totalTicketByPriorite(tickets, Priorite.FAIBLE));
        }

        @Test
        @DisplayName("Retourne 0 pour une priorité sans tickets")
        void retourneZeroPrioriteSansTickets() {
            // MOYEN = ticket 4 seulement, CRITIQUE = ticket 3
            assertEquals(0, Statistique.totalTicketByPriorite(new ArrayList<>(), Priorite.MOYEN));
        }
    }

    // ─────────── listTicketByStatut() ───────────

    @Nested
    @DisplayName("listTicketByStatut()")
    class ListTicketByStatut {

        @Test
        @DisplayName("Retourne la liste des tickets OUVERT")
        void listeTicketsOuvert() {
            List<Ticket> result = Statistique.listTicketByStatut(tickets, Statut.OUVERT);
            assertEquals(3, result.size());
            assertTrue(result.stream().allMatch(t -> t.getStatut() == Statut.OUVERT));
        }

        @Test
        @DisplayName("Retourne une liste vide si aucun ticket FERMER")
        void listeVideSiAucunFermer() {
            List<Ticket> result = Statistique.listTicketByStatut(tickets, Statut.FERMER);
            assertTrue(result.isEmpty());
        }
    }

    // ─────────── listTicketByPriority() ───────────

    @Nested
    @DisplayName("listTicketByPriority()")
    class ListTicketByPriority {

        @Test
        @DisplayName("Retourne les tickets CRITIQUE")
        void listeTicketsCritique() {
            List<Ticket> result = Statistique.listTicketByPriority(tickets, Priorite.CRITIQUE);
            assertEquals(1, result.size());
            assertEquals(Priorite.CRITIQUE, result.get(0).getPriorite());
        }

        @Test
        @DisplayName("Retourne liste vide si aucun ticket avec cette priorité dans liste vide")
        void listeVideSiAucun() {
            List<Ticket> result = Statistique.listTicketByPriority(new ArrayList<>(), Priorite.CRITIQUE);
            assertTrue(result.isEmpty());
        }
    }
}
