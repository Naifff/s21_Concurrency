package task2;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

// Класс банка для управления счетами и транзакциями
public class ConcurrentBank {
	private final ConcurrentMap<Integer, BankAccount> accounts = new ConcurrentHashMap<>();
	private final AtomicInteger accountIdGenerator = new AtomicInteger(0);
	private final ConcurrentMap<Integer, ReentrantLock> accountLocks = new ConcurrentHashMap<>();


	// Создание нового счета
	public BankAccount createAccount(double initialBalance) {
		if (initialBalance < 0) {
			throw new IllegalArgumentException("Initial balance cannot be negative");
		}

		int accountId = accountIdGenerator.incrementAndGet();
		BankAccount account = new BankAccount(accountId, initialBalance);
		accounts.put(accountId, account);
		accountLocks.put(accountId, new ReentrantLock());
		return account;
	}

	// Выполнение перевода между счетами
	public boolean transfer(BankAccount from, BankAccount to, double amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Transfer amount must be positive");
		}
		if (from == null || to == null) {
			throw new IllegalArgumentException("Accounts cannot be null");
		}
		if (from.getId() == to.getId()) {
			throw new IllegalArgumentException("Cannot transfer to the same account");
		}

		// Блокируем счета в определенном порядке для предотвращения взаимоблокировки

		ReentrantLock firstLock = accountLocks.get(Math.min(from.getId(), to.getId()));
		ReentrantLock secondLock = accountLocks.get(Math.max(from.getId(), to.getId()));

		firstLock.lock();
		try {
			secondLock.lock();
			try {
				// Проверяем наличие средств и выполняем перевод
				if (from.withdraw(amount)) {
					to.deposit(amount);
					return true;
				}
				return false;
			} finally {
				secondLock.unlock();
			}
		} finally {
			firstLock.unlock();
		}
	}

	// Получение общего баланса всех счетов
	public double getTotalBalance() {
		return accounts.values().stream()
				.mapToDouble(BankAccount::getBalance)
				.sum();
	}
}

