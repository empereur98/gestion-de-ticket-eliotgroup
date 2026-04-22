import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;
import repository.TicketRepositoryImplement;
import services.Statistique;
import services.TicketService;
import utils.CsvManager;

import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée de l'application Gestionnaire de Tickets IT — Eliot Group.
 * Présente un menu console couvrant toutes les fonctionnalités du cahier des charges.
 *
 * Fonctionnalités couvertes :
 *  - Créer un ticket
 *  - Lister les tickets
 *  - Rechercher un ticket par titre
 *  - Filtrer par statut
 *  - Filtrer par priorité
 *  - Assigner un ticket à un technicien
 *  - Changer le statut d'un ticket
 *  - Voir les statistiques
 *  - Importer des tickets depuis un CSV
 *  - Exporter des tickets vers un CSV
 *
 * @author Africa Tech
 * @version 1.0
 */
public class Main {

    // ── Constantes ──────────────────────────────────────────────────────────
    private static final String SEPARATEUR =
            "═══════════════════════════════════════════════════════════";
    private static final String SEPARATEUR_LEGER =
            "───────────────────────────────────────────────────────────";

    // ── Dépendances ─────────────────────────────────────────────────────────
    private static final TicketRepositoryImplement repository =
            new TicketRepositoryImplement();
    private static final TicketService service =
            new TicketService(repository);
    private static final CsvManager csvManager =
            new CsvManager();
    private static final Scanner scanner =
            new Scanner(System.in);

    // ════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        afficherBandeau();
        boolean continuer = true;

        while (continuer) {
            afficherMenu();
            String choix = lireEntree("Votre choix").trim();

            switch (choix) {
                case "1"  -> creerTicket();
                case "2"  -> listerTickets();
                case "3"  -> rechercherTicket();
                case "4"  -> filtrerParStatut();
                case "5"  -> filtrerParPriorite();
                case "6"  -> assignerTechnicien();
                case "7"  -> changerStatut();
                case "8"  -> afficherStatistiques();
                case "9"  -> importerCSV();
                case "10" -> exporterCSV();
                case "0"  -> {
                    System.out.println("\n  Au revoir ! — Africa Tech pour Eliot Group\n");
                    continuer = false;
                }
                default -> System.out.println(
                        "\n  ⚠  Choix invalide. Saisissez un nombre entre 0 et 10.\n");
            }
        }
        scanner.close();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  AFFICHAGE
    // ════════════════════════════════════════════════════════════════════════

    /** Bannière d'accueil affichée au démarrage. */
    private static void afficherBandeau() {
        System.out.println("\n" + SEPARATEUR);
        System.out.println("   GESTIONNAIRE DE TICKETS IT — ELIOT GROUP");
        System.out.println("   Développé par Africa Tech  |  v1.0");
        System.out.println(SEPARATEUR + "\n");
    }

