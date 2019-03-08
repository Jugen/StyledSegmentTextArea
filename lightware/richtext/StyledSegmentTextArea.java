package lightware.richtext;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.StyledSegment;
import org.fxmisc.richtext.model.TextOps;
import javafx.scene.Node;
import javafx.scene.text.TextFlow;

public class StyledSegmentTextArea extends GenericStyledArea<String,AbstractSegment,String>
{
	private static final String  initialParStyle = "";		// May not be null, otherwise copy paste fails !
	private static final String  initialSegStyle = "";
	private static final boolean preserveStyle = true;		// May not be false, otherwise undo doesn't work on custom nodes !

	private static final BiConsumer<TextFlow,String>  applyParStyle = (txtflow,pstyle) -> txtflow.getStyleClass().add( pstyle );
	private static final TextOps<AbstractSegment,String>  segmentOps = new MySegmentOps();

	public StyledSegmentTextArea( Function<StyledSegment<AbstractSegment, String>, Node> nodeFactory )
	{
		super( initialParStyle, applyParStyle, initialSegStyle, segmentOps, preserveStyle, nodeFactory );
    	setStyleCodecs( Codec.STRING_CODEC, new MySegmentCodec() );	 // Needed for copy paste.
        setWrapText(true);
	}

	public StyledSegmentTextArea()
	{
		this( styledSeg -> styledSeg.getSegment().createNode( styledSeg.getStyle() ) ); 
	}

	public void append( AbstractSegment customSegment )
	{
        insert( getLength(), customSegment );
	}

	public void insert( int pos, AbstractSegment customSegment )
	{
		insert( pos, ReadOnlyStyledDocument.fromSegment( customSegment, initialParStyle, initialSegStyle, segmentOps ) );
	}
}
