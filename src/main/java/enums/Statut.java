package enums;
public enum Statut {

    OUVERT(1) {
        @Override
        public boolean peutTransitionnerVers(Statut suivant) {
            return suivant == EN_COURS;
        }
    },

    EN_COURS(2) {
        @Override
        public boolean peutTransitionnerVers(Statut suivant) {
            return suivant == RESOLUE;
        }
    },

    RESOLUE(3) {
        @Override
        public boolean peutTransitionnerVers(Statut suivant) {
            return suivant == FERMER;
        }
    },

    FERMER(4) {
        @Override
        public boolean peutTransitionnerVers(Statut suivant) {
            return false; // statut final, aucune transition possible
        }
    };

    private final int ordre;

    Statut(int ordre) {
        this.ordre = ordre;
    }

    public int getOrdre() { return ordre; }

    // Méthode abstraite : chaque statut implémente sa propre règle
    public abstract boolean peutTransitionnerVers(Statut suivant);
}
