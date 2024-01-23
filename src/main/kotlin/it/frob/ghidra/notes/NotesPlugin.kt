/*
 * Copyright (C) 2023 Alessandro Gatti - frob.it
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package it.frob.ghidra.notes

import docking.WindowPosition
import generic.theme.ApplicationThemeManager
import generic.theme.GThemeDefaults
import generic.theme.ThemeEvent
import generic.theme.ThemeListener
import ghidra.app.plugin.PluginCategoryNames
import ghidra.app.plugin.ProgramPlugin
import ghidra.framework.main.UtilityPluginPackage
import ghidra.framework.plugintool.ComponentProviderAdapter
import ghidra.framework.plugintool.PluginInfo
import ghidra.framework.plugintool.PluginTool
import ghidra.framework.plugintool.util.PluginStatus
import ghidra.program.model.listing.Program
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import org.fife.ui.rtextarea.RTextArea
import org.fife.ui.rtextarea.RTextScrollPane

@PluginInfo(
    status = PluginStatus.UNSTABLE,
    packageName = UtilityPluginPackage.NAME,
    category = PluginCategoryNames.USER_ANNOTATION,
    shortDescription = "Attach text notes to a disassembled program.",
    description =
        "Keep track of notes taken during reverse engineering, " +
            "storing them along with the program being disassembled."
)
class NotesPlugin(tool: PluginTool) : ProgramPlugin(tool) {
    private var provider: NotesProvider = NotesProvider(this, getName())

    override fun programActivated(program: Program) {
        super.programActivated(program)

        val userData = program.programUserData
        provider.currentText = userData.getStringProperty(STORAGE_PROPERTY, "")
    }

    override fun programDeactivated(program: Program) {
        super.programDeactivated(program)

        val userData = program.programUserData
        val transaction = userData.openTransaction()
        try {
            userData.setStringProperty(STORAGE_PROPERTY, provider.currentText)
            transaction.commit()
        } catch (_: Exception) {
            transaction.abort()
        } finally {
            transaction.close()
        }
    }

    override fun dispose() {
        provider.dispose()
        super.dispose()
    }

    companion object {
        private const val STORAGE_PROPERTY = "it.frob.ghidra.notes.storageProperty"
    }
}

internal class NotesProvider(plugin: NotesPlugin, owner: String) :
    ComponentProviderAdapter(plugin.tool, "Notes Window", owner), ThemeListener {

    private var panel: JPanel = JPanel(BorderLayout())
    private val textArea: RTextArea = RTextArea()
    private val textScrollPane: RTextScrollPane = RTextScrollPane(textArea, false)

    var currentText: String
        get() = textArea.text
        set(text) {
            textArea.text = text
        }

    init {
        buildPanel()

        defaultWindowPosition = WindowPosition.RIGHT
        intraGroupPosition = WindowPosition.STACK
        title = "Notes"
        windowGroup = "NOTES"
    }

    fun dispose() {
        tool.removeComponentProvider(this)
    }

    private fun buildPanel() {
        textArea.apply {
            tabSize = 2
            caretPosition = 0
            highlightCurrentLine = false
            lineWrap = true
            wrapStyleWord = true
        }

        updateTheme()
        panel.add(textScrollPane)
        isVisible = true

        ApplicationThemeManager.getInstance().addThemeListener(this)
    }

    override fun closeComponent() {
        ApplicationThemeManager.getInstance().removeThemeListener(this)
        super.closeComponent()
    }

    private fun updateTheme(updateColours: Boolean = true, updateFont: Boolean = true) {
        val theme = ApplicationThemeManager.getInstance().activeTheme

        textArea.apply {
            if (updateColours) {
                background = GThemeDefaults.Colors.BACKGROUND
                foreground = GThemeDefaults.Colors.FOREGROUND
            }

            if (updateFont) {
                font = theme.getResolvedFont("font.listing.base")
            }
        }
    }

    override fun getComponent(): JComponent = panel

    override fun themeChanged(event: ThemeEvent) {
        updateTheme(event.hasAnyColorChanged(), event.hasAnyFontChanged())
    }
}
