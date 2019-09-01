package lightware.richtext;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class IndentSegment extends AbstractSegment
{
    /**
     * Displays a Label containing indentStr<br>
     * @param indentStr is tabs or spaces depending on the desired indent width 
     */
    public IndentSegment( Object indentStr )
    {
        super( indentStr );
    }

    @Override
    public Node createNode( String style )
    {
        Label  item = new Label( getData().toString() );
        if ( style != null && ! style.isEmpty() )  item.getStyleClass().add( style );
        return item;
    }
    
    @Override
    public String getText() { return ""; }
}
