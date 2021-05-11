package bn.inference;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.base.BayesianNetwork;
import bn.base.StringValue;
import bn.core.Assignment;
import bn.core.Distribution;
import bn.core.RandomVariable;
import bn.parser.BIFParser;
import bn.parser.XMLBIFParser;

public class mainInferencer {
	public static void main(String[] args) {
		String inferencerString=args[0];
		String Xvariable="";
		String filenameString ="";
		int samplesize=0;
		String[] evidenceStrings=new String[10000];

		//get the inferencer type
		if(inferencerString.equals("MyBNInferencer")) {
			filenameString=args[1];
			Xvariable=args[2];
			evidenceStrings = Arrays.copyOfRange(args, 3, args.length);
		}else if(inferencerString.equals("RejectionSampleInferencer")) {
			samplesize=Integer.parseInt(args[1]);
			filenameString=args[2];
			Xvariable=args[3];
			evidenceStrings = Arrays.copyOfRange(args, 4, args.length);
		}else if(inferencerString.equals("LikelihoodWeightInferencer")) {
			samplesize=Integer.parseInt(args[1]);
			filenameString=args[2];
			Xvariable=args[3];
			evidenceStrings = Arrays.copyOfRange(args, 4, args.length);
		}else if(inferencerString.equals("GibbsSampleInferencer")) {
			samplesize=Integer.parseInt(args[1]);
			filenameString=args[2];
			Xvariable=args[3];
			evidenceStrings = Arrays.copyOfRange(args, 4, args.length);
		}else {
			System.out.println("No such inferencer exists!");
		}

		//Get file type
		String filetypeString="";
		if(filenameString.toLowerCase().endsWith("xml")) {
			System.out.println("It is a XML file");
			filetypeString="XML";
		}else if(filenameString.toLowerCase().endsWith("bif")) {
			System.out.println("It is a BIF file");
			filetypeString="BIF";
		}else {
			System.out.println("No such file exists");
		}

		try {
			System.out.println("\nAttempting to read file: " + filenameString + " at location");
			//--------------------------------------------------------------------------------------
			// if you are using Eclispse, uncomment the following line and comment out the next one

			//String newpath = "src/bn/examples/" + filenameString;
			String newpath = "./bn/examples/" + filenameString;

			//--------------------------------------------------------------------------------------

			BayesianNetwork BN = new bn.base.BayesianNetwork();

			if (filetypeString.equals("BIF")) {
				BIFParser parser = new BIFParser(new FileInputStream(newpath));
				BN = (BayesianNetwork) parser.parseNetwork();
			}
			else {
				XMLBIFParser parser = new XMLBIFParser();
				BN = (BayesianNetwork) parser.readNetworkFromFile(newpath);
			}

			System.out.println("The query variable is "+ Xvariable);
			RandomVariable query=BN.getVariableByName(Xvariable);

			Assignment eAssignment=new bn.base.Assignment();

			RandomVariable[] RV=new RandomVariable[evidenceStrings.length];

			for(int i=0;i<evidenceStrings.length;i=i+2) {

				System.out.println("\nshowing evidence: " + evidenceStrings[i] + " is set to " + evidenceStrings[i+1]);
				RV[i] = BN.getVariableByName(evidenceStrings[i]);
				eAssignment.put(RV[i], new StringValue(evidenceStrings[i+1]));

			}

			System.out.println("\nStarting Inferencing with " + inferencerString + " ... ");

			if (inferencerString.equals("MyBNInferencer")) {
				EnumerationInferencer enumerationInference = new bn.inference.EnumerationInferencer();
				Distribution dist = enumerationInference.query(query, eAssignment,BN);
				System.out.println(inferencerString);
				System.out.println(Xvariable+":" + dist.toString() + "\n");
			}
			else if(inferencerString.equals("RejectionSampleInferencer"))
			{
				RejectionSampling rejection_Sampling=new RejectionSampling();
				Distribution answer=rejection_Sampling.RejectionSample(query, eAssignment, BN, samplesize);
				System.out.println(inferencerString);
				System.out.println(Xvariable+":" + answer.toString() + "\n");
			}
			else if(inferencerString.equals("LikelihoodWeightInferencer"))
			{
				LikelihoodWeighting likelikHoodWeighting=new LikelihoodWeighting();
				Distribution answer=likelikHoodWeighting.LikelihoodWeight(query, eAssignment, BN, samplesize);
				System.out.println(inferencerString);
				System.out.println(Xvariable+":" + answer.toString() + "\n");
			}
			else if(inferencerString.equals("GibbsSampleInferencer"))
			{
				GibbsSampling gibbsSampling=new GibbsSampling();
				Distribution answer=gibbsSampling.GibbsAsk(query, eAssignment, BN, samplesize);
				System.out.println(inferencerString);
				System.out.println(Xvariable+":" + answer.toString() + "\n");
			}


		}catch(IOException e){
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
