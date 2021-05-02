package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ItemSet {
    private int index;
    private Set<Item> closure;
    private Map<Integer, Integer> gotoTable = new HashMap<>();


    public void setIndex(int index) {
        this.index = index;
    }

    public void setClosure(Set<Item> closure) {
        this.closure = closure;
    }

    public void setGotoTable(Map<Integer, Integer> gotoTable) {
        this.gotoTable = gotoTable;
    }

    public int getIndex() {
        return index;
    }

    public Set<Item> getClosure() {
        return closure;
    }

    public Map<Integer, Integer> getGotoTable() {
        return gotoTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSet itemSet = (ItemSet) o;
        return Objects.equals(closure, itemSet.closure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(closure);
    }

    @Override
    public String toString() {
        return "ItemSet{" +
                "index=" + index +
                ", closure=" + closure +
                ", gotoTable=" + gotoTable +
                '}';
    }
}
