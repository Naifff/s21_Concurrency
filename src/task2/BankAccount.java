package task2;

// Класс банковского счета с потокобезопасными операциями
public class BankAccount {
    private final int id;
    private final AtomicDouble balance;  // Используем task2.AtomicDouble для атомарных операций с балансом

    public BankAccount(int id, double initialBalance) {
        this.id = id;
        this.balance = new AtomicDouble(initialBalance);
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance.get();
    }

    // Метод пополнения счета
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance.getAndAdd(amount);
    }

    // Метод снятия средств
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        while (true) {
            double currentBalance = balance.get();
            if (currentBalance < amount) {
                return false;  // Недостаточно средств
            }

            if (balance.compareAndSet(currentBalance, currentBalance - amount)) {
                return true;  // Успешное снятие
            }
            // Если compareAndSet не удался, повторяем попытку
        }
    }
}