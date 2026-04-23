package services;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import exception.IllegalChangeStatutException;
import modele.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TicketRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Tests d'intégration - Flux complets")
@ExtendWith(MockitoExtension.class)
class TicketServiceIntegrationTest {

    @Mock
    private TicketRepository mockRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() throws IOException {
        doNothing().when(mockRepository).importerCSV(anyString());
        ticketService = new TicketService(mockRepository);
    }

    @Nested
    @DisplayName("Flux : Cycle de vie complet d'un ticket")
    class CycleDeVie {

        @Test
        @DisplayName("Un ticket peut passer de OUVERT à EN_COURS à RESOLUE")
        void cycleCompletOuvertEnCoursResolue() throws IllegalChangeStatutException {
            Ticket ticket = new Ticket("T-CYC", "Cycle test", "desc",
                    "Alice", Service.SUPPORT, Priorite.ELEVEE);
            when(mockRepository.findById("T-CYC")).thenReturn(Optional.of(ticket));

            // OUVERT -> EN_COURS
            ticketService.changerStatut("T-CYC", Statut.EN_COURS);
            assertEquals(Statut.EN_COURS, ticket.getStatut());

            // EN_COURS -> RESOLUE
            ticketService.changerStatut("T-CYC", Statut.RESOLUE);
            assertEquals(Statut.RESOLUE, ticket.getStatut());

            // Le repository a été appelé 2 fois
            verify(mockRepository, times(2)).sauvegarder(ticket);
        }

        @Test
        @DisplayName("Impossible de passer directement de OUVERT à RESOLUE")
        void ouvertNePeutPasAllerDirectementEnResolue() {
            Ticket ticket = new Ticket("T-SKIP", "Skip test", "desc",
                    "Bob", Service.RH, Priorite.FAIBLE);
            when(mockRepository.findById("T-SKIP")).thenReturn(Optional.of(ticket));

            assertThrows(IllegalChangeStatutException.class,
                    () -> ticketService.changerStatut("T-SKIP", Statut.RESOLUE));
        }
    }

    @Nested
    @DisplayName("Flux : Création et assignation")
    class CreationEtAssignation {

        @Test
        @DisplayName("Ticket créé et technicien assigné sont cohérents")
        void creerPuisAssigner() {
            Ticket ticket = TicketService.createTicket(
                    "Problème VPN", "VPN ne fonctionne pas", "Alice",
                    Service.SUPPORT, Priorite.CRITIQUE);

            when(mockRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
            TicketService.assignerTechnician(ticket.getId(), "Tech Martin");

            assertEquals("Tech Martin", ticket.getTechnicien());
        }
    }

    @Nested
    @DisplayName("Flux : Suppression")
    class FluxSuppression {

        @Test
        @DisplayName("Supprimer un ticket fait bien appel au repository")
        void supprimerAppelleRepository() {
            Ticket ticket = new Ticket("T-DEL", "À supprimer", "desc",
                    "Alice", Service.RH, Priorite.FAIBLE);
            when(mockRepository.findById("T-DEL")).thenReturn(Optional.of(ticket));

            ticketService.supprimerTicket("T-DEL");
            verify(mockRepository).supprimer("T-DEL");
        }

        @Test
        @DisplayName("Supprimer un ticket inexistant ne supprime rien")
        void supprimerInexistantNeFaitRien() {
            when(mockRepository.findById("GHOST")).thenReturn(Optional.empty());
            ticketService.supprimerTicket("GHOST");
            verify(mockRepository, never()).supprimer(any());
        }
    }
}