    /** Menu principal numéroté. */
    private static void afficherMenu() {
        System.out.println(SEPARATEUR);
        System.out.println("  MENU PRINCIPAL");
        System.out.println(SEPARATEUR_LEGER);
        System.out.println("  1.  Créer un ticket");
        System.out.println("  2.  Lister tous les tickets");
        System.out.println("  3.  Rechercher un ticket (par titre)");
        System.out.println("  4.  Filtrer par statut");
        System.out.println("  5.  Filtrer par priorité");
        System.out.println(SEPARATEUR_LEGER);
        System.out.println("  6.  Assigner un ticket à un technicien");
        System.out.println("  7.  Changer le statut d'un ticket");
        System.out.println(SEPARATEUR_LEGER);
        System.out.println("  8.  Statistiques");
        System.out.println("  9.  Importer tickets depuis CSV");
        System.out.println("  10. Exporter tickets vers CSV");
        System.out.println(SEPARATEUR_LEGER);
        System.out.println("  0.  Quitter");
        System.out.println(SEPARATEUR);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  1. CRÉER UN TICKET
    // ════════════════════════════════════════════════════════════════════════
    private static void creerTicket() {
        titre("Créer un ticket");

        String titreTicket   = lireEntree("Titre du ticket");
        String description   = lireEntree("Description");
        String demandeur     = lireEntree("Demandeur (nom et prénom)");
        Service serviceEnum  = choisirService();
        Priorite prioriteEnum = choisirPriorite();

        try {
            Ticket ticket = service.createTicket(
                    titreTicket, description, demandeur, serviceEnum, prioriteEnum);
            System.out.println("\n  ✔  Ticket créé avec succès !");
            afficherTicket(ticket);
        } catch (IllegalArgumentException e) {
            System.out.println("\n  ✘  Erreur : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  2. LISTER TOUS LES TICKETS
    // ════════════════════════════════════════════════════════════════════════
    private static void listerTickets() {
        titre("Liste de tous les tickets");
        List<Ticket> tickets = service.listerTickets();

        if (tickets.isEmpty()) {
            System.out.println("  Aucun ticket enregistré.");
        } else {
            System.out.println("  " + tickets.size() + " ticket(s) trouvé(s) :\n");
            tickets.forEach(Main::afficherTicket);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  3. RECHERCHER PAR TITRE
    // ════════════════════════════════════════════════════════════════════════
    private static void rechercherTicket() {
        titre("Rechercher un ticket");
        String motCle = lireEntree("Mot-clé (dans le titre)");

        List<Ticket> resultats = service.searchByTitre(motCle);
        if (resultats.isEmpty()) {
            System.out.println("\n  Aucun ticket trouvé pour : \"" + motCle + "\"");
        } else {
            System.out.println("\n  " + resultats.size() + " résultat(s) :\n");
            resultats.forEach(Main::afficherTicket);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  4. FILTRER PAR STATUT
    // ════════════════════════════════════════════════════════════════════════
    private static void filtrerParStatut() {
        titre("Filtrer par statut");
        Statut statut = choisirStatut();

        List<Ticket> resultats = service.filterByStatus(statut).get();
        if (resultats.isEmpty()) {
            System.out.println("\n  Aucun ticket avec le statut : " + statut);
        } else {
            System.out.println("\n  " + resultats.size() + " ticket(s) [" + statut + "] :\n");
            resultats.forEach(Main::afficherTicket);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  5. FILTRER PAR PRIORITÉ
    // ════════════════════════════════════════════════════════════════════════
    private static void filtrerParPriorite() {
        titre("Filtrer par priorité");
        Priorite priorite = choisirPriorite();

        List<Ticket> resultats = service.filterByPriority(priorite);
        if (resultats.isEmpty()) {
            System.out.println("\n  Aucun ticket avec la priorité : " + priorite);
        } else {
            System.out.println("\n  " + resultats.size() + " ticket(s) [" + priorite + "] :\n");
            resultats.forEach(Main::afficherTicket);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  6. ASSIGNER UN TECHNICIEN
    // ════════════════════════════════════════════════════════════════════════
    private static void assignerTechnicien() {
        titre("Assigner un ticket à un technicien");
        String id          = lireEntree("ID du ticket");
        String technicien  = lireEntree("Nom du technicien");

        try {
            service.assignerTechnician(id, technicien);
            System.out.println("\n  ✔  Ticket [" + id + "] assigné à " +
                    technicien + ". Statut → EN_COURS.");
        } catch (Exception e) {
            System.out.println("\n  ✘  Erreur : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  7. CHANGER LE STATUT
    // ════════════════════════════════════════════════════════════════════════
    private static void changerStatut() {
        titre("Changer le statut d'un ticket");
        String id      = lireEntree("ID du ticket");
        Statut statut  = choisirStatut();

        try {
            service.changerStatut(id, statut);
            System.out.println("\n  ✔  Statut du ticket [" + id + "] → " + statut);
        } catch (Exception e) {
            System.out.println("\n  ✘  Erreur : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  8. STATISTIQUES
    // ════════════════════════════════════════════════════════════════════════
    private static void afficherStatistiques() {
        titre("Statistiques des tickets");
        List<Ticket> tous = service.listerTickets();

        if (tous.isEmpty()) {
            System.out.println("  Aucun ticket enregistré. Statistiques indisponibles.");
            return;
        }

        System.out.println("  Total tickets : " + Statistique.totalTicket(tous));
        System.out.println();

        System.out.println("  Par statut :");
        for (Statut s : Statut.values()) {
            System.out.printf("    %-12s : %d%n", s, Statistique.totalTicketByStatut(tous,s));
        }

        System.out.println("\n  Par priorité :");
        for (Priorite p : Priorite.values()) {
            System.out.printf("    %-10s : %d%n", p, Statistique.totalTicketByPriorite(tous,p));
        }

        System.out.println("\n  Par service :");
        for (Service sv : Service.values()) {
            System.out.printf("    %-14s : %d%n", sv, Statistique.totalTicketByService(tous,sv));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  9. IMPORTER CSV
    // ════════════════════════════════════════════════════════════════════════
    private static void importerCSV() {
        titre("Importer des tickets depuis un fichier CSV");
        String chemin = lireEntree("Chemin du fichier CSV");

        List<Ticket> importes = csvManager.importer(chemin);
        if (!importes.isEmpty()) {
            importes.forEach(service::sauvegarder);
            System.out.println("\n  ✔  " + importes.size() +
                    " ticket(s) importé(s) et chargé(s) en mémoire.");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  10. EXPORTER CSV
    // ════════════════════════════════════════════════════════════════════════
    private static void exporterCSV() {
        titre("Exporter les tickets vers un fichier CSV");
        List<Ticket> tous = service.listerTickets();

        if (tous.isEmpty()) {
            System.out.println("  Aucun ticket à exporter.");
            return;
        }

        try {
            csvManager.exporter(tous);
            System.out.println("\n  ✔  " + tous.size() + " ticket(s) exporté(s).");
        } catch (Exception e) {
            System.out.println("\n  ✘  Erreur export : " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPERS — SAISIE CONSOLE
    // ════════════════════════════════════════════════════════════════════════

    /** Affiche un titre de section formaté. */
    private static void titre(String texte) {
        System.out.println("\n" + SEPARATEUR);
        System.out.println("  ▶  " + texte.toUpperCase());
        System.out.println(SEPARATEUR);
    }

    /** Lit une saisie non vide depuis la console. */
    private static String lireEntree(String label) {
        String valeur;
        do {
            System.out.print("  " + label + " : ");
            valeur = scanner.nextLine().trim();
            if (valeur.isEmpty()) {
                System.out.println("  ⚠  Ce champ est obligatoire.");
            }
        } while (valeur.isEmpty());
        return valeur;
    }

    /** Menu de sélection d'un Service. */
    private static Service choisirService() {
        System.out.println("  Service :");
        Service[] services = Service.values();
        for (int i = 0; i < services.length; i++) {
            System.out.println("    " + (i + 1) + ". " + services[i]);
        }
        return choisirEnum(services, "Votre choix (service)");
    }

    /** Menu de sélection d'une Priorité. */
    private static Priorite choisirPriorite() {
        System.out.println("  Priorité :");
        Priorite[] priorites = Priorite.values();
        for (int i = 0; i < priorites.length; i++) {
            System.out.println("    " + (i + 1) + ". " + priorites[i]);
        }
        return choisirEnum(priorites, "Votre choix (priorité)");
    }

    /** Menu de sélection d'un Statut. */
    private static Statut choisirStatut() {
        System.out.println("  Statut :");
        Statut[] statuts = Statut.values();
        for (int i = 0; i < statuts.length; i++) {
            System.out.println("    " + (i + 1) + ". " + statuts[i]);
        }
        return choisirEnum(statuts, "Votre choix (statut)");
    }

    /**
     * Utilitaire générique : affiche un menu numéroté pour choisir
     * parmi les valeurs d'un enum.
     */
    private static <T> T choisirEnum(T[] valeurs, String label) {
        int choix = -1;
        while (choix < 1 || choix > valeurs.length) {
            System.out.print("  " + label + " [1-" + valeurs.length + "] : ");
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
                if (choix < 1 || choix > valeurs.length) {
                    System.out.println("  ⚠  Choix invalide.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  ⚠  Veuillez saisir un nombre.");
            }
        }
        return valeurs[choix - 1];
    }

    // ════════════════════════════════════════════════════════════════════════
    //  AFFICHAGE D'UN TICKET
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Affiche les détails d'un ticket de manière formatée dans la console.
     *
     * @param ticket le ticket à afficher
     */
    private static void afficherTicket(Ticket ticket) {
        System.out.println("  " + SEPARATEUR_LEGER);
        System.out.printf("  ID          : %s%n",  ticket.getId());
        System.out.printf("  Titre       : %s%n",  ticket.getTitle());
        System.out.printf("  Description : %s%n",  ticket.getDescription());
        System.out.printf("  Demandeur   : %s%n",  ticket.getDemandeur());
        System.out.printf("  Service     : %s%n",  ticket.getService());
        System.out.printf("  Priorité    : %s%n",  ticket.getPriorite());
        System.out.printf("  Statut      : %s%n",  ticket.getStatut());
        System.out.printf("  Technicien  : %s%n",
                ticket.getTechnicien() != null ? ticket.getTechnicien() : "Non assigné");
        System.out.printf("  Créé le     : %s%n",  ticket.getDateCreation());
        System.out.printf("  Mis à jour  : %s%n",  ticket.getDateMiseAjour());
    }
}
