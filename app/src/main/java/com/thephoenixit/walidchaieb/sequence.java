package com.thephoenixit.walidchaieb;

import com.google.gson.annotations.SerializedName;

public class sequence {

    @SerializedName("NomStade")
    private String NomStade;

    @SerializedName("DateMatch")
    private String DateMatch;

    @SerializedName("NumerSiege")
    private String NumerSiege;

    @SerializedName("CheminImage")
    private String CheminImage;

    @SerializedName("CheminSon")
    private String CheminSon;

    @SerializedName("NomEnchainement")
    private String NomEnchainement;

    @SerializedName("HeureExecution")
    private String HeureExecution;

    @SerializedName("HeureFin")
    private String HeureFin;

    @SerializedName("DureeAffichage")
    private int DureeAffichage;

    @SerializedName("DureeEteint")
    private int DureeEteint;

    public sequence() {
    }

    public sequence(String nomStade, String dateMatch, String numerSiege, String cheminImage, String cheminSon, String nomEnchainement, String heureExecution, String heureFin, int dureeAffichage, int dureeEteint) {
        NomStade = nomStade;
        DateMatch = dateMatch;
        NumerSiege = numerSiege;
        CheminImage = cheminImage;
        CheminSon = cheminSon;
        NomEnchainement = nomEnchainement;
        HeureExecution = heureExecution;
        HeureFin = heureFin;
        DureeAffichage = dureeAffichage;
        DureeEteint = dureeEteint;
    }

    public String getNomStade() {
        return NomStade;
    }

    public void setNomStade(String nomStade) {
        NomStade = nomStade;
    }

    public String getDateMatch() {
        return DateMatch;
    }

    public void setDateMatch(String dateMatch) {
        DateMatch = dateMatch;
    }

    public String getNumerSiege() {
        return NumerSiege;
    }

    public void setNumerSiege(String numerSiege) {
        NumerSiege = numerSiege;
    }

    public String getCheminImage() {
        return CheminImage;
    }

    public void setCheminImage(String cheminImage) {
        CheminImage = cheminImage;
    }

    public String getCheminSon() {
        return CheminSon;
    }

    public void setCheminSon(String cheminSon) {
        CheminSon = cheminSon;
    }

    public String getNomEnchainement() {
        return NomEnchainement;
    }

    public void setNomEnchainement(String nomEnchainement) {
        NomEnchainement = nomEnchainement;
    }

    public String getHeureExecution() {
        return HeureExecution;
    }

    public void setHeureExecution(String heureExecution) {
        HeureExecution = heureExecution;
    }

    public String getHeureFin() {
        return HeureFin;
    }

    public void setHeureFin(String heureFin) {
        HeureFin = heureFin;
    }

    public int getDureeAffichage() {
        return DureeAffichage;
    }

    public void setDureeAffichage(int dureeAffichage) {
        DureeAffichage = dureeAffichage;
    }

    public int getDureeEteint() {
        return DureeEteint;
    }

    public void setDureeEteint(int dureeEteint) {
        DureeEteint = dureeEteint;
    }

    @Override
    public String toString() {
        return "sequence{" +
                "NomStade='" + NomStade + '\'' +
                ", DateMatch='" + DateMatch + '\'' +
                ", NumerSiege='" + NumerSiege + '\'' +
                ", CheminImage='" + CheminImage + '\'' +
                ", CheminSon='" + CheminSon + '\'' +
                ", NomEnchainement='" + NomEnchainement + '\'' +
                ", HeureExecution='" + HeureExecution + '\'' +
                ", HeureFin='" + HeureFin + '\'' +
                ", DureeAffichage=" + DureeAffichage +
                ", DureeEteint=" + DureeEteint +
                '}';
    }
}
