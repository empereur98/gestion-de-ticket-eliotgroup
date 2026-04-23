package utils;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CsvManager {

    // FIX #6 : slash manquant corrigé
    private static final String DOSSIER  = "src/ressource";
    private static final String FICHIER  = DOSSIER + "/" + "ticket.csv";
    private static final Path   RESSOURCE = Path.of(FICHIER);

    private static final String            DELIMITER   = ";";
    // FIX #2 : même formatter pour écriture ET lecture
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String EN_TETE =
            "id;titre;description;demandeur;service;priorite;statut;" +
                    "dateCreation;dateMiseAJour;technicien";

    // ─────────────────────────────── EXPORT ──────────────────────────────────

    public void exporter(List<Ticket> tickets) throws IOException {
        if (tickets == null || tickets.isEmpty()) {
            System.out.println("Rien à exporter.");
            return;
        }

        // FIX #5 : on crée le dossier si nécessaire, on ne bloque plus
        Files.createDirectories(Path.of(DOSSIER));

        boolean fichierExiste = Files.exists(RESSOURCE)
                && Files.size(RESSOURCE) > 0;

        try (BufferedWriter writer = Files.newBufferedWriter(
                RESSOURCE,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            if (!fichierExiste) {
                writer.write(EN_TETE);
                writer.newLine();
            }

            for (Ticket ticket : tickets) {
                writer.write(convertirEnLigneCSV(ticket));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Erreur lors de l'export CSV : " + e.getMessage(), e);
        }

        System.out.println(tickets.size() +
                " ticket(s) exporté(s) vers : " + FICHIER);
    }

    private String convertirEnLigneCSV(Ticket ticket) {
        return escapeCsv(ticket.getId())              + DELIMITER +
                escapeCsv(ticket.getTitle())            + DELIMITER +
                escapeCsv(ticket.getDescription())      + DELIMITER +
                escapeCsv(ticket.getDemandeur())        + DELIMITER +
                (ticket.getService()  != null ? ticket.getService().name()  : "") + DELIMITER +
                (ticket.getPriorite() != null ? ticket.getPriorite().name() : "") + DELIMITER +
                (ticket.getStatut()   != null ? ticket.getStatut().name()   : "") + DELIMITER +
                (ticket.getDateCreation()  != null
                        ? ticket.getDateCreation().format(DATE_FORMAT)  : "") + DELIMITER +
                (ticket.getDateMiseAjour() != null
                        ? ticket.getDateMiseAjour().format(DATE_FORMAT) : "") + DELIMITER +
                escapeCsv(ticket.getTechnicien());
    }

    private String escapeCsv(String valeur) {
        if (valeur == null) return "";
        if (valeur.contains(";") || valeur.contains("\"") || valeur.contains("\n")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }

    // ─────────────────────────────── IMPORT ──────────────────────────────────

    public List<Ticket> importer(String path) {
        Path ressource = Path.of(path);
        List<Ticket> tickets = new ArrayList<>();

        if (!Files.exists(ressource)) {
            System.out.println("Fichier introuvable : " + path);
            return tickets;
        }

        try {
            List<String> lignes = Files.readAllLines(
                    ressource, StandardCharsets.UTF_8);

            // FIX #4 : on saute la ligne d'en-tête (index 0)
            for (int i = 1; i < lignes.size(); i++) {
                String ligne = lignes.get(i).trim();
                // FIX #7 : on ignore les lignes vides
                if (ligne.isEmpty()) continue;
                try {
                    tickets.add(parseLigne(ligne));
                } catch (Exception e) {
                    // FIX #7 : une ligne mal formée ne bloque pas tout l'import
                    System.out.println("Ligne " + i +
                            " ignorée (format invalide) : " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Erreur lecture fichier : " + e.getMessage());
        }

        System.out.println(tickets.size() + " ticket(s) importé(s).");
        return tickets;
    }

    // FIX #1 + #2 + #3 + #8 : ordre colonnes correct, LocalDateTime.parse,
    // reconstruction complète du ticket, méthode private
    private Ticket parseLigne(String ligne) {
        String[] parts = ligne.trim().split(";", -1);
        // Ordre CSV : id;titre;description;demandeur;service;priorite;statut;dateCreation;dateMiseAJour;technicien
        String        id           = parts[0];
        String        titre        = parts[1];
        String        description  = parts[2];
        String        demandeur    = parts[3];
        Service       service      = Service.valueOf(parts[4]);
        Priorite      priorite     = Priorite.valueOf(parts[5]);
        Statut        statut       = Statut.valueOf(parts[6]);
        LocalDateTime dateCreation = parts[7].isEmpty() ? null
                : LocalDateTime.parse(parts[7], DATE_FORMAT);
        LocalDateTime dateMiseAJour = parts[8].isEmpty() ? null
                : LocalDateTime.parse(parts[8], DATE_FORMAT);
        String        technicien   =
                parts.length > 9 ? parts[9] : "";

        Ticket ticket = new Ticket(id, titre, description, demandeur, service, priorite);
        ticket.setTicketStatut(statut);
        ticket.setDateCreation(dateCreation);
        ticket.setDateMiseAjour(dateMiseAJour);
        ticket.setTechnicien(technicien.isEmpty() ? null : technicien);
        return ticket;
    }
}
