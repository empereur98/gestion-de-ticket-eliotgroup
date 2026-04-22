package interfaces;

import modele.Ticket;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface TicketInterface {
    public void sauvegarder(Ticket t);
    public List<Ticket> findAll();
    public Optional<Ticket> findById(String id);
    public void supprimer(String id);
    public void importerCSV(Path p);
}
