package lightware.richtext;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class LabelSegment extends AbstractSegment
{
	public LabelSegment( Object data )
	{
		super( data );
	}

	@Override
	public Node createNode( String style )
	{
    	Label  item = new Label( data.toString() );
    	item.setStyle( "-fx-border-width: 1; -fx-border-style: solid; -fx-border-color: lightgrey; -fx-padding: 0 2 0 2; -fx-font-weight: normal; -fx-font-size: 10px;" );
		if ( style != null &&  ! style.isEmpty() ) item.getStyleClass().add( style );
		item.setUserData( getData() );
    	return item;
	}
}
