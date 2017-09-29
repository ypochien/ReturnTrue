package com.android.volley.toolbox;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Created by josephwang on 15/7/13.
 */
public class FileLoader
{
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private final Object mDiskCacheLock;
    /** RequestQueue for dispatching ImageRequests onto. */
    private final RequestQueue mRequestQueue;
    private boolean mDiskCacheStarting = true;

    /** Amount of time to wait after first response arrives before delivering all responses. */
    private int mBatchResponseDelayMs = 100;

    /** The cache implementation to be used as an L1 cache before calling into volley. */
    private final ByteArrayCache mCache;

    /**
     * HashMap of Cache keys -> BatchedImageRequest used to track in-flight requests so
     * that we can coalesce multiple requests to the same URL into a single network request.
     */
    private final ArrayMap<String, BatchedByteArrayRequest> mInFlightRequests =
            new ArrayMap<String, BatchedByteArrayRequest>();

    /** HashMap of the currently pending responses (waiting to be delivered). */
    private final ArrayMap<String, BatchedByteArrayRequest> mBatchedResponses =
            new ArrayMap<String, BatchedByteArrayRequest>();

    /** Handler to the main thread. */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /** Runnable for in-flight response delivery. */
    private Runnable mRunnable;

    public int getDiskCacheSize()
    {
        return diskCacheSize;
    }

    public void setDiskCacheSize(int diskCacheSize)
    {
        this.diskCacheSize = diskCacheSize;
    }

    private int diskCacheSize = 0;

    public String getAppFilePath()
    {
        return appFilePath;
    }

    public void setAppFilePath(String appFilePath)
    {
        this.appFilePath = appFilePath;
    }

    private String appFilePath = "";
    /**
     * Simple cache adapter interface. If provided to the ImageLoader, it
     * will be used as an L1 cache before dispatch to Volley. Implementations
     * must not block. Implementation with an LruCache is recommended.
     */
    public interface ByteArrayCache {
         byte[] getByteArray(String url);
         void putByteArray(String url,  byte[] data);
    }

    private static FileLoader.ByteArrayCache ignoreCache = new FileLoader.ByteArrayCache()
    {
        public void putByteArray(String url, byte[] file) { }

        public byte[] getByteArray(String url)
        {
            return null;
        }
    };

    public enum FileNameType
    {
        URL, FILE_NAME;
    }

    private FileNameType fileNameType = FileNameType.URL;

    public FileNameType getFileNameType()
    {
        return fileNameType;
    }

    public void setFileNameType(FileNameType fileNameType)
    {
        this.fileNameType = fileNameType;
    }

    public FileLoader(RequestQueue queue)
    {
        this(queue, ignoreCache, FileNameType.URL);
    }

    /**
     * Constructs a new ImageLoader.
     * @param queue The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public FileLoader(RequestQueue queue, ByteArrayCache imageCache)
    {
        this(queue, imageCache, FileNameType.URL);
    }

    public FileLoader(RequestQueue queue, ByteArrayCache imageCache, FileNameType fileNameType) {
        mRequestQueue = queue;
        mCache = imageCache;
        mDiskCacheLock = new Object();
        this.fileNameType = fileNameType;
    }

    /**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     */
    public static FileLoaderListener getByteArrayListener(FileLoaderListener listener) {
        return listener;
    }
    /**
     * Interface for the response handlers on image requests.
     *
     * The call flow is this:
     * 1. Upon being  attached to a request, onResponse(response, true) will
     * be invoked to reflect any cached data that was already available. If the
     * data was available, response.getBitmap() will be non-null.
     *
     * 2. After a network response returns, only one of the following cases will happen:
     *   - onResponse(response, false) will be called if the image was loaded.
     *   or
     *   - onErrorResponse will be called if there was an error loading the image.
     */
    public interface FileLoaderListener extends Response.ErrorListener
    {
         void preLoadData();
        /**
         * Listens for non-error changes to the loading of the image request.
         *
         * @param response Holds all information pertaining to the request, as well
         * as the bitmap (if it is loaded).
         * @param isImmediate True if this was called during ImageLoader.get() variants.
         * This can be used to differentiate between a cached image loading and a network
         * image loading in order to, for example, run an animation to fade in network loaded
         * images.
         */
         void onResponse(FileLoaderContainer response, boolean isImmediate);
    }

    /**
     * Checks if the item is available in the cache.
     * @param requestUrl The url of the remote image
     * @return True if the item exists in cache, false otherwise.
     */
    public boolean isCached(String requestUrl) {
        throwIfNotOnMainThread();

        String cacheKey = getFileCacheKey(requestUrl,fileNameType);
        return mCache.getByteArray(cacheKey) != null;
    }

