package utils;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests - CsvManager")
class CsvManagerTest {

    private CsvManager csvManager;
    private Path tempCsvFile;

    @BeforeEach
    void setUp() throws IOException {
        csvManager = new CsvManager();
        // Crée un fichier CSV temporaire pour les tests d'import
        tempCsvFile = Files.createTempFile("tickets_test_", ".csv");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Nettoyage du fichier temporaire
        Files.deleteIfExists(tempCsvFile);
        // Nettoyage du fichier d'export si créé par CsvManager
        Path exportPath = Path.of("src/ressource/ticket.csv");
        // On ne supprime pas le fichier source du projet
    }

    // ─────────── importer() ───────────

    @Nested
    @DisplayName("importer()")
    class Importer {

        @Test
        @DisplayName("Retourne liste vide si le fichier n'existe pas")
        void retourneListeVideSiFichierInexistant() {
            List<Ticket> result = csvManager.importer("fichier_inexistant_xyz.csv");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Importe correctement un ticket depuis un CSV valide")
        void importeTicketDepuisCSVValide() throws IOException {
            // CSV avec en-tête + 1 ticket
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "abc-123;Bug réseau;Le réseau est coupé;Alice;SUPPORT;ELEVEE;OUVERT;" +
                    "2024-01-15T10:00:00;2024-01-15T10:00:00;\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertEquals(1, result.size());

            Ticket t = result.get(0);
            assertEquals("abc-123", t.getId());
            assertEquals("Bug réseau", t.getTitle());
            assertEquals("Alice", t.getDemandeur());
            assertEquals(Service.SUPPORT, t.getService());
            assertEquals(Priorite.ELEVEE, t.getPriorite());
            assertEquals(Statut.OUVERT, t.getStatut());
        }

        @Test
        @DisplayName("Importe plusieurs tickets depuis un CSV")
        void importePlusieursTickets() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-1;Ticket 1;desc1;Alice;RH;FAIBLE;OUVERT;2024-01-01T08:00:00;2024-01-01T08:00:00;\n" +
                    "id-2;Ticket 2;desc2;Bob;SUPPORT;CRITIQUE;EN_COURS;2024-01-02T09:00:00;2024-01-02T09:00:00;Tech1\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Ignore les lignes vides dans le CSV")
        void ignoreLignesVides() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-1;Ticket 1;desc1;Alice;RH;FAIBLE;OUVERT;2024-01-01T08:00:00;2024-01-01T08:00:00;\n" +
                    "\n" +
                    "\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Importe un ticket avec technicien assigné")
        void importeTicketAvecTechnicien() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-1;Ticket 1;desc1;Alice;SUPPORT;MOYEN;EN_COURS;" +
                    "2024-03-10T12:00:00;2024-03-10T14:00:00;Jean Dupont\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertEquals(1, result.size());
            assertEquals("Jean Dupont", result.get(0).getTechnicien());
        }

        @Test
        @DisplayName("Importe un ticket sans technicien (champ vide)")
        void importeTicketSansTechnicien() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-1;Ticket 1;desc1;Alice;SUPPORT;MOYEN;OUVERT;" +
                    "2024-03-10T12:00:00;2024-03-10T12:00:00;\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertNull(result.get(0).getTechnicien());
        }

        @Test
        @DisplayName("Saute les lignes mal formées sans planter")
        void sauteLignesMalFormees() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "ligne_invalide_sans_colonnes\n" +
                    "id-2;Ticket 2;desc2;Bob;SUPPORT;CRITIQUE;EN_COURS;" +
                    "2024-01-02T09:00:00;2024-01-02T09:00:00;Tech1\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            // Ne doit pas lever d'exception
            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            // Au moins le ticket valide est importé
            assertFalse(result.isEmpty());
        }

        @Test
        @DisplayName("Retourne liste vide pour un CSV ne contenant que l'en-tête")
        void retourneVidePourCSVAvecSeulementEnTete() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertTrue(result.isEmpty());
        }
    }

    // ─────────── exporter() ───────────

    @Nested
    @DisplayName("exporter()")
    class Exporter {

        @Test
        @DisplayName("Ne lève pas d'exception pour une liste vide")
        void pasExceptionListeVide() {
            assertDoesNotThrow(() -> csvManager.exporter(List.of()));
        }

        @Test
        @DisplayName("Ne lève pas d'exception pour une liste null")
        void pasExceptionListeNull() {
            assertDoesNotThrow(() -> csvManager.exporter(null));
        }

        @Test
        @DisplayName("exporter() ne lève pas d'exception pour un ticket valide")
        void exporterTicketValide() {
            Ticket t = new Ticket("EXP-001", "Export test", "desc",
                    "Alice", Service.SUPPORT, Priorite.ELEVEE);
            assertDoesNotThrow(() -> csvManager.exporter(List.of(t)));
        }
    }

    // ─────────── Aller-retour import/export ───────────

    @Nested
    @DisplayName("Cohérence import / export")
    class AllerRetour {

        @Test
        @DisplayName("Un ticket importé a le bon statut")
        void ticketImporteABonStatut() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-resolue;Bug critique;Description;Alice;DIRECTION;CRITIQUE;RESOLUE;" +
                    "2025-06-01T08:30:00;2025-06-02T16:45:00;\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            assertEquals(Statut.RESOLUE, result.get(0).getStatut());
        }

        @Test
        @DisplayName("Un ticket importé conserve les dates")
        void ticketImporteConserveDates() throws IOException {
            String contenu = "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien\n" +
                    "id-dt;Dates;desc;Bob;LOGISTIQUE;MOYEN;OUVERT;" +
                    "2025-01-15T08:00:00;2025-01-20T18:30:00;\n";
            Files.writeString(tempCsvFile, contenu, StandardCharsets.UTF_8);

            List<Ticket> result = csvManager.importer(tempCsvFile.toString());
            Ticket t = result.get(0);
            assertNotNull(t.getDateCreation());
            assertNotNull(t.getDateMiseAjour());
            assertEquals(2025, t.getDateCreation().getYear());
        }
    }
}
