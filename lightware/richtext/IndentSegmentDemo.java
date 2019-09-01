package lightware.richtext;
import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This demo shows how to use a custom object with RichTextFX editor to indent the first line of a paragraph.
 */
public class IndentSegmentDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        StyledSegmentTextArea textArea = new StyledSegmentTextArea();
        textArea.setIndentOn( true );

        textArea.replaceText(0, 0, "This demo shows how to use a custom object with RichTextFX editor to indent the first line of a paragraph.\n\n");
        textArea.appendText("The first line of this second paragraph should be indented. It is using a Label containing a Tab to do this.");
        textArea.appendText("\n\nPressing Enter twice to start a new paragraph will result in the first line to be indented.\n\n");
        textArea.appendText("This is all achieved in StyledSegmentTextArea which extends GenericStyledArea using AbstractSegments: see IndentSegment and TextSegment.");

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 350, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Indent Demo");
        primaryStage.show();
    }
}