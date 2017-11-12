package lightware.richtext;
import java.util.Optional;

import org.fxmisc.richtext.TextExt;

import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextSegment extends AbstractSegment
{
	private final String text;

	public TextSegment( Object text )
	{
		super( text );
		this.text = text.toString();
	}

	@Override
	public Node createNode( String style )
	{
		Text  textNode = new TextExt( text );
		if ( style != null && ! style.isEmpty() ) textNode.getStyleClass().add( style );
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
			new TextSegment( text.substring( start, end ) )
		);
	}

	@Override
	public Optional<AbstractSegment> join( AbstractSegment nextSeg )
	{
		if ( nextSeg instanceof TextSegment )
		{
			return Optional.of
			(
				new TextSegment( text + nextSeg.getText() )
			);
		}
		return Optional.empty();
	}

}
