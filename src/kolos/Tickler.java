/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kolos;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Michal
 */
public class Tickler implements Iterator<String> {

    private final long _limit;
    private long _cursor;

    public Tickler(long limit) {
        _limit = limit;
        _cursor = limit*2;
    }

    @Override
    public boolean hasNext() {
        return _limit < _cursor;

    }

    @Override
    public String next() {
        if (hasNext()) {
            --_cursor;
        }
        return Long.toBinaryString(_cursor);
    }

}
