package com.guet.flexbox.handshake.ui;

import com.guet.flexbox.handshake.configuration.CompileRunConfiguration;
import com.intellij.openapi.options.SettingsEditor;

import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class CompileSettingFormBase extends SettingsEditor<CompileRunConfiguration>
{
    public JPanel wrapPanel;
    public JTextField template;
    public JTextField output;

}