    public FileLoaderContainer get(String requestUrl, FileLoaderListener listener) {
        return get(requestUrl, null,  listener);
    }

    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUrl The url of the remote image
     * @param listener The listener to call when the remote image is loaded
     * @return A container object that contains all of the properties of the request, as well as
     *     the currently available image (default if remote is not loaded).
     */
    public FileLoaderContainer get(String requestUrl, String attachedFileName, FileLoaderListener listener) {
        // only fulfill requests that were initiated from the main thread.
        throwIfNotOnMainThread();

        final String cacheKey = getFileCacheKey(requestUrl, fileNameType);

        // Try to look up the request in the cache of remote images.
        if (TextUtils.isEmpty(attachedFileName))
        {
            final String tempUrl = requestUrl;
            attachedFileName = tempUrl.substring(tempUrl.lastIndexOf(".") + 1);
        }

        File file = new File(getFileCache(requestUrl, attachedFileName));
        if (file != null && file.length() > 0)
        {
            // Return the cached bitmap.
            FileLoaderContainer container = new FileLoaderContainer(new byte[(int) file.length()], requestUrl, null, null, attachedFileName);
            try
            {
                mDiskCacheStarting = false;
                listener.onResponse(container, true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                listener.onErrorResponse(new VolleyError(e));
            }
            return container;
        }
        mDiskCacheStarting = true;
        // The bitmap did not exist in the cache, fetch it!
        FileLoaderContainer container =
                new FileLoaderContainer(null, requestUrl, cacheKey, listener, attachedFileName);
        container.file = null;
        // Update the caller to let them know that they should use the default bitmap.
//        listener.onResponse(container, true);
        listener.preLoadData();
        // Check to see if a request is already in-flight.
        BatchedByteArrayRequest request = mInFlightRequests.get(cacheKey);

        if (request != null) {
            // If it is, add this request to the list of listeners.
            request.addContainer(container);
            return container;
        }

        // The request is not already in flight. Send the new request to the network and
        // track it.
        Log.d("Volley" , "FileLoaderContainer get requestUrl " + requestUrl);
        InputStreamRequest newRequest = new InputStreamRequest(Request.Method.GET, requestUrl, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                onGetImageSuccess(cacheKey, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onGetImageError(cacheKey, error);
            }
        } );

        newRequest.setTag(requestUrl);

        mRequestQueue.add(newRequest);
        mInFlightRequests.put(cacheKey, new BatchedByteArrayRequest(newRequest, container));
        return container;
    }

