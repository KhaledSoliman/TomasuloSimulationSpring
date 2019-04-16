
package com.tomasolo.sim.Algorithm.Main;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ListIterator implements Iterator<RobNode> {
        private RobNode current;

        public ListIterator(RobNode first) {
            current = first;
        }

        public boolean hasNext()  { return current != null;                     }
        
        public void remove()      { throw new UnsupportedOperationException();  }

        public RobNode next() {
            if (!hasNext()) throw new NoSuchElementException();
            RobNode item = current;
            current = current.next; 
            return item;
        }

    }