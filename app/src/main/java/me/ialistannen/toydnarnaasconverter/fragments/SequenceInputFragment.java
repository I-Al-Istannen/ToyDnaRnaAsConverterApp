package me.ialistannen.toydnarnaasconverter.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Collections;
import java.util.List;
import me.ialistannen.toydnarnaasconverter.DnaSequencer;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.codon.Codon;
import me.ialistannen.toydnarnaasconverter.exception.NoStartCodonFoundException;
import me.ialistannen.toydnarnaasconverter.fragments.OcrFragment.OcrResultCallback;

/**
 * A Fragment to input a base sequence.
 */
public abstract class SequenceInputFragment extends FragmentBase {

  @BindView(R.id.input_sequence_field)
  TextInputLayout sequenceInput;

  private DnaSequencer dnaSequencer = new DnaSequencer();

  private String lastText;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_sequence_input, container, false);

    ButterKnife.bind(this, view);

    setHasOptionsMenu(true);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.fragment_sequence_input_action_bar, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.fragment_sequence_input_action_bar_ocr) {
      OcrFragment ocrFragment = new OcrFragment();
      ocrFragment.setCallback(new OcrResultCallback() {
        @Override
        public void onReceiveOcr(String text) {
          if (sequenceInput != null && sequenceInput.getEditText() != null) {
            String normalizedText = normalizeString(text);

            Log.d("SequenceInputFragment", "Normalized message: " + normalizedText);

            // as the fragment might be shown again (resumed) after this is called
            SequenceInputFragment.this.lastText = normalizedText;

            // if it is already shown
            sequenceInput.getEditText().setText(normalizedText);
          }
        }

        private String normalizeString(String input) {
          String result = input;
          for (String line : input.split("\n")) {
            // Just somewhere have at least two of these valid letters
            if (line.matches(".*[UATGC].*[UATGC].*[UATGC].*")) {
              result = line.replaceAll("[^UATGC]", "");
              break;
            }
          }
          return result;
        }
      });
      getFragmentHolderActivity().switchToFragmentPushBack(ocrFragment);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (lastText != null && sequenceInput != null && sequenceInput.getEditText() != null) {
      sequenceInput.getEditText().setText(lastText);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (sequenceInput.getEditText() != null) {
      String text = sequenceInput.getEditText().getText().toString();
      if (!text.trim().isEmpty()) {
        lastText = text;
      }
    }
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    getFragmentHolderActivity().setActionbarUpPopsFragment(true);
  }

  @OnClick(R.id.accept_button)
  void onAccept() {
    EditText editText = sequenceInput.getEditText();

    if (editText == null) {
      return;
    }

    if (editText.getText() == null || editText.getText().toString().isEmpty()) {
      return;
    }

    String sequence = editText.getText().toString().replaceAll("\\s", "");
    lastText = sequence;
    onReceivedSequence(sequence);
  }

  /**
   * Called when a sequence was entered by the user.
   *
   * @param sequence The sequence the user entered
   */
  abstract protected void onReceivedSequence(String sequence);

  /**
   * @param sequence The Sequence to convert to codons. Needs to be a mRNA sequence
   * @return The list with codons, if any
   */
  List<Codon> mRnaToCodons(String sequence) {
    try {
      return dnaSequencer.convert(sequence);
    } catch (NoStartCodonFoundException e) {
      new AlertDialog.Builder(getActivity())
          .setTitle(R.string.error_parsing_sequence)
          .setMessage(e.getMessage())
          .create()
          .show();

      return Collections.emptyList();
    }
  }
}
