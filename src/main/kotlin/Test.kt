interface Tag : Containable {
    val name: String
    var attributes: List<Attribute>
    var children: List<Containable>
}

interface Attribute {
    val name: String
    val value: String
}

interface Text : Containable

interface Containable

@HtmlDSL
abstract class AbstractTag(
    override val name: String,
    override var attributes: List<Attribute> = emptyList(),
    override var children: List<Containable> = emptyList(),
) : Tag {
    fun Tag.addChild(child: Containable) {
        this.children += child
    }

    fun Tag.addAttribute(attribute: Attribute) {
        this.attributes += attribute
    }
}

@HtmlDSL
abstract class AbstractTagWithText(name: String) : AbstractTag(name) {
    operator fun String.unaryMinus(): Unit = TODO()
}

@HtmlDSL
abstract class BodyTag(name: String) : AbstractTag(name)

data class AttributeImpl(val attribute: Pair<String, String>) : Attribute {
    override val name: String = attribute.first
    override val value: String = attribute.second
}

class HTML : AbstractTag("html")
class Head : AbstractTag("head")
class Body : AbstractTag("body")
class Title : AbstractTagWithText("title")
class Paragraph : BodyTag("a")
class Anchor(val href: String) : AbstractTagWithText("a")

fun html(block: HTML.() -> Unit) = HTML().apply(block)
fun HTML.head(block: Head.() -> Unit) = Head().apply(block).also { this.addChild(it) }
fun HTML.body(block: Body.() -> Unit) = Body().apply(block).also { this.addChild(it) }
fun Head.title(block: Title.() -> Unit) = Title().apply(block).also { this.addChild(it) }
fun Body.p(vararg attributes: Pair<String, String>, block: Paragraph.() -> Unit) = Paragraph().apply {
    block()
    attributes.forEach { this.addAttribute(AttributeImpl(it)) }
}.also { this.addChild(it) }
fun BodyTag.a(href: String, block: Anchor.() -> Unit) = Anchor(href).apply(block).also {
    this.addChild(it)
}

@DslMarker
annotation class HtmlDSL

fun main() {
    html {
        head {
            title { -"A link to the unibo webpage" }
        }
        body {
            p("class" to "myCustomCssClass") {
                a(href = "http://www.unibo.it") { -"Unibo Website" }
            }
        }
    } // .render()
}
