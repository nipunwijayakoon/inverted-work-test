package test;

import java.util.concurrent.Callable;

/**
 * WorkerApi defines an Executor abstraction for work items with different priority.
 * 
 * An implementation will maintain a set of worker Threads that process available jobs
 * in order of priority.
 */
public interface WorkerApi {

    /**
     * Adds a job that will be run by executed by this worker.
     */
    <T> void addJob(Priority priority, Callable<T> call);

    /**
     * Start a number of worker threads that will process jobs.
     */
    void startWorkers(int workerCount);

    /**
     * Stop the worker threads for safe shutdown.
     */
    void stopWorkers();

    enum Priority {
        HIGH,
        LOW,
        MEDIUM
    }


}
