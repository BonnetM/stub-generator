package m.bonnet.generator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StubGeneratorTest {

    @Test(expected = IllegalStateException::class)
    fun `create when no primary constructor should throw exception`() {
        // When
        StubGenerator.create(NoPrimaryConstructor::class)

        // Then
        // Should throw exception
    }

    @Test
    fun `create when only primitive types should assign default value to each parameters`() {
        // When
        val createdStub = StubGenerator.create(OnlyPrimitive::class)

        // Then
        val expected = OnlyPrimitive(0.0, 0F, 0L, 0, 0.toShort(), 0.toByte(), false, 'a', "String")
        assertThat(createdStub).isEqualTo(expected)
    }

    @Test
    fun `create when class has user defined class as parameters should create it`() {
        // When
        val createdStub = StubGenerator.create(WithCustomClass::class)

        // Then
        val expected = WithCustomClass(CustomClass(0))
        assertThat(createdStub).isEqualTo(expected)
    }

    @Test
    fun `create when class has two user defined class as parameters should work fine`() {
        // When
        val createdStub = StubGenerator.create(WithMultipleCustomClasses::class)

        // Then
        val expected = WithMultipleCustomClasses(CustomClass(0), CustomClass(0))
        assertThat(createdStub).isEqualTo(expected)
    }

    @Test(expected = IllegalStateException::class)
    fun `create when class has a circular dependency should throw exception`() {
        // When
        val createdStub = StubGenerator.create(Circular::class)

        // Then
        // Should throw exception
    }

    @Test
    fun `create when class has common collections should return default values`() {
        // When
        val createdStub = StubGenerator.create(OnlyCommonCollections::class)

        // Then
        val expected = OnlyCommonCollections(
                emptyList(),
                mutableListOf(),
                emptySet(),
                mutableSetOf(),
                emptyMap(),
                mutableMapOf()
        )
        assertThat(createdStub).isEqualTo(expected)
    }

    @Test
    fun `create when argument is marked as nullable should set it as null`() {
        // When
        val createdStub = StubGenerator.create(WithNullable::class)

        // Then
        val expected = WithNullable(null)
        assertThat(createdStub).isEqualTo(expected)
    }

    @Test(expected = IllegalStateException::class)
    fun `create when class has an interface as parameter should throw exception`() {
        // When
        StubGenerator.create(WithInterface::class)

        // Then
        // Should throw exception
    }

    @Test
    fun `createStub should work just as create`() {
        // When
        val createdStub = createStub<OnlyPrimitive>()

        // Then
        val expected = OnlyPrimitive(0.0, 0F, 0L, 0, 0.toShort(), 0.toByte(), false, 'a', "String")
        assertThat(createdStub).isEqualTo(expected)
    }

}

interface NoPrimaryConstructor

data class OnlyPrimitive(
        val double: Double,
        val float: Float,
        val long: Long,
        val int: Int,
        val short: Short,
        val byte: Byte,
        val boolean: Boolean,
        val char: Char,
        val string: String
)

data class WithCustomClass(
        val customClass: CustomClass
)

data class WithMultipleCustomClasses(
        val customClass: CustomClass,
        val secondCustomClass: CustomClass
)

data class CustomClass(val i: Int)

data class Circular(val next: Circular)

data class OnlyCommonCollections(
        val intList: List<Int>,
        val mutIntList: MutableList<Int>,
        val stringSet: Set<String>,
        val mutStringSet: MutableSet<String>,
        val doubleMap: Map<Double, Double>,
        val mutDoubleMap: MutableMap<Double, Double>
)

data class WithNullable(
        val nullable: Int?
)

data class WithInterface(
        val noPrimaryConstructor: NoPrimaryConstructor
)