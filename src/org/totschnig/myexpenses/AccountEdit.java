/*   This file is part of My Expenses.
 *   My Expenses is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   My Expenses is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.totschnig.myexpenses;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for editing an account
 * @author Michael Totschnig
 */
public class AccountEdit extends EditActivity {
  private static final String OPENINTENTS_COLOR_EXTRA = "org.openintents.extra.COLOR";
  private static final String OPENINTENTS_PICK_COLOR_ACTION = "org.openintents.action.PICK_COLOR";
  private static final int PICK_COLOR_REQUEST = 25;
  private static final int CURRENCY_DIALOG_ID = 0;
  private static final int TYPE_DIALOG_ID = 1;
  private static final int COLOR_DIALOG_ID = 2;
  private EditText mLabelText;
  private EditText mDescriptionText;
  private AutoCompleteTextView mCurrencyText;
  private Button mCurrencyButton;
  private Button mTypeButton;
  Account mAccount;
  private String[] currencyCodes;
  private String[] currencyDescs;
  private TextWatcher currencyInformer;
  private Account.Type mAccountType;
  private int mAccountColor;
  private String[] mTypes = new String[Account.Type.values().length];
  private String[] mColorNames;
  private static Integer[] mColors = new Integer[] { Color.BLUE, Color.CYAN, 
                                                     Color.GREEN, Color.MAGENTA, 
                                                     Color.RED, Color.YELLOW, 
                                                     Color.BLACK, Color.DKGRAY, 
                                                     Color.GRAY, Color.LTGRAY, 
                                                     Color.WHITE };
  private static int[] mColorIds = new int[] { R.string.blue_color_name, 
                                               R.string.cyan_color_name,
                                               R.string.green_color_name,
                                               R.string.magenta_color_name,
                                               R.string.red_color_name,
                                               R.string.yellow_color_name,
                                               R.string.black_color_name,
                                               R.string.dkgray_color_name,
                                               R.string.gray_color_name,
                                               R.string.ltgray_color_name,
                                               R.string.white_color_name };
  private TextView mColorText;
  private Button mColorButton;
  
/*  private int monkey_state = 0;

  @Override
  public boolean onKeyDown (int keyCode, KeyEvent event) {
    if (keyCode == MyApplication.BACKDOOR_KEY) {
      switch (monkey_state) {
      case 0:
        mLabelText.setText(R.string.monkey_label_text);
        mDescriptionText.setText(R.string.monkey_description_text);
        mAmountText.setText("100");
        mCurrencyText.setText("EUR");
        monkey_state = 1;
        return true;
      case 1:
        saveState();
        Intent intent=new Intent();
        intent.putExtra("account_id", mAccount.id);
        setResult(RESULT_OK,intent);
        finish();
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }*/
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    currencyCodes = Account.getCurrencyCodes();
    currencyDescs = Account.getCurrencyDescs();
    
    setContentView(R.layout.one_account);
    configAmountInput();

    mLabelText = (EditText) findViewById(R.id.Label);
    mDescriptionText = (EditText) findViewById(R.id.Description);

    TextView openingBalanceLabel = (TextView) findViewById(R.id.OpeningBalanceLabel); 
    if (mMinorUnitP) {
      openingBalanceLabel.setText(getString(R.string.opening_balance) + "(¢)");
    } else {
      openingBalanceLabel.setText(getString(R.string.opening_balance));
    }

