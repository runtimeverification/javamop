package com.runtimeverification.rvmonitor.util;

import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameterSet;
import com.runtimeverification.rvmonitor.java.rvj.parser.ast.rvmspec.RVMParameters;

class RVMonitorTruthTable {
    private final int level;

    private final RVMonitorTruthTable trueTable;
    private final RVMonitorTruthTable falseTable;

    // 0 = false, 1 = true, 2 = true and covered
    private int trueEntry = 0;
    private int falseEntry = 0;

    public RVMonitorTruthTable(int level) {
        this.level = level;

        if (level > 1) {
            trueTable = new RVMonitorTruthTable(level - 1);
            falseTable = new RVMonitorTruthTable(level - 1);
        } else {
            trueTable = null;
            falseTable = null;
        }
    }

    public void setTrue(boolean[] bitmap) {
        if (bitmap.length != level)
            return;
        setTrue(bitmap, 0);
    }

    private void setTrue(boolean[] bitmap, int index) {
        if (bitmap[index]) {
            if (index == bitmap.length - 1) {
                this.trueEntry = 1;
            } else {
                trueTable.setTrue(bitmap, index + 1);
            }
        } else {
            if (index == bitmap.length - 1) {
                this.trueEntry = 1;
                this.falseEntry = 1;
            } else {
                trueTable.setTrue(bitmap, index + 1);
                falseTable.setTrue(bitmap, index + 1);
            }
        }
    }

    public void setCovered(boolean[] bitmap) {
        if (bitmap.length != level)
            return;
        setCovered(bitmap, 0);
    }

    private void setCovered(boolean[] bitmap, int index) {
        if (bitmap[index]) {
            if (index == bitmap.length - 1) {
                this.trueEntry = 2;
            } else {
                trueTable.setCovered(bitmap, index + 1);
            }
        } else {
            if (index == bitmap.length - 1) {
                this.trueEntry = 2;
                this.falseEntry = 2;
            } else {
                trueTable.setCovered(bitmap, index + 1);
                falseTable.setCovered(bitmap, index + 1);
            }
        }
    }

    public int checkAllTrue(boolean[] bitmap) {
        if (bitmap.length != level)
            return -1;
        return checkAllTrue(bitmap, 0);
    }

    private int checkAllTrue(boolean[] bitmap, int index) {
        int ret = 3;
        if (bitmap[index]) {
            if (index == bitmap.length - 1) {
                ret = this.trueEntry;
            } else {
                ret = trueTable.checkAllTrue(bitmap, index + 1);
            }
        } else {
            if (index == bitmap.length - 1) {
                if (this.trueEntry < this.falseEntry)
                    ret = this.trueEntry;
                else
                    ret = this.falseEntry;
            } else {
                int temp1 = trueTable.checkAllTrue(bitmap, index + 1);
                int temp2 = falseTable.checkAllTrue(bitmap, index + 1);
                if (temp1 < temp2)
                    ret = temp1;
                else
                    ret = temp2;
            }
        }

        return ret;
    }
}

class RVMBitmap {
    private final int truesize;
    private final int totalsize;
    private final boolean[] bitmap;

    public RVMBitmap(int truesize, int totalsize) {
        bitmap = new boolean[totalsize];

        this.totalsize = totalsize;
        if (truesize > this.totalsize) {
            this.truesize = this.totalsize;
        } else {
            this.truesize = truesize;
        }

        for (int i = 0; i < this.truesize; i++) {
            bitmap[i] = true;
        }
    }

    public boolean[] getBitmap() {
        return this.bitmap;
    }

    public boolean[] getNextBitmap() {
        // find the last true
        int lastTrue = -1;
        for (int i = 0; i < totalsize; i++) {
            if (bitmap[i])
                lastTrue = i;
        }

        // if the last true is not the last bit, move it to right and return
        if (lastTrue < totalsize - 1) {
            bitmap[lastTrue] = false;
            bitmap[lastTrue + 1] = true;
            return bitmap;
        }

        // find the last true before lastTrue such that it can move right
        int movableTrue = -1;
        int numTrueBeforeMovableTrue = 0;
        int countTrue = 0;
        for (int i = 0; i < lastTrue; i++) {
            if (bitmap[i] && !bitmap[i + 1]) {
                movableTrue = i;
                numTrueBeforeMovableTrue = countTrue;
            }
            if (bitmap[i])
                countTrue++;
        }

        // if nothing can move, that's it.
        if (movableTrue == -1)
            return null;

        // move this one to right and reset all bits after this
        bitmap[movableTrue] = false;
        bitmap[movableTrue + 1] = true;
        for (int i = movableTrue + 2; i < movableTrue + 2
                + (truesize - numTrueBeforeMovableTrue - 1); i++) {
            bitmap[i] = true;
        }

        for (int i = movableTrue + 2
                + (truesize - numTrueBeforeMovableTrue - 1); i < totalsize; i++) {
            bitmap[i] = false;
        }
        return bitmap;
    }

}

public class RVMBooleanSimplifier {

    public static RVMParameterSet simplify(RVMParameterSet paramSet,
            RVMParameters fullParam) {
        RVMParameterSet ret = new RVMParameterSet();

        RVMParameterSet simplifiedSet = new RVMParameterSet();

        for (RVMParameters param : paramSet) {
            if (param.size() == 0) {
                ret.add(param);
                return ret;
            }
            boolean exist = false;
            for (RVMParameters param2 : paramSet) {
                if (!param.equals(param2) && param.contains(param2))
                    exist = true;
            }
            if (!exist)
                simplifiedSet.add(param);
        }

        int numParam = fullParam.size();
        RVMonitorTruthTable truthTable = new RVMonitorTruthTable(numParam);

        for (RVMParameters param : simplifiedSet) {
            boolean[] bitmap = new boolean[numParam];
            for (int i = 0; i < numParam; i++) {
                if (param.contains(fullParam.get(i)))
                    bitmap[i] = true;
            }
            truthTable.setTrue(bitmap);
        }

        for (int size = 1; size <= numParam; size++) {
            RVMBitmap rvmBitmap = new RVMBitmap(size, numParam);
            boolean[] bitmap = rvmBitmap.getBitmap();
            while (bitmap != null) {
                int min = truthTable.checkAllTrue(bitmap);

                if (min == 1) {
                    RVMParameters param = new RVMParameters();
                    for (int i = 0; i < numParam; i++) {
                        if (bitmap[i]) {
                            param.add(fullParam.get(i));
                        }
                    }
                    ret.add(param);

                    truthTable.setCovered(bitmap);
                }

                bitmap = rvmBitmap.getNextBitmap();
            }

        }

        return ret;
    }

}
