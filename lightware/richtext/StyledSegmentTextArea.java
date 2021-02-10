package lightware.richtext;
import java.text.BreakIterator;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.Selection.Direction;
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
        
        // Intercept Enter to insert an indent after an empty line.
        addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( indent && KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = getCaretPosition();
                if ( getParagraph( getCurrentParagraph()-1 ).length() == 0 ) {
                    Platform.runLater( () -> replace( caretPosition, caretPosition, INDENT_DOC ) );
                }
            }
        });
        
        // Hijack Ctrl+Left (incl Shift) to navigate around an indent.
        addEventFilter( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.isShortcutDown() ) switch ( KE.getCode() )
            {
                case LEFT : case KP_LEFT : {
                    this.skipToPrevWord( KE.isShiftDown() );
                    KE.consume();
                    break;
                }
            }
        });

        // Prevent the caret from appearing on the left hand side of an indent.
        caretPositionProperty().addListener( (ob,oldPos,newPos) ->
        {
            if ( indent && newPos < getLength() && getCaretColumn() == 0 ) {
                AbstractSegment seg = getParagraph( getCurrentParagraph() ).getSegments().get(0);
                if ( seg instanceof IndentSegment && getSelection().getLength() == 0 ) {
                    displaceCaret( newPos + 1 );
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

    @Override // Navigating around/over indents
    public void nextChar( SelectionPolicy policy )
    {
        if ( getCaretPosition() < getLength() ) {
            // offsetByCodePoints throws an IndexOutOfBoundsException unless colPos is adjusted to accommodate any indents, see this.moveTo
            moveTo( Direction.RIGHT, policy, (paragraphText,colPos) -> Character.offsetByCodePoints( paragraphText, colPos, +1 ) );
        }
    }
    
    @Override // Navigating around/over indents
    public void previousChar( SelectionPolicy policy )
    {
        if ( getCaretPosition() > 0 ) {
            // offsetByCodePoints throws an IndexOutOfBoundsException unless colPos is adjusted to accommodate any indents, see this.moveTo 
            moveTo( Direction.LEFT, policy, (paragraphText,colPos) -> Character.offsetByCodePoints( paragraphText, colPos, -1 ) );
        }
    }

    public void deletePreviousChar()
    {
        if ( getCaretPosition() > 0 ) {
            // offsetByCodePoints throws an IndexOutOfBoundsException unless colPos is adjusted to accommodate any indents, see this.moveTo
            moveTo( Direction.LEFT, SelectionPolicy.CLEAR, (paragraphText,colPos) -> Character.offsetByCodePoints( paragraphText, colPos, -1 ) );
            int col = getCaretPosition();
            deleteText( col, col+1 );
        }
    }

    // Handles Ctrl+Left and Ctrl+Shift+Left
    private void skipToPrevWord( boolean isShiftDown )
    {
        int caretPos = getCaretPosition();
        if ( caretPos >= 1 )
        {
            boolean prevCharIsWhiteSpace = false;
            if ( indent && getCaretColumn() == 1 ) {
                // Check for indent as charAt(0) throws an IndexOutOfBoundsException because Indents aren't represented by a character 
                AbstractSegment seg = getParagraph( getCurrentParagraph() ).getSegments().get(0);
                prevCharIsWhiteSpace = seg instanceof IndentSegment;
            }
            if ( ! prevCharIsWhiteSpace ) prevCharIsWhiteSpace = Character.isWhitespace( getText( caretPos-1, caretPos ).charAt(0) );
            wordBreaksBackwards( prevCharIsWhiteSpace ? 2 : 1, isShiftDown ? SelectionPolicy.ADJUST : SelectionPolicy.CLEAR );
        }
    }

    /**
     * Skips n number of word boundaries backwards.
     */
    @Override // Accommodating Indent
    public void wordBreaksBackwards( int n, SelectionPolicy selection )
    {
        if( getLength() == 0 ) return;

        moveTo( Direction.LEFT, selection, (paragraphText,colPos) ->
        {
            BreakIterator wordIterator = BreakIterator.getWordInstance();
            wordIterator.setText( paragraphText );
            wordIterator.preceding( colPos );
            for ( int i = 1; i < n; i++ ) {
                wordIterator.previous();
            }
            return wordIterator.current();
        });
    }

    /**
     * Skips n number of word boundaries forward.
     */
    @Override // Accommodating Indent
    public void wordBreaksForwards( int n, SelectionPolicy selection )
    {
        if( getLength() == 0 ) return;

        moveTo( Direction.RIGHT, selection, (paragraphText,colPos) ->
        {
            BreakIterator wordIterator = BreakIterator.getWordInstance();
            wordIterator.setText( paragraphText );
            wordIterator.following( colPos );
            for ( int i = 1; i < n; i++ ) {
                wordIterator.next();
            }
            return wordIterator.current();
        });
    }
    
    /**
     * Because Indents are not represented in the text by a character there is a discrepancy
     * between the caret position and the text position which has to be taken into account.
     * So this method ADJUSTS the caret position before invoking the supplied function.    
     * 
     * @param dir LEFT for backwards, and RIGHT for forwards
     * @param selection CLEAR or ADJUST
     * @param colPosCalculator a function that receives PARAGRAPH text and an ADJUSTED
     * starting column position as parameters and returns an end column position.  
     */
    private void moveTo( Direction dir, SelectionPolicy selection, BiFunction<String,Integer,Integer> colPosCalculator )
    {
        int colPos = getCaretColumn();
        int pNdx = getCurrentParagraph();
        Paragraph p = getParagraph( pNdx );
        int pLen = p.length();

        boolean adjustCol = indent && p.getSegments().get(0) instanceof IndentSegment;
        if ( adjustCol ) colPos--;
        
        if ( dir == Direction.LEFT && colPos == 0 && pNdx > 0 ) {
            p = getParagraph( --pNdx );
            adjustCol = indent && p.getSegments().get(0) instanceof IndentSegment;
            colPos = p.getText().length(); // don't simplify !
        }
        else if ( dir == Direction.RIGHT && (pLen == 0 || colPos >= pLen-1) && pNdx < getParagraphs().size()-1 )
        {
            p = getParagraph( ++pNdx );
            adjustCol = indent && p.getSegments().get(0) instanceof IndentSegment;
            colPos = 0;
        }
        else colPos = colPosCalculator.apply( p.getText(), colPos );

        if ( adjustCol ) colPos++;

        moveTo( pNdx, colPos, selection );
    }
}
