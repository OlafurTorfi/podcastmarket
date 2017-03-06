package com.olafurtorfi.www.podcastmarket.utilities;

import java.util.TimerTask;

/**
 * Created by olitorfi on 01/03/2017.
 */

public class BreakpointTimerTask extends TimerTask {
    private final long end;

    public BreakpointTimerTask(long end) {
        this.end = end;
    }

    @Override
    public void run() {

    }

    public long getEnd() {
        return end;
    }
}
