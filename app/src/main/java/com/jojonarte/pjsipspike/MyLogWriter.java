package com.jojonarte.pjsipspike;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

class MyLogWriter extends LogWriter
{
    @Override
    public void write(LogEntry entry)
    {
        System.out.println(entry.getMsg());
    }
}
