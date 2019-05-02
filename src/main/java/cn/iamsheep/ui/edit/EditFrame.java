package cn.iamsheep.ui.edit;

import cn.iamsheep.api.Factory;
import cn.iamsheep.util.FileHandler;
import com.jfoenix.controls.JFXTextArea;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;

/**
 * @author Magical Sheep
 */
@ViewController(value = "/page/EditFrame.fxml", title = "Edit Frame")
public class EditFrame {

    private File file;
    @FXML
    private JFXTextArea jfxTextArea;
    @FXML
    private StackPane saveBurger;
    @FXML
    private ImageView save;

    public EditFrame() {
        file = EditApp.file;
    }

    @PostConstruct
    public void init() throws IOException {
        Image saveImage = new Image(String.valueOf(getClass().getClassLoader().getResource("icon/save.png")));
        save.setImage(saveImage);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String content;
        while ((content = bufferedReader.readLine()) != null) {
            jfxTextArea.setText(jfxTextArea.getText() + content + "\n");
        }
        bufferedReader.close();

        saveBurger.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false));
                bufferedWriter.write(jfxTextArea.getText());
                bufferedWriter.close();
                Factory.UI.getSideMenu().sync();
                rename(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void rename(int i) throws IOException {
        List<File> list = FileHandler.loadRuleProperties();
        String fileName;
        if(i== 0) {
            fileName = FileHandler.getRulePropertiesName(file.getPath());
        } else {
            fileName = FileHandler.getRulePropertiesName(file.getPath()) + i;
        }
        if (!file.getName().equals(fileName)) {
            for (File f : list) {
                if (f.getName().equals(fileName)) {
                    rename(i++);
                }
            }
            File newName = new File(FileHandler.root + "\\Properties\\" + fileName + ".properties");
            file.renameTo(newName);
        }
    }
}
