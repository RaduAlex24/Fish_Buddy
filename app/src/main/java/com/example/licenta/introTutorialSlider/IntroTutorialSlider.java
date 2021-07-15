package com.example.licenta.introTutorialSlider;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.licenta.R;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroFragment;

public class IntroTutorialSlider extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Folosire sageti
        setWizardMode(true);

        // Adaugare slideuri

        // Forum
        // General
        addSlide(AppIntroFragment.newInstance("Modul forum",
                "Acest modul permite interactiunea cu alti pescari, prin principiul de intrebare raspuns, pe diferite teme.",
                R.drawable.forum, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

        // Interactiune cu butoanele
        addSlide(AppIntroFragment.newInstance("Modul forum",
                "Poti interactiona cu orice postare prin intermediul butoaneleor de pe aceasta. Poti aprecia sau nu postare, iar" +
                        " prin butonul in forma de inima o poti adauga la favorite, pentru a o regasi mai usor.",
                R.drawable.forum, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

        // Forum detaliat
        addSlide(AppIntroFragment.newInstance("Modul forum",
                "Daca vrei sa citesti toata postarea forum poti da click pe ea si vei fi condus la pagina ei. " +
                        "In aceasta pagina poti adauga si citi comentariile postarii.",
                R.drawable.forum, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

        // Sortari
        addSlide(AppIntroFragment.newInstance("Modul forum",
                "Cele doua butoane permit sortarea tuturor postarilor. Unul dintre acestea sorteaza in " +
                        "functie de categorii, iar celalalt in functie de diferite crieterii precum numar puncte, numar comentarii, etc.",
                R.drawable.forum, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

        // Adaugare postare
        addSlide(AppIntroFragment.newInstance("Modul forum",
                "Butonul plus te duce la pagina de creare a unei postari. Dupa ce completezi toate campurile, " +
                        "postarea devine vizibila pentru toata lumea.",
                R.drawable.forum, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Asistet virtual
        // General
        addSlide(AppIntroFragment.newInstance("Modul asistent virtual",
                "Acest modul iti permite sa vorbesti direct cu robotul aplicatie noastre, ce" +
                        " are rolul de ati raspunde la orice intrebare.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Redirectionari
        addSlide(AppIntroFragment.newInstance("Modul asistent virtual",
                "Principalul scop al asistentului este sa te duca unde ai nevoie, " +
                        " asa ca daca ii sugerezi o intentie a ta, te va redirectiona la pagina dorita.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Redirectionari
        addSlide(AppIntroFragment.newInstance("Modul asistent virtual",
                "De asemenea, asistentul poate efectua cautari pe forum, asa ca " +
                        "daca iti doresti sa gasesti o postare spune-i cateva cuvinte cheie si o va gasi pentru tine.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Harti
        // General
        addSlide(AppIntroFragment.newInstance("Modul harti",
                "Modulul harti iti permite sa vezi locuri de pescuit din jurul tau si sa " +
                        "interactionezi cu acestea.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Balta detaliata
        addSlide(AppIntroFragment.newInstance("Modul harti",
                "Daca doresti mai multe informatii despre o balta, poti da click pe ea si " +
                        "vezi afla lucruri precum distanta pana la ea, ce pesti au mai fost prinsi acolo, chiar" +
                        " si review-uri de la alti oameni.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));

        // Adaugare peste
        addSlide(AppIntroFragment.newInstance("Modul harti",
                "Daca ai prins un peste si doresti sa il impartasesti cu ceilalti, poti face acest " +
                        "lucru prin optiunea de adaugare peste. Dupa ce completezi toate campurile, pestele " +
                        "va deveni vizibil pe balta unde a fost prins.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Profil
        // General
        addSlide(AppIntroFragment.newInstance("Modul profil",
                "Gestionarea contului persoanal se face de pe pagina de profil. Aici poti vedea " +
                        "diferite statistici rezultate din folosirea aplciatiei.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


        // Modificare stergere cont
        addSlide(AppIntroFragment.newInstance("Modul profil",
                "Modificarea si stergerea contului pot fi facute tot de pe aceasta pagina. " +
                        "Modificarea contului poate fi facuta oricand, insa stergerea contului este o actiune permanenta" +
                        " si ireversibila.",
                R.drawable.b, ContextCompat.getColor(getApplicationContext(), R.color.purple_200)));


    }


    // Metode
    // Apasare done
    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }


    // Apasare skip
    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

}
