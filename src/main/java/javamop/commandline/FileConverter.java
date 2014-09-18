// Copyright (c) 2002-2014 JavaMOP Team. All Rights Reserved.
package javamop.commandline;

import com.beust.jcommander.IStringConverter;

import java.io.File;

public class FileConverter implements IStringConverter<File> {
    @Override
    public File convert(String value) {
        return new File(value);
    }
}