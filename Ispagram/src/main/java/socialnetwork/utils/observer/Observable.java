package socialnetwork.utils.observer;

import socialnetwork.utils.events.Event;

public interface Observable <E extends Event> {

    void addObserver(Observer<E> e);
    void removeObserver(Observable<E> e);
    void notifyObservers(E t);

}
