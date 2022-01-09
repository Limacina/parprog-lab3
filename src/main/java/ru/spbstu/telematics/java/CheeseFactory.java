package ru.spbstu.telematics.java;
import java.util.LinkedList;
import java.util.List;

public class CheeseFactory {

    public static class Seller{
        public void getCheese(Buyer b) throws InterruptedException {
            if(b.type)
                synchronized (this) {
                    wait();
                }
            Cheese(b);
            if(!b.type)
                synchronized (this){
                        notify();
                }
        }

        public void Cheese(Buyer b) throws InterruptedException {
            Thread.sleep(500);
            System.out.println(Thread.currentThread().getName() + " got the cheese and is ready to go");
            b.finished = true;
        }

    }

    public static class Buyer implements Runnable{
        public boolean type; // true if humble
        private final Seller seller;
        public boolean finished = false;

        Buyer(boolean t, Seller s){
            this.seller = s;
            this.type = t;
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted() && !this.finished) {
                try {
                    seller.getCheese(this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(this.finished){
                System.out.println(Thread.currentThread().getName() + " left with his cheese");
                createNewBuyer(this.type, this.seller);
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized static void createNewBuyer(boolean type, Seller s) {
        Buyer b = new Buyer(type, s);
        Thread t = new Thread(b);
        String str = type ? "Humble " : "Brave ";
        System.out.println(str + t.getName() + " entered the queue");
        t.start();
    }

    public static void main(String[] args) throws InterruptedException {
        Seller seller = new Seller();
        for(int i = 0; i < 4; i++) // creating first four buyers
            createNewBuyer(i%2==0, seller);

    }
}
