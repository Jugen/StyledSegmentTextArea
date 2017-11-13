package lightware.richtext;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import javax.xml.bind.JAXB;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.StyledSegment;

public class MySegmentCodec implements Codec<StyledSegment<AbstractSegment,String>>
{
	@Override
	public String getName()
	{
		return "AbstractSegment";
	}

	@Override
	public void encode( DataOutputStream os, StyledSegment<AbstractSegment,String> styledSeg ) throws IOException
	{
		AbstractSegment  seg = styledSeg.getSegment();
		
		os.writeUTF( seg.getClass().getName() );
		os.writeUTF( seg.getData().getClass().getName() );

		Writer  data2xml = new StringWriter();
		JAXB.marshal( seg.getData(), data2xml );
		os.writeUTF( data2xml.toString() );

		String  style = styledSeg.getStyle();
		if ( style == null ) style = "";
		os.writeUTF( style );
	}

	@Override
	public StyledSegment<AbstractSegment,String> decode( DataInputStream is ) throws IOException
	{
		String  segmentType = is.readUTF();
		String  dataType = is.readUTF();
		String  xmlData = is.readUTF();
		String  style = is.readUTF();

		try
		{
			Reader  xml2data = new StringReader( xmlData );
			Class<?>  dataClass = Class.forName( dataType );
			Object  data = JAXB.unmarshal( xml2data, dataClass );

			Class<AbstractSegment>  segClass = (Class<AbstractSegment>) Class.forName( segmentType );
			Constructor<AbstractSegment>  segCreate = segClass.getConstructor( Object.class );

			return new StyledSegment( segCreate.newInstance( data ), style );
		}
		catch ( Exception EX )
		{
			EX.printStackTrace();
		}

		return null;
	}
}
