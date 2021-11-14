/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threading;

import annotations.Injectable;
import threading.exceptions.ThreadAlreadyStartedException;
import threading.exceptions.ThreadNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Manel
 */
@Injectable
public class ThreadPoolSupervisor {

    List<BackgroundWorker> threadPool;

    public ThreadPoolSupervisor() {
        threadPool = new ArrayList<>();
    }

    public void addThread(BackgroundWorker worker) {
        threadPool.add(worker);
    }

    public void startThread(BackgroundWorker thread) throws ThreadNotFoundException, ThreadAlreadyStartedException {
        for (int i = 0; i < threadPool.size(); i++) {
            if (thread == threadPool.get(i)) {
                handleThreadStart(thread);
                return;
            }
        }
        throw new ThreadNotFoundException();
    }

    public boolean containsWorker(BackgroundWorker backgroundWorker) {
        return threadPool.contains(backgroundWorker);
    }

    public void terminateAllThreads() {
        for (BackgroundWorker backgroundWorker : threadPool) {
            backgroundWorker.terminate();
            backgroundWorker.interrupt();
        }
    }

    private void handleThreadStart(BackgroundWorker thread) throws ThreadAlreadyStartedException {
        if (thread.isAlive()) {
            throw new ThreadAlreadyStartedException();
        }
        thread.start();
    }

    public void removeIOWorkers() {
        threadPool = threadPool.stream().filter(t -> !(t instanceof IOWorker)).collect(Collectors.toList());
    }

    public void terminateIOWorkers() {
        for (BackgroundWorker backgroundWorker : threadPool) {
            if (!(backgroundWorker instanceof IOWorker)) {
                continue;
            }
            
            backgroundWorker.interrupt();
        }
    }
}
