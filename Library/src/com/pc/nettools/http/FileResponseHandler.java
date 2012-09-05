package com.pc.nettools.http;

import java.io.*;

/**
 * Created by Pietro Caselani
 */
public class FileResponseHandler extends HttpResponseHandler {
    private File mResponseFile;

    public FileResponseHandler(String responseFilePath) {
        if (responseFilePath == null)
            throw new RuntimeException("Response file can't be null");
        mResponseFile = new File(responseFilePath);
    }

    public void onSuccess(File file, AsyncHttpRequest request) {}

    @Override
    public void onFinish() {
        if (mResponseFile != null)
            onSuccess(mResponseFile, mRequest);
        else
            onFailure(mException, mRequest);
    }

    @Override
    public void sendSuccessMessage(ByteArrayOutputStream outputStream) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
            FileOutputStream fos = new FileOutputStream(mResponseFile);
            byte data[] = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = byteArrayInputStream.read(data)) > 0)
                fos.write(data, 0, bytesRead);

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            mResponseFile = null;
            mException = e;
        } catch (IOException e) {
            mResponseFile = null;
            mException = e;
        }
    }
}