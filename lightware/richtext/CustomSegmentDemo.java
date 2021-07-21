package lightware.richtext;

import org.fxmisc.flowless.VirtualizedScrollPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This demo creates a sample document with some text and a custom Label node.
 */
public class CustomSegmentDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        StyledSegmentTextArea textArea = new StyledSegmentTextArea();

        textArea.replaceText(0, 0, "This example shows how to add custom nodes, for example Labels: ");

        textArea.append( new LabelSegment("[Result Field]") );

        textArea.appendText(". Now, select some text from above (including one or more of the custom objects)\n\nusing CTRL-C, and paste it somewhere in the document with CTRL-V.");

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Custom Object demo");
        primaryStage.show();

//        DelayedFx.runLater( 500, () -> System.err.println( textArea.getText() ) );
/*
        Platform.runLater( () ->
            textArea.getParagraphs().stream()
                .peek( p -> System.out.println( "---" ) )
                .flatMap( p -> p.getSegments().stream() )
                .forEach( s -> System.out.println( s.getClass().getName() +"\t"+ s.getData() ) ) );
*/
/*
        DelayedFx.runLater( 500, () -> 
        {
            String text = textArea.getText();
            System.err.println( text );
            int pastePos = text.indexOf( "paste" );
            System.out.println( "\nFound 'paste' in data text at: "+ pastePos );
            System.out.println( "Using raw DataTextPos to getText: "+ textArea.getText( pastePos, pastePos+5) );
            pastePos = textArea.dataTextToRichTextPos( pastePos );
            System.out.println( "Using converted RichTextPos: "+ textArea.getText( pastePos, pastePos+5) );
        } );
 */
    }
}