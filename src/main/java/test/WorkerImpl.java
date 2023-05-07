package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class WorkerImpl implements WorkerApi {

    private final Map<Priority, List<Job<?>>> jobMap = new HashMap<>();
    private volatile List<Thread> workers;

    @Override
    public <T> void addJob(Priority priority, Callable<T> call) {
        List<Job<?>> jobList = jobMap.get(priority);
        if (jobList == null) {
            jobList = new ArrayList<>();
            jobMap.put(priority, jobList);
        }
        jobList.add(new Job<>(priority, call));
    }

    @Override
    public void startWorkers(int workerCount) {
        workers = new ArrayList<>();
        for (int i = 0; i < workerCount; i++) {
            Thread t = new Thread(this::workerLoop);
            t.start();
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                t.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            workers.add(t);
        }


    }

    @Override
    public void stopWorkers() {
        workers.forEach(Thread::stop);
    }

    private void workerLoop() {
        while (true) {
            doWork(Priority.HIGH);
            doWork(Priority.MEDIUM);
            doWork(Priority.LOW);
        }
    }

    private void doWork(Priority prio) {
        List<Job<?>> jobs = jobMap.remove(prio);
        if(!(jobs == null)) {    //check job in present
            jobs.forEach(Job::invoke);
        }
    }

    class Job<T> {
        public final Priority priority;
        public final Callable<T> call;

        public Job(Priority priority, Callable<T> call) {
            this.priority = priority;
            this.call = call;
        }

        public T invoke() {
            try {
                return call.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
