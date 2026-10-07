package local.vip;
import java.util.Comparator;
import java.util.function.Predicate;
public final class VipOrder {
    private VipOrder() {}
    // Keep our player first
    public static <T> Comparator<T> selfFirst(Comparator<T> original, Predicate<T> isSelf) {
        return (left, right) -> {
            int priority = Boolean.compare(isSelf.test(right), isSelf.test(left));
            return priority != 0 ? priority : original.compare(left, right);
        };
    }
}

