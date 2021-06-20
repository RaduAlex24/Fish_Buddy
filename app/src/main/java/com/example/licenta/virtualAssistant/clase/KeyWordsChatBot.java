package com.example.licenta.virtualAssistant.clase;

public enum KeyWordsChatBot {
    // Redirectionari
    CREARE_INTERVENTIE("CREARE INTERVENTIE FORUM"),
    VIZUALIZARE_LISTA_PESTI("VIZUALIZARE LISTA PESTI"),
    VIZUALIZARE_SETARI("VIZUALIZARE SETARI"),
    VIZUALIZARE_CONT("VIZUALIZARE CONT"),
    VIZUALIZARE_POSTARI_FORUM("VIZUALIZARE POSTARI FORUM"),
    VIZUALIZARE_HARTI("VIZUALIZARE HARTI"),
    CAUTARE("CAUTARE"),
    TUTORIAL("TUTORIAL"),

    // Inlocuiri
    INLOCCUIESTE_NUME("INLOCUIESTE_NUME");

    private String label;

    KeyWordsChatBot(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
