package bn.parser;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import bn.core.BayesianNetwork;
import org.xml.sax.SAXException;

/**
 * Uses BIFParser and XMLBIFPrinter to convert from BIF to XMLBIF.
 */
public class BIF2XMLBIF {

    /**
     * Parse a BIF file and print out the resulting BayesianNetwork as XMLBIF.
     * <p>
     * Usage: java bn.bn.bn.base.base.parser.BIF2XMLBIF FILE
     * <p>
     * With no arguments: reads dog-problem.bif in the src tree
     */
    public static void main(String[] argv) throws IOException, ParserConfigurationException, SAXException {
        String filename = "src/bn/bn.bn.base.base.examples/dog-problem.bif";
        String networkName = "Dog-Problem";
        if (argv.length > 0) {
            filename = argv[0];
            if (argv.length > 1) {
                networkName = argv[1];
            } else {
                networkName = null;
            }
        }
        BIFParser parser = new BIFParser(new FileInputStream(filename));
        XMLBIFPrinter printer = new XMLBIFPrinter(System.out);
        BayesianNetwork network = parser.parseNetwork();
        printer.print(network, networkName);
    }

}
