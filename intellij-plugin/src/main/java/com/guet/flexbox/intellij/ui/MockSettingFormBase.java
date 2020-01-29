package com.guet.flexbox.intellij.ui;

import com.guet.flexbox.intellij.configuration.MockRunConfiguration;
import com.intellij.openapi.options.SettingsEditor;

import javax.swing.*;

public abstract class MockSettingFormBase extends SettingsEditor<MockRunConfiguration>
{
    public JPanel wrapPanel;
    public JTextField port;
    public JTextField packageJson;

    public MockSettingFormBase(){
        port.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                try {
                    Integer.parseInt(port.getText());
                    return true;
                }catch (Exception e){
                    return false;
                }
            }
        });
    }
}
