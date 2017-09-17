package me.ialistannen.toydnarnaasconverter.util;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.AllCaps;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * A {@link android.widget.EditText} that groups the input.
 */
public class GroupingEditText extends AppCompatEditText {

  {
    init();
  }

  public GroupingEditText(Context context) {
    super(context);
  }

  public GroupingEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public GroupingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void init() {
    setFilters(new InputFilter[]{new AllCaps()});
    addTextChangedListener(new GroupingTextWatcher(3));
  }

  private class GroupingTextWatcher implements TextWatcher {

    private int groupSize;
    private boolean myChange;
    private String textBefore;

    private GroupingTextWatcher(int groupSize) {
      this.groupSize = groupSize;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      textBefore = getText().toString().replace(" ", "");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      if (myChange) {
        myChange = false;
        return;
      }
      String text = s.toString();

      text = text.replace(" ", "");

      boolean deleted = textBefore.length() > text.length();

      StringBuilder result = new StringBuilder();

      char[] charArray = text.toCharArray();
      for (int i = 0; i < charArray.length; i++) {
        if (i > 0 && i % groupSize == 0) {
          result.append(" ");
        }
        result.append(charArray[i]);
      }

      int selectionPosition = getSelectionStart();

      if (selectionPosition != 0 && (selectionPosition) % (groupSize + 1) == 0) {
        if (deleted) {
          selectionPosition--;
        } else {
          selectionPosition++;
        }
      }

      myChange = true;
      setText(result.toString());
      setSelection(selectionPosition);
    }
  }
}
