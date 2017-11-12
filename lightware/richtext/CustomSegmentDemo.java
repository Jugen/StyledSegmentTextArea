package lightware.richtext;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This demo shows how to register custom objects with the RichTextFX editor.
 * It creates a sample document with some text and a custom node.
 */
public class CustomSegmentDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
//    	Function<AbstractSegment,Node>  nodeFactory = seg -> seg.createNode();

    	StyledSegmentTextArea textArea = new StyledSegmentTextArea();

        textArea.replaceText(0, 0, "This example shows how to add custom nodes, for example Labels ");

        textArea.append( new LabelSegment("Result Field") );

        textArea.appendText(" Now, select some text from above (including one or more of the custom objects) using CTRL-C, and paste it somewhere in the document with CTRL-V.");

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Custom Object demo");
        primaryStage.show();

        Platform.runLater( () ->
        	textArea.getDocument().getParagraphs().stream()
        		.peek( p -> System.out.println( "---" ) )
        		.flatMap( p -> p.getSegments().stream() )
        		.forEach( s -> System.out.println( s.getClass().getName() +"\t"+ s.getData() ) ) );

    }
}
