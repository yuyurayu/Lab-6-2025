package threads;

public class Semaphore {
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;

    // Флаг для контроля обработки данных
    private boolean dataProcessed = true;

    public synchronized void beginRead() throws InterruptedException {
        // Читатель может начать, если нет писателей, запросов на запись И данные были обработаны
        while (writers > 0 || writeRequests > 0 || !dataProcessed) {
            wait();
        }
        readers++;
    }

    public synchronized void endRead() {
        readers--;
        dataProcessed = true; // Данные обработаны
        notifyAll();
    }

    public synchronized void beginWrite() throws InterruptedException {
        writeRequests++;
        // Писатель может начать, если нет читателей, других писателей И данные были обработаны
        while (readers > 0 || writers > 0 || !dataProcessed) {
            wait();
        }
        writeRequests--;
        writers++;
        dataProcessed = false; // Данные еще не обработаны
    }

    public synchronized void endWrite() {
        writers--;
        notifyAll();
    }
}