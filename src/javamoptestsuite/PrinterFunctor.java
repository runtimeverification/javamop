package javamoptestsuite;

import java.io.File;

public class PrinterFunctor implements TestCaseFunctor{

	public FunctorResult apply(TestCase testCase) {
		FunctorResult ret = new FunctorResult();
		
		System.out.println(testCase.path);
		
		for(TestCaseProgDir testCaseProg : testCase.testing_programs){
			System.out.println("-" + testCaseProg.dirName);
		}
		
		return ret;
	}

	
}
