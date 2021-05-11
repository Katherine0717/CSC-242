//package bn.inference;
//
//import java.io.*;
//import javax.xml.parsers.ParserConfigurationException;
//
//import bn.base.Assignment;
//import bn.base.BayesianNetwork;
//import bn.base.Distribution;
//import bn.parser.XMLBIFParser;
//import org.xml.sax.SAXException;
//
//public class MyBNInferencer {
//
//    public static void main(String[] args) {
//        EnumerationInferencer EI = new EnumerationInferencer();
//        RejectionSampling RS = new RejectionSampling();
//        LikelihoodWeighting LW = new LikelihoodWeighting();
//
//        if(args[0].contains(".xml")){
//            XMLBIFParser xml = new XMLBIFParser();
//            try {
//                BayesianNetwork bn = (BayesianNetwork) xml.readNetworkFromFile(args[0]);
//                Assignment e = new Assignment();
//                for(int i = 2; i < args.length; i+=2){
//                    e.put(bn.getVariableByName(args[i]), args[i+1]);
//                }
//                //Distribution dist = asker.ask(bn, bn.getVariableByName(argv[1]), e);
//                //Distribution dist = rej.rejectionSampling(bn.getVariableByName(argv[1]),e, bn,100000 );
//                Distribution dist = LW.LikelihoodWeighting(bn.getVariableByName(args[1]), e, bn, 100000);
//                System.out.println(dist);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ParserConfigurationException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            }
//        } else {
//            BIFParser x;
//            try {
//                x = new BIFParser(new FileInputStream(args[0]));
//
//                BayesianNetwork bn = x.parseNetwork();
//                Assignment e = new Assignment();
//                for(int i = 2; i < args.length; i+=2){
//                    e.put(bn.getVariableByName(args[i]), args[i+1]);
//                }
//                Distribution dist = rej.rejectionSampling(bn.getVariableByName(args[1]),e, bn,100000 );
//
//                System.out.println(dist);
//            } catch (FileNotFoundException e1) {
//                e1.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//
//
//    }
