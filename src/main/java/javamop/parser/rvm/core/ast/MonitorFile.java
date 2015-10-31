package javamop.parser.rvm.core.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A RVM input file, which contains one preamble and at least one specification.
 *
 * @author A. Cody Schuffelen
 */
public class MonitorFile {

    private final String preamble;
    private final List<Specification> specifications;

    /**
     * Construct a MonitorFile out of the preamble and some specifications.
     *
     * @param preamble Declarations at the beginning of the file, e.g. imports.
     */
    public MonitorFile(final String preamble,
                       final List<Specification> specifications) {
        this.preamble = preamble;
        if (specifications.size() == 0) {
            throw new RuntimeException(
                    "RVM files must have at least one specification.");
        }
        this.specifications = Collections
                .unmodifiableList(new ArrayList<Specification>(specifications));
    }

    /**
     * Language-specific declarations that go at the top of the file, e.g.
     * includes, imports.
     *
     * @return Language-specific top of the file declarations.
     */
    public String getPreamble() {
        return preamble;
    }

    /**
     * An unmodifiable list of the specifications in the monitoring file.
     *
     * @return A list of the specifications.
     */
    public List<Specification> getSpecifications() {
        return specifications;
    }

}