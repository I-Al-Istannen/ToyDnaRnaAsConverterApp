package me.ialistannen.toydnarnaasconverter.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * An {@link AsyncTask} to process an image.
 */
public class OcrAsyncTask extends AsyncTask<Bitmap, Void, String> {

  private Context context;

  public OcrAsyncTask(Context context) {
    this.context = context;
  }

  @Override
  protected String doInBackground(Bitmap... bitmaps) {
    return OcrUtil.doOcr(bitmaps[0], context);
  }
}
