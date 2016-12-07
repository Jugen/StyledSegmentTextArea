package lightware.richtext;
import java.util.Optional;
import javafx.scene.Node;

/**
 * Most of these methods are derived from the TextOps interface implemented by MySegmentedOps,
 * which delegates its method calls (here) to instances of AbstractSegment subclasses.
 *
 * Only createNode() and getData() are not directly from RichTextFX. The former is used
 * by a node factory function which we have to pass to RichTextFX and the latter is
 * required to extract any data out of a custom segment.
 *
 * @author Jurgen
 */
public abstract class AbstractSegment
{
	protected final Object  data;
	protected String  style = NO_STYLE;
	protected static final String NO_STYLE = "";

	private AbstractSegment() { data = null; }

	public AbstractSegment( Object data, String style )
	{
		setStyle( style );
		this.data = data;
	}

	public void setStyle( String style )
	{
		if ( style != null && ! style.isEmpty() )
		{
			this.style = style;
		}
	}

	public String getStyle()
	{
		return style;
	}

	public String getText() { return "\ufffc"; }
	public Object getData() { return data; }
	public int length() { return 1; }
	public char charAt( int index )
	{
        return getText().charAt(0);
	}

	public Optional<AbstractSegment> subSequence( int start, int end )
	{
		if ( start == 0 )  return Optional.of( this );
		return Optional.empty();
	}

	public Optional<AbstractSegment> join( AbstractSegment nextSeg )
	{
		return Optional.empty();
	}

	public abstract Node createNode();

	/**
	 * RichTextFX uses this for undo and redo.
	 */
    @Override public boolean equals( Object obj )
    {
    	if ( obj == this )  return true;
    	else if ( obj instanceof AbstractSegment && getClass().equals( obj.getClass() ) )
        {
        	AbstractSegment otherSeg = (AbstractSegment) obj;
            return getText().equals( otherSeg.getText() ) && getStyle().equals( otherSeg.getStyle() );
        }

        return false;
    }

}
