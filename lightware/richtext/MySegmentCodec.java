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

public class MySegmentCodec implements Codec<AbstractSegment>
{
	@Override
	public String getName()
	{
		return "AbstractSegment";
	}

	@Override
	public void encode( DataOutputStream os, AbstractSegment seg ) throws IOException
	{
		os.writeUTF( seg.getClass().getName() );
		os.writeUTF( seg.getData().getClass().getName() );

		Writer  data2xml = new StringWriter();
		JAXB.marshal( seg.getData(), data2xml );
		os.writeUTF( data2xml.toString() );

		os.writeUTF( seg.getStyle() );
	}

	@Override
	public AbstractSegment decode( DataInputStream is ) throws IOException
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
			Constructor<AbstractSegment>  segCreate = segClass.getConstructor( Object.class, String.class );

			return  segCreate.newInstance( data, style );
		}
		catch ( Exception EX )
		{
			EX.printStackTrace();
		}

		return null;
	}
}
