package services;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import exception.IllegalChangeStatutException;
import modele.Ticket;
import repository.TicketRepository;
import repository.TicketRepositoryImplement;


import java.io.IOException;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.util.*;

public class TicketService {
    private final String DOSSIER="src/ressource";
    private final String FICHIER=DOSSIER+"ticket.csv";
    private final Path RESSOURCE= Path.of(FICHIER);
    private static TicketRepository repository=new TicketRepositoryImplement();
    private static final HashSet<Ticket> ticketsHash=new HashSet<>();
    public TicketService(TicketRepository repository){
        TicketService.repository =repository;
        try{
            TicketService.repository.importerCSV("src/ressource/ticket.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Ticket createTicket(String title, String description, String demandeur, Service service, Priorite priorite){
        if(description==null || description.trim().isEmpty()){
            throw  new IllegalArgumentException();
        }
        if(title==null || title.isBlank()){
            throw new IllegalArgumentException();
        }
        if(demandeur==null || demandeur.isBlank()){
            throw new IllegalArgumentException();
        }
        if(service==null){
            throw new IllegalArgumentException();
        }
        if(priorite==null){
            throw new IllegalArgumentException();
        }
        String id= UUID.randomUUID().toString();
        Ticket ticket=new Ticket(id,title,description,demandeur,service,priorite);
        System.out.println("Ticket create with success");
        ticketsHash.add(ticket);
        repository.sauvegarder(ticket);
        return ticket;
    }
    public static void ouvrirTicket(String id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trouvé : " + id));

        ticket.open();  // Appel métier sur l'objet Ticket
        repository.sauvegarder(ticket);  // Persiste la modification

        System.out.println("✅ Ticket " + id + " ouvert");
    }
    public static void fermerTicket(String id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket non trouvé : " + id));

        ticket.close();  // Appel métier sur l'objet Ticket
        repository.sauvegarder(ticket);  // Persiste la modification

        System.out.println("✅ Ticket " + id + " fermé (résolu)");
    }
    public List<Ticket> listerTickets(){
      return repository.findAll();
    }
    public List<Ticket> searchByTitre(String titre){
        return  repository.findAll().stream().filter(ticket -> ticket.getTitle().equals(titre)).toList();
    }
    public Optional<List<Ticket>> filterByStatus(Statut statut){
       return  Optional.of(ticketsHash.stream().filter(ticket->ticket.getStatut().equals(statut)).toList());
    }
    public List<Ticket> filterByPriority(Priorite priorite){
        return ticketsHash.stream().filter(ticket->ticket.getPriorite().equals(priorite)).toList();
    }
    public void supprimerTicket(String ticketId){
        if(ticketId==null){
            System.out.println("entree l'id du ticket a supprimer");
            return;
        }
        Optional<Ticket> ticket=repository.findById(ticketId);
        if (ticket.isEmpty()){
            System.out.println("le fichier que vous souhaiter supprimer n'hexiste pas");
            return;
        }
        repository.supprimer(ticketId);
    }
    public static void assignerTechnician(String ticketId,String technician){
        if(technician==null){
            System.out.println("enter technician for assignation of ticket");
            return;
        }
        if(ticketId==null){
            System.out.println("Enter ID ticket valid");
            return;
        }
        Optional<Ticket> ticketOptional=repository.findById(ticketId);
        if(ticketOptional.isEmpty()){
            System.out.println("Ticket not Exist");
            return;
        }
        Ticket ticket=ticketOptional.get();
       ticket.setTechnicien(technician);
       repository.sauvegarder(ticket);
        System.out.println("Technician assigned success");
    }
    public void changerStatut(String ticketId, Statut nouveauStatut)
            throws IllegalChangeStatutException {
        Optional<Ticket> ticketOptional=repository.findById(ticketId);
        // Validation des paramètres
        if (ticketOptional.isEmpty()) {
            throw new IllegalArgumentException("Le ticket ne peut pas être null");
        }
        if (nouveauStatut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être null");
        }
        Ticket ticket = ticketOptional.get();
        Statut statutActuel = ticket.getStatut();
        // Vérification de la transition via l'enum
        if (!statutActuel.peutTransitionnerVers(nouveauStatut)) {
            throw new IllegalChangeStatutException(statutActuel, nouveauStatut);
        }
        ticket.setTicketStatut(nouveauStatut);
        repository.sauvegarder(ticket);
    }
    public void afficherStatistique(){
        Statistique statistique=new Statistique(repository);
        System.out.println(statistique.toString());
    }
    public void sauvegarder(Ticket ticket){
        repository.sauvegarder(ticket);
    }
}
