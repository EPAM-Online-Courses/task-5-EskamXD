package efs.task.collections.game;

import efs.task.collections.data.DataProvider;
import efs.task.collections.entity.Hero;
import efs.task.collections.entity.Town;

import java.util.*;
import java.util.stream.Collectors;

public class GameLobby {

    public static final String HERO_NOT_FOUND = "Nie ma takiego bohatera ";
    public static final String NO_SUCH_TOWN = "Nie ma takiego miasta ";

    private final DataProvider dataProvider;
    private final Map<Town, List<Hero>> playableTownsWithHeroesList;

    public GameLobby() {
        this.dataProvider = new DataProvider();
        this.playableTownsWithHeroesList =
                mapHeroesToStartingTowns(dataProvider.getTownsList(), dataProvider.getHeroesSet());
//        System.out.println(dataProvider.getTownsList());
//        System.out.println(dataProvider.getHeroesSet());
//        System.out.println(playableTownsWithHeroesList);
    }

    public Map<Town, List<Hero>> getPlayableTownsWithHeroesList() {
        return playableTownsWithHeroesList;
    }

    //TODO Dodać miasta i odpowiadających im bohaterów z DLC gry do mapy dostępnych
    // miast - playableTownsWithHeroesList, tylko jeżeli jeszcze się na niej nie znajdują.
    public void enableDLC() {
        List<Town> townsDLC = dataProvider.getDLCTownsList();
        List<Hero> heroesDLC = List.copyOf(dataProvider.getDLCHeroesSet());

        for (Town town : townsDLC) {
            if (!playableTownsWithHeroesList.containsKey(town)) {
                playableTownsWithHeroesList.put(town, heroesDLC);
            }
        }
    }


    //TODO Usunąć miasta i odpowiadających im bohaterów z DLC gry z mapy dostępnych
    // miast - playableTownsWithHeroesList.
    public void disableDLC() {
        List<Town> townsDLC = dataProvider.getDLCTownsList();

        for (Town town : townsDLC) {
            if (playableTownsWithHeroesList.containsKey(town)) {
                playableTownsWithHeroesList.remove(town);
            }
        }
    }

    // TODO Sprawdza czy mapa playableCharactersByTown zawiera dane miasto.
    //  Jeśli tak zwróć listę bohaterów z tego miasta.
    //  Jeśli nie rzuć wyjątek NoSuchElementException z wiadomością NO_SUCH_TOWN + town.getName()
    public List<Hero> getHeroesFromTown(Town town) {
        if(playableTownsWithHeroesList.containsKey(town)){
//            System.out.println(playableTownsWithHeroesList.get(town));
            return playableTownsWithHeroesList.get(town);
        }
        else {
            throw new NoSuchElementException(NO_SUCH_TOWN + town.getTownName());
        }
    }

    // TODO Metoda powinna zwracać mapę miast w kolejności alfabetycznej z odpowiadającymi im bohaterami.
    //  Każde z miast charakteryzuje się dwoma klasami bohaterów dostępnymi dla tego miasta - Town.startingHeroClass.
    //  Mapa ma zawierać pare klucz-wartość gdzie klucz: miasto, wartość: lista bohaterów;
    public Map<Town, List<Hero>> mapHeroesToStartingTowns(List<Town> availableTowns, Set<Hero> availableHeroes) {
        Map<Town, List<Hero>> map = new TreeMap<>(Comparator.comparing(Town::getTownName));
        for(var town : availableTowns) {
            List<Hero> heroList = new ArrayList<>();
            for(var hero : availableHeroes) {
//                System.out.println(town.getStartingHeroClasses() + " " + hero.getHeroClass());
                if(town.getStartingHeroClasses().contains(hero.getHeroClass())) {
//                    System.out.println("watunek z MO");
                    heroList.add(hero);
                }
            }
            map.put(town, heroList);
        }

        return map;
    }

    //TODO metoda zwraca wybranego bohatera na podstawie miasta z którego pochodzi i imienia.
    // Jeżeli istnieje usuwa go z listy dostępnych bohaterów w danym mieście i zwraca bohatera.
    // Jeżeli nie ma go na liście dostępnych bohaterów rzuca NoSuchElementException z wiadomością HERO_NOT_FOUND + name
    public Hero selectHeroByName(Town heroTown, String name) {
        for (var hero : playableTownsWithHeroesList.get(heroTown)) {
            if (hero.getName().equals(name)) {
                playableTownsWithHeroesList.get(heroTown).remove(hero);
                return hero;
            }
        }

        throw new NoSuchElementException(HERO_NOT_FOUND + name);
    }
}