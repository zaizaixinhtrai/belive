package com.apster.common;

import android.util.Log;

import com.appster.utility.RxUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by ThanhBan on 10/5/2016.
 */

public class FileDownloader {
    private static FileDownloader sInstance = new FileDownloader();
    String mRootDir = Constants.FILE_CACHE_FOLDER;

    private CompositeSubscription fileDownloadSubscription;

    public static FileDownloader getInstance() {
        return sInstance;
    }

    private FileDownloader() {
    }

    public void downloadFile(String urlFile, final DownloadVideos.IDownloadListener iDownloadListener) {
        fileDownloadSubscription.add(Observable.just(urlFile)
                .subscribeOn(Schedulers.io())
                .flatMap((Func1<String, Observable<String>>) s -> {
                    try {
                        return Observable.just(downloadFile(s));
                    } catch (IOException e) {
                        Timber.e(e);
                        return Observable.just("");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileDownloadedLink -> {
                    Log.d(DownloadVideos.class.getName(), "File downloaded: " + fileDownloadedLink);
                    if (fileDownloadedLink.isEmpty()) {
                        iDownloadListener.fail();
                    } else {
                        iDownloadListener.successful(fileDownloadedLink);
                    }
                }, Timber::e));
    }

    private String downloadFile(String urlFile) throws IOException {
        String fileLocal;

        File dir = new File(mRootDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = urlFile.substring(urlFile.lastIndexOf('/'));
        File file = new File(mRootDir, fileName);
        fileLocal = file.toString();


        long oldFileSize = file.length();
        Log.d(FileDownloader.class.getName(), "local: " + fileLocal);
        Log.d(FileDownloader.class.getName(), "Local file size: " + oldFileSize);
        OutputStream output = null;
        URL url = new URL(urlFile);
        URLConnection connection = url.openConnection();
        connection.connect();
        int fileLength = connection.getContentLength();
        Log.d(FileDownloader.class.getName(), "Remote: " + urlFile);
        Log.d(FileDownloader.class.getName(), "Remote file size: " + fileLength);
        try (InputStream input = new BufferedInputStream(url.openStream())) {
            if (!file.exists() || (file.exists() && (oldFileSize < fileLength))) {
                Log.d(FileDownloader.class.getName(), "Start download file...: " + urlFile);
                // download the file

                output = new FileOutputStream(fileLocal);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....

                    output.write(data, 0, count);
                }
                Log.d(FileDownloader.class.getName(), "Downloaded file size: " + total);

                output.flush();
                output.close();
                input.close();

            } else {
                Log.d(FileDownloader.class.getName(), "Do not need to download");
                fileLocal = file.toString();
            }
        } catch (Exception e) {
            Log.d(FileDownloader.class.getName(), e.toString());
            fileLocal = "";
        } finally {
            if (output != null) output.close();
        }

        return fileLocal;
    }

    /**
     * Return local file
     *
     * @param fileUrl
     * @return
     */
    public void isFileAlreadyDownloaded(String fileUrl, final DownloadVideos.IFileAlreadyDownloadedListener fileAlreadyDownloadedListener) {
        LogUtils.logV("NCS", "isFileAlreadyDownloaded" + fileUrl);
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        fileDownloadSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(fileDownloadSubscription);
        fileDownloadSubscription.add(Observable.just(fileUrl)
                .subscribeOn(Schedulers.newThread())
                .flatMap((Func1<String, Observable<String>>) s -> Observable.fromCallable(() -> getLocalFileName(s)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileLink -> fileAlreadyDownloadedListener.needToDownload(fileLink.isEmpty(), fileLink), Timber::e));
    }

    public void clearAllDownloadThread() {
        RxUtils.unsubscribeIfNotNull(fileDownloadSubscription);
    }

    String getLocalFileName(String fileLink) {
        File dir = new File(mRootDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = fileLink.substring(fileLink.lastIndexOf('/'));
        File localFile = new File(mRootDir, fileName);
        if (localFile.exists()) {
            long oldFileSize = localFile.length();

            try {
                URL url = new URL(fileLink);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                if (oldFileSize < fileLength) {
                    return "";
                } else {
                    return fileName;
                }

            } catch (Exception e) {
                Timber.e(e);
                return "";
            }

        } else {
            return "";
        }
    }
}
