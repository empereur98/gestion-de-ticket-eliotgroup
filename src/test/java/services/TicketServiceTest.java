package services;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import exception.IllegalChangeStatutException;
import modele.Ticket;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TicketRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests - TicketService")
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository mockRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() throws IOException {
        doNothing().when(mockRepository).importerCSV(anyString());
        ticketService = new TicketService(mockRepository);
    }

    // ─────────── createTicket() ───────────

    @Nested
    @DisplayName("createTicket()")
    class CreateTicket {

        @Test
        @DisplayName("Crée un ticket avec tous les champs valides")
        void creerTicketValide() {
            Ticket ticket = TicketService.createTicket(
                    "Titre", "Description valide", "Alice", Service.SUPPORT, Priorite.MOYEN);
            assertNotNull(ticket);
            assertEquals("Titre", ticket.getTitle());
            assertEquals("Alice", ticket.getDemandeur());
            assertEquals(Service.SUPPORT, ticket.getService());
            assertEquals(Priorite.MOYEN, ticket.getPriorite());
        }

        @Test
        @DisplayName("Le ticket créé a un ID non null")
        void ticketAUnIdNonNull() {
            Ticket ticket = TicketService.createTicket(
                    "Titre", "Description", "Bob", Service.RH, Priorite.FAIBLE);
            assertNotNull(ticket.getId());
            assertFalse(ticket.getId().isBlank());
        }

        @Test
        @DisplayName("Le ticket créé a le statut OUVERT")
        void ticketStatutOuvertParDefaut() {
            Ticket ticket = TicketService.createTicket(
                    "Titre", "Description", "Bob", Service.RH, Priorite.FAIBLE);
            assertEquals(Statut.OUVERT, ticket.getStatut());
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si le titre est null")
        void leveExceptionTitreNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket(null, "Description", "Alice", Service.SUPPORT, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si le titre est vide")
        void leveExceptionTitreVide() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("  ", "Description", "Alice", Service.SUPPORT, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si la description est null")
        void leveExceptionDescriptionNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("Titre", null, "Alice", Service.SUPPORT, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si la description est vide")
        void leveExceptionDescriptionVide() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("Titre", "   ", "Alice", Service.SUPPORT, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si le demandeur est null")
        void leveExceptionDemandeurNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("Titre", "Description", null, Service.SUPPORT, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si le service est null")
        void leveExceptionServiceNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("Titre", "Description", "Alice", null, Priorite.MOYEN));
        }

        @Test
        @DisplayName("Lève IllegalArgumentException si la priorité est null")
        void leveExceptionPrioriteNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    TicketService.createTicket("Titre", "Description", "Alice", Service.SUPPORT, null));
        }
    }

    // ─────────── changerStatut() ───────────

    @Nested
    @DisplayName("changerStatut()")
    class ChangerStatut {

        @Test
        @DisplayName("Transition valide OUVERT -> EN_COURS")
        void transitionValideOuvertVersEnCours() throws IllegalChangeStatutException {
            Ticket ticket = new Ticket("T-1", "titre", "desc", "Alice", Service.SUPPORT, Priorite.MOYEN);
            when(mockRepository.findById("T-1")).thenReturn(Optional.of(ticket));

            assertDoesNotThrow(() -> ticketService.changerStatut("T-1", Statut.EN_COURS));
            verify(mockRepository).sauvegarder(ticket);
        }

        @Test
        @DisplayName("Transition invalide OUVERT -> FERMER lève IllegalChangeStatutException")
        void transitionInvalideLeveException() {
            Ticket ticket = new Ticket("T-2", "titre", "desc", "Alice", Service.SUPPORT, Priorite.MOYEN);
            when(mockRepository.findById("T-2")).thenReturn(Optional.of(ticket));

            assertThrows(IllegalChangeStatutException.class,
                    () -> ticketService.changerStatut("T-2", Statut.FERMER));
        }

        @Test
        @DisplayName("Ticket inexistant lève IllegalArgumentException")
        void ticketInexistantLeveException() {
            when(mockRepository.findById("XXXX")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> ticketService.changerStatut("XXXX", Statut.EN_COURS));
        }

        @Test
        @DisplayName("Statut null lève IllegalArgumentException")
        void statutNullLeveException() {
            Ticket ticket = new Ticket("T-3", "titre", "desc", "Alice", Service.SUPPORT, Priorite.MOYEN);
            when(mockRepository.findById("T-3")).thenReturn(Optional.of(ticket));

            assertThrows(IllegalArgumentException.class,
                    () -> ticketService.changerStatut("T-3", null));
        }
    }

    // ─────────── listerTickets() ───────────

    @Nested
    @DisplayName("listerTickets()")
    class ListerTickets {

        @Test
        @DisplayName("Retourne la liste complète du repository")
        void retourneListeComplete() {
            Ticket t1 = new Ticket("T-1", "t", "d", "A", Service.RH, Priorite.FAIBLE);
            Ticket t2 = new Ticket("T-2", "t", "d", "B", Service.SUPPORT, Priorite.ELEVEE);
            when(mockRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

            List<Ticket> result = ticketService.listerTickets();
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Retourne une liste vide si repository est vide")
        void retourneListeVideSiVide() {
            when(mockRepository.findAll()).thenReturn(List.of());
            assertTrue(ticketService.listerTickets().isEmpty());
        }
    }

    // ─────────── searchByTitre() ───────────

    @Nested
    @DisplayName("searchByTitre()")
    class SearchByTitre {

        @Test
        @DisplayName("Trouve les tickets correspondant au titre exact")
        void trouveTicketsParTitre() {
            Ticket t1 = new Ticket("T-1", "Bug réseau", "d", "A", Service.SUPPORT, Priorite.ELEVEE);
            Ticket t2 = new Ticket("T-2", "Autre bug", "d", "B", Service.RH, Priorite.FAIBLE);
            when(mockRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

            List<Ticket> result = ticketService.searchByTitre("Bug réseau");
            assertEquals(1, result.size());
            assertEquals("Bug réseau", result.get(0).getTitle());
        }

        @Test
        @DisplayName("Retourne liste vide si aucun ticket ne correspond")
        void retourneVideSiAucunMatch() {
            Ticket t1 = new Ticket("T-1", "Bug réseau", "d", "A", Service.SUPPORT, Priorite.ELEVEE);
            when(mockRepository.findAll()).thenReturn(List.of(t1));

            List<Ticket> result = ticketService.searchByTitre("Titre introuvable");
            assertTrue(result.isEmpty());
        }
    }

    // ─────────── supprimerTicket() ───────────

    @Nested
    @DisplayName("supprimerTicket()")
    class SupprimerTicket {

        @Test
        @DisplayName("Supprime un ticket existant")
        void supprimeTicketExistant() {
            Ticket t = new Ticket("T-1", "titre", "desc", "A", Service.SUPPORT, Priorite.MOYEN);
            when(mockRepository.findById("T-1")).thenReturn(Optional.of(t));

            ticketService.supprimerTicket("T-1");
            verify(mockRepository).supprimer("T-1");
        }

        @Test
        @DisplayName("Ne supprime pas si le ticket n'existe pas")
        void neSupprimePassSiInexistant() {
            when(mockRepository.findById("T-XXX")).thenReturn(Optional.empty());
            ticketService.supprimerTicket("T-XXX");
            verify(mockRepository, never()).supprimer(any());
        }

        @Test
        @DisplayName("Ne lève pas d'exception si l'ID est null")
        void pasExceptionSiNull() {
            assertDoesNotThrow(() -> ticketService.supprimerTicket(null));
            verify(mockRepository, never()).supprimer(any());
        }
    }

    // ─────────── assignerTechnician() ───────────

    @Nested
    @DisplayName("assignerTechnician()")
    class AssignerTechnician {

        @Test
        @DisplayName("Assigne un technicien à un ticket existant")
        void assigneTechnicienTicketExistant() {
            Ticket t = new Ticket("T-1", "titre", "desc", "A", Service.SUPPORT, Priorite.MOYEN);
            when(mockRepository.findById("T-1")).thenReturn(Optional.of(t));

            TicketService.assignerTechnician("T-1", "Technicien Bob");
            assertEquals("Technicien Bob", t.getTechnicien());
            verify(mockRepository).sauvegarder(t);
        }

        @Test
        @DisplayName("Ne lève pas d'exception si technicien est null")
        void pasExceptionSiTechnicienNull() {
            assertDoesNotThrow(() -> TicketService.assignerTechnician("T-1", null));
            verify(mockRepository, never()).sauvegarder(any());
        }

        @Test
        @DisplayName("Ne lève pas d'exception si ticketId est null")
        void pasExceptionSiTicketIdNull() {
            assertDoesNotThrow(() -> TicketService.assignerTechnician(null, "Bob"));
            verify(mockRepository, never()).sauvegarder(any());
        }

        @Test
        @DisplayName("Ne lève pas d'exception si le ticket n'existe pas")
        void pasExceptionSiTicketInexistant() {
            when(mockRepository.findById("T-XXXX")).thenReturn(Optional.empty());
            assertDoesNotThrow(() -> TicketService.assignerTechnician("T-XXXX", "Bob"));
            verify(mockRepository, never()).sauvegarder(any());
        }
    }
}
