package org.example.liquidsort;

import java.util.ArrayList;
import java.util.List;

public class Tube {
    private final int capacity;
    private final List<Integer> drops;

    public Tube(int capacity) {
        this.capacity = capacity;
        this.drops = new ArrayList<>();
    }

    public Tube(int capacity, List<Integer> initialDrops) {
        this.capacity = capacity;
        this.drops = new ArrayList<>(initialDrops);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return drops.size();
    }

    public boolean isEmpty() {
        return drops.isEmpty();
    }

    public boolean isFull() {
        return drops.size() >= capacity;
    }

    public int getFreeSpace() {
        return capacity - drops.size();
    }

    public Integer getTopColor() {
        if (drops.isEmpty()) {
            return null;
        }
        return drops.get(drops.size() - 1);
    }

    public int getTopColorCount() {
        if (drops.isEmpty()) {
            return 0;
        }
        Integer topColor = getTopColor();
        int count = 0;
        for (int i = drops.size() - 1; i >= 0; i--) {
            if (drops.get(i).equals(topColor)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public void addDrop(Integer color) {
        if (isFull()) {
            throw new IllegalStateException("Пробирка заполнена");
        }
        drops.add(color);
    }

    public Integer removeTopDrop() {
        if (isEmpty()) {
            throw new IllegalStateException("Пробирка пуста");
        }
        return drops.remove(drops.size() - 1);
    }

    public int pourTo(Tube target, Integer maxDrops) {
        if (isEmpty()) {
            return 0;
        }
        if (target.isFull()) {
            return 0;
        }

        Integer topColor = getTopColor();
        if (target.isEmpty() || target.getTopColor().equals(topColor)) {
            int availableSpace = target.getFreeSpace();
            int topColorCount = getTopColorCount();
            int dropsToPour = maxDrops != null 
                ? Math.min(maxDrops, Math.min(availableSpace, topColorCount))
                : Math.min(availableSpace, topColorCount);

            for (int i = 0; i < dropsToPour; i++) {
                target.addDrop(removeTopDrop());
            }
            return dropsToPour;
        }
        return 0;
    }

    public boolean isSorted() {
        if (isEmpty() || drops.size() == 1) {
            return true;
        }
        Integer firstColor = drops.get(0);
        for (Integer color : drops) {
            if (!color.equals(firstColor)) {
                return false;
            }
        }
        return true;
    }

    public Tube copy() {
        return new Tube(capacity, new ArrayList<>(drops));
    }


    public List<Integer> getDrops() {
        return new ArrayList<>(drops);
    }

    @Override
    public String toString() {
        return "Tube{" + drops + "}";
    }
}

