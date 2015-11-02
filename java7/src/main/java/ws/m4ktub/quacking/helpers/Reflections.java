package ws.m4ktub.quacking.helpers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods related with reflection classes.
 * 
 * @author m4ktub
 */
public final class Reflections {

	private static Class<?>[][] PRIMITIVE_TYPES = new Class<?>[][] { { boolean.class, Boolean.class }, { byte.class, Byte.class }, { char.class, Character.class },
			{ double.class, Double.class }, { float.class, Float.class }, { int.class, Integer.class }, { long.class, Long.class }, { short.class, Short.class },
			{ void.class, Void.class } };

	/**
	 * Verify if a method is accessible and make it accessible if needed. If the
	 * method is public or is already accessible then nothing is done.
	 * 
	 * @param method
	 *            The method to make accessible.
	 * @throws SecurityException
	 *             If the method is not accessible and there is a security
	 *             manager that forbids the change in the method's
	 *             accessibility.
	 */
	public static void ensureAccessible(Method method) {
		boolean isMethodPublic = Modifier.isPublic(method.getModifiers());
		boolean isClassPublic = Modifier.isPublic(method.getDeclaringClass().getModifiers());
		if ((!isMethodPublic || !isClassPublic) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * Obtains a compatible method from a, possibly unrelated, object instance.
	 * <p>
	 * A compatible method is a method:
	 * <ol>
	 * <li>with the same name,
	 * <li>with compatible argument types, and
	 * <li>with a compatible return type.
	 * </ol>
	 * <p>
	 * In this sense, compatible is more general than standard Java rules. For
	 * example, a method is compatible as long it is possible to call it, via
	 * reflection, without explicitly converting values between types. This
	 * means that primitive wrapper types are considered compatible because the
	 * Reflection API handles that case implicitly.
	 * <p>
	 * If the object implements the given method given then the same method is
	 * returned as it is considered the most compatible method found. Otherwise,
	 * a multi-dispatch resolution is used. From all methods with the same name,
	 * the method with the most specific parameter types matching the actual
	 * argument's types will be used.
	 * <p>
	 * 
	 * @param object
	 *            The object instance where to look for a compatible method.
	 * @param method
	 *            The template method used to obtain the name and types.
	 * @param args
	 *            The actual arguments that will be used in the invocation.
	 *            Argument's types will be used to determine candidate methods
	 *            in a multi-dispatch resolution.
	 * @return The compatible method found, or <code>null</code> if no
	 *         compatible method was found.
	 */
	public static Method getCompatibleMethod(Object object, Method method, Object[] args) {
		if (method.getDeclaringClass().isAssignableFrom(object.getClass())) {
			return method;
		}

		Method objMethod = getJavaMethod(object, method);
		if (objMethod == null) {
			objMethod = getMultiDispatchMethod(object, method, args);
		}

		if (objMethod == null) {
			return null;
		}

		// check compatible return types
		Type intfMethodReturnType = method.getGenericReturnType();
		Type implMethodReturnType = objMethod.getGenericReturnType();

		if (!isTypeCompatible(intfMethodReturnType, implMethodReturnType)) {
			return null;
		}

		return objMethod;
	}

	private static Method getJavaMethod(Object object, Method method) {
		try {
			Class<?> objClass = object.getClass();
			return objClass.getMethod(method.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private static Method getMultiDispatchMethod(Object object, Method method, Object[] args) {
		// get candidates
		List<Method> candidates = getMethodsWithName(object.getClass(), method.getName());
		if (candidates.isEmpty()) {
			return null;
		}

		// ignore those with different parameter count
		Iterator<Method> candidatesIterator = candidates.iterator();
		while (candidatesIterator.hasNext()) {
			Method candidate = (Method) candidatesIterator.next();

			if (candidate.getParameterTypes().length != args.length) {
				candidatesIterator.remove();
			}
		}

		if (candidates.isEmpty()) {
			return null;
		}

		// find most specific method using left-to-right disambiguation
		Type[] parameterTypes = getMultiDispatchParameterTypes(method, args);
		return getMultiDispatchMethodMatch(candidates, parameterTypes, 0);
	}

	private static Method getMultiDispatchMethodMatch(List<Method> candidates, Type[] concreteParameterTypes, int pos) {
		if (candidates.isEmpty()) {
			return null;
		}

		if (pos >= concreteParameterTypes.length) {
			return candidates.get(0);
		}

		int score = Integer.MAX_VALUE;
		List<Method> others = new ArrayList<Method>();
		List<Method> best = new ArrayList<Method>();
		Type concreteParamType = concreteParameterTypes[pos];

		Iterator<Method> candidatesIterator = candidates.iterator();
		while (candidatesIterator.hasNext()) {
			Method method = candidatesIterator.next();
			Type paramType = method.getGenericParameterTypes()[pos];

			int paramScore = getAssignableScore(paramType, concreteParamType);
			if (paramScore < 0) {
				continue;
			}

			if (paramScore == score) {
				best.add(method);
			} else if (paramScore < score) {
				score = paramScore;
				others.addAll(best);
				best.clear();
				best.add(method);
			} else {
				others.add(method);
			}
		}

		Method result = getMultiDispatchMethodMatch(best, concreteParameterTypes, pos + 1);
		if (result == null) {
			result = getMultiDispatchMethodMatch(others, concreteParameterTypes, pos);
		}

		return result;
	}

	private static int getAssignableScore(Type paramType, Type argType) {
		if (argType == null || paramType.equals(argType)) {
			// reached Object or the same type
			return 0;
		}

		if (!isTypeCompatible(paramType, argType)) {
			return -1;
		}

		if (!(argType instanceof Class<?>)) {
			return 0;
		}

		Class<?> classArgType = (Class<?>) argType;
		int score = getAssignableScore(paramType, classArgType.getSuperclass());
		return score < 0 ? -1 : score + 1;
	}

	/**
	 * Obtains the most specific types for the parameters of a method based on
	 * concrete arguments that will be passed. This allows to determine a more
	 * specific method using a dynamic resolution based on the arguments types.
	 * 
	 * <p>
	 * The rules are very simple. For each <code>null</code> argument the
	 * parameter type will be used. For the other arguments their value type
	 * will be used.
	 * </p>
	 * 
	 * @param method
	 *            The base method from which to obtain the static parameter
	 *            types.
	 * @param argumentTypes
	 *            The concrete arguments that will be passed.
	 * @return An array with the most specific type that can be determined for
	 *         each parameter.
	 */
	public static Type[] getMultiDispatchParameterTypes(Method method, Object[] argumentTypes) {
		Type[] genericParameterTypes = method.getGenericParameterTypes();

		Type[] result = new Type[argumentTypes.length];
		for (int i = 0; i < result.length; i++) {
			Object argType = argumentTypes[i];
			result[i] = argType != null ? argType.getClass() : genericParameterTypes[i];
		}

		return result;
	}

	/**
	 * Obtains all public methods, declared or inherited by the given class, and
	 * with a particular name.
	 * 
	 * @param clazz
	 *            The target class use to search for methods.
	 * @param name
	 *            The name of the method.
	 * @return A list of methods with the indicated name. The list has no
	 *         particular order.
	 */
	public static List<Method> getMethodsWithName(Class<?> clazz, String name) {
		List<Method> result = new ArrayList<Method>();

		final String internedName = name.intern();
		final Method[] methods = clazz.getMethods();

		for (int i = 0; i < methods.length; i++) {
			Method m = methods[i];

			if (m.getName() != internedName) {
				continue;
			}

			result.add(m);
		}

		return result;
	}

	public static boolean isTypeCompatible(Type targetType, Type valueType) {
		if (targetType instanceof Class<?>) {
			return isTypeCompatibleValue((Class<?>) targetType, valueType);
		}

		if (targetType instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariableType = (TypeVariable<?>) targetType;
			Type[] bounds = typeVariableType.getBounds();

			boolean boundsCompatible = true;
			for (int i = 0; i < bounds.length; i++) {
				boundsCompatible &= isTypeCompatible(bounds[i], valueType);

				if (!boundsCompatible) {
					break;
				}
			}

			return boundsCompatible;
		}

		return false;
	}

	private static boolean isTypeCompatibleValue(Class<?> targetType, Type valueType) {
		if (valueType instanceof Class<?>) {
			return isGenerallyAssignableFrom(targetType, (Class<?>) valueType);
		}

		if (valueType instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariableType = (TypeVariable<?>) valueType;
			Type[] bounds = typeVariableType.getBounds();

			boolean boundsCompatible = false;
			for (int i = 0; i < bounds.length; i++) {
				boundsCompatible |= isTypeCompatible(targetType, bounds[i]);

				if (boundsCompatible) {
					break;
				}
			}

			return boundsCompatible;
		}

		return false;
	}

	/**
	 * Determines if a target type is assignable from other a value type in all
	 * situations supported by reflection. This means primitive types and their
	 * corresponding object types are assignable to each other.
	 * 
	 * @param targetType
	 *            The variable, parameter, or return type.
	 * @param valueType
	 *            The type of the value that will be assigned.
	 * @return <code>true</code> when the value type is assignable to the target
	 *         type even if autoboxing or unboxing is required.
	 */
	public static boolean isGenerallyAssignableFrom(Class<?> targetType, Class<?> valueType) {
		if (targetType.isAssignableFrom(valueType)) {
			return true;
		}

		if (targetType.isPrimitive() || valueType.isPrimitive()) {
			return isPrimitiveAssignable(targetType, valueType);
		}

		return false;
	}

	private static boolean isPrimitiveAssignable(Class<?> returnType, Class<?> valueType) {
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			Class<?>[] primitivePair = PRIMITIVE_TYPES[i];

			if (returnType.equals(primitivePair[0])) {
				return valueType.equals(primitivePair[1]);
			}
			if (valueType.equals(primitivePair[0])) {
				return returnType.equals(primitivePair[1]);
			}
		}

		return false;
	}

}
