package me.ialistannen.toydnarnaasconverter.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.util.OcrAsyncTask;
import me.ialistannen.toydnarnaasconverter.view.ResizableRectangle;
import me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A fragment for OCR input.
 */
public class OcrFragment extends FragmentBase {

  private static final int PHOTO_REQUEST_CODE = 1;
  private Uri outputImageUri;

  @BindView(R.id.ocr_image)
  ImageView ocrImageView;

  @BindView(R.id.ocr_image_selection_overlay)
  ResizableRectangle imageSelectionOverlay;

  @BindView(R.id.accept_image)
  Button acceptImageButton;

  private Bitmap image;

  private OcrResultCallback callback;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_ocr, container, false);

    ButterKnife.bind(this, view);

    return view;
  }

  @OnClick(R.id.capture_new_image)
  void onCaptureNewImage() {
    startCameraActivity();
  }

  /**
   * @param callback The callback to invoke when the OCR was performed.
   */
  public void setCallback(OcrResultCallback callback) {
    this.callback = callback;
  }

  /**
   * Just offload the task of making an image to android.
   */
  private void startCameraActivity() {
    try {
      File imageFolder = new File(getActivity().getExternalCacheDir() + "/Tesseract/images");
      outputImageUri = Uri.fromFile(new File(imageFolder, "ocr.jpg"));

      if (!imageFolder.exists() && !imageFolder.mkdirs()) {
        throw new IOException("Couldn't create image dir");
      }

      final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputImageUri);

      if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
        startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE);
      } else {
        throw new IOException("No camera app found.");
      }
    } catch (IOException e) {
      showErrorTakingPhotoDialog(e.getLocalizedMessage());
      Log.w("OcrFragment", e.getMessage(), e);
    }
  }

  private void showErrorTakingPhotoDialog(String message) {
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.error_capturing_photo_title)
        .setMessage(message)
        .show();
  }

  @OnClick(R.id.accept_image)
  void onAcceptImage() {
    Toast.makeText(getActivity(), "OCR in progress", Toast.LENGTH_SHORT).show();

    Location location = imageSelectionOverlay.getLocationSnapshot();

    Bitmap croppedImage = Bitmap.createBitmap(
        image,
        location.getTopLeftX(), location.getTopLeftY(), location.getWidth(), location.getHeight()
    );

    acceptImageButton.setEnabled(false);

    new OcrAsyncTask(getActivity()) {
      @Override
      protected void onPostExecute(String text) {
        if (!isAdded()) {
          return;
        }
        acceptImageButton.setEnabled(true);

        if (text == null) {
          return;
        }
        // kill myself
        getFragmentManager().popBackStack();

        callback.onReceiveOcr(text);
      }
    }.execute(croppedImage);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != PHOTO_REQUEST_CODE) {
      return;
    }

    if (resultCode != Activity.RESULT_OK) {
      showErrorTakingPhotoDialog(getActivity().getString(R.string.unknown_error));
      return;
    }

    image = BitmapFactory.decodeFile(outputImageUri.getPath(), getBitmapOptions());

    if (image == null) {
      showErrorTakingPhotoDialog(
          getActivity().getString(R.string.fragment_ocr_error_could_not_decode_image)
      );
    }

    ocrImageView.setImageBitmap(image);
    ocrImageView.setVisibility(View.VISIBLE);

    imageSelectionOverlay.getLayoutParams().width = image.getWidth();
    imageSelectionOverlay.getLayoutParams().height = image.getHeight();
    getView().invalidate();
  }

  @NotNull
  private Options getBitmapOptions() {
    Options options = new Options();
    options.inSampleSize = 4; // save some memory (1/4 of the original image now)
    return options;
  }

  public interface OcrResultCallback {

    /**
     * @param text The text that the OCR software thinks might be in the image.
     */
    void onReceiveOcr(String text);
  }
}
