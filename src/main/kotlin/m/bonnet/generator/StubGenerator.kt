package m.bonnet.generator

import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

/**
 * Creates an instance of type [T] with default values
 *
 * @param T the type to be created
 * @return an instance of type [T] which is constructed with default values
 * @throws IllegalStateException if an instance cannot be constructed
 */

inline fun <reified T : Any> createStub(): T {
    return StubGenerator.create(T::class)
}

object StubGenerator {
    /**
     * Creates an instance of [KC] with default values
     *
     * @param T the type to be created
     * @param KC the KClass type associated with [T]
     * @param kClass the KClass of type [T] to be created
     * @return an instance of [KC] which is constructed with default values
     * @throws IllegalStateException if an instance cannot be constructed
     */
    fun <T : Any, KC : KClass<T>> create(kClass: KC): T {
        kClass.primaryConstructor?.let { constructor ->
            val arguments = constructor.parameters.map { it to getParameterDefaultValue(it, listOf(kClass)) }
            return constructor.callBy(arguments.toMap())
        } ?: throw IllegalStateException("Cannot create a stub without a primary constructor for class $kClass")
    }

    private fun <KCA : KClass<*>> createAny(kClassAny: KCA, creatingValueOf: List<KClass<*>>): Any {
        if (creatingValueOf.contains(kClassAny)) {
            throw IllegalStateException("Type circular depedency, cannot create value for $kClassAny")
        }
        val constructor = kClassAny.constructors.firstOrNull()
                ?: throw IllegalStateException("Cannot create a stub without a constructor for class $kClassAny")
        val arguments = constructor.parameters.map { it to getParameterDefaultValue(it, creatingValueOf.plus(kClassAny)) }
        return constructor.callBy(arguments.toMap())
    }

    private fun getParameterDefaultValue(kParameter: KParameter, creatingValueOf: List<KClass<*>>): Any? {
        val kType = kParameter.type
        return getTypeDefaultValue(kType, creatingValueOf)
    }

    private fun getTypeDefaultValue(kType: KType, creatingValueOf: List<KClass<*>>): Any? {
        return when {
            kType.isMarkedNullable -> null
            kType.classifier!!.starProjectedType.isSupertypeOf(List::class.createType(listOf(anyProjection()))) -> emptyList<Any>()
            kType.classifier!!.starProjectedType.isSupertypeOf(Map::class.createType(listOf(anyProjection(), anyProjection()))) -> emptyMap<Any, Any>()
            kType.classifier!!.starProjectedType.isSupertypeOf(Set::class.createType(listOf(anyProjection()))) -> emptySet<Any>()
            else -> getBasicTypeDefaultValue(kType, creatingValueOf)
        }
    }

    private fun anyProjection() = KTypeProjection(KVariance.INVARIANT, Any::class.createType())

    private fun getBasicTypeDefaultValue(kType: KType, creatingValueOf: List<KClass<*>>): Any? {
        return when (kType) {
            Double::class.createType() -> 0.0
            Float::class.createType() -> 0F
            Long::class.createType() -> 0L
            Int::class.createType() -> 0
            Short::class.createType() -> 0.toShort()
            Byte::class.createType() -> 0.toByte()
            Boolean::class.createType() -> false
            Char::class.createType() -> 'a'
            String::class.createType() -> "String"
            else -> createAny(kType.jvmErasure, creatingValueOf)
        }
    }
}