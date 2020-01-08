package lielTitel.thesimpsons;

import java.util.ArrayList;
import java.util.Collections;

public class PersonList {
    private ArrayList<Person> personList;
    private final int MAX = 10;
    private int size;

    public PersonList() {
        personList = new ArrayList<Person>();
        size = 0;
    }

    public void addPerson(Person person){
        if (size >= MAX) {
            if (personList.get(MAX - 1).getScore() < person.getScore()) {//if there is a new high score-> replace
                personList.remove(MAX -1);
                personList.add(MAX-1,person);
            }
        } else {
            personList.add(person);
            size++;
        }
        Collections.sort(personList);
    }

    public int getSize()
    {
        return size;
    }

    public ArrayList<Person> getPersonList()
    {
        return personList;
    }

}
