package com.appster.features.edit_video;

/**
 * Created by Ngoc on 8/17/2017.
 */

public class MyKSYRecordKit  {
//    private static final String a = "MyKSYRecordKit";
//    private static int b = -1;
//    private static int c = 0;
//    private static int d = 1;
//    private List<String> e;
//    private AsyncTask f;
//    private OnErrorListener g;
//    private OnInfoListener h;
//    private long i;
//    private long j;
//    private c k;
//    private a l;
//    private AtomicInteger m;
//    private String n;
//    private MyKSYRecordKit.MergeFilesFinishedListener o;
//    public OnInfoListener mOnInfoListener = new OnInfoListener() {
//        public void onInfo(int var1, int var2, int var3) {
//            Log.d("KSYRecordKit", "record kit:" + var1);
//            switch (var1) {
//                case 2:
//                    MyKSYRecordKit.this.a(MyKSYRecordKit.this.n, MyKSYRecordKit.this.o);
//                    break;
//                default:
//                    if (MyKSYRecordKit.this.h != null) {
//                        MyKSYRecordKit.this.h.onInfo(var1, var2, var3);
//                    }
//
//            }
//        }
//    };
//    public OnErrorListener mOnErrorListener = new OnErrorListener() {
//        public void onError(int var1, int var2, int var3) {
//            switch (var1) {
//                case -4004:
//                case -4003:
//                case -4002:
//                case -4001:
//                case -4000:
//                case -1004:
//                case -1003:
//                    Log.w("MyKSYRecordKit", "record error delete files:" + var1);
//                    MyKSYRecordKit.this.j = System.currentTimeMillis() - MyKSYRecordKit.this.i;
//                    MyKSYRecordKit.this.i = 0L;
//                    MyKSYRecordKit.this.deleteRecordFile(MyKSYRecordKit.this.getLastRecordedFiles());
//                    MyKSYRecordKit.this.a(true, var1);
//                    break;
//                default:
//                    if (MyKSYRecordKit.this.g != null) {
//                        MyKSYRecordKit.this.g.onError(var1, var2, var3);
//                    }
//
//            }
//        }
//    };
//
//    public MyKSYRecordKit(Context var1) {
//        super(var1);
//        this.mAudioProfile = 1;
//        this.mIFrameInterval = 1.0F;
//        this.e = new LinkedList();
//        this.k = com.ksyun.media.shortvideo.a.c.a();
//        this.l = new a();
//        super.setOnInfoListener(this.mOnInfoListener);
//        super.setOnErrorListener(this.mOnErrorListener);
//        this.m = new AtomicInteger(b);
//    }
//
//    public void setAudioEncodeProfile(int var1) {
//    }
//
//    public synchronized boolean startRecord(String var1) {
//        if (this.m.get() != b) {
//            Log.w("KSYRecordKit", "merging files please wait");
//            return false;
//        } else {
//            this.i = System.currentTimeMillis();
//            boolean var2 = super.startRecord(var1);
//            if (var2) {
//                this.e.add(var1);
//            }
//
//            this.b();
//            return var2;
//        }
//    }
//
//    public void setOnInfoListener(OnInfoListener var1) {
//        this.h = var1;
//    }
//
//    public void setOnErrorListener(OnErrorListener var1) {
//        this.g = var1;
//    }
//
//    public void stopRecord() {
//        if (this.mIsFileRecording) {
//            Log.d("KSYRecordKit", "stop Record");
//            if (!this.mIsRecording && this.mVideoEncoderMgt.getEncoder().isEncoding() && this.mAudioEncoderMgt.getEncoder().isEncoding()) {
//                this.stopCapture();
//            } else {
//                this.mFilePublisher.stop();
//            }
//
//            this.mIsFileRecording = false;
//            this.j = System.currentTimeMillis() - this.i;
//            this.i = 0L;
//            this.a(false, 0);
//        }
//    }
//
//    public void stopRecord(String var1, MyKSYRecordKit.MergeFilesFinishedListener var2) {
//        if (this.mIsFileRecording) {
//            this.stopRecord();
//            if (TextUtils.isEmpty(var1)) {
//                Log.w("MyKSYRecordKit", "the output file is null");
//                return;
//            }
//
//            if (this.m.get() == d) {
//                Log.w("MyKSYRecordKit", "merging files, please wait");
//                return;
//            }
//
//            if (this.m.get() == c && this.f != null) {
//                this.f.cancel(true);
//                this.f = null;
//            }
//
//            this.m.set(c);
//            this.n = var1;
//            this.o = var2;
//        } else {
//            this.m.set(c);
//            this.n = var1;
//            this.o = var2;
//            this.a(this.n, this.o);
//        }
//
//    }
//
//    private synchronized void a(final String var1, final MyKSYRecordKit.MergeFilesFinishedListener var2) {
//        if (TextUtils.isEmpty(var1)) {
//            Log.w("MyKSYRecordKit", "the output file is null");
//        } else if (this.m.get() == d) {
//            Log.w("MyKSYRecordKit", "merging files, please wait");
//        } else {
//            if (this.m.get() == c) {
//                if (this.e.size() > 1) {
//                    if (this.f != null) {
//                        this.f.cancel(true);
//                        this.f = null;
//                    }
//
//                    this.m.set(d);
//                    this.f = new AsyncTask() {
//                        protected Object doInBackground(Object[] var1x) {
//                            Log.d("MyKSYRecordKit", "merge file:" + MyKSYRecordKit.this.e.size());
//                            com.ksyun.media.shortvideo.utils.a.a(MyKSYRecordKit.this.e, var1);
//                            MyKSYRecordKit.this.m.set(MyKSYRecordKit.b);
//                            MyKSYRecordKit.this.f = null;
//                            if (var2 != null) {
//                                var2.onFinished(var1);
//                            }
//
//                            MyKSYRecordKit.this.k.a(MyKSYRecordKit.this.e.size(), false, 0);
//                            return null;
//                        }
//                    };
//                    this.f.execute(new Object[0]);
//                } else {
//                    Log.d("MyKSYRecordKit", "no need for merge:" + this.e.size());
//                    this.m.set(b);
//                    if (var2 != null) {
//                        if (this.e.size() == 1) {
//                            var2.onFinished((String) this.e.get(0));
//                        } else {
//                            var2.onFinished((String) null);
//                        }
//                    }
//                }
//            }
//
//        }
//    }
//
//    public synchronized void deleteAllFiles() {
//        Log.d("KSYRecordKit", "deleteAllFiles");
//        if (this.e.size() > 0) {
//            Iterator var1 = this.e.iterator();
//
//            while (var1.hasNext()) {
//                String var2 = (String) var1.next();
//                FileUtils.deleteFile(var2);
//                var1.remove();
//            }
//        }
//
//    }
//
//    public synchronized void deleteFileByIndex(int var1) {
//        if (var1 < this.e.size() && var1 >= 0) {
//            String var2 = (String) this.e.get(var1);
//            this.e.remove(var1);
//            FileUtils.deleteFile(var2);
//        } else {
//            Log.w("KSYRecordKit", "do not have this file:" + var1);
//        }
//    }
//
//    public int getRecordedFilesCount() {
//        return this.e.size();
//    }
//
//    public synchronized boolean deleteRecordFile(String var1) {
//        if (!this.e.contains(var1)) {
//            return false;
//        } else {
//            Log.d("KSYRecordKit", "delete Record File:" + var1);
//            this.e.remove(var1);
//            return FileUtils.deleteFile(var1);
//        }
//    }
//
//    public String getLastRecordedFiles() {
//        String file = "";
//        if (this.e != null && this.e.size() > 0) {
//            file = (String) this.e.get(this.e.size() - 1);
//        }
//        return file;
//    }
//
//    private void b() {
//        this.l.a = this.k.a(this.mInitVideoBitrate);
//        this.l.b = this.k.a(this.mAudioBitrate);
//        this.l.c = this.k.a(this.mTargetWidth, this.mTargetHeight);
//        this.l.d = this.mTargetFps;
//        this.l.e = this.k.b(this.getVideoEncodeMethod(), this.mVideoCodecId);
//        this.l.f = this.k.c(this.getAudioEncodeMethod(), this.getAudioEncodeProfile());
//        this.l.g = this.mAudioChannels;
//        this.l.h = this.mRotateDegrees % 180 != 0;
//        if ((this.getImgTexFilterMgt().getFilter() == null || this.getImgTexFilterMgt().getFilter().size() <= 0) && (this.getImgTexFilterMgt().getExtraFilters() == null || this.getImgTexFilterMgt().getExtraFilters().size() <= 0)) {
//            this.l.i = 0;
//        } else {
//            this.l.i = 1;
//        }
//
//        this.l.j = 0;
//        this.l.k = 0;
//        if (this.getAudioFilterMgt().getFilter() != null && this.getAudioFilterMgt().getFilter().size() > 0) {
//            Iterator var1 = this.getAudioFilterMgt().getFilter().iterator();
//
//            while (var1.hasNext()) {
//                AudioFilterBase var2 = (AudioFilterBase) var1.next();
//                if (var2 instanceof AudioReverbFilter) {
//                    this.l.j = 1;
//                } else if (var2 instanceof KSYAudioEffectFilter) {
//                    this.l.k = 1;
//                }
//
//                if (this.l.j > 0 && this.l.k > 0) {
//                    break;
//                }
//            }
//        }
//
//        if (this.getBGMAudioFilterMgt().getFilter() != null && this.getBGMAudioFilterMgt().getFilter().size() > 0) {
//            this.l.l = 1;
//        } else {
//            this.l.l = 0;
//        }
//
//        this.l.m = this.mFrontCameraMirror;
//        this.l.n = this.mIFrameInterval;
//        this.l.o = this.mAudioSampleRate;
//        this.l.p = this.k.d(this.getVideoEncodeMethod(), this.getVideoEncodeProfile());
//        this.l.q = this.k.e(this.getVideoEncodeMethod(), this.getVideoEncodeScene());
//    }
//
//    private void a(boolean var1, int var2) {
//        this.k.a(var1, var2, this.j, this.l);
//    }
//
//    @Override
//    public void release() {
//        super.release();
//        setOnLogEventListener(null);
//        setOnInfoListener(null);
//        setOnErrorListener(null);
//        requestScreenShot(null);
//        this.o = null;
//    }
//
//    public interface MergeFilesFinishedListener {
//        void onFinished(String var1);
//    }
}
