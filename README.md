## Introduction

A library to build data classes with default values. It can help you use your data classes in tests without mocking them.

## How to use

```
val hugePojo = createStub<HugePojo>()
```

This creates a `HugePojo` with its values set to default values.
You can then set the properties you want, using the data classes' `copy` function :

```
val myCustomPojo = createStub<HugePojo>().copy(usefulBoolean = true)
```

## Get it

[![](https://jitpack.io/v/BonnetM/stub-generator.svg)](https://jitpack.io/#BonnetM/stub-generator)

Add jitpack dependency :

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the library dependency :

```
dependencies {
    testImplementation 'com.github.BonnetM:stub-generator:v1.1'
}
```
## Example

The data classes :

```
data class HugePojo(
    val uselessIntA: Int,
    val uselessIntB: Int,
    val uselessIntC: Int,
    val uselessIntD: Int = 175,
    val uselessIntE: Int?,
    val uselessIntF: Int?,
    val list: List<Int>,
    val set: Set<Int>,
    val map: Map<Int, List<List<OtherPojo>>>,
    val uselessStringA: String,
    val uselessStringB: String,
    val uselessStringC: String,
    val uselessStringD: String,
    val uselessBoolean: Boolean,
    val usefulBoolean: Boolean, // Only variable that will be useful in my tests
    val otherPojo: OtherPojo
)

data class OtherPojo(val aString: String, val otherotherPojo: OtherOtherPojo)

data class OtherOtherPojo(val i : List<Int>)
```

The class to be tested

```
class ClassUnderTest(private val ifTrue: IfTrue, private val ifFalse: IfFalse) {
    fun compute(hugePojo: HugePojo) {
        if (hugePojo.usefulBoolean) {
            ifTrue.compute(hugePojo)
        } else {
            ifFalse.compute(hugePojo)
        }
    }
}
```
Its unit test, in vanilla kotlin:

```
@Test
fun `vanilla kotlin`() {
    val hugePojo = HugePojo(
        1,
        2,
        3,
        4,
        5,
        6,
        emptyList(),
        emptySet(),
        emptyMap(),
        "",
        "",
        "",
        "",
        false,
        usefulBoolean = true,
        OtherPojo("",
            OtherOtherPojo(emptyList()))
    )

    classUnderTest.compute(hugePojo)

    verify(ifTrue).compute(hugePojo)
    verifyZeroInteractions(ifFalse)
}
```

And with the library :

```
@Test
fun `with magic`() {
    val hugePojo = createStub<HugePojo>().copy(usefulBoolean = false)

    classUnderTest.compute(hugePojo)

    verify(ifFalse).compute(hugePojo)
    verifyZeroInteractions(ifTrue)
}
```
## Known limitations

For now, arrays are not supported.

