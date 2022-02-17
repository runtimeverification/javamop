package com.runtimeverification.rvmonitor.java.rvj.output.codedom.helper;

/**
 * This interface is used to mark a class that is capable of generating code.
 *
 * @author Choonghwan Lee <clee83@illinois.edu>
 */
public interface ICodeGenerator {
    /**
     * Generate code using the provided formatter. It should be noted that this
     * method is assumed to be const (in C++ term); i.e., the method should not
     * have any side-effect. Although the current implementation does not invoke
     * this method multiple times, it would be great to conform to the
     * convention.
     *
     * @param fmt
     *            formatter to be used to dump code
     */
    public void getCode(ICodeFormatter fmt);
}
