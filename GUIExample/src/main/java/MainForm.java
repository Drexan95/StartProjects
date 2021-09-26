import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainForm {
    private JPanel mainPanel;
    private JButton collapse;
    private JPanel textPanel;
    private JTextField surname;
    private JTextField dadsName;
    private JTextField name;
    private boolean collapsed = false;

    public MainForm(){
        name.setText("Enter your name");
        surname.setText("Enter your surname");
        dadsName.setText("What's your father's name");

        name.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                name.setText("");
            }
        });
        surname.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                surname.setText("");
            }
        });
        dadsName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dadsName.setText("");
            }
        });
        collapse.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!collapsed) {
                    if (checkForm()) {
                        collapsed = true;
                        JOptionPane.showMessageDialog(mainPanel,
                                name.getText()+"\n"+surname.getText()+"\n"+dadsName.getText(),"Ф.И.О",JOptionPane.PLAIN_MESSAGE);
                        collapse.setText("Expand");
                    }else {
                        JOptionPane.showMessageDialog(mainPanel,"Неверное имя или фамилия,провертье правильностей введенных данных");
                    }
                }
                else {
                    if(checkForm()){
                        collapsed = false;
                        JOptionPane.showMessageDialog(mainPanel,
                                name.getText()+"\n"+surname.getText()+"\n"+dadsName.getText(),"Ф.И.О",JOptionPane.PLAIN_MESSAGE);
                        collapse.setText("Expand");
                        collapse.setText("Collapse");
                    }else {
                        JOptionPane.showMessageDialog(mainPanel,"Неверное имя или фамилия,провертье правильностей введенных данных");
                    }
                }
            }
        });
    }

    public JPanel getMainPanel(){
        return mainPanel;
    }

    public boolean checkForm(){
        return !name.getText().equals("") && !name.getText().matches("\\s+") && (!surname.getText().equals("") && !surname.getText().matches("\\s+"));
    }

}
