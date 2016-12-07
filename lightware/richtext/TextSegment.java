package lightware.richtext;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextSegment extends AbstractSegment
{
	private final String text;

	public TextSegment( Object text, String style )
	{
		super( text, style );
		this.text = text.toString();
	}

	@Override
	public Node createNode()
	{
		Text  textNode = new Text( text );
		if ( style != NO_STYLE ) textNode.getStyleClass().add( style );
		return textNode;
	}

	@Override
	public char charAt( int index )
	{
        return text.charAt( index );
	}

	@Override
	public String getText() { return text; }

	@Override
	public int length() { return text.length(); }

	@Override
	public Optional<AbstractSegment> subSequence( int start, int end )
	{
		if ( start == 0 && end == length() )  return Optional.of( this );
		return Optional.of
		(
			new TextSegment( text.substring( start, end ), style )
		);
	}

	@Override
	public Optional<AbstractSegment> join( AbstractSegment nextSeg )
	{
		if ( nextSeg instanceof TextSegment )
		{
			if ( getStyle().equals( nextSeg.getStyle() ) )
			{
				return Optional.of
				(
					new TextSegment( text + nextSeg.getText(), style )
				);
			}
		}
		return Optional.empty();
	}

}
