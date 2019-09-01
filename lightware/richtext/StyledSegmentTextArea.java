package lightware.richtext;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;
import org.fxmisc.richtext.model.ReadOnlyStyledDocumentBuilder;
import org.fxmisc.richtext.model.StyledDocument;
import org.fxmisc.richtext.model.StyledSegment;
import org.fxmisc.richtext.model.TextOps;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.TextFlow;

public class StyledSegmentTextArea extends GenericStyledArea<String,AbstractSegment,String>
{
    private static final String  initialParStyle = "";        // May not be null, otherwise copy paste fails !
    private static final String  initialSegStyle = "";        // May not be null, otherwise copy paste fails !
    private static final boolean preserveStyle = true;        // May not be false, otherwise undo doesn't work on custom nodes !

    private static final BiConsumer<TextFlow,String>  applyParStyle = (txtflow,pstyle) -> txtflow.getStyleClass().add( pstyle );
    private static final TextOps<AbstractSegment,String>  segmentOps = new MySegmentOps();

    private static final StyledDocument INDENT_DOC = ReadOnlyStyledDocument.fromSegment( new IndentSegment( "\t" ), initialParStyle, initialSegStyle, segmentOps );
    private static final StyledSegment INDENT_SEG = (StyledSegment) INDENT_DOC.getParagraph(0).getStyledSegments().get(0);
    private boolean indent = false;

    public StyledSegmentTextArea()
    {
        this( styledSeg -> styledSeg.getSegment().createNode( styledSeg.getStyle() ) ); 
    }

    public StyledSegmentTextArea( Function<StyledSegment<AbstractSegment, String>, Node> nodeFactory )
    {
        super( initialParStyle, applyParStyle, initialSegStyle, segmentOps, preserveStyle, nodeFactory );
        setStyleCodecs( Codec.STRING_CODEC, new MySegmentCodec() );     // Needed for copy paste.
        setWrapText(true);
        
        addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( indent && KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = getCaretPosition();
                if ( getParagraph( getCurrentParagraph()-1 ).length() == 0 ) {
                    Platform.runLater( () -> replace( caretPosition, caretPosition, INDENT_DOC ) );
                }
            }
        });
    }
    
    public void setIndentOn( boolean indent ) {
        this.indent = indent;
    }
    
    public boolean isIndentOn() {
        return indent;
    }

    public void append( AbstractSegment customSegment )
    {
        insert( getLength(), customSegment );
    }

    public void insert( int pos, AbstractSegment customSegment )
    {
        insert( pos, ReadOnlyStyledDocument.fromSegment( customSegment, initialParStyle, initialSegStyle, segmentOps ) );
    }

    @Override
    public void replace( int start, int end, StyledDocument replacement )
    {
        if ( ! indent ) super.replace( start, end, replacement );
        else
        {
            List<Paragraph> pl = replacement.getParagraphs();
            ReadOnlyStyledDocumentBuilder db = new ReadOnlyStyledDocumentBuilder( segmentOps, initialParStyle );
    
            for ( int p = 0; p < pl.size(); p++ )
            {
                List segments = pl.get(p).getStyledSegments();
                
                if ( p > 1 && pl.get( p-1 ).length() == 0 )
                {
                    if ( ! (pl.get( p ).getSegments().get(0) instanceof IndentSegment) )
                    {
                        if ( segments instanceof AbstractList ) {
                            segments = new ArrayList<>( segments );
                        }
                        segments.add( 0, INDENT_SEG );
                    }
                }
                
                db.addParagraph( segments );
            }
    
            super.replace( start, end, db.build() );
        }
    }
    
}
