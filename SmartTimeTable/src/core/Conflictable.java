package core;

/**
 * @author Diogo Regateiro
 *
 * @param <T> The class that can conflict with the implementing class.
 */
public interface Conflictable<T> {
	/**
	 * Checks if the object conflicts with the given one.
	 * @param obj Object to be checks against.
	 * @return true if a conflict exist, false otherwise.
	 */
	boolean conflictsWith(T obj, OverlapOptions oo);
}
