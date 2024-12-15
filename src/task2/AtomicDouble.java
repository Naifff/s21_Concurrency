package task2;

import java.util.concurrent.atomic.AtomicLong;

// Вспомогательный класс task2.AtomicDouble для атомарных операций с double
class AtomicDouble {
    private final AtomicLong bits;

    public AtomicDouble(double initialValue) {
        bits = new AtomicLong(Double.doubleToLongBits(initialValue));
    }

    public double get() {
        return Double.longBitsToDouble(bits.get());
    }

    public void set(double newValue) {
        bits.set(Double.doubleToLongBits(newValue));
    }

    public boolean compareAndSet(double expect, double update) {
        return bits.compareAndSet(Double.doubleToLongBits(expect),
                Double.doubleToLongBits(update));
    }

    public double getAndAdd(double delta) {
        while (true) {
            double current = get();
            double next = current + delta;
            if (compareAndSet(current, next)) {
                return current;
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(get());
    }
}
