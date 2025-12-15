import functions.*;
import functions.basic.*;
import threads.*;
import java.util.Random;

public class Main {
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Лабораторная работа №6: Многопоточное интегрирование ===");
        System.out.println("=== Полное тестирование всех заданий ===\n");

        // Часть 1: Тестирование интегрирования (Задание 1)
        System.out.println("ЧАСТЬ 1: ТЕСТИРОВАНИЕ МЕТОДА ИНТЕГРИРОВАНИЯ\n");
        testIntegration();

        // Часть 2: Последовательная версия (Задание 2)
        System.out.println("\n\nЧАСТЬ 2: ПОСЛЕДОВАТЕЛЬНАЯ ВЕРСИЯ (Задание 2)\n");
        nonThread();

        // Часть 3: Простая многопоточная версия (Задание 3)
        System.out.println("\n\nЧАСТЬ 3: ПРОСТАЯ МНОГОПОТОЧНАЯ ВЕРСИЯ (Задание 3)\n");
        simpleThreads();

        // Часть 4: Версия с семафором (Задание 4)
        System.out.println("\n\nЧАСТЬ 4: ВЕРСИЯ С СЕМАФОРОМ (Задание 4)\n");
        complicatedThreads();

        // Часть 5: Дополнительные тесты приоритетов
        System.out.println("\n\nЧАСТЬ 5: ТЕСТИРОВАНИЕ ПРИОРИТЕТОВ ПОТОКОВ\n");
        testWithDifferentPriorities();

