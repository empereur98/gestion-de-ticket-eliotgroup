package exception;

import enums.Statut;

public class IllegalChangeStatutException extends RuntimeException {
    private final Statut statutActuel;
    private final Statut statutDemande;
    public IllegalChangeStatutException(Statut statutActuel, Statut statutDemande) {
        super("Transition Invalid,Impossible de passer de :"+statutActuel+" vers:"+statutDemande);
        this.statutActuel=statutActuel;
        this.statutDemande=statutDemande;
    }

    public Statut getStatutActuel() {
        return statutActuel;
    }

    public Statut getStatutDemande() {
        return statutDemande;
    }
}
