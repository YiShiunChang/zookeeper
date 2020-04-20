package performancemeasure;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class MeasureSetDataStrongWeak {

    static class MyRunnable implements Runnable {
        ZooKeeper zk;
        String path;
        public MyRunnable(String path, ZooKeeper zk) {
            this.path = path;
            this.zk = zk;
        }

        public void run() {
            double durationSum = 0;
            int count = 0;

            while (true) {
                try {
                    long start = System.nanoTime();
                    zk.setData("/" + path, "test".getBytes(), -1);
                    long end = System.nanoTime();

                    ++count;
                    durationSum += (end - start) / 1000000.0;

                    // print avg
                    if (count % 1000 == 0) {
                        System.out.println("[" + path + "] " + durationSum / count + " ms");
                    }
                } catch (KeeperException|InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args)
            throws IOException, KeeperException, InterruptedException {

        for (String path : args) {
            System.out.println("starting thread for path: " + path);
            Runnable r = new MyRunnable(path, new ZooKeeper("localhost:2183", 5000, null));
            new Thread(r).start();
        }
    }
}