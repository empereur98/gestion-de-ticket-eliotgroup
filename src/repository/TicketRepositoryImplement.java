package repository;

import modele.Ticket;
import utils.CsvManager;

import java.io.IOException;
import java.util.*;

public class TicketRepositoryImplement implements TicketRepository {
    private final Map<String, Ticket> tickets = new HashMap<>();
    private final CsvManager csvManager=new CsvManager();

    public TicketRepositoryImplement() {
    }
    @Override
    public void sauvegarder(Ticket t) {
        if(t==null){
            System.out.println("Veuiller entrer le ticket a sauvegarder");
            return;
        }
        tickets.put(t.getId(), t);
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return Optional.ofNullable(tickets.get(id));
    }

    @Override
    public void supprimer(String id) {
        tickets.remove(id);
    }

    @Override
    public void importerCSV(String path) {
        List<Ticket> imported = csvManager.importer(path);
        imported.forEach(this::sauvegarder);
    }

    @Override
    public void exporterVersCsv(List<Ticket> tickets){
        try{
             csvManager.exporter(tickets);
            System.out.println("vos Tickets ont bien ete exporter dans le fichier adequat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
