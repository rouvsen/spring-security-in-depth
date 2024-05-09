package az.ingress.example.singleton;

public final class ClassLoaderLoading {

    private ClassLoaderLoading() {}

    private static class LoaderHolder {
        private static final ClassLoaderLoading INSTANCE = new ClassLoaderLoading();
    }

    public static ClassLoaderLoading getInstance() {
        return LoaderHolder.INSTANCE;
    }
}
