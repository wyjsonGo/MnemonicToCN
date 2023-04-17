package com.wyjson.mnemonic_to_cn;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private MultiAutoCompleteTextView etContent;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        etContent = findViewById(R.id.et_content);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, MnemonicUtils.bip0039ENArray);
        etContent.setAdapter(adapter);
        etContent.setTokenizer(new SuperTokenizer(' ', true));

        TextView tvResult = findViewById(R.id.tv_result);
        radioGroup = findViewById(R.id.rg);

        findViewById(R.id.btn_clean).setOnClickListener(view -> {
            closeKeyboard(MainActivity.this);
            etContent.setText(null);
        });

        findViewById(R.id.btn_en_to_cn).setOnClickListener(view -> {
            closeKeyboard(MainActivity.this);
            radioGroup.setVisibility(View.VISIBLE);
            String content = etContent.getText().toString().trim();
            String result = setFormat(MnemonicUtils.parseEnToCN(content));
            tvResult.setText(result);
            if (TextUtils.isEmpty(result)) {
                Toast.makeText(this, "请输入英文助记词", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn_cn_to_en).setOnClickListener(view -> {
            closeKeyboard(MainActivity.this);
            radioGroup.setVisibility(View.GONE);
            String content = etContent.getText().toString().trim();
            String result = MnemonicUtils.parseCNToEN(content);
            tvResult.setText(result);
            if (TextUtils.isEmpty(result)) {
                Toast.makeText(this, "请输入中文", Toast.LENGTH_SHORT).show();
            }
        });

        tvResult.setOnLongClickListener(view -> {
            String result = ((TextView) view).getText().toString();
            if (TextUtils.isEmpty(result))
                return false;
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("result", result));
            Toast.makeText(this, "复制成功!", Toast.LENGTH_SHORT).show();
            return true;
        });

        radioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            format = Integer.parseInt(radioGroup.findViewById(id).getTag().toString());
            tvResult.setText(setFormat(tvResult.getText().toString()));
        });
    }

    private int format = 0;

    private String setFormat(String content) {
        if (TextUtils.isEmpty(content))
            return content;
        content = content.replace("\n", "");
        if (format != 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= content.length(); i++) {
                String item = content.substring(i - 1, i);
                sb.append(item);
                if (i % format == 0 && i != content.length())
                    sb.append("\n");
            }
            content = sb.toString();
        }
        return content;
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}