    mCurrencyText = (AutoCompleteTextView) findViewById(R.id.Currency);
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_dropdown_item_1line, currencyCodes);
    mCurrencyText.setAdapter(adapter);
    
    currencyInformer = new TextWatcher() {
      public void afterTextChanged(Editable s) {
        if (s.length() == 3) {
          int index = java.util.Arrays.asList(currencyCodes).indexOf(
              s.toString());
          if (index > -1) {
            Toast.makeText(AccountEdit.this,currencyDescs[index], Toast.LENGTH_LONG).show();
          }
        }
      }
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}
      public void onTextChanged(CharSequence s, int start, int before, int count){}
    };

    mCurrencyButton = (Button) findViewById(R.id.Select);
    mCurrencyButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        mCurrencyText.removeTextChangedListener(currencyInformer);
        showDialog(CURRENCY_DIALOG_ID);
      }
    });
    
    mTypeButton = (Button) findViewById(R.id.TaType);
    mTypeButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
        showDialog(TYPE_DIALOG_ID);
      }
    });
    
    Button confirmButton = (Button) findViewById(R.id.Confirm);
    Button cancelButton = (Button) findViewById(R.id.Revert);

    confirmButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (saveState()) {
          Intent intent=new Intent();
          intent.putExtra("account_id", mAccount.id);
          setResult(RESULT_OK,intent);
          finish();
        }
      }
    });
    cancelButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
      }
    });
    Account.Type [] allTypes = Account.Type.values();
    for(int i = 0;i< allTypes.length; i++){
      mTypes[i] = allTypes[i].getDisplayName(this);
    }
    
    mColorText = (TextView) findViewById(R.id.Color);
    
    mColorButton = (Button)  findViewById(R.id.SelectColor);
    mColorButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (Utils.isIntentAvailable(AccountEdit.this, OPENINTENTS_PICK_COLOR_ACTION)) {
          Intent intent = new Intent(OPENINTENTS_PICK_COLOR_ACTION);
          intent.putExtra(OPENINTENTS_COLOR_EXTRA, mAccountColor);
          startActivityForResult(intent, PICK_COLOR_REQUEST);
        } else {
          showDialog(COLOR_DIALOG_ID);
        }
      }
    });

    mColorNames = new String[mColorIds.length+1];
    for (int i = 0 ; i < mColorIds.length ; i++) {
      mColorNames[i] = getString(mColorIds[i]);
    }
    mColorNames[mColorIds.length] = getString(R.string.oi_pick_colors_info);
    
    populateFields();
  }
  
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    
    if (requestCode == PICK_COLOR_REQUEST) {
      if (resultCode == RESULT_OK) {
        mAccountColor = data.getExtras().getInt(OPENINTENTS_COLOR_EXTRA);
        mColorText.setBackgroundDrawable(new ColorDrawable(mAccountColor));
      }
    }
  }
  
  /* (non-Javadoc)
   * @see android.app.Activity#onPostCreate(android.os.Bundle)
   * we add the textwatcher only here, to prevent it being triggered
   * during orientation change
   */
  @Override
  protected void onPostCreate (Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    mCurrencyText.addTextChangedListener(currencyInformer);
  }
  @Override
  protected Dialog onCreateDialog(int id) {
    int checked;
    switch (id) {
      case CURRENCY_DIALOG_ID:
        checked = java.util.Arrays.asList(currencyCodes).indexOf(
            mCurrencyText.getText().toString());
        return new AlertDialog.Builder(this)
          .setTitle(R.string.dialog_title_select_currency)
          .setSingleChoiceItems(currencyDescs, checked, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
              mCurrencyText.setText(currencyCodes[item]);
              dismissDialog(CURRENCY_DIALOG_ID);
              mCurrencyText.addTextChangedListener(currencyInformer);
            }
          }).create();
      case TYPE_DIALOG_ID:
        checked = mAccount.type.ordinal();
        return new AlertDialog.Builder(this)
          .setTitle(R.string.dialog_title_select_type)
          .setSingleChoiceItems(mTypes, checked, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
              mTypeButton.setText(mTypes[item]);
              mAccountType = Account.Type.values()[item];
              dismissDialog(TYPE_DIALOG_ID);
            }
          }).create();
      case COLOR_DIALOG_ID:
          checked = java.util.Arrays.asList(mColors).indexOf(mAccountColor);
          return new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_select_color)
            .setSingleChoiceItems(mColorNames, checked, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int item) {
                if (item < mColors.length) {
                  mColorText.setBackgroundColor(mColors[item]);
                  mAccountColor = mColors[item];
                  dismissDialog(COLOR_DIALOG_ID);
                } else {
                  try {
                    dismissDialog(COLOR_DIALOG_ID);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=org.openintents.colorpicker"));
                    startActivity(intent);
                  } catch(Exception e) {
                    Toast toast = Toast.makeText(AccountEdit.this, R.string.error_accessing_gplay, Toast.LENGTH_SHORT);
                    toast.show();
                  }
                }
              }
            }).create();
    }
    return null;
  }
  /**
   * populates the input field either from the database or with default value for currency (from Locale)
   */
  private void populateFields() {
    Bundle extras = getIntent().getExtras();
    long rowId = extras != null ? extras.getLong(ExpensesDbAdapter.KEY_ROWID)
          : 0;
    if (rowId != 0) {
      try {
        mAccount = Account.getInstanceFromDb(rowId);
      } catch (DataObjectNotFoundException e) {
        e.printStackTrace();
        setResult(RESULT_CANCELED);
        finish();
      }
      setTitle(R.string.menu_edit_account);
      mLabelText.setText(mAccount.label);
      mDescriptionText.setText(mAccount.description);
      BigDecimal amount;
      if (mMinorUnitP) {
        amount = new BigDecimal(mAccount.openingBalance.getAmountMinor());
      } else {
        amount = mAccount.openingBalance.getAmountMajor();
      }
      mAmountText.setText(nfDLocal.format(amount));
      mCurrencyText.setText(mAccount.currency.getCurrencyCode());
      mAccountType = mAccount.type;
      mTypeButton.setText(mAccountType.getDisplayName(this));
      mAccountColor = mAccount.color;
      mColorText.setBackgroundColor(mAccountColor);
    } else {
      mAccount = new Account();
      setTitle(R.string.menu_insert_account);
      Locale l = Locale.getDefault();
      Currency c = Currency.getInstance(l);
      String s = c.getCurrencyCode();
      mCurrencyText.setText(s);
      mAccountType = Account.Type.CASH;
      mAccountColor = Color.LTGRAY;
    }
  }

  /**
   * validates currency (must be code from ISO 4217) and opening balance
   * (a valid float according to the format from the locale)
   * @return true upon success, false if validation fails
   */
  private boolean saveState() {
    String strCurrency = mCurrencyText.getText().toString();
    try {
      mAccount.setCurrency(strCurrency);
    } catch (IllegalArgumentException e) {
      Toast.makeText(this,getString(R.string.currency_not_iso4217,strCurrency), Toast.LENGTH_LONG).show();
      return false;
    }
    mAccount.label = mLabelText.getText().toString();
    mAccount.description = mDescriptionText.getText().toString();
    BigDecimal openingBalance = Utils.validateNumber(nfDLocal, mAmountText.getText().toString());
    if (openingBalance == null) {
      Toast.makeText(this,getString(R.string.invalid_number_format,nfDLocal.format(11.11)), Toast.LENGTH_LONG).show();
      return false;
    }
    if (mMinorUnitP) {
      mAccount.openingBalance.setAmountMinor(openingBalance.longValue());
    } else {
      mAccount.openingBalance.setAmountMajor(openingBalance);
    }
    //TODO make sure that this is retained upon orientation change
    mAccount.type = mAccountType;
    mAccount.color = mAccountColor;
    mAccount.save();
    return true;
  }
}
