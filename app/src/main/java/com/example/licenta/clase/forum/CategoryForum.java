package com.example.licenta.clase.forum;

public enum CategoryForum {
    SFATURI("Sfaturi"),
    PESTI("Pesti"),
    ECHIPAMENT("Echipamente"),
    INCEPATORI("Incepatori"),
    GLUME("Glume");

    private String label;

    CategoryForum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
