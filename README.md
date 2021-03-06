[![Build Status](https://travis-ci.org/redundent/kotlin-xml-builder.svg?branch=master)](https://travis-ci.org/redundent/kotlin-xml-builder)
[![Download](https://api.bintray.com/packages/redundent/maven/kotlin-xml-builder/images/download.svg)](https://bintray.com/redundent/maven/kotlin-xml-builder/_latestVersion)

Kotlin XML Builder
=

This library can be used to build xml documents from Kotlin code.
It is based on the HTML builder described in the [Kotlin docs](https://kotlinlang.org/docs/reference/type-safe-builders.html)

It is designed to be lightweight and fast. There isn't any validation except to 
escape text to not violate xml standards.

License
-
Apache 2.0

Usage
=
To use in Gradle, simply add the jcenter repository and then add the following dependency.
```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'org.redundent:kotlin-xml-builder:[VERSION]'
}
```

Example
=
```kotlin
val people = xml("people") {
    xmlns = "http://example.com/people"
    "person" {
        attribute("id", 1)
        "firstName" {
            -"John"
        }
        "lastName" {
            -"Doe"
        }
        "phone" {
            -"555-555-5555"
        }
    }
}

val asString = people.toString()
```
produces
```xml
<people xmlns="http://github.com/people">
    <person id="1">
        <firstName>John</firstName>
        <lastName>Doe</lastName>
        <phone>555-555-5555</phone>
    </person>
</people>
```

```kotlin
class Person(val id: Long, val firstName: String, val lastName: String, val phone: String)

val listOfPeople = listOf(
    Person(1, "John", "Doe", "555-555-5555"),
    Person(2, "Jane", "Doe", "555-555-6666")
)

val people = xml("people") {
    xmlns = "http://example.com/people"
    for (person in listOfPeople) {
        "person" {
            attribute("id", person.id)
            "firstName" {
                -person.firstName
            }
            "lastName" {
                -person.lastName
            }
            "phone" {
                -person.phone
            }
        }
    }    
}

val asString = people.toString()
```
produces
```xml
<people xmlns="http://github.com/people">
    <person id="1">
        <firstName>John</firstName>
        <lastName>Doe</lastName>
        <phone>555-555-5555</phone>
    </person>
    <person id="2">
        <firstName>Jane</firstName>
        <lastName>Doe</lastName>
        <phone>555-555-6666</phone>
    </person>
</people>
```

Release Notes
=
Version 1.3
-
* Added ability to parse an xml document into a builder object using the new `parse` method.
* Upgrading Gradle and Kotlin versions.

Version 1.2
-
* Added a `sitemap` method to allow for easy generation of sitemaps (which is what this project was created for in the first place).
* Added `String.invoke` for elements allowing you to specify elements by just their name (see docs above)
* Added some searching methods to search child nodes. `filter`, `first`, `firstOrNull`, and `exists`.
* Added some mutation methods to allow you to add/remove/replace nodes in an element. 

Version 1.1
-
* Added convenience method for elements with just a name and value. `element("name", "value")`
* Removed `ns` method. Please use `namespace(...)` instead.
* Upgraded Gradle version
