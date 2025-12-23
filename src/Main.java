import functions.*;
import functions.basic.*;
import threads.*;
import java.util.Random;

public class Main {
    private static Random random = new Random();

    public static void main(String[] args) {
        System.out.println("=== Лабораторная работа №6: Многопоточное интегрирование ===");
        System.out.println("=== Исправленная версия с предотвращением пропуска данных ===\n");

        // Часть 1: Тестирование интегрирования (Задание 1)
        System.out.println("ЧАСТЬ 1: ТЕСТИРОВАНИЕ МЕТОДА ИНТЕГРИРОВАНИЯ\n");
        testIntegration();

        // Часть 2: Последовательная версия (Задание 2)
        System.out.println("\n\nЧАСТЬ 2: ПОСЛЕДОВАТЕЛЬНАЯ ВЕРСИЯ (Задание 2)\n");
        nonThread();

        // Часть 3: Простая многопоточная версия (Задание 3)
        System.out.println("\n\nЧАСТЬ 3: ПРОСТАЯ МНОГОПОТОЧНАЯ ВЕРСИЯ (Задание 3)\n");
        simpleThreads();

        // Часть 4: ИСПРАВЛЕННАЯ версия с семафором (Задание 4)
        System.out.println("\n\nЧАСТЬ 4: ИСПРАВЛЕННАЯ ВЕРСИЯ С СЕМАФОРОМ (Задание 4)\n");
        complicatedThreads();

        System.out.println("\n\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===");
    }

    private static void testIntegration() {
        System.out.println("Задание 1: Реализация и тестирование метода интегрирования\n");

        Exp exp = new Exp();
        double theoretical = Math.exp(1) - 1;
        System.out.println("Функция: f(x) = e^x");
        System.out.println("Интеграл от 0 до 1");
        System.out.printf("Теоретическое значение: I(e^x dx) на отрезке (0,1) = e - 1 = %.10f\n\n", theoretical);

        System.out.println("Интегрирование с разными шагами:");
        System.out.println("==================================");

        double[] steps = {1.0, 0.5, 0.1, 0.01, 0.001};
        System.out.println("| Шаг      | Результат      | Ошибка          |");
        System.out.println("|----------|----------------|-----------------|");

        for (double step : steps) {
            try {
                double result = Functions.integrate(exp, 0, 1, step);
                double error = Math.abs(theoretical - result);
                System.out.printf("| %-8.4f | %-14.10f | %-15.10f |\n", step, result, error);
            } catch (IllegalArgumentException e) {
                System.out.printf("| %-8.4f | ОШИБКА: %s\n", step, e.getMessage());
            }
        }
    }

    private static void nonThread() {
        System.out.println("Задание 2: Последовательная (без потоков) версия программы\n");

        int tasksCount = 5;
        Task task = new Task(tasksCount);

        System.out.println("Генерация и вычисление " + tasksCount + " задач:");
        System.out.println("================================================");

        for (int i = 0; i < tasksCount; i++) {
            double base = 1 + random.nextDouble() * 9;
            Log logFunc = new Log(base);
            double left = random.nextDouble() * 100;
            double right = 100 + random.nextDouble() * 100;
            double step = random.nextDouble();

            System.out.printf("  Source [%2d]: %.4f %.4f %.4f (base=%.4f)\n",
                    i+1, left, right, step, base);

            double result = Functions.integrate(logFunc, left, right, step);
            System.out.printf("  Result [%2d]: %.4f %.4f %.4f %.10f\n\n",
                    i+1, left, right, step, result);
        }

        System.out.println("  Последовательная версия завершена!");
    }

    private static void simpleThreads() {
        System.out.println("Задание 3: Простая многопоточная версия (синхронизация wait/notify)\n");

        int tasksCount = 5;
        Task task = new Task(tasksCount);

        System.out.println("Запуск потоков: SimpleGenerator и SimpleIntegrator");
        System.out.println("Количество задач: " + tasksCount);
        System.out.println("======================================================");

        Thread generatorThread = new Thread(new SimpleGenerator(task));
        Thread integratorThread = new Thread(new SimpleIntegrator(task));

        generatorThread.setPriority(Thread.NORM_PRIORITY);
        integratorThread.setPriority(Thread.NORM_PRIORITY);

        System.out.println("Запуск потоков...");

        long startTime = System.currentTimeMillis();
        generatorThread.start();
        integratorThread.start();

        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n  Простая многопоточная версия завершена!");
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Все задания выполнены успешно.");
    }

    private static void complicatedThreads() {
        System.out.println("Задание 4: Исправленная версия с семафором\n");

        int tasksCount = 5;
        Task task = new Task(tasksCount);
        Semaphore semaphore = new Semaphore();

        System.out.println("Запуск потоков с использованием исправленного семафора:");
        System.out.println("Количество задач: " + tasksCount);
        System.out.println("=====================================================");

        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        generator.setPriority(Thread.NORM_PRIORITY);
        integrator.setPriority(Thread.NORM_PRIORITY);

        System.out.println("Запуск потоков...");

        long startTime = System.currentTimeMillis();
        generator.start();
        integrator.start();

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nПрерывание потоков через 50 мс...");
        generator.stopGenerator();
        integrator.stopIntegrator();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\n  Исправленная версия с семафором завершена!");
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Статистика:");
        System.out.println("  Сгенерировано заданий: " + generator.getGeneratedCount());
        System.out.println("  Обработано заданий: " + integrator.getIntegratedCount());
        System.out.println("  Пропущено заданий: " + (generator.getGeneratedCount() - integrator.getIntegratedCount()));
    }
}