package az.ingress.example.singleton;

public final class DoubleCheckLocking {

    private volatile static DoubleCheckLocking doubleCheckLocking;

    private DoubleCheckLocking(){}

    public static DoubleCheckLocking getInstance() {
        if (doubleCheckLocking == null)
            synchronized (DoubleCheckLocking.class) {
                if (doubleCheckLocking == null)
                    doubleCheckLocking = new DoubleCheckLocking();
            }
        return doubleCheckLocking;
    }
}
