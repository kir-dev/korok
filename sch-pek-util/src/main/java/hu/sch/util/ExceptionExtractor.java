package hu.sch.util;

import java.util.Objects;

public class ExceptionExtractor {

    public static <T> T extract(Throwable ex, Class<T> exClass) {
        Objects.requireNonNull(ex);
        Objects.requireNonNull(exClass);

        if (ex.getClass().equals(exClass)) {
            return (T)ex;
        }

        Throwable e = ex;

        while (e.getCause() != null) {
            e = e.getCause();
            if (e.getClass().equals(exClass)) {
                return (T)e;
            }
        }

        return null;
    }

    private ExceptionExtractor() {
    }
}