    /**
     * Sets the amount of time to wait after the first response arrives before delivering all
     * responses. Batching can be disabled entirely by passing in 0.
     * @param newBatchedResponseDelayMs The time in milliseconds to wait.
     */
    public void setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        mBatchResponseDelayMs = newBatchedResponseDelayMs;
    }

    /**
     * Handler for when an image was successfully loaded.
     * @param cacheKey The cache key that is associated with the image request.
     * @param response The bitmap that was returned from the network.
     */
    private void onGetImageSuccess(String cacheKey, byte[] response) {
        // cache the image that was fetched.
        mCache.putByteArray(cacheKey, response);

        // remove the request from the list of in-flight requests.
        BatchedByteArrayRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            // Update the response bitmap.
            request.mResponseBitmap = response;

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    /**
     * Handler for when an image failed to load.
     * @param cacheKey The cache key that is associated with the image request.
     */
    private void onGetImageError(String cacheKey, VolleyError error) {
        // Notify the requesters that something failed via a null result.
        // Remove this request from the list of in-flight requests.
        BatchedByteArrayRequest request = mInFlightRequests.remove(cacheKey);

        // Set the error for this request
        request.setError(error);

        if (request != null) {
            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    public String getFileCache(String mRequestUrl, String mAttachedFileName)
    {
        Log.d("Volley" , "getFileCache appFilePath " + appFilePath);
        File cacheDir = null;
        String filePath;
        if(!TextUtils.isEmpty(appFilePath))
        {
            cacheDir = new File(appFilePath);//fold eventID命名
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }
            File file =  new File(appFilePath, getFileCacheKey(mRequestUrl, fileNameType) + "." + mAttachedFileName);
            filePath = file.getAbsolutePath();
        }
        else
        {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "Volley_Data");//fold eventID命名
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }
            filePath = Environment.getExternalStorageDirectory() + "/Volley_Data/" + getFileCacheKey(mRequestUrl,fileNameType) + "." + mAttachedFileName;
        }

        Log.d("Volley" , "getFileCache appFilePath filePath " + filePath);
        return filePath;
    }


    public File getFileCacheDir()
    {
        File cacheDir = null;
        if(!TextUtils.isEmpty(appFilePath))
        {
            cacheDir = new File(appFilePath);//fold eventID命名
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }
        }
        else
        {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "Volley_Data");//fold eventID命名
            if (!cacheDir.exists())
            {
                cacheDir.mkdirs();
            }
        }
        Log.d("Volley", "getFileCacheDir cacheDir getAbsolutePath " + cacheDir.getAbsolutePath());
        return cacheDir;
    }


    /**
     * Container object for all of the data surrounding an image request.
     */
    public class FileLoaderContainer
    {
        /**
         * The most relevant bitmap for the container. If the image was in cache, the
         * Holder to use for the final bitmap (the one that pairs to the requested URL).
         */
        public File file;
        public byte[] mBitmap;
        public final FileLoaderListener mListener;

        /** The cache key that was associated with the request */
        public final String mCacheKey;

        /** The request URL that was specified */
        public final String mRequestUrl;
        public final String mAttachedFileName;
        /**
         * Constructs a BitmapContainer object.
         * @param bitmap The final bitmap (if it exists).
         * @param requestUrl The requested URL for this container.
         * @param cacheKey The cache key that identifies the requested URL for this container.
         */
        public FileLoaderContainer(byte[] bitmap, String requestUrl,
                                   String cacheKey, FileLoaderListener listener, String attachedFileName) {
            mBitmap = bitmap;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mListener = listener;
            mAttachedFileName = attachedFileName;
            file = new File(getFileCache(mRequestUrl, mAttachedFileName));
            if (file.exists() && file.length() == 0)
            {
                file.delete();
                file = null;
            }
        }

        /**
         * Releases interest in the in-flight request (and cancels it if no one else is listening).
         */
        public void cancelRequest() {
            if (mListener == null) {
                return;
            }

            BatchedByteArrayRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                // check to see if it is already batched for delivery.
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        /**
         * Returns the bitmap associated with the request URL if it has been loaded, null otherwise.
         */
        public byte[] getByteArray() {
            return mBitmap;
        }

        /**
         * Returns the bitmap associated with the request URL if it has been loaded, null otherwise.
         */
        public File getCacheFile()
        {
            synchronized (mDiskCacheLock) {
                while (mDiskCacheStarting ||
                        ((file != null &&
                                file.exists() &&
                                file.length() == 0))) {
                    try {
                        mDiskCacheLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (file != null && file.exists() && file.length() > 0)
                {
                    return file;
                }
                else
                {
                    return null;
                }
            }
        }
        /**
         * Returns the requested URL for this container.
         */
        public String getRequestUrl() {
            return mRequestUrl;
        }
    }

    public long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
            {
                length += file.length();
            }
            else
            {
                length += folderSize(file);
            }
        }
        return length;
    }

    /**
     * Wrapper class used to map a Request to the set of active ImageContainer objects that are
     * interested in its results.
     */
    private class BatchedByteArrayRequest {
        /** The request being tracked */
        private final Request<?> mRequest;

        /** The result of the request being tracked by this item */
        private byte[] mResponseBitmap;

        /** Error if one occurred for this response */
        private VolleyError mError;

        /** List of all of the active ImageContainers that are interested in the request */
        private final LinkedList<FileLoaderContainer> mContainers = new LinkedList<FileLoaderContainer>();

        /**
         * Constructs a new BatchedImageRequest object
         * @param request The request being tracked
         * @param container The ImageContainer of the person who initiated the request.
         */
        public BatchedByteArrayRequest(Request<?> request, FileLoaderContainer container) {
            mRequest = request;
            mRequest.setTag(container.getRequestUrl());
            mContainers.add(container);
        }

        /**
         * Set the error for this response
         */
        public void setError(VolleyError error) {
            mError = error;
        }

        /**
         * Get the error for this response
         */
        public VolleyError getError() {
            return mError;
        }

        /**
         * Adds another ImageContainer to the list of those interested in the results of
         * the request.
         */
        public void addContainer(FileLoaderContainer container) {
            mContainers.add(container);
        }

        /**
         * Detatches the bitmap container from the request and cancels the request if no one is
         * left listening.
         * @param container The container to remove from the list
         * @return True if the request was canceled, false otherwise.
         */
        public boolean removeContainerAndCancelIfNecessary(FileLoaderContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    /**
     * Starts the runnable for batched delivery of responses if it is not already started.
     * @param cacheKey The cacheKey of the response being delivered.
     * @param request The BatchedImageRequest to be delivered.
     */
    private void batchResponse(String cacheKey, BatchedByteArrayRequest request) {
        mBatchedResponses.put(cacheKey, request);
        // If we don't already have a batch delivery runnable in flight, make a new one.
        // Note that this will be used to deliver responses to all callers in mBatchedResponses.

        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedByteArrayRequest bir : mBatchedResponses.values()) {
                        for (FileLoaderContainer container : bir.mContainers) {
                            // If one of the callers in the batched request canceled the request
                            // after the response was received but before it was delivered,
                            // skip them.
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.getError() == null) {
                                container.mBitmap = bir.mResponseBitmap;
                                try
                                {
                                    addFileCacheToContainer(container);
                                    container.mListener.onResponse(container, false);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    container.mListener.onErrorResponse(new VolleyError(e));
                                }
                            } else {
                                Log.d("FileLoader", "FileLoader bir.getError() " + bir.getError().getMessage());
                                container.mListener.onErrorResponse(bir.getError());
                                if (container.mBitmap != null)
                                {
                                    container.mBitmap = null;
                                }
                                if (bir.mResponseBitmap != null)
                                {
                                    bir.mResponseBitmap = null;
                                }
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mRunnable = null;
                }
            };
            // Post the runnable.
            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    private Comparator<Long> comparator = new Comparator<Long>()
    {
        @Override
        public int compare(Long lhs, Long rhs)
        {
            return String.valueOf(lhs).compareTo(String.valueOf(rhs));
        }
    };

    private void addFileCacheToContainer(FileLoaderContainer container) throws Exception
    {
        synchronized (mDiskCacheLock) {
            File cacheDir = getFileCacheDir();
            container.file = new File(getFileCache(container.mRequestUrl, container.mAttachedFileName));
            int diskCache = diskCacheSize > 0 ? diskCacheSize : DISK_CACHE_SIZE;
            while (folderSize(cacheDir) > diskCache)
            {
                /*****************按時間排，移除時間最早的File****************/
                TreeMap<Long, File> map = new TreeMap<Long, File>(comparator);
                for (File f : cacheDir.listFiles())
                {
                    map.put(f.lastModified(), f);
                }
                if (map.size() > 0)
                {
                    map.values().toArray(new File[map.size()])[0].delete();
                }
                map.clear();
            }

            if (!container.file.exists())
            {
                try
                {
                    container.file.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    mDiskCacheStarting = false;
                    mDiskCacheLock.notifyAll();
                    throw new Exception("does have permission to write Data!!!");
                }
            }
            OutputStream fOut = new FileOutputStream(container.file);
            try
            {
                if (container.mBitmap != null)
                {
                    fOut.write(container.mBitmap);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                fOut.flush();
                fOut.close();
                fOut = null;
                mDiskCacheStarting = false;
                mDiskCacheLock.notifyAll();
                mRequestQueue.cancelAll(container.getRequestUrl());
                if (container.mBitmap != null)
                {
                    container.mBitmap = null;
                }
            }
        }
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

    /**
     * Creates a cache key for use with the L1 cache.
     * @param url The URL of the request.
     */
    public static String getFileCacheKey(final String url, FileNameType fileNameType)
    {
        final String replaceFileName = url;
        String fileName = "";
        switch (fileNameType)
        {
            case URL:
                fileName = replaceFileName.replace("/", "_").replace(":", "_").replace(".","");
                return fileName;
            case FILE_NAME:
                fileName = replaceFileName.substring(replaceFileName.lastIndexOf("/") + 1).replace(".","");
                return fileName;
            default:
                return "";
        }
    }

    public static String getFileCacheKey(final String url)
    {
        return getFileCacheKey(url, FileNameType.URL);
    }

    public boolean isEvenDownload(String url, String mAttachedFileName)
    {
        File f = new File(getFileCache(url, mAttachedFileName));
        return f.exists() && f.length() > 0;
    }

    public boolean isEvenDownload(String url)
    {
        final String tempUrl = url;
        String attachedFileName = tempUrl.substring(tempUrl.lastIndexOf(".") + 1);
        return isEvenDownload(url, attachedFileName);
    }

    public String getDownloadPath(String url)
    {
        final String tempUrl = url;
        if (!TextUtils.isEmpty(tempUrl))
        {
            String attachedFileName = tempUrl.substring(tempUrl.lastIndexOf(".") + 1);
            return getDownloadPath(url, attachedFileName);
        }
        else
        {
            return "";
        }
    }

    public String getDownloadPath(String url, String mAttachedFileName)
    {
        File f = new File(getFileCache(url, mAttachedFileName));
        return f.getPath();
    }
}
