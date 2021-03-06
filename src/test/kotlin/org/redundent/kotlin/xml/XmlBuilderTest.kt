package org.redundent.kotlin.xml

import org.junit.Test
import org.xml.sax.SAXException
import java.io.ByteArrayInputStream
import kotlin.test.*

class XmlBuilderTest : XmlBuilderTestBase() {
	@Test
	fun basicTest() {
		val urlset = xml("urlset") {
			xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9"

			for (i in 0..2) {
				element("url") {
					element("loc") {
						-"http://google.com/$i"
					}
				}
			}
		}

		validate(urlset)
	}

	@Test
	fun customNamespaces() {
		val root = xml("root") {
			xmlns = "http://someurl.org"
			namespace("t", "http://t.org")

			element("t:element") {
				-"Test"
			}

			element("p") {
				xmlns = "http://t.co"
			}

			element("d:p") {
				namespace("d", "http://b.co")
			}
		}

		validate(root)
	}

	@Test
	fun notPrettyFormatting() {
		val root = xml("root", false) {
			element("element") {
				-"Hello"
			}
			element("otherElement") {
				-"Test"
			}
		}

		validate(root)
	}

	@Test
	fun multipleAttributes() {
		val root = xml("root") {
			element("test") {
				attribute("key", "value")
				attribute("otherAttr", "hello world")
			}
			element("attributes") {
				attributes(
						"test" to "value",
						"key" to "pair"
				)
			}
		}

		validate(root)
	}

	@Test
	fun emptyRoot() {
		validate(xml("root"))
	}

	@Test
	fun emptyElement() {
		validate(xml("root") {
			element("test")
		})
	}

	@Test
	fun cdata() {
		val root = xml("root") {
			cdata("Some & xml")
		}

		validate(root)
	}

	@Test
	fun updateAttribute() {
		val root = xml("root") {
			attribute("key", "value")
		}

		root["key"] = "otherValue"

		validate(root)
	}

	@Test
	fun xmlEncode() {
		val root = xml("root") {
			-"&<>"
		}

		validate(root)
	}

	@Test
	fun elementValue() {
		val root = xml("root") {
			element("name", "value")
		}

		validate(root)
	}

	@Test
	fun elementAsString() {
		val root = xml("root") {
			"name"("value")
		}

		validate(root)
	}

	@Test
	fun elementAsStringWithAttributes() {
		validate(xml("root") {
			"name"("attr" to "value", "attr2" to "other")
		})
	}

	@Test
	fun elementAsStringWithAttributesAndContent() {
		validate(xml("root") {
			"name"("attr" to "value") {
				-"Content"
			}
		})
	}

	@Test
	fun attributes() {
		val xmlns = "testing"
		val value = "value"

		val xml = xml("root") {
			this.xmlns = xmlns
			attribute("attr", value)
		}

		assertEquals(xmlns, xml.xmlns, "xmlns is correct")
		assertNotNull(xml["attr"], "attr is not null")
		assertEquals(value, xml["attr"]!!, "attr getting is correct")

		//Update the attr value
		xml["attr"] = "something else"
		assertEquals("something else", xml["attr"]!!, "attr value is updated")

		//Remove the
		xml.xmlns = null
		assertNull(xml.xmlns, "xmlns is removed")

		xml["attr"] = null
		assertFalse(xml.attributes.containsKey("attr"))
		assertNull(xml["attr"], "attr value is null")
	}

	@Test(expected = SAXException::class)
	fun invalidElementName() {
		val root = xml("invalid root")

		validateXml(root.toString())
	}

	@Test(expected = SAXException::class)
	fun invalidAttributeName() {
		val root = xml("root") {
			attribute("invalid name", "")
		}

		validateXml(root.toString())
	}

	@Test
	fun filterFunctions() {
		val xml = xml("root") {
			"child1" {
				"other"()
			}
			"child2"()
			"multiple"()
			"multiple"()
		}

		val child1 = xml.filter("child1")
		assertEquals(1, child1.size, "filter returned one element")

		val hasChild = xml.filter { it.name == "child1" && it.exists("other") }
		assertEquals(1, hasChild.size, "filter with exists returned one element")

		val multiple = xml.filter("multiple")
		assertEquals(2, multiple.size, "filter with multiple returned two element")

		assertNull(xml.firstOrNull("junk"), "firstOrNull returned null")
		assertNotNull(xml.firstOrNull("child1"), "firstOrNull returned element")

		assertFailsWith(NoSuchElementException::class) {
			xml.first("junk")
		}

		assertTrue("element exists") { xml.exists("child1") }
		assertFalse("element doesn't exists") { xml.exists("junk") }
	}

	@Test
	fun addNode() {
		val root = xml("root") {
			"a"()
		}

		root.addNode(node("b"))

		validate(root)
	}

	@Test
	fun removeNode() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.removeNode(root.first("b"))

		validate(root)
	}

	@Test
	fun addNodeAfter() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.addNodeAfter(node("c"), root.first("a"))

		validate(root)
	}

	@Test
	fun addNodeAfterLastChild() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.addNodeAfter(node("c"), root.first("b"))

		validate(root)
	}

	@Test(expected = IllegalArgumentException::class)
	fun addNodeAfterNonExistent() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.addNodeAfter(node("c"), node("d"))
	}

	@Test
	fun addNodeBefore() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.addNodeBefore(node("c"), root.first("b"))

		validate(root)
	}

	@Test(expected = IllegalArgumentException::class)
	fun addNodeBeforeNonExistent() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.addNodeBefore(node("c"), node("d"))
	}

	@Test
	fun replaceNode() {
		val root = xml("root") {
			"a"()
			"b"()
		}

		root.replaceNode(root.first("b"), node("c"))

		validate(root)
	}

	@Test
	fun parseAndVerify() {
		val xmlns = "http://blog.redundent.org"
		val value = "value"
		val input = ByteArrayInputStream("<root xmlns=\"$xmlns\"><child>$value</child></root>".toByteArray())

		val root = parse(input)

		assertEquals("root", root.name, "root element name is correct")
		assertEquals(xmlns, root.xmlns, "root xmlns is correct")

		val children = root.children
		assertEquals(1, children.size, "root has 1 child")
		assertTrue(children[0] is Node, "child is a node")

		val child = children.first() as Node
		assertTrue(child.children[0] is TextElement, "element is text")
		assertEquals(value, (child.children[0] as TextElement).text)
	}

	@Test
	fun parseCData() = parseTest()

	@Test
	fun parseCustomNamespaces() = parseTest()

	@Test
	fun praseMultipleAttributes() = parseTest()

	@Test
	fun parseBasicTest() = parseTest()

	@Test
	fun parseXmlEncode() = parseTest()

	private fun parseTest() {
		val input = getInputStream()
		val xml = parse(input)

		validateTest(xml)
	}
}
