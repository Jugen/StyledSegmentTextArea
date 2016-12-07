package lightware.richtext;
import java.util.Optional;
import org.fxmisc.richtext.model.TextOps;

public class MySegmentOps implements TextOps<AbstractSegment,String>
{
	private final AbstractSegment EMPTY = new TextSegment("","");

	@Override
	public AbstractSegment create( String text, String style )
	{
		if ( text == null || text.isEmpty() )  return EMPTY;
		return new TextSegment( text, style );
	}

	@Override
	public AbstractSegment createEmpty()
	{
		return EMPTY;
	}

	@Override
	public char charAt( AbstractSegment seg, int index )
	{
		return seg.charAt( index );
	}

	@Override
	public String getStyle( AbstractSegment seg )
	{
		return seg.getStyle();
	}

	@Override
	public AbstractSegment setStyle( AbstractSegment seg, String style )
	{
		seg.setStyle( style );	// TODO Undo / Redo might require that this returns a new object ?
		return seg;
	}

	@Override
	public String getText( AbstractSegment seg )
	{
		return seg.getText();
	}

	@Override
	public int length( AbstractSegment seg )
	{
		return seg.length();
	}

	@Override
	public Optional<AbstractSegment> subSequence( AbstractSegment seg, int start, int end )
	{
		if ( start == seg.length() || end == 0 ) return Optional.of( EMPTY );
		return seg.subSequence( start, end );
	}

	@Override
	public Optional<AbstractSegment> subSequence( AbstractSegment seg, int start )
	{
		if ( start == seg.length() ) return Optional.of( EMPTY );
		return seg.subSequence( start, seg.length() );
	}

	@Override
	public Optional<AbstractSegment> join( AbstractSegment currentSeg, AbstractSegment nextSeg )
	{
		return currentSeg.join( nextSeg );
	}

}
