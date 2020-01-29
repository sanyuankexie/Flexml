package com.guet.flexbox.intellij.ui;

import com.guet.flexbox.intellij.configuration.CompileRunConfiguration;
import com.intellij.openapi.options.SettingsEditor;

import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class CompileSettingFormBase extends SettingsEditor<CompileRunConfiguration>
{
    public JPanel wrapPanel;
    public JTextField template;
    public JTextField output;

}
