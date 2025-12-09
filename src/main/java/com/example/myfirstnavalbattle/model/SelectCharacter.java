package com.example.myfirstnavalbattle.model;

import java.util.ArrayList;
import java.util.List;

public class SelectCharacter {

    private static final List<Characters> characters = new ArrayList<>();
    private static Characters selectedCharacter = new Characters("Default");

    public SelectCharacter() {
    }

    public static void selectCharacter(Characters character) {
        selectedCharacter = character;
    }
    public static Characters getSelectedCharacter() { return  selectedCharacter;}

    public static List<Characters> loadCharacters() {
        characters.add(selectedCharacter);
        characters.add(new Characters("Cage"));
        characters.add(new Characters("Hanks"));
        characters.add(new Characters("Neeson"));
        characters.add(new Characters("Smith"));
        return characters;
    }

}
