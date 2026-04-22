package repository;

import modele.Ticket;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    void sauvegarder(Ticket t);

    List<Ticket> findAll();

    Optional<Ticket> findById(String id);

    void supprimer(String id);

    void importerCSV(String path) throws IOException;
    void exporterVersCsv(List<Ticket> tickets);
}
