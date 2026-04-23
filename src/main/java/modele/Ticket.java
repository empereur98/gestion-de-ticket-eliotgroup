package modele;

import enums.Priorite;
import enums.Service;
import enums.Statut;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private  String id;
    private  String title;
    private  String description;
    String demandeur;
    String technicien;
    Service ticketService;
    private Priorite ticketPriorite;
    Statut ticketStatut;
    LocalDateTime dateCreation;
    LocalDateTime dateMiseAjour;

    public Ticket(){
        this.dateMiseAjour=LocalDateTime.now();
        this.dateCreation=LocalDateTime.now();
        this.ticketStatut=Statut.OUVERT;
    }
    public Ticket(String id,String title,String description,String demandeur,Service ticketService,
                  Priorite ticketPriorite){
        this();
       this.id=id;
       this.title=title;
       this.demandeur=demandeur;
       this.description=description;
       this.ticketPriorite=ticketPriorite;
       this.ticketService=ticketService;
    }
    public void open(){
        if(this.getStatut()==Statut.OUVERT){
            throw new IllegalStateException("le ticket: "+ this.getId()+" est deja ouvert");
        }
        this.ticketStatut=Statut.OUVERT;
        this.dateMiseAjour=LocalDateTime.now();
        this.technicien=null;
    }

    public void close() {
        if (this.ticketStatut == Statut.RESOLUE) {
            throw new IllegalStateException("Le ticket " + this.id + " est déjà résolu");
        }
        this.ticketStatut = Statut.RESOLUE;
        this.dateMiseAjour = LocalDateTime.now();
    }
    public String toString(){
        return "-"+this.title+"-"+this.ticketService;
    }
    //GETTERS/SETTERS

    public String getDemandeur() {
        return demandeur;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateMiseAjour() {
        return dateMiseAjour;
    }

    public Priorite getPriorite() {
        return ticketPriorite;
    }

    public Service getService() {
        return ticketService;
    }

    public Statut getStatut() {
        return ticketStatut;
    }

    public String getTechnicien() {
        return technicien;
    }

    public void setTechnicien(String technicien) {
        this.technicien = technicien;
    }

    public void setDemandeur(String demandeur) {
        this.demandeur = demandeur;
    }

    public void setTicketPriorite(Priorite ticketPriorite) {
        this.ticketPriorite = ticketPriorite;
    }

    public void setTicketStatut(Statut ticketStatut) {
        this.ticketStatut = ticketStatut;
    }
    public void setDateMiseAjour(LocalDateTime dateMiseAjour) {
        this.dateMiseAjour = dateMiseAjour;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
