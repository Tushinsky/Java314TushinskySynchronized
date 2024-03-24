import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static List<Integer> list;

    public static void main(String[] args) {
        // 1. Параллельный поиск в коллекции:
        System.out.println("1.\tПараллельный поиск в коллекции:");
        int bound = 5000;
        ArrayList<Integer> list1 = new ArrayList<>(bound);// список из 500 элементов
        // заполняем список
        for(int i = 0; i < bound; i++) {
            int num = new Random().nextInt(bound);
            list1.add(num);
        }
        list = Collections.synchronizedList(list1);
//        list = new ArrayList<>(bound);// список из 500 элементов
//        // заполняем список
//        for(int i = 0; i < bound; i++) {
//            int num = new Random().nextInt(bound);
//            list.add(num);
//        }

//        System.out.println("list: " + list);
        // список делим на 5 частей по 100 элементов, для них создаём свой поток
        int step = bound / 5;
        List<ValueThread> threadList = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            int value = new Random().nextInt(bound);
            System.out.println("value=" + value);
            ValueThread thread = new ValueThread(value, i * step, i * step + step);
            threadList.add(thread);
        }
        // запускаем потоки
        threadList.forEach(thread -> {
            thread.start();// запускаем каждый поток
            try {
                thread.join();// присоединяем его к основному потоку
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        // выводим сумму для каждого массива
        threadList.forEach(thread -> System.out.println(thread.getName() + ": find index = " + thread.getFindIndex()));
    }

//    private static synchronized boolean isFindValue(int index, int value) {
//            return list.get(index) == value;
//    }

    private static boolean isFindValue(int index, int value) {
//        synchronized (list) {
        return list.get(index) == value;
//        }

    }

//    private static boolean isFindValue(int index, int value) {
//        synchronized (list) {
//        return list.get(index) == value;
//        }
//
//    }


    /**
     * Класс, реализующий поток
     */
    private static class ValueThread extends Thread {
        private final int value;// значение, которое возвращается после обработки
        private final int lBound;//нижний индекс поиска
        private final int uBound;//верхний индекс поиска
        private int findIndex;

        /**
         * Создаёт новый объект потока
         * @param value значение для поиска
         * @param lBound нижний индекс массива для поиска
         * @param uBound верхний индекс массива для поиска
         */
        public ValueThread(int value, int lBound, int uBound) {
            this.value = value;
            this.lBound = lBound;
            this.uBound = uBound;
        }

        /**
         * Возвращает вычисленное переданной функцией значение
         * @return целочисленное значение вычисления функции
         */
        public int getFindIndex() {
            return findIndex;
        }


        @Override
        public void run() {
            int index = lBound;// счётчик цикла
            while(index < uBound) {
                // цикл пока не достигнут конец массива
                if (isFindValue(index, value)) {
                    // если условие удовлетворяет, запоминаем индекс в массиве, выходим из цикла
                    findIndex = index;
                    break;
                }
//                if(index % 100 == 0) {
//                    // информация о прогрессе
//                    System.out.println(Thread.currentThread().getName() + " обработано " + index + " элементов");
//                }
                index++;// увеличиваем счётчик

                try {
                    sleep(5);// поток спит, передаёт управление другим потокам
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }
}