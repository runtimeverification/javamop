package com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.util;

import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.parser.CFGParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.parser.ParseException;
import com.runtimeverification.rvmonitor.logicrepository.plugins.cfg.parser.TokenMgrError;

import java.util.*;
import java.io.*;

public class GLRGenTest {
    public static void main(String[] args)
            throws ParseException, TokenMgrError, java.io.IOException {
        CFGParser parser = new CFGParser(new FileInputStream(args[0]));
        parser.Start();
        CFG g = parser.getCFG();
        g.simplify();
        HashMap<Terminal,Integer> tmap = new HashMap<Terminal,Integer>();
        int tint = 1;
        for (Terminal t : g.terminals()) {
            tmap.put(t,tint++);
        }
        LR lr = new LR(g,tmap);
        System.out.println(GLRGen.gen(lr,"Foo"));
    }
}
