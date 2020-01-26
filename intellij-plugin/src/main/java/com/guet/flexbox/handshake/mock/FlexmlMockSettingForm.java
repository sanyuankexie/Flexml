package com.guet.flexbox.handshake.mock;

import javax.swing.*;

public class FlexmlMockSettingForm {
    public JPanel wrapPanel;
    public JTextField port;
    public JTextField template;
    public JTextField dataSource;

    public FlexmlMockSettingForm(){
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
