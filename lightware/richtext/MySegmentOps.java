package lightware.richtext;
import java.util.Optional;
import org.fxmisc.richtext.model.TextOps;

public class MySegmentOps implements TextOps<AbstractSegment,String>
{
	private final AbstractSegment EMPTY = new TextSegment("");

	@Override
	public AbstractSegment create( String text )
	{
		if ( text == null || text.isEmpty() )  return EMPTY;
		return new TextSegment( text );
	}

	@Override
	public AbstractSegment createEmptySeg()
	{
		return EMPTY;
	}

	@Override
	public char charAt( AbstractSegment seg, int index )
	{
		return seg.charAt( index );
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
	public AbstractSegment subSequence( AbstractSegment seg, int start )
	{
		return subSequence( seg, start, seg.length() );
	}

	@Override
	public AbstractSegment subSequence( AbstractSegment seg, int start, int end )
	{
		if ( start == seg.length() || end == 0 ) return EMPTY;
		Optional<AbstractSegment>  opt = seg.subSequence( start, end );
		return opt.orElse( EMPTY );
	}

	@Override
	public Optional<AbstractSegment> joinSeg( AbstractSegment currentSeg, AbstractSegment nextSeg )
	{
		return currentSeg.join( nextSeg );
	}

}
