package com.appdynamics.ace.extension.rest.util.command;

import java.util.Date;

/**
 * Created by stefan.marx on 10.02.14.
 */
public class FileInfo {
    private final Date _modifiedDate;
    private final String _name;
    private final long _length;

    public Date getModifiedDate() {
        return _modifiedDate;
    }

    public String getName() {
        return _name;
    }

    public long getLength() {
        return _length;
    }

    public FileInfo() {
       this("",0,0);
    }
    public FileInfo(String name, long length, long lastModified) {
        _name = name;
        _length = length;
        _modifiedDate = new Date(lastModified);

    }
}
