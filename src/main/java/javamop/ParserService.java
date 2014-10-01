package javamop;

import javamop.parser.ast.MOPSpecFile;
import javamop.parser.ast.mopspec.EventDefinition;
import javamop.parser.ast.mopspec.JavaMOPSpec;
import javamop.parser.astex.MOPSpecFileExt;

import java.io.File;

/**
 * Created by xiaohe on 9/30/14.
 */
public interface ParserService {
    /**
     * Customers of this parser can put new requirements on debugging information as methods.
     */
    interface MOPExceptionAdditionalRequirements{
        //customers can put new requirements here as methods.
//        public String getDetailedDebuggingInfo(); //this is a possible use-case.
    }

    abstract class MOPExceptionImpl extends Exception implements MOPExceptionAdditionalRequirements{
        public MOPExceptionImpl(String errMsg, final Exception e){
            super(errMsg, e);
        }

        public MOPExceptionImpl(String errMsg){
            super(errMsg);
        }
    }

    /**
     * Wrap another exception as a MOP logic exception, with an additional message.
     * @param errorMessage A descriptive message for the error.
     * @param e The exception to be wrapped.
     */
    public MOPExceptionImpl generateMOPException(String errorMessage, Exception e);

    /**
     * Wrap another exception as a MOP logic exception.
     * @param e The original Exception object.
     * @return An object of MOPExceptionImpl.
     */
    public MOPExceptionImpl generateMOPException(Exception e);

    /**
     * Construct a MOPException with an informative message.
     * @param str An informative message describing the error.
     */
    public MOPExceptionImpl generateMOPException(final String str);


    /**
     * Produce a MOPSpecFileExt by reading a file through the language-independent MOP parser.
     * @param file The file to read from.
     * @return A Java-specific MOP specification object.
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    public MOPSpecFileExt parse2MOPSpecFileExt(File file) throws MOPExceptionImpl;

    /**
     * Produce a MOPSpecFileExt by reading a string through the language-independent MOP parser.
     * @param str The string to read from.
     * @return A Java-specific MOP specification object.
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    public MOPSpecFileExt parse2MOPSpecFileExt(String str) throws MOPExceptionImpl;


    /**
     * Translate the MOPSpecFileExt to MOPSpecFile.
     * @param currentFile
     * @return MOPSpecFile
     * @throws javamop.ParserService.MOPExceptionImpl
     */
    public MOPSpecFile translateMopSpecFile(MOPSpecFileExt currentFile) throws MOPExceptionImpl;

    /**
     * Verify that certain properties about the specification are true.
     * @param mopSpec The specification to verify the properties of.
     * @throws MOPExceptionImpl If some properties are not met.
     */
    public void verify(JavaMOPSpec mopSpec) throws MOPExceptionImpl;

    /**
     * Ensure that thread parameters are not used improperly.
     * @param event The event definition to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the thread variable is used improperly.
     */
    public void verifyThreadPointCut(final EventDefinition event) throws MOPExceptionImpl;

    /**
     * Verify there is only one endProgram event.
     * @param mopSpec The specification to verify.
     * @throws javamop.ParserService.MOPExceptionImpl if there is more than one endProgram event.
     */
    public void verifyUniqueEndProgram(JavaMOPSpec mopSpec) throws MOPExceptionImpl;

    /**
     * Verify that parametric properties have at least one parameter.
     * @param mopSpec The specification to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the specification has no parameters and is parametric.
     */
    public void verifyGeneralParametric(JavaMOPSpec mopSpec) throws MOPExceptionImpl;


    /**
     * Verify that an endProgram event cannot have parameters.
     * @param event The event to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the event is an endProgram event and has parameters.
     */
    public void verifyEndProgramParam(EventDefinition event) throws MOPExceptionImpl;


    /**
     * Verify that an endThread parameter only has the thread as a parameter.
     * @param event The event to verify.
     * @throws javamop.ParserService.MOPExceptionImpl If the endThread event doesn't have only the thread parameter.
     */
    public void verifyEndThreadParam(EventDefinition event) throws MOPExceptionImpl;


    /**
     * Construct a MOPProcessor.
     * @param name The name of the processor.
     */
    public void setUpMOPProcessor(String name);

    /**
     * Convert a MOPSpecFile into .rvm file and remove all the aspectJ related parts
     * @param mopSpecFile The parameter to convert.
     * @return The generated .rvm file string
     * @throws javamop.ParserService.MOPExceptionImpl If there is a logic error in conversion.
     */
    public String generateRVFile(MOPSpecFile mopSpecFile) throws MOPExceptionImpl;

    /**
     * Convert a MOPSpecFile into aspectJ file.
     * @param mopSpecFile The parameter to convert.
     * @return The generated aspectJ file.
     * @throws javamop.ParserService.MOPExceptionImpl If there is a logic error in conversion.
     */
    public String generateAJFile(MOPSpecFile mopSpecFile) throws MOPExceptionImpl;


    /**
     * Retrieve the specification information from a File. If it is a Java file, return
     * the annotations in the file. If it is a specification file, return the entire file.
     * @param file The file to read from.
     * @return The specification information in the file.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong in reading the file.
     */
    public String process(final File file) throws MOPExceptionImpl;

    /**
     * Produce a MOP Specification File object from text input.
     * @param input The specification as text.
     * @return The specifications parsed into an object.
     * @throws javamop.ParserService.MOPExceptionImpl If something goes wrong reading or parsing the specification.
     */
    public MOPSpecFile parse2MOPSpecFile(final String input) throws MOPExceptionImpl;


    /**
     * Update the MOPProcessor's verbose mode to true.
     */
    public void setMOPProcessorToVerboseMode();

    /**
     * Check if the MOPProcessor is in verbose mode.
     */
    public boolean isMOPProcessorInVerboseMode();

}
