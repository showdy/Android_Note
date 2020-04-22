package com.showdy.learnandroid.binderpool;

import android.os.RemoteException;

import com.showdy.learnandroid.ICompute;

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
