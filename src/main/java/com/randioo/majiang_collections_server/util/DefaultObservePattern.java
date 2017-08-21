package com.randioo.majiang_collections_server.util;

import java.util.HashSet;
import java.util.Set;

import com.randioo.randioo_server_base.service.ObservableInterface;
import com.randioo.randioo_server_base.template.Observer;

public class DefaultObservePattern implements ObservableInterface, Observer {
    /** 所有观察者 */
    private Set<Observer> observers = new HashSet<>();

    @Override
    public void addObserver(Observer paramObserver) {
        observers.add(paramObserver);
    }

    @Override
    public void deleteObserver(Observer paramObserver) {
        observers.remove(paramObserver);
    }

    @Override
    public void notifyObservers(String msg, Object... params) {
        for (Observer observer : observers) {
            try {
                observer.update(this, msg, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int countObservers() {
        return observers.size();
    }

    @Override
    public void deleteObservers() {
        observers.clear();
    }

    @Override
    public void update(Observer observer, String msg, Object... args) {
        // TODO Auto-generated method stub
        
    }
}
