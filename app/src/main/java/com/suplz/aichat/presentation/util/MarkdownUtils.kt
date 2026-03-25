package com.suplz.aichat.presentation.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.BulletList
import org.commonmark.node.Code
import org.commonmark.node.Emphasis
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HardLineBreak
import org.commonmark.node.Heading
import org.commonmark.node.ListItem
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.node.SoftLineBreak
import org.commonmark.node.StrongEmphasis
import org.commonmark.node.Text
import org.commonmark.parser.Parser

fun String.parseMarkdown(): AnnotatedString {

    val parser = Parser.builder().build()
    val document = parser.parse(this)

    return buildAnnotatedString {
        document.accept(object : AbstractVisitor() {
            // Обычный текст
            override fun visit(text: Text) {
                append(text.literal)
            }

            // Жирный текст (**text**)
            override fun visit(strongEmphasis: StrongEmphasis) {
                val start = length
                visitChildren(strongEmphasis)
                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, length)
            }

            // Курсив (*text*)
            override fun visit(emphasis: Emphasis) {
                val start = length
                visitChildren(emphasis)
                addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, length)
            }

            // Встроенный код (`code`)
            override fun visit(code: Code) {
                val start = length
                append(code.literal)
                addStyle(
                    style = SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color.Gray.copy(alpha = 0.2f)
                    ),
                    start = start,
                    end = length
                )
            }

            // Блоки кода с отступами (```code```)
            override fun visit(fencedCodeBlock: FencedCodeBlock) {
                val start = length
                append(fencedCodeBlock.literal)
                addStyle(
                    style = SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = Color.Gray.copy(alpha = 0.2f)
                    ),
                    start = start,
                    end = length
                )
                if (fencedCodeBlock.next != null) append("\n\n")
            }

            // Абзацы
            override fun visit(paragraph: Paragraph) {
                visitChildren(paragraph)
                if (paragraph.next != null) {
                    append("\n\n")
                }
            }

            // Маркированные списки
            override fun visit(bulletList: BulletList) {
                visitChildren(bulletList)
                if (bulletList.next != null) append("\n")
            }

            // Нумерованные списки
            override fun visit(orderedList: OrderedList) {
                visitChildren(orderedList)
                if (orderedList.next != null) append("\n")
            }

            // Элементы списков
            override fun visit(listItem: ListItem) {
                if (listItem.parent is BulletList) {
                    append("• ")
                } else if (listItem.parent is OrderedList) {
                    append("- ")
                }
                visitChildren(listItem)
                if (listItem.next != null) append("\n")
            }

            override fun visit(softLineBreak: SoftLineBreak) {
                append(" ")
            }

            override fun visit(hardLineBreak: HardLineBreak) {
                append("\n")
            }

            // Заголовки (# Заголовок)
            override fun visit(heading: Heading) {
                val start = length
                visitChildren(heading)
                addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, length)
                if (heading.next != null) append("\n\n")
            }
        })
    }
}