package az.ingress.example.singleton;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicLoadingThreadSafe {

    private static final AtomicReference<AtomicLoadingThreadSafe> atomic = new AtomicReference<>();

    private AtomicLoadingThreadSafe() {}

    public static AtomicLoadingThreadSafe getInstance() {
        if (atomic.get() == null)
            atomic.compareAndSet(null, new AtomicLoadingThreadSafe());
        return atomic.get();
    }

}