        System.out.println("\n\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===");
    }

    // =========================================================================
    // ЗАДАНИЕ 1: МЕТОД ИНТЕГРИРОВАНИЯ
    // =========================================================================

    private static void testIntegration() {
        System.out.println("Задание 1: Реализация и тестирование метода интегрирования\n");

        // Создаем объект экспоненты
        Exp exp = new Exp();

        // Теоретическое значение интеграла экспоненты от 0 до 1
        // ∫₀¹ e^x dx = e - 1
        double theoretical = Math.exp(1) - 1;
        System.out.println("Функция: f(x) = e^x");
        System.out.println("Интеграл от 0 до 1");
        System.out.printf("Теоретическое значение: I(e^x dx) на отрезке (0, 1) = e - 1 = %.10f\n\n", theoretical);

        // Тест 1: Интегрирование с разными шагами
        System.out.println("1. Интегрирование с разными шагами:");
        System.out.println("==================================");

        double[] steps = {1.0, 0.5, 0.1, 0.05, 0.01, 0.005, 0.001, 0.0005, 0.0001};

        System.out.println("| Шаг      | Результат      | Ошибка          | Относительная ошибка |");
        System.out.println("|----------|----------------|-----------------|---------------------|");

        for (double step : steps) {
            try {
                double result = Functions.integrate(exp, 0, 1, step);
                double error = Math.abs(theoretical - result);
                double relativeError = error / theoretical;
                System.out.printf("| %-8.4f | %-14.10f | %-15.10f | %-19.10f |\n",
                        step, result, error, relativeError);
            } catch (IllegalArgumentException e) {
                System.out.printf("| %-8.4f | ОШИБКА: %s\n", step, e.getMessage());
            }
        }

        // Тест 2: Поиск шага для точности 1e-7
        System.out.println("\n2. Поиск оптимального шага для точности 1e-7:");
        System.out.println("============================================");

        double targetError = 1e-7;
        double currentStep = 0.1;
        double currentResult = Functions.integrate(exp, 0, 1, currentStep);
        double currentError = Math.abs(theoretical - currentResult);
        int iterations = 0;

        System.out.printf("Целевая точность: %.10f\n", targetError);
        System.out.println("\nПроцесс поиска:");
        System.out.println("| Итерация | Шаг        | Результат      | Ошибка          |");
        System.out.println("|----------|------------|----------------|-----------------|");

        while (currentError > targetError && iterations < 20) {
            System.out.printf("| %-8d | %-10.8f | %-14.10f | %-15.10f |\n",
                    iterations, currentStep, currentResult, currentError);

            currentStep /= 2;
            currentResult = Functions.integrate(exp, 0, 1, currentStep);
            currentError = Math.abs(theoretical - currentResult);
            iterations++;
        }

        if (currentError <= targetError) {
            System.out.printf("\n !Достигнута точность %.10f при шаге %.10f\n", targetError, currentStep);
        } else {
            System.out.println("\n !Не удалось достичь заданной точности за 20 итераций");
        }

        // Тест 3: Проверка граничных случаев
        System.out.println("\n3. Проверка граничных случаев:");
        System.out.println("================================");

        // 3.1. Выход за левую границу
        System.out.println("3.1. Интегрирование за пределами области определения (левая граница):");
        try {
            double result = Functions.integrate(exp, -10, 1, 0.1);
            System.out.printf("  Результат: %.10f\n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("   !Поймано исключение: " + e.getMessage());
        }

        // 3.2. Выход за правую границу
        System.out.println("\n3.2. Интегрирование за пределами области определения (правая граница):");
        try {
            double result = Functions.integrate(exp, 0, 1000, 0.1);
            System.out.printf("  Результат: %.10f\n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("   !Поймано исключение: " + e.getMessage());
        }

        // 3.3. Некорректный шаг
        System.out.println("\n3.3. Некорректный шаг интегрирования:");
        try {
            double result = Functions.integrate(exp, 0, 1, -0.1);
            System.out.printf("  Результат: %.10f\n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("   !Поймано исключение: " + e.getMessage());
        }

        // 3.4. Шаг = 0
        System.out.println("\n3.4. Шаг интегрирования = 0:");
        try {
            double result = Functions.integrate(exp, 0, 1, 0);
            System.out.printf("  Результат: %.10f\n", result);
        } catch (IllegalArgumentException e) {
            System.out.println("   !Поймано исключение: " + e.getMessage());
        }

        // Тест 4: Интегрирование других функций
        System.out.println("\n4. Интегрирование других функций:");
        System.out.println("==================================");

        // 4.1. Синус
        Sin sin = new Sin();
        double sinTheoretical = 1 - Math.cos(1); // ∫₀¹ sin(x) dx = 1 - cos(1)
        double sinResult = Functions.integrate(sin, 0, 1, 0.001);
        System.out.printf("4.1. I(sin(x))dx на отрезке (0, 1):\n");
        System.out.printf("  Теоретическое: %.10f\n", sinTheoretical);
        System.out.printf("  Численное:     %.10f\n", sinResult);
        System.out.printf("  Ошибка:        %.10f\n\n", Math.abs(sinTheoretical - sinResult));

        // 4.2. Косинус
        Cos cos = new Cos();
        double cosTheoretical = Math.sin(1); // ∫₀¹ cos(x) dx = sin(1)
        double cosResult = Functions.integrate(cos, 0, 1, 0.001);
        System.out.printf("4.2. I(cos(x) dx) на отрезке (0, 1):\n");
        System.out.printf("  Теоретическое: %.10f\n", cosTheoretical);
        System.out.printf("  Численное:     %.10f\n", cosResult);
        System.out.printf("  Ошибка:        %.10f\n", Math.abs(cosTheoretical - cosResult));
    }

    // =========================================================================
    // ЗАДАНИЕ 2: ПОСЛЕДОВАТЕЛЬНАЯ ВЕРСИЯ
    // =========================================================================

    private static void nonThread() {
        System.out.println("Задание 2: Последовательная (без потоков) версия программы\n");

        int tasksCount = 10; // Для наглядности используем 10 задач вместо 100
        Task task = new Task(tasksCount);

        System.out.println("Генерация и вычисление " + tasksCount + " задач:");
        System.out.println("================================================");

        for (int i = 0; i < tasksCount; i++) {
            // 1. Создаем логарифмическую функцию со случайным основанием
            double base = 1 + random.nextDouble() * 9; // от 1 до 10
            Log logFunc = new Log(base);

            // 2. Левая граница: от 0 до 100
            double left = random.nextDouble() * 100;

            // 3. Правая граница: от 100 до 200
            double right = 100 + random.nextDouble() * 100;

            // 4. Шаг дискретизации: от 0 до 1
            double step = random.nextDouble();

            // 5. Выводим сообщение
            System.out.printf("  Source [%2d]: %.4f %.4f %.4f (base=%.4f)\n",
                    i+1, left, right, step, base);

            // 6. Вычисляем интеграл
            double result = Functions.integrate(logFunc, left, right, step);

            // 7. Выводим результат
            System.out.printf("  Result [%2d]: %.4f %.4f %.4f %.10f\n\n",
                    i+1, left, right, step, result);
        }

        System.out.println(" !Последовательная версия завершена!");
    }

    // =========================================================================
    // ЗАДАНИЕ 3: ПРОСТАЯ МНОГОПОТОЧНАЯ ВЕРСИЯ
    // =========================================================================

    private static void simpleThreads() {
        System.out.println("Задание 3: Простая многопоточная версия (синхронизация wait/notify)\n");

        int tasksCount = 10; // Для наглядности используем 10 задач
        Task task = new Task(tasksCount);

        System.out.println("Запуск двух потоков: SimpleGenerator и SimpleIntegrator");
        System.out.println("Количество задач: " + tasksCount);
        System.out.println("======================================================");

        // Создаем потоки
        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));

        // Устанавливаем приоритеты (для тестирования)
        generatorThread.setPriority(Thread.NORM_PRIORITY);
        integratorThread.setPriority(Thread.NORM_PRIORITY);

        System.out.println("Запуск потоков...");

        // Запускаем потоки
        generatorThread.start();
        integratorThread.start();

        // Ждем завершения потоков
        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n !Простая многопоточная версия завершена!");
        System.out.println("Все задания выполнены успешно.");
    }

    // =========================================================================
    // ЗАДАНИЕ 4: ВЕРСИЯ С СЕМАФОРОМ
    // =========================================================================

    private static void complicatedThreads() {
        System.out.println("Задание 4: Версия с семафором (с управлением доступом)\n");

        int tasksCount = 10; // Для наглядности используем 10 задач
        Task task = new Task(tasksCount);
        Semaphore semaphore = new Semaphore();

        System.out.println("Запуск потоков с использованием семафора:");
        System.out.println("Количество задач: " + tasksCount);
        System.out.println("=========================================");

        // Создаем потоки
        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        // Устанавливаем приоритеты
        generator.setPriority(Thread.NORM_PRIORITY);
        integrator.setPriority(Thread.NORM_PRIORITY);

        System.out.println("Запуск потоков...");

        // Запускаем потоки
        long startTime = System.currentTimeMillis();
        generator.start();
        integrator.start();

        // Ждем 50 мс и прерываем (как требует задание)
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Прерываем потоки
        System.out.println("\nПрерывание потоков через 50 мс...");
        generator.stopGenerator();
        integrator.stopIntegrator();

        // Ждем завершения потоков
        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n !Версия с семафором завершена!");
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Потоки корректно обработали прерывание.");
    }

    // =========================================================================
    // ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ: ПРИОРИТЕТЫ ПОТОКОВ
    // =========================================================================

    private static void testWithDifferentPriorities() {
        System.out.println("Дополнительные тесты: Влияние приоритетов на выполнение\n");

        int tasksCount = 20; // Для теста производительности

        // Тест 1: Генератор с высоким приоритетом
        System.out.println("Тест 1: Генератор с высоким приоритетом, интегратор с низким:");
        long time1 = testPriority(tasksCount, Thread.MAX_PRIORITY, Thread.MIN_PRIORITY);

        // Тест 2: Интегратор с высоким приоритетом
        System.out.println("\nТест 2: Интегратор с высоким приоритетом, генератор с низким:");
        long time2 = testPriority(tasksCount, Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);

        // Тест 3: Одинаковые приоритеты
        System.out.println("\nТест 3: Оба потока с нормальным приоритетом:");
        long time3 = testPriority(tasksCount, Thread.NORM_PRIORITY, Thread.NORM_PRIORITY);

        // Анализ результатов
        System.out.println("\nАнализ результатов:");
        System.out.println("==========================================");
        System.out.println("Время выполнения при разных приоритетах:");
        System.out.printf("1. Приоритет у генератора: %d мс\n", time1);
        System.out.printf("2. Приоритет у интегратора: %d мс\n", time2);
        System.out.printf("3. Одинаковые приоритеты: %d мс\n", time3);

        long minTime = Math.min(Math.min(time1, time2), time3);
        if (minTime == time1) {
            System.out.println("\nВывод: Наилучшая производительность достигается, когда у генератора высокий приоритет.");
        } else if (minTime == time2) {
            System.out.println("\nВывод: Наилучшая производительность достигается, когда у интегратора высокий приоритет.");
        } else {
            System.out.println("\nВывод: Наилучшая производительность достигается при одинаковых приоритетах.");
        }
    }

    private static long testPriority(int tasksCount, int generatorPriority, int integratorPriority) {
        Task task = new Task(tasksCount);
        Semaphore semaphore = new Semaphore();

        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        generator.setPriority(generatorPriority);
        integrator.setPriority(integratorPriority);

        long startTime = System.currentTimeMillis();

        generator.start();
        integrator.start();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    // =========================================================================
    // ВСПОМОГАТЕЛЬНЫЙ КЛАСС ДЛЯ ТЕСТИРОВАНИЯ
    // =========================================================================

    // Вспомогательный класс для функции x (линейной функции) - используется в тестах
    static class LinearFunction implements Function {
        @Override
        public double getLeftDomainBorder() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double getRightDomainBorder() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public double getFunctionValue(double x) {
            return x;
        }
    }
}