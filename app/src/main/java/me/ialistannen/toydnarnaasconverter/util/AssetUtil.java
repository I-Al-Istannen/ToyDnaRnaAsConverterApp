package me.ialistannen.toydnarnaasconverter.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import me.ialistannen.toydnarnaasconverter.R;

/**
 * Aids with Assets!
 */
public class AssetUtil {

  /**
   * Copies all assets in the given folder to the given folder.
   *
   * @param targetFolder The folder to copy to
   * @param path The path to get them from
   * @param context The context to use for retrieving the assets.
   */
  public static boolean copyAssets(File targetFolder, String path, Context context) {
    AssetManager assets = context.getAssets();
    try {
      String[] files = assets.list(path);
      for (String file : files) {
        copyFile(path + "/" + file, assets, new File(targetFolder, file));
      }

      return true;
    } catch (IOException e) {
      new AlertDialog.Builder(context)
          .setTitle(R.string.error_copying_assets_title)
          .setMessage(e.getLocalizedMessage())
          .create()
          .show();

      return false;
    }
  }

  private static void copyFile(String file, AssetManager assets, File outputFile)
      throws IOException {
    if (outputFile.exists()) {
      return;
    }
    if (!outputFile.getParentFile().exists() && !outputFile.getParentFile().mkdirs()) {
      throw new IOException(
          "Couldn't create dir: '" + outputFile.getParentFile().getAbsolutePath() + "'"
      );
    }
    try (InputStream inputStream = assets.open(file);
        FileOutputStream outputStream = new FileOutputStream(outputFile)) {

      byte[] buffer = new byte[1024];
      int len;
      while ((len = inputStream.read(buffer)) > -1) {
        outputStream.write(buffer, 0, len);
      }

    }
  }
}
