package me.ialistannen.toydnarnaasconverter.util;

import android.content.Context;
import android.graphics.Bitmap;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;

/**
 * Helps with doing OCR.
 */
public class OcrUtil {

  private static synchronized TessBaseAPI getTessBaseApi(Context context) {
    File tessData = new File(context.getFilesDir(), "tessdata");
    AssetUtil.copyAssets(tessData, "tessdata", context);

    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    tessBaseAPI.init(tessData.getParentFile().getAbsolutePath(), "deu");
    return tessBaseAPI;
  }

  /**
   * Performs <strong>blocking</strong> OCR.
   *
   * @param bitmap The {@link Bitmap} to perform OCR on
   * @param context The context to use for asset lookup and training data
   * @return The recognized String
   */
  public static String doOcr(Bitmap bitmap, Context context) {
    TessBaseAPI tessBaseApi = getTessBaseApi(context);

    tessBaseApi.setImage(bitmap);

    String text = tessBaseApi.getUTF8Text();
    tessBaseApi.end(); // probably worth it, OCR will be infrequent

    return text;
  }

}
