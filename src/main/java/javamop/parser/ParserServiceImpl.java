package javamop.parser;


import javamop.ParserService;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.astex.MOPSpecFileExt;

import java.io.File;

/**
 * Created by xiaohe on 9/30/14.
 */
public class ParserServiceImpl implements javamop.ParserService {
    private MOPProcessor mopProcessor;

    public static String DefaultNameForMOPProcessor="MOP-Processor";

    public ParserServiceImpl(){
        this(DefaultNameForMOPProcessor);
    }

    /**
     * When the ParserServiceImpl object is created, it initiates a default mop processor in order to perform
     * some tasks.
     * @param mopProcessorName
     */
    public ParserServiceImpl(String mopProcessorName) {
        this.mopProcessor = new MOPProcessor(mopProcessorName);
    }

    /**
     * Wrap another exception as a MOP logic exception, with an additional message.
     * @param errorMessage A descriptive message for the error.
     * @param e The exception to be wrapped.
     */
    @Override
    public MOPExceptionImpl generateMOPException(String errorMessage, Exception e) {
        return new MOPException(errorMessage, e);
    }

    /**
     * Wrap another exception as a MOP logic exception.
     * @param e The original Exception object.
     * @return An object of MOPExceptionImpl.
     */
    @Override
    public MOPExceptionImpl generateMOPException(Exception e) {
        return new MOPException(e);
    }

    /**
     * Construct a MOPException with an informative message.
     * @param str An informative message describing the error.
     */
    @Override
    public MOPExceptionImpl generateMOPException(String str) {
        return new MOPException(str);
    }

    /**
     * Produce a MOPSpecFileExt by reading a file through the language-independent MOP parser.
     * @param file The file to read from.
     * @return A Java-specific MOP specification object.
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    @Override
    public MOPSpecFileExt parse2MOPSpecFileExt(File file) throws MOPExceptionImpl {
        return JavaParserAdapter.parse(file);
    }

    /**
     * Produce a MOPSpecFileExt by reading a string through the language-independent MOP parser.
     * @param str The string to read from.
     * @return A Java-specific MOP specification object.
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    @Override
    public MOPSpecFileExt parse2MOPSpecFileExt(String str) throws MOPExceptionImpl {
        return JavaParserAdapter.parse(str);
    }

    /**
     * Translate the MOPSpecFileExt to MOPSpecFile.
     * @param currentFile
     * @return MOPSpecFile
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    @Override
    public MOPSpecFile translateMopSpecFile(MOPSpecFileExt currentFile) throws MOPExceptionImpl {
        return JavaMOPExtender.translateMopSpecFile(currentFile);
    }

    /**
     * Verify that certain properties about the specification are true.
     * @param mopSpec The specification to verify the properties of.
     * @throws MOPExceptionImpl If some properties are not met.
     */
    @Override
    public void verify(JavaMOPSpec mopSpec) throws MOPExceptionImpl {
        MOPErrorChecker.verify(mopSpec);
    }

    /**
     * Ensure that thread parameters are not used improperly.
     * @param event The event definition to verify.
     * @throws MOPExceptionImpl If the thread variable is used improperly.
     */
    @Override
    public void verifyThreadPointCut(EventDefinition event) throws MOPExceptionImpl {
        MOPErrorChecker.verifyThreadPointCut(event);
    }

    /**
     * Verify there is only one endProgram event.
     * @param mopSpec The specification to verify.
     * @throws javamop.ParserService.MOPExceptionImpl if there is more than one endProgram event.
     */
    @Override
    public void verifyUniqueEndProgram(JavaMOPSpec mopSpec) throws MOPExceptionImpl {
        MOPErrorChecker.verifyUniqueEndProgram(mopSpec);
    }

    /**
     * Verify that parametric properties have at least one parameter.
     * @param mopSpec The specification to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the specification has no parameters and is parametric.
     */
    @Override
    public void verifyGeneralParametric(JavaMOPSpec mopSpec) throws MOPExceptionImpl {
        MOPErrorChecker.verifyGeneralParametric(mopSpec);
    }

    /**
     * Verify that an endProgram event cannot have parameters.
     * @param event The event to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the event is an endProgram event and has parameters.
     */
    @Override
    public void verifyEndProgramParam(EventDefinition event) throws MOPExceptionImpl {
        MOPErrorChecker.verifyEndProgramParam(event);
    }

    /**
     * Verify that an endThread parameter only has the thread as a parameter.
     * @param event The event to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the endThread event doesn't have only the thread parameter.
     */
    @Override
    public void verifyEndThreadParam(EventDefinition event) throws MOPExceptionImpl {
        MOPErrorChecker.verifyEndThreadParam(event);
    }

    /**
     * Construct a MOPProcessor.
     * The new MOPProcessor is obtained by renaming the default MOPProcessor.
     * @param newName The name of the processor.
     */
    @Override
    public void setUpMOPProcessor(String newName) {
        this.mopProcessor.updateName(newName);
    }

    /**
     * Convert a MOPSpecFile into .rvm file and remove all the aspectJ related parts
     * @param mopSpecFile The parameter to convert.
     * @return The generated .rvm file string
     * @throws javamop.ParserService.MOPExceptionImpl If there is a logic error in conversion.
     */
    @Override
    public String generateRVFile(MOPSpecFile mopSpecFile) throws MOPExceptionImpl {
        return this.mopProcessor.generateRVFile(mopSpecFile);
    }

    /**
     * Convert a MOPSpecFile into aspectJ file.
     * @param mopSpecFile The parameter to convert.
     * @return The generated aspectJ file.
     * @throws javamop.ParserService.MOPExceptionImpl If there is a logic error in conversion.
     */
    @Override
    public String generateAJFile(MOPSpecFile mopSpecFile) throws MOPExceptionImpl {
        return this.mopProcessor.generateAJFile(mopSpecFile);
    }

    /**
     * Retrieve the specification information from a File. If it is a Java file, return
     * the annotations in the file. If it is a specification file, return the entire file.
     * @param file The file to read from.
     * @return The specification information in the file.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong in reading the file.
     */
    @Override
    public String process(File file) throws MOPExceptionImpl {
        return SpecExtractor.process(file);
    }

    /**
     * Produce a MOP Specification File object from text input.
     * @param input The specification as text.
     * @return The specifications parsed into an object.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong reading or parsing the specification.
     */
    @Override
    public MOPSpecFile parse2MOPSpecFile(String input) throws MOPExceptionImpl {
        return SpecExtractor.parse(input);
    }

    /**
     * Update the MOPProcessor's verbose mode to true.
     */
    @Override
    public void setMOPProcessorToVerboseMode() {
        this.mopProcessor.verbose=true;
    }

    /**
     * Check if the MOPProcessor is in verbose mode.
     */
    @Override
    public boolean isMOPProcessorInVerboseMode() {
        return this.mopProcessor.verbose;
    }


}
