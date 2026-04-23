package services;

import enums.Priorite;
import enums.Service;
import enums.Statut;
import modele.Ticket;
import repository.TicketRepository;

import java.util.ArrayList;
import java.util.List;

public class Statistique {
    private static TicketRepository ticketRepository;
    public Statistique(TicketRepository repository){
       ticketRepository=repository;
    }
    public static int totalTicket(List<Ticket> ticketList){
        if(ticketList.isEmpty()){
            return 0;
        }
        return ticketList.size();
    }
    public static int totalTicketByStatut(List<Ticket> ticketList,Statut statut){
        if(ticketList.stream().filter(ticket -> ticket.getStatut() == statut).toList().isEmpty()){
            return 0;
        }
        return ticketList.stream().filter(ticket -> ticket.getStatut()==statut).toList().size();
    }
    public static int totalTicketByService(List<Ticket> ticketList,Service service){
        if(ticketList.stream().filter(ticket -> ticket.getService()==service).toList().isEmpty()){
            return 0;
        }
        return ticketList.stream().filter(ticket -> ticket.getService()==service).toList().size();
    }
    public static int totalTicketByPriorite(List<Ticket> ticketList,Priorite priorite){
        if(ticketList.stream().filter(ticket -> ticket.getPriorite()==priorite).toList().isEmpty()){
            return 0;
        }
        return ticketList.stream().filter(ticket -> ticket.getPriorite()==priorite).toList().size();
    }
    public static List<Ticket> listTicketByStatut(List<Ticket> ticketList,Statut statut){
        return ticketList.stream().filter(ticket -> ticket.getStatut()==statut).toList();
    }
    public static List<Ticket> listTicketByPriority(List<Ticket> ticketList,Priorite priorite){
        return ticketList.stream().filter(ticket -> ticket.getPriorite()==priorite).toList();
    }

    public String toString(List<Ticket> tickets){
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║         STATISTIQUES TICKETS         ║\n");
        sb.append("╠══════════════════════════════════════╣\n");
        sb.append(String.format("║  Total tickets    : %3d             ║%n", totalTicket(tickets)));
        sb.append(String.format("║  • Ouverts        : %3d             ║%n", totalTicketByStatut(tickets,Statut.OUVERT)));
        sb.append(String.format("║  • En cours       : %3d             ║%n", totalTicketByStatut(tickets,Statut.EN_COURS)));
        sb.append(String.format("║  • Résolus        : %3d             ║%n", totalTicketByStatut(tickets,Statut.RESOLUE)));
        sb.append("╚══════════════════════════════════════╝");
        return sb.toString();
    }
}
