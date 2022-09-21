package generated;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SchemaBasedJAXB {
    private final static String JAXB_XML_MACHINE_PACKAGE_NAME="generated";

    public static CTEEnigma deserialize(String filePath) throws FileNotFoundException, JAXBException {
        CTEEnigma cteEnigma=null;
        InputStream inputStream=new FileInputStream(new File(filePath));
        cteEnigma=deserializeFrom(inputStream);
        return cteEnigma;
    }

    private static CTEEnigma deserializeFrom(InputStream in)throws JAXBException {
        JAXBContext jc= JAXBContext.newInstance(JAXB_XML_MACHINE_PACKAGE_NAME);
        Unmarshaller u=jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }
}
