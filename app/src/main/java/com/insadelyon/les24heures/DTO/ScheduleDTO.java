package com.insadelyon.les24heures.DTO;

/**
 * Created by remi on 29/01/15.
 */
public class ScheduleDTO {
    String jour;
    String debut;
    String fin;

    public ScheduleDTO(String fin, String debut, String jour) {
        this.fin = fin;
        this.debut = debut;
        this.jour = jour;
    }


    public String getJour() {
        return jour;
    }

    public void setJour(String jour) {
        this.jour = jour;
    }

    public String getDebut() {
        return debut;
    }

    public void setDebut(String debut) {
        this.debut = debut;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }
}
