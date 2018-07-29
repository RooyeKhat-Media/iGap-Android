package net.iGap.eventbus;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by keyvan on 3/14/16.
 */
public class EventManager {

    /**
     * READ ME :
     * this class used for notifying part of application to another part that not related to each other
     * for using this class you need to use instance method.
     * if your event number is exists just use it (I will tell in continue how to use), else declare your event number
     * event number MUST NOT equal with other event number, for this purpose use '++' to increment last number.
     * <p/>
     * HOW TO USE:
     * in onCreate() or onStart() ( It's related to your usage ) use following function :
     * {@link EventManager#getInstance()#addEventListener(int, EventListener)}
     * int is your event num and EventListener can be `this` so your activity must be implement or new instance of EventListener
     * {@link EventListener}.
     * then in onDestroy() or onPause() use {@link EventManager#getInstance()#removeEventListener(int, EventListener)}
     * to optimize eventManager and prevent from crash.
     * PS: if you remove your listener with above code your component not listen on event anymore.
     * <p/>
     * after getting event onReceived will be called in eventManager thread,
     * if you want heavy process in {@link EventListener#receivedMessage(int, Object...)} use your thread
     */


    public static final int ON_ACCESS_TOKEN_RECIVE = 1;
    public static final int ON_INIT_PAY = 2;
    public static final int ON_INIT_PAY_ERROR = 4;
    public static final int ON_PAYMENT_RESULT_RECIEVED = 3;


    private static EventManager eventListenerInstance;

    private ErrorHandler errorHandler;
    private ExecutorService threadPool;
    private SparseArray<List<EventListener>> eventListenerMap;
    private ReentrantLock eventLock = new ReentrantLock(true);


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int DEFAULT_POOL_SIZE = 2;


    private EventManager(int threadPoolSize, boolean loggingInDebug, @Nullable ErrorHandler errorHandler) {
        eventListenerMap = new SparseArray<>();

        this.errorHandler = errorHandler;

        if (threadPoolSize > MAXIMUM_POOL_SIZE) {
            threadPoolSize = MAXIMUM_POOL_SIZE;
        }

        threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }


    public static EventManager getInstance() {
        if (eventListenerInstance == null) {
            eventListenerInstance = new EventManager(DEFAULT_POOL_SIZE, false, null);
        }

        return eventListenerInstance;
    }

    public void addEventListener(final int eventNum, final EventListener listener) {

        if (listener == null) {
            return;
        }


        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                eventLock.lock();
                try {
                    List<EventListener> objects = eventListenerMap.get(eventNum);
                    if (objects == null) {
                        objects = new ArrayList<>();
                    }

                    if (objects.contains(listener)) {
                        return;
                    }

                    objects.add(listener);
                    eventListenerMap.put(eventNum, objects);

                    Logger.printLog("addEventListener with eventNum = " + eventNum + ".\nNow " + objects.size() + " listener listen on this eventNum", null);
                } finally {
                    eventLock.unlock();
                }
            }
        });
    }

    public void removeEventListener(final int eventNum, final EventListener listener) {

        final List<EventListener> eventListeners = eventListenerMap.get(eventNum);
        if (eventListeners == null || eventListeners.size() == 0) {
            Logger.printLog("eventListeners is null, there is no event with this eventNum = " + eventNum, null);
            return;
        }

        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                eventLock.lock();
                try {
                    eventListeners.remove(listener);
                    Logger.printLog("removeEvent for this eventNum = " + eventNum + ".\nNow " + eventListeners.size() + " listener listen on this eventNum", null);
                } finally {
                    eventLock.unlock();
                }

            }
        });
    }

    public void postEvent(final int eventNum, final Object... message) {

        final List<EventListener> eventListeners = eventListenerMap.get(eventNum);
        if (eventListeners == null || eventListeners.size() == 0) {
            Logger.printLog("there is no listener for this eventNum = " + eventNum, null);
            return;
        }

        Logger.printLog("postEvent for this eventNum = " + eventNum, null);

        threadPool.submit(new Runnable() {
            @Override
            public void run() {

                eventLock.lock();
                try {
                    for (EventListener listener : eventListeners) {
                        try {
                            listener.receivedMessage(eventNum, message);
                        } catch (Exception e) {
                            Logger.printLog("error happened in receiveMessage", e);
                            if (errorHandler != null) {
                                errorHandler.onFailure(e);
                            }
                        }
                    }
                } finally {
                    eventLock.unlock();
                }
            }
        });
    }


    public static class Builder {


        private int threadPoolSize = DEFAULT_POOL_SIZE;
        private ErrorHandler errorHandler;
        private boolean loggingInDebug = false;

        public Builder setThreadPoolSize(int threadPoolSize) {
            this.threadPoolSize = threadPoolSize;
            return this;
        }

        public Builder setErrorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder enableLoggingInDebug() {
            this.loggingInDebug = true;
            return this;
        }

        public void build() {

            // only one object can build. you can't init twice.
            if (eventListenerInstance != null) {
                return;
            }

            eventListenerInstance = new EventManager(threadPoolSize, loggingInDebug, errorHandler);
        }
    }
}