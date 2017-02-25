package edu.upenn.cis.citation.Corecover;

class JCProf {
    static {
        System.loadLibrary("jcprof");
    }

    native static long getCpuTime();
}
