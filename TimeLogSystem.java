package projetModelisationfinal;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TimeLogSystem {

    // Classe Employe représentant un employé dans le système
    public static class Employe {
        private String nom;
        private String id;
        private String nomUtilisateur;
        private double tauxHoraireBase;
        private double tauxHoraireSupplementaire;
        private Activite activiteEnCours;

        // Constructeur de la classe Employe
        public Employe(String nom, String id, String nomUtilisateur, double tauxHoraireBase, double tauxHoraireSupplementaire) {
            this.nom = nom;
            this.id = id;
            this.nomUtilisateur = nomUtilisateur;
            this.tauxHoraireBase = tauxHoraireBase;
            this.tauxHoraireSupplementaire = tauxHoraireSupplementaire;
            this.activiteEnCours = null;
        }

        // Méthode pour débuter une activité
        public void debuterActivite(Projet projet, Discipline discipline) {
            activiteEnCours = new Activite(this, projet, discipline);
            activiteEnCours.setHeureDebut(new Date());
            System.out.println("Activité débutée pour " + nom + " sur le projet " + projet.getNom());
        }

        // Méthode pour terminer une activité
        public void terminerActivite() {
            if (activiteEnCours != null) {
                activiteEnCours.setHeureFin(new Date());
                double tempsTotal = activiteEnCours.calculerTempsTotal();
                double salaire = activiteEnCours.calculerSalaire();
                System.out.println("Activité terminée pour " + nom);
                System.out.println("Temps total travaillé : " + tempsTotal + " heures");
                System.out.println("Salaire calculé : " + salaire + " $");

                // Sauvegarde de l'activité dans un fichier JSON
                activiteEnCours.sauvegarderActiviteJSON();

                activiteEnCours = null; // Réinitialiser l'activité en cours
            } else {
                System.out.println("Aucune activité en cours pour " + nom);
            }
        }

        // Getters pour obtenir les informations de l'employé
        public String getNom() {
            return nom;
        }

        public String getId() {
            return id;
        }

        public String getNomUtilisateur() {
            return nomUtilisateur;
        }

        public double getTauxHoraireBase() {
            return tauxHoraireBase;
        }

        public double getTauxHoraireSupplementaire() {
            return tauxHoraireSupplementaire;
        }
    }

    // Classe Projet représentant un projet dans le système
    public static class Projet {
        private String nom;
        private String numeroIdentification;
        private Date dateDebut;
        private Date dateFin;
        private Map<Discipline, Integer> heuresBudgetees;

        // Constructeur de la classe Projet
        public Projet(String nom, String numeroIdentification) {
            this.nom = nom;
            this.numeroIdentification = numeroIdentification;
            this.heuresBudgetees = new HashMap<>();
        }

        // Méthode pour vérifier si un employé est assigné au projet
        public boolean estAssigne(Employe employe) {
            // Validation de l'assignation - Ici on suppose que l'employé est toujours assigné
            return true;
        }

        // Getters pour obtenir les informations du projet
        public String getNom() {
            return nom;
        }

        public String getNumeroIdentification() {
            return numeroIdentification;
        }
    }

    // Classe Activite représentant une activité dans le système
    public static class Activite {
        private Employe employe;
        private Projet projet;
        private Discipline discipline;
        private Date heureDebut;
        private Date heureFin;

        // Constructeur de la classe Activite
        public Activite(Employe employe, Projet projet, Discipline discipline) {
            this.employe = employe;
            this.projet = projet;
            this.discipline = discipline;
        }

        // Méthodes pour définir l'heure de début et de fin de l'activité
        public void setHeureDebut(Date heureDebut) {
            this.heureDebut = heureDebut;
        }

        public void setHeureFin(Date heureFin) {
            this.heureFin = heureFin;
        }

        // Méthode pour calculer le temps total travaillé
        public double calculerTempsTotal() {
            if (heureDebut != null && heureFin != null) {
                long diff = heureFin.getTime() - heureDebut.getTime();
                return diff / (1000.0 * 60 * 60); // Conversion en heures
            }
            return 0.0;
        }

        // Méthode pour calculer le salaire de l'activité
        public double calculerSalaire() {
            double heures = calculerTempsTotal();
            double salaire = heures * employe.getTauxHoraireBase();
            return salaire;
        }

        // Méthode pour sauvegarder l'activité dans un fichier JSON
        public void sauvegarderActiviteJSON() {
            Gson gson = new Gson();
            try (FileWriter writer = new FileWriter(employe.getNom() + "_activite.json")) {
                gson.toJson(this, writer);
                System.out.println("Activité sauvegardée dans le fichier JSON.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe SystemeTimeLog représentant le système TimeLog
    public static class SystemeTimeLog {
        private List<Employe> employes;
        private List<Projet> projets;

        // Constructeur de la classe SystemeTimeLog
        public SystemeTimeLog() {
            this.employes = new ArrayList<>();
            this.projets = new ArrayList<>();
        }

        // Méthodes pour ajouter des employés et des projets au système
        public void ajouterEmploye(Employe employe) {
            employes.add(employe);
        }

        public void ajouterProjet(Projet projet) {
            projets.add(projet);
        }

        // Méthode pour authentifier un employé
        public boolean authentifier(String nomUtilisateur, String id) {
            for (Employe employe : employes) {
                if (employe.getNomUtilisateur().equals(nomUtilisateur) && employe.getId().equals(id)) {
                    return true;
                }
            }
            return false;
        }

        // Méthode pour enregistrer le début d'une activité
        public void enregistrerDebutActivite(Employe employe, Projet projet, Discipline discipline) {
            if (projet.estAssigne(employe)) {
                employe.debuterActivite(projet, discipline);
            } else {
                System.out.println("L'employé n'est pas assigné à ce projet.");
            }
        }

        // Méthode pour enregistrer la fin d'une activité
        public void enregistrerFinActivite(Employe employe) {
            employe.terminerActivite();
        }
    }

    // Enum Discipline représentant les différentes disciplines dans le système
    public enum Discipline {
        DEVELOPPEMENT,
        DESIGN,
        TEST,
        MANAGEMENT
    }

    // Classe principale pour exécuter le système TimeLog
    public static void main(String[] args) {
        // Initialisation du système TimeLog
        SystemeTimeLog systeme = new SystemeTimeLog();

        // Création d'un employé et ajout au système
        Employe employe = new Employe("Bula Bula", "12345", "balthajonel", 20.0, 30.0);
        systeme.ajouterEmploye(employe);

        // Authentification de l'employé
        if (systeme.authentifier("balthajonel", "12345")) {
            System.out.println("Authentification réussie pour " + employe.getNom());
        } else {
            System.out.println("Authentification échouée.");
            return;
        }

        // Création d'un projet et ajout au système
        Projet projet = new Projet("Projet A", "001");
        systeme.ajouterProjet(projet);

        // Sélection d'une discipline
        Discipline discipline = Discipline.DEVELOPPEMENT;

        // Enregistrement du début de l'activité
        systeme.enregistrerDebutActivite(employe, projet, discipline);

        // Pause simulant le travail effectué par l'employé
        try {
            Thread.sleep(5000); // Attendre 5 secondes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Enregistrement de la fin de l'activité
        systeme.enregistrerFinActivite(employe);
    }
}