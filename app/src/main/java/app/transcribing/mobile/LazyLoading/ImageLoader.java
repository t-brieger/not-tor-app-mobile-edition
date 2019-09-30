package app.transcribing.mobile.LazyLoading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.transcribing.mobile.R;

public class ImageLoader {
    public static ImageLoader instance;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler = new Handler();//handler to display images in UI thread

    public ImageLoader(Context context) {
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = R.drawable.stub;

    /**
     * this is only an approximate answer, because it assumes that the URL ends in png, jpg or jpeg if it is a picture.
     * TODO: check for mimetype instead
     */
    public static boolean isSupportedImage(String url) {
        return url.endsWith("png") || url.endsWith("jpg") || url.endsWith("jpeg");
    }

    public void DisplayImage(String url, ImageView imageView) {
        if (!isSupportedImage(url))
            return;
        imageViews.put(imageView, url);
        queuePhoto(url, imageView);
        imageView.setImageResource(stub_id);
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                BitmapDisplayer bd = new BitmapDisplayer(photoToLoad.url, photoToLoad.imageView);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        String url;
        ImageView iv;

        public BitmapDisplayer(String url, ImageView iv) {
            this.url = url;
            this.iv = iv;
        }

        public void run() {
            Picasso.get().load(url).into(iv);
        }
    }

}
