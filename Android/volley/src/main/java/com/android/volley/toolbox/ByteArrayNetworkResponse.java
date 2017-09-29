package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by josephwang on 15/7/13.
 */
public class ByteArrayNetworkResponse extends NetworkResponse
{
    public ByteArrayNetworkResponse(int statusCode, byte[] data, InputStream ins,
                                    Map<String, String> headers, boolean notModified)
    {
        super(statusCode, data, headers, notModified);
        this.ins = ins;
    }

    public ByteArrayNetworkResponse(byte[] data, InputStream ins)
    {
        super(data);
        this.ins = ins;
    }

    public ByteArrayNetworkResponse(byte[] data, InputStream ins, Map<String, String> headers)
    {
        super(data, headers);
        this.ins = ins;
    }

    public final InputStream ins;
